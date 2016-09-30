/**
 * Copyright (C) 2011 Whisper Systems
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mymobkit.mms;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.mymobkit.common.AppPreference;
import com.mymobkit.common.ServiceUtils;
import com.mymobkit.common.TelephonyUtils;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.mms.databases.ApnDatabase;
import com.mymobkit.mms.utils.Utils;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.NoConnectionReuseStrategyHC4;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

@SuppressWarnings("deprecation")
public abstract class LegacyMmsConnection {

    public static final String USER_AGENT = "Android-Mms/2.0";

    private static final String TAG = makeLogTag(LegacyMmsConnection.class);

    protected final Context context;
    protected final Apn apn;

    protected LegacyMmsConnection(Context context) throws ApnUnavailableException {
        this.context = context;
        this.apn = getApn(context);
    }

    public static Apn getApn(Context context) throws ApnUnavailableException {

        try {
            Apn apn = ApnDatabase.getInstance(context).getMmsConnectionParameters(TelephonyUtils.getMccMnc(context), TelephonyUtils.getApn(context));
            if (apn == null || TextUtils.isEmpty(apn.getMmsc())) {
                throw new ApnUnavailableException("No parameters available from ApnDefaults.");
            }
            return apn;
        } catch (IOException ioe) {
            throw new ApnUnavailableException("ApnDatabase threw an IOException", ioe);
        }
    }

    protected boolean isDirectConnect() {
        // We think Sprint supports direct connection over wifi/data, but not Verizon
        Set<String> sprintMccMncs = new HashSet<String>() {{
            add("312530");
            add("311880");
            add("311870");
            add("311490");
            add("310120");
            add("316010");
            add("312190");
        }};

        return ServiceUtils.getTelephonyManager(context).getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA &&
                sprintMccMncs.contains(TelephonyUtils.getMccMnc(context));
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    protected static boolean checkRouteToHost(Context context, String host, boolean usingMmsRadio)
            throws IOException {
        InetAddress inetAddress = InetAddress.getByName(host);
        if (!usingMmsRadio) {
            if (inetAddress.isSiteLocalAddress()) {
                throw new IOException("RFC1918 address in non-MMS radio situation!");
            }
            LOGW(TAG, "returning vacuous success since MMS radio is not in use");
            return true;
        }

        if (inetAddress == null) {
            throw new IOException("Unable to lookup host: InetAddress.getByName() returned null.");
        }

        byte[] ipAddressBytes = inetAddress.getAddress();
        if (ipAddressBytes == null) {
            LOGW(TAG, "resolved IP address bytes are null, returning true to attempt a connection anyway.");
            return true;
        }

        LOGW(TAG, "Checking route to address: " + host + ", " + inetAddress.getHostAddress());
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            final Method requestRouteMethod = manager.getClass().getMethod("requestRouteToHostAddress", Integer.TYPE, InetAddress.class);
            final boolean routeToHostObtained = (Boolean) requestRouteMethod.invoke(manager, MmsRadio.TYPE_MOBILE_MMS, inetAddress);
            LOGW(TAG, "requestRouteToHostAddress(" + inetAddress + ") -> " + routeToHostObtained);
            return routeToHostObtained;
        } catch (NoSuchMethodException nsme) {
            LOGW(TAG, "[checkRouteToHost] No such method", nsme);
        } catch (IllegalAccessException iae) {
            LOGW(TAG, "[checkRouteToHost] Illegal access", iae);
        } catch (InvocationTargetException ite) {
            LOGW(TAG, "[checkRouteToHost] Invocation target exception", ite);
        }

        final int ipAddress = ValidationUtils.byteArrayToIntLittleEndian(ipAddressBytes, 0);
        final boolean routeToHostObtained = manager.requestRouteToHost(MmsRadio.TYPE_MOBILE_MMS, ipAddress);
        LOGW(TAG, "requestRouteToHost(" + ipAddress + ") -> " + routeToHostObtained);
        return routeToHostObtained;
    }

    protected static byte[] parseResponse(InputStream is) throws IOException {
        InputStream in = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Utils.copy(in, baos);

        LOGW(TAG, "Received full server response, " + baos.size() + " bytes");

        return baos.toByteArray();
    }

    protected CloseableHttpClient constructHttpClient() throws IOException {
        int timeOutSeconds = 120;    // Before is 20
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeOutSeconds * 1000)
                .setConnectionRequestTimeout(timeOutSeconds * 1000)
                .setSocketTimeout(timeOutSeconds * 1000)
                .setMaxRedirects(20)
                .build();

        URL mmsc = new URL(apn.getMmsc());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        if (apn.hasAuthentication()) {
            credsProvider.setCredentials(new AuthScope(mmsc.getHost(), mmsc.getPort() > -1 ? mmsc.getPort() : mmsc.getDefaultPort()),
                    new UsernamePasswordCredentials(apn.getUsername(), apn.getPassword()));
        }

        return HttpClients.custom()
                .setConnectionReuseStrategy(new NoConnectionReuseStrategyHC4())
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setUserAgent(AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_APN_MMS_USER_AGENT, USER_AGENT))
                .setConnectionManager(new BasicHttpClientConnectionManager())
                .setDefaultRequestConfig(config)
                .setDefaultCredentialsProvider(credsProvider)
                .build();
    }

    protected byte[] execute(HttpUriRequest request) throws IOException {
        LOGW(TAG, "connecting to " + apn.getMmsc());

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = constructHttpClient();
            response = client.execute(request);

            LOGW(TAG, "* response code: " + response.getStatusLine());

            if (response.getStatusLine().getStatusCode() == 200) {
                return parseResponse(response.getEntity().getContent());
            }
        } catch (NullPointerException npe) {
            // TODO determine root cause
            // see: https://github.com/WhisperSystems/Signal-Android/issues/4379
            throw new IOException(npe);
        } finally {
            if (response != null) response.close();
            if (client != null) client.close();
        }

        throw new IOException("unhandled response code");
    }

    protected List<Header> getBaseHeaders() {
        final String number = TelephonyUtils.getManager(context).getLine1Number();
        ;

        return new LinkedList<Header>() {{
            add(new BasicHeader("Accept", "*/*, application/vnd.wap.mms-message, application/vnd.wap.sic"));
            add(new BasicHeader("x-wap-profile", "http://www.google.com/oha/rdf/ua-profile-kila.xml"));
            add(new BasicHeader("Content-Type", "application/vnd.wap.mms-message"));
            add(new BasicHeader("x-carrier-magic", "http://magic.google.com"));
            if (!TextUtils.isEmpty(number)) {
                add(new BasicHeader("x-up-calling-line-id", number));
                add(new BasicHeader("X-MDN", number));
            }
        }};
    }

    public static class Apn {

        public static Apn EMPTY = new Apn("", "", "", "", "");

        private final String mmsc;
        private final String proxy;
        private final String port;
        private final String username;
        private final String password;

        public Apn(String mmsc, String proxy, String port, String username, String password) {
            this.mmsc = mmsc;
            this.proxy = proxy;
            this.port = port;
            this.username = username;
            this.password = password;
        }


        public boolean hasProxy() {
            return !TextUtils.isEmpty(proxy);
        }

        public String getMmsc() {
            return mmsc;
        }

        public String getProxy() {
            return hasProxy() ? proxy : null;
        }

        public int getPort() {
            return TextUtils.isEmpty(port) ? 80 : Integer.parseInt(port);
        }

        public boolean hasAuthentication() {
            return !TextUtils.isEmpty(username);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return Apn.class.getSimpleName() +
                    "{ mmsc: \"" + mmsc + "\"" +
                    ", proxy: " + (proxy == null ? "none" : '"' + proxy + '"') +
                    ", port: " + (port == null ? "(none)" : port) +
                    ", user: " + (username == null ? "none" : '"' + username + '"') +
                    ", pass: " + (password == null ? "none" : '"' + password + '"') + " }";
        }
    }
}
