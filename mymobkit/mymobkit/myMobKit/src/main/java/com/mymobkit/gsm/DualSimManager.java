package com.mymobkit.gsm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import com.mymobkit.common.TelephonyUtils;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;


@SuppressLint("NewApi")
public class DualSimManager {

    private static final String TAG = makeLogTag(DualSimManager.class);

    private String simVariant = "";
    private String telephonyClassName = "";
    private int slotNumber1 = 0;
    private int slotNumber2 = 1;
    private String slotName1 = "null";
    private String slotName2 = "null";
    private String[] listofClass;

    private String imei1, imsi1, networkType1, networkOperatorName1;
    private String simOperatorCode1;
    private String simOperatorName1;
    private String simNetworkSignalStrength1;
    private String imei2, imsi2, networkType2, networkOperatorName2;
    private String simOperatorCode2;
    private String simOperatorName2;
    private String simNetworkSignalStrength2;
    private String networkOperatorCode1, isGPRS1;
    private String networkOperatorCode2, isGPRS2;
    private String simSerial1, simSerial2;
    boolean isRoaming1, isRoaming2;
    private int[] cellLoc1;
    private int[] cellLoc2;
    private String networkCountryIso1;
    private String networkCountryIso2;

    public final static String m_IMEI = "getDeviceId";
    public final static String m_IMSI = "getSubscriberId";

    public final static String m_SIM_OPERATOR_NAME = "getSimOperatorName";

    public final static String m_NETWORK_COUNTRY_ISO = "getNetworkCountryIso";

    public final static String m_NETWORK_OPERATOR = "getNetworkOperatorName";
    public final static String m_NETWORK_OPERATOR_CODE = "getNetworkOperator";

    public final static String m_NETWORK_TYPE_NAME = "getNetworkTypeName";

    public final static String m_CELL_LOC = "getNeighboringCellInfo";
    public final static String m_IS_ROAMING = "isNetworkRoaming";

    public final static String m_SIM_SERIAL = "getSimSerialNumber";
    public final static String m_SIM_SUBSCRIBER = "getSubscriberId";

    public final static String m_SIM_OPERATOR_CODE = "getSimOperator";
    public final static String m_DATA_STATE = "getDataNetworkType";

    protected static CustomTelephony customTelephony;

    public DualSimManager(Context mContext) {
        try {
            if (imei1 == null) {
                customTelephony = new CustomTelephony(mContext);
            } else {
                customTelephony.getCurrentData();
            }
            if (customTelephony.getImeiList().size() > 0)
                customTelephony.getDefaultSimInfo();
        } catch (Exception e) {
        }

    }

    public boolean isSimSupported() {
        if (!TextUtils.isEmpty(imei1) || !TextUtils.isEmpty(imei2)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isDualSIMSupported() {
        if (!TextUtils.isEmpty(imei1) && !TextUtils.isEmpty(imei2)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFirstSimActive() {
        if (imsi1 == null || TextUtils.isEmpty(imsi1)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSecondSimActive() {
        if (imsi2 == null || TextUtils.isEmpty(imsi2)) {
            return false;
        } else if (imsi1 != null && imsi1.equalsIgnoreCase(imsi2)) {
            return false;
        } else {
            return true;
        }

    }

    public int getSupportedSimCount() {
        if (isSimSupported()) {
            if (isDualSIMSupported()) {
                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    public String getImei(int slotNumber) {
        if (slotNumber == 0)
            return imei1;
        else
            return imei2;
    }

    public String getImsi(int slotNumber) {
        if (slotNumber == 0)
            return imsi1;
        else
            return imsi2;
    }


    public String getNetworkOperatorName(int slotNumber) {
        if (slotNumber == 0)
            return networkOperatorName1;
        else {
            return networkOperatorName2;
        }
    }

    public String getSimOperatorCode(int slotNumber) {
        if (slotNumber == 0)
            return simOperatorCode1;
        else
            return simOperatorCode2;
    }

    public String getSimOperatorName(int slotNumber) {
        if (slotNumber == 0)
            return simOperatorName1;
        else
            return simOperatorName2;
    }

    public String getNetworkCountryIso(int slotNumber) {
        if (slotNumber == 0)
            return networkCountryIso1;
        else
            return networkCountryIso1;
    }

    //public String getSimSubscriber() {
    //    return m_SIM_SUBSCRIBER;
    //}

    public String getSimSerial(int slotNumber) {
        //return m_SIM_SERIAL;

        if (slotNumber == 0)
            return simSerial1;
        else
            return simSerial2;
    }

    public String getSimNetworkSignalStrength(int slotNumber) {
        if (slotNumber == 0)
            return simNetworkSignalStrength1;
        else
            return simNetworkSignalStrength2;
    }

    public boolean isGPRS(int slotnumber) {
        boolean isGPRS = false;
        try {
            String gprsFlag = isGPRS1;
            if (slotnumber == 1) {
                gprsFlag = isGPRS2;
            }
            int gprsInt = Integer.parseInt(gprsFlag);
            if (gprsInt == TelephonyManager.DATA_CONNECTING || gprsInt == TelephonyManager.DATA_CONNECTED) {
                isGPRS = true;
            }
        } catch (Exception e) {
            LOGE(TAG, "[isGPRS] Unable to validate slot", e);
        }
        return isGPRS;
    }


    /*public void setSimNetworkSignalStrength1(String simNetworkSignalStrength1) {
        this.simNetworkSignalStrength1 = simNetworkSignalStrength1;
    }*/


    public int getSimCellId(int slotNumber) {
        if (slotNumber == 0)
            return cellLoc1[0];
        else
            return cellLoc2[0];
    }


    public int getSimLocId(int slotNumber) {
        if (slotNumber == 0)
            return cellLoc1[1];
        else
            return cellLoc2[1];
    }

    public String getNetworkType(int slotNumber) {

        if (slotNumber == 0)
            return networkType1;
        else
            return networkType2;
    }

    public int[] getNetworkOperatorCode(int slotNumber) {
        int operatorCode[] = new int[2];
        String code = networkOperatorCode1;
        if (slotNumber == 0)
            if (TextUtils.isEmpty(networkOperatorCode1) && !TextUtils.isEmpty(simOperatorCode1)) {
                code = simOperatorCode1;
            } else {
                code = networkOperatorCode1;
            }
        else {
            if (TextUtils.isEmpty(networkOperatorCode2) && !TextUtils.isEmpty(simOperatorCode2)) {
                code = simOperatorCode2;
            } else {
                code = networkOperatorCode2;
            }
        }

		/*if(slotnumber==1){
            code = networkOperatorCode2;
		}*/
        operatorCode[0] = -1;
        operatorCode[1] = -1;
        try {
            if (code != null) {
                operatorCode[0] = Integer.parseInt(code.substring(0, 3));
                operatorCode[1] = Integer.parseInt(code.substring(3));
            }
        } catch (Exception e) {
        }
        return operatorCode;
    }

    public boolean isRoaming(int slotNumber) {
        if (slotNumber == 0) {
            return isRoaming1;
        } else {
            return isRoaming2;
        }
    }

    class CustomTelephony {
        Context context;
        TelephonyManager telephony;

        public CustomTelephony(Context context) {
            try {
                this.context = context;
                telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                telephonyClassName = "";
                simVariant = "";
                slotName1 = "";
                slotName2 = "";
                slotNumber1 = 1;
                slotNumber2 = 2;
                fetchClassInfo();
                if (telephonyClassName.equalsIgnoreCase("")) {
                    fetchClassInfo();
                } else if (!isValidMethod(telephonyClassName)) {
                    fetchClassInfo();
                }
                gettingAllMethodValues();

            } catch (Exception e) {
                LOGE(TAG, "[CustomTelephony] Init error", e);
            }
        }

        /**
         * This method returns the class name in which we fetch dual sim details
         */
        public void fetchClassInfo() {
            try {
                telephonyClassName = "android.telephony.TelephonyManager";
                listofClass = new String[]{
                        "com.mediatek.telephony.TelephonyManagerEx",
                        "android.telephony.TelephonyManager",
                        "android.telephony.MSimTelephonyManager",
                        "android.telephony.TelephonyManager"};
                for (int index = 0; index < listofClass.length; index++) {
                    if (isTelephonyClassExists(listofClass[index])) {
                        if (isMethodExists(listofClass[index], "getDeviceId")) {
                            if (!simVariant.equalsIgnoreCase("")) {
                                break;
                            }
                        }
                        if (isMethodExists(listofClass[index], "getNetworkOperatorName")) {
                            break;
                        } else if (isMethodExists(listofClass[index], "getSimOperatorName")) {
                            break;
                        }
                    }
                }
                for (int index = 0; index < listofClass.length; index++) {
                    try {
                        if (slotName1 == null || slotName1.equalsIgnoreCase("")) {
                            getValidSlotFields(listofClass[index]);
                            // if(slotName1!=null || !slotName1.equalsIgnoreCase("")){
                            getSlotNumber(listofClass[index]);
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        LOGE(TAG, "[fetchClassInfo] Unable to get class info", e);
                    }
                }
            } catch (Exception e) {
                LOGE(TAG, "[fetchClassInfo] Unable to get class info", e);
            }
        }


        /**
         * Check Method is found in class
         */
        public boolean isValidMethod(String className) {
            boolean isValidMail = false;
            try {
                if (isMethodExists(className, "getDeviceId")) {
                    isValidMail = true;
                } else if (isMethodExists(className, "getNetworkOperatorName")) {
                    isValidMail = true;
                } else if (isMethodExists(className, "getSimOperatorName")) {
                    isValidMail = true;
                }
            } catch (Exception e) {
                LOGE(TAG, "[fetchClassInfo] Unable to validate method", e);
            }
            return isValidMail;
        }

        public String getMethodValue(String className, String compairMethod, int slotNumber1) {
            String value = "";
            try {
                Class<?> telephonyClass = Class.forName(className);
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Method[] methodList = telephonyClass.getDeclaredMethods();
                for (int index = methodList.length - 1; index >= 0; index--) {
                    if (methodList[index].getReturnType().equals(String.class)) {
                        String methodName = methodList[index].getName();
                        if (methodName.contains(compairMethod)) {
                            Class<?>[] param = methodList[index].getParameterTypes();
                            if (param.length > 0) {
                                if (param[0].equals(int.class)) {
                                    try {
                                        simVariant = methodName.substring(
                                                compairMethod.length(),
                                                methodName.length());
                                        if (!methodName.equalsIgnoreCase(compairMethod + "Name") && !methodName.equalsIgnoreCase(compairMethod + "ForSubscription")) {
                                            value = invokeMethod(telephonyClassName, slotNumber1, compairMethod, simVariant);
                                            if (!TextUtils.isEmpty(value)) {
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        LOGE(TAG, "[getMethodValue] Unable to get method value", e);
                                    }
                                } else if (param[0].equals(long.class)) {
                                    try {
                                        simVariant = methodName.substring(
                                                compairMethod.length(),
                                                methodName.length());
                                        if (!methodName.equalsIgnoreCase(compairMethod + "Name") && !methodName.equalsIgnoreCase(compairMethod + "ForSubscription")) {
                                            value = invokeLongMethod(telephonyClassName, slotNumber1, compairMethod, simVariant);
                                            if (!TextUtils.isEmpty(value)) {
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        LOGE(TAG, "[getMethodValue] Unable to get method value", e);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGE(TAG, "[getMethodValue] Unable to get method value", e);
            }
            return value;
        }

        /**
         * Check method with sim variant
         */
        public boolean isMethodExists(String className, String compairMethod) {
            boolean isExists = false;
            try {
                Class<?> telephonyClass = Class.forName(className);
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                StringBuffer sbf = new StringBuffer();
                Method[] methodList = telephonyClass.getDeclaredMethods();
                for (int index = methodList.length - 1; index >= 0; index--) {
                    sbf.append("\n\n" + methodList[index].getName());
                    if (methodList[index].getReturnType().equals(String.class)) {
                        String methodName = methodList[index].getName();
                        if (methodName.contains(compairMethod)) {
                            Class<?>[] param = methodList[index]
                                    .getParameterTypes();
                            if (param.length > 0) {
                                if (param[0].equals(int.class)) {
                                    try {
                                        simVariant = methodName.substring(compairMethod.length(), methodName.length());
                                        telephonyClassName = className;
                                        isExists = true;
                                        break;
                                    } catch (Exception e) {
                                        LOGE(TAG, "[isMethodExists] Unable to get check method", e);
                                    }
                                } else {
                                    telephonyClassName = className;
                                    isExists = true;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGE(TAG, "[isMethodExists] Unable to get check method", e);
            }
            return isExists;
        }

        public ArrayList<String> getImsiList() {
            ArrayList<String> imeiList = new ArrayList<String>();
            imsi1 = invokeMethod(telephonyClassName, slotNumber1, m_IMSI, simVariant);
            if (TextUtils.isEmpty(imsi1)) {
                imsi1 = getMethodValue(telephonyClassName, m_IMSI, slotNumber1);
            }
            if (imsi1 == null || imsi1.equalsIgnoreCase("")) {
                imsi1 = telephony.getSubscriberId();
            }
            imsi2 = invokeMethod(telephonyClassName, slotNumber2, m_IMSI, simVariant);
            if (TextUtils.isEmpty(imsi2)) {
                imsi2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber2);
                if (TextUtils.isEmpty(imsi2)) {
                    imsi2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber2 + 1);
                }
            }
            if (!TextUtils.isEmpty(imsi2) && !TextUtils.isEmpty(imsi1)) {
                if (imsi1.equalsIgnoreCase(imsi2)) {
                    imsi1 = "";
                }
            }
            if (imsi1 != null && imsi2 != null && imsi1.equalsIgnoreCase("")) {
                String IMSI2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber2 + 1);
                if (!TextUtils.isEmpty(IMSI2)) {
                    imsi1 = imsi2;
                    imsi2 = IMSI2;
                    slotNumber1 = slotNumber2;
                    slotNumber2 = slotNumber2 + 1;
                }
            }
            if (!TextUtils.isEmpty(imsi1)) {
                imeiList.add(imsi1);
            }
            if (!TextUtils.isEmpty(imsi2)) {
                imeiList.add(imsi2);
            }
            return imeiList;
        }

        public ArrayList<String> getImeiList() {
            ArrayList<String> imeiList = new ArrayList<String>();
            try {
                imei1 = invokeMethod(telephonyClassName, slotNumber1, m_IMEI, simVariant);
                if (TextUtils.isEmpty(imei1)) {
                    imei1 = getMethodValue(telephonyClassName, m_IMEI, slotNumber1);
                }
                if (imei1 == null || imei1.equalsIgnoreCase("")) {
                    imei1 = telephony.getDeviceId();
                }
                imei2 = invokeMethod(telephonyClassName, slotNumber2, m_IMEI, simVariant);
                if (TextUtils.isEmpty(imei2)) {
                    imei2 = getMethodValue(telephonyClassName, m_IMEI, slotNumber1);
                }
            } catch (Exception e) {
                LOGE(TAG, "[getImeiList] Unable to get IMEI", e);
            }
            if (!TextUtils.isEmpty(imei2)) {
                imeiList.add(imei1);
            }
            if (!TextUtils.isEmpty(imei2)) {
                imeiList.add(imei2);
            }
            return imeiList;
        }

        public void getDefaultSimInfo() {
            telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String IMSI = telephony.getSubscriberId();
            if (TextUtils.isEmpty(imsi1) || imsi1.equalsIgnoreCase(IMSI)) {
                imei1 = telephony.getDeviceId();
                isGPRS1 = String.valueOf(telephony.isNetworkRoaming());
                simOperatorCode1 = telephony.getSimOperator();
                simOperatorName1 = telephony.getSimOperatorName();
                networkOperatorCode1 = telephony.getNetworkOperator();
                networkOperatorName1 = telephony.getNetworkOperatorName();
                networkType1 = TelephonyUtils.getNetworkTypeName(context, telephony.getNetworkType());
            } else if (isSecondSimActive() && imsi2.equalsIgnoreCase(IMSI)) {
                imei2 = telephony.getDeviceId();
                isGPRS2 = String.valueOf(telephony.isNetworkRoaming());
                simOperatorCode2 = telephony.getSimOperator();
                simOperatorName2 = telephony.getSimOperatorName();
                networkOperatorCode2 = telephony.getNetworkOperator();
                networkOperatorName2 = telephony.getNetworkOperatorName();
                networkType2 = TelephonyUtils.getNetworkTypeName(context, telephony.getNetworkType());
            }

        }


        public void gettingAllMethodValues() {
            try {
                imei1 = invokeMethod(telephonyClassName, slotNumber1, m_IMEI, simVariant);
                if (TextUtils.isEmpty(imei1)) {
                    imei1 = getMethodValue(telephonyClassName, m_IMEI, slotNumber1);
                }
                if (imei1 == null || imei1.equalsIgnoreCase("")) {
                    imei1 = telephony.getDeviceId();
                }
                imei2 = invokeMethod(telephonyClassName, slotNumber2, m_IMEI, simVariant);
                if (TextUtils.isEmpty(imei2)) {
                    imei2 = getMethodValue(telephonyClassName, m_IMEI, slotNumber1);
                }
                imsi1 = invokeMethod(telephonyClassName, slotNumber1, m_IMSI, simVariant);
                if (TextUtils.isEmpty(imsi1)) {
                    imsi1 = getMethodValue(telephonyClassName, m_IMSI, slotNumber1);
                }
                if (imsi1 == null || imsi1.equalsIgnoreCase("")) {
                    imsi1 = telephony.getSubscriberId();
                }
                imsi2 = invokeMethod(telephonyClassName, slotNumber2, m_IMSI, simVariant);
                if (TextUtils.isEmpty(imsi2)) {
                    imsi2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber2);
                    if (TextUtils.isEmpty(imsi2)) {
                        imsi2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber2 + 1);
                    }
                }
                if (!TextUtils.isEmpty(imsi2) && !TextUtils.isEmpty(imsi1)) {
                    if (imsi1.equalsIgnoreCase(imsi2)) {
                        imsi1 = "";
                    }
                }
                if (imsi1 != null && imsi2 != null && imsi1.equalsIgnoreCase("")) {
                    String IMSI2 = getMethodValue(telephonyClassName, m_IMSI, slotNumber2 + 1);
                    if (!TextUtils.isEmpty(IMSI2)) {
                        imsi1 = imsi2;
                        imsi2 = IMSI2;
                        slotNumber1 = slotNumber2;
                        slotNumber2 = slotNumber2 + 1;
                    }
                }

                networkCountryIso1 = getMethodValue(telephonyClassName, m_NETWORK_COUNTRY_ISO, 0);
                networkCountryIso2 = getMethodValue(telephonyClassName, m_NETWORK_COUNTRY_ISO, 1);
                if (TextUtils.isEmpty(networkCountryIso1))
                    networkCountryIso1 = invokeMethod(telephonyClassName, slotNumber1, m_NETWORK_COUNTRY_ISO, simVariant);
                if (TextUtils.isEmpty(networkCountryIso1)) {
                    networkCountryIso1 = getMethodValue(telephonyClassName, m_NETWORK_COUNTRY_ISO, slotNumber1);
                }
                if (TextUtils.isEmpty(networkCountryIso2))
                    networkCountryIso2 = invokeMethod(telephonyClassName, slotNumber2, m_NETWORK_COUNTRY_ISO, simVariant);
                if (TextUtils.isEmpty(networkCountryIso2)) {
                    networkCountryIso2 = getMethodValue(telephonyClassName, m_NETWORK_COUNTRY_ISO, slotNumber2);
                }

                simOperatorName1 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_NAME, 0);
                simOperatorName2 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_NAME, 1);
                if (TextUtils.isEmpty(simOperatorName1))
                    simOperatorName1 = invokeMethod(telephonyClassName, slotNumber1, m_SIM_OPERATOR_NAME, simVariant);
                if (TextUtils.isEmpty(simOperatorName1)) {
                    simOperatorName1 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_NAME, slotNumber1);
                }
                if (TextUtils.isEmpty(simOperatorName2))
                    simOperatorName2 = invokeMethod(telephonyClassName, slotNumber2, m_SIM_OPERATOR_NAME, simVariant);
                if (TextUtils.isEmpty(simOperatorName2)) {
                    simOperatorName2 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_NAME, slotNumber2);
                }

                simOperatorCode1 = invokeMethod(telephonyClassName, slotNumber1, m_SIM_OPERATOR_CODE, simVariant);
                if (TextUtils.isEmpty(simOperatorCode1)) {
                    simOperatorCode1 = getMethodValue(telephonyClassName, m_SIM_OPERATOR_CODE, slotNumber1);
                }
                simOperatorCode2 = invokeMethod(telephonyClassName, slotNumber2, m_SIM_OPERATOR_CODE, simVariant);

                networkOperatorName1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, 0);
                networkOperatorName2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, 1);
                if (TextUtils.isEmpty(networkOperatorName1))
                    networkOperatorName1 = invokeMethod(telephonyClassName, slotNumber1, m_NETWORK_OPERATOR, simVariant);
                if (TextUtils.isEmpty(networkOperatorName1)) {
                    networkOperatorName1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, slotNumber1);
                }

                if (TextUtils.isEmpty(networkOperatorName2))
                    networkOperatorName2 = invokeMethod(telephonyClassName, slotNumber2, m_NETWORK_OPERATOR_CODE, simVariant);
                if (TextUtils.isEmpty(networkOperatorName2)) {
                    networkOperatorName2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, slotNumber2);
                }
                if (networkOperatorName1.equalsIgnoreCase(""))
                    networkOperatorName1 = invokeMethod(telephonyClassName, slotNumber1, m_NETWORK_OPERATOR, simVariant);

                if (networkOperatorName2.equalsIgnoreCase(""))
                    networkOperatorName2 = invokeMethod(telephonyClassName, slotNumber2, m_NETWORK_OPERATOR, simVariant);

                if (TextUtils.isEmpty(networkOperatorCode1)) {
                    networkOperatorName1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, slotNumber1);
                }

                if (TextUtils.isEmpty(networkOperatorCode1)) {
                    networkOperatorName2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR, slotNumber2);
                }
                networkOperatorCode1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, 0);
                networkOperatorCode2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, 1);
                if (TextUtils.isEmpty(networkOperatorCode1))
                    networkOperatorCode1 = invokeMethod(telephonyClassName, slotNumber1, m_NETWORK_OPERATOR_CODE, simVariant);
                if (TextUtils.isEmpty(networkOperatorCode1)) {
                    networkOperatorCode1 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, slotNumber1);
                }

                if (TextUtils.isEmpty(networkOperatorCode2))
                    networkOperatorCode2 = invokeMethod(telephonyClassName, slotNumber2, m_NETWORK_OPERATOR_CODE, simVariant);
                if (TextUtils.isEmpty(networkOperatorCode2)) {
                    networkOperatorCode2 = getMethodValue(telephonyClassName, m_NETWORK_OPERATOR_CODE, slotNumber2);
                }
                simSerial1 = getSimSerialNumber(slotNumber1);
                simSerial2 = getSimSerialNumber(slotNumber2);
                if (simSerial1.equalsIgnoreCase(simSerial2)) {
                    simSerial2 = getSimSerialNumber(slotNumber2 + 1);
                }
                getCurrentData();
                if (networkOperatorName1 == null || networkOperatorName1.equalsIgnoreCase("") || networkOperatorName1.equalsIgnoreCase("UNKNOWN")) {
                    networkOperatorName1 = telephony.getSimOperatorName();
                }
                if (networkOperatorCode1 == null || networkOperatorCode1.equalsIgnoreCase("") || networkOperatorCode1.equalsIgnoreCase("UNKNOWN")) {
                    networkOperatorCode1 = telephony.getSimOperator();
                }
                if (TextUtils.isEmpty(m_IS_ROAMING)) {
                    isRoaming1 = telephony.isNetworkRoaming();
                }

            } catch (Exception e) {
                LOGE(TAG, "[gettingAllMethodValues] Unable to get method values", e);
            }
        }

        public String getSimSerialNumber(int slotNumber) {
            String simSerial = invokeMethod(telephonyClassName, slotNumber, m_SIM_SUBSCRIBER, simVariant);
            if (TextUtils.isEmpty(simSerial)) {
                simSerial = getMethodValue(telephonyClassName, m_SIM_SUBSCRIBER, slotNumber);
                if (TextUtils.isEmpty(simSerial)) {
                    simSerial = getMethodValue(telephonyClassName, m_SIM_SERIAL, slotNumber);
                }
                if (TextUtils.isEmpty(simSerial)) {
                    simSerial = getMethodValue(telephonyClassName, m_SIM_SERIAL, slotNumber);
                }
            }
            return simSerial;
        }

        private void getCurrentData() {
            try {
                telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                networkType1 = getNetworkType(0);
                networkType2 = getNetworkType(1);
                if (networkType1 == null || networkType1.equalsIgnoreCase("") || networkType1.equalsIgnoreCase("UNKNOWN")) {
                    networkType1 = TelephonyUtils.getNetworkTypeName(context, telephony.getNetworkType());
                }
                isRoaming1 = invokeMethodBoolean(telephonyClassName, slotNumber1, m_IS_ROAMING, simVariant);
                if (!isRoaming1) {
                    isRoaming1 = telephony.isNetworkRoaming();
                }
                isRoaming2 = invokeMethodBoolean(telephonyClassName, slotNumber2, m_IS_ROAMING, simVariant);

                isGPRS1 = invokeMethod(telephonyClassName, slotNumber1, m_DATA_STATE, simVariant);
                isGPRS2 = invokeMethod(telephonyClassName, slotNumber2, m_DATA_STATE, simVariant);

                cellLoc1 = getCellLocation(slotNumber1);
                cellLoc2 = getCellLocation(slotNumber2);


            } catch (Exception e) {
                LOGE(TAG, "[getCurrentData] Unable to get data", e);
            }
        }

        public String getNetworkType(int slotNumber) {
            String networkType = "UNKNOWN";
            try {
                if (slotNumber == 0) {
                    networkType = invokeMethod(telephonyClassName, slotNumber1, m_NETWORK_TYPE_NAME, simVariant);
                } else {
                    networkType = invokeMethod(telephonyClassName, slotNumber2, m_NETWORK_TYPE_NAME, simVariant);
                }
                if (networkType.equalsIgnoreCase("")) {
                    try {
                        networkType = getDeviceIdBySlot("getNetworkType", slotNumber);
                    } catch (Exception e) {
                    }
                    if (networkType.equalsIgnoreCase("")) {
                        try {
                            networkType = getDeviceIdBySlotOld("getNetworkTypeGemini", slotNumber);
                        } catch (Exception e) {
                        }
                    }
                }
                ConnectivityInfo connInfo = new ConnectivityInfo(context);
                networkType = connInfo.getNetworkTypeName(Integer.parseInt(networkType));
                if (slotNumber == 0 && !TextUtils.isEmpty(networkType)) {
                    networkType = connInfo.getNetworkTypeName(telephony.getNetworkType());
                }
            } catch (Exception e) {
                LOGE(TAG, "[getNetworkType] Unable to get network type", e);
            }
            return networkType;
        }

        public int[] getCellLocation(int slot) {
            int[] cellLoc = new int[]{-1, -1};
            try {
                if (slot == slotNumber1) {
                    if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                        GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
                        if (location == null) {
                            location = (GsmCellLocation) telephony
                                    .getCellLocation();
                        }
                        if (location != null) {
                            cellLoc[0] = location.getCid();
                            cellLoc[1] = location.getLac();
                        }
                    }
                } else {
                    Object cellInfo = (Object) getObjectBySlot("getNeighboringCellInfo" + simVariant, slot);
                    if (cellInfo != null) {
                        List<NeighboringCellInfo> info = (List<NeighboringCellInfo>) cellInfo;
                        cellLoc[0] = info.get(0).getCid();
                        cellLoc[1] = info.get(0).getLac();
                    }
                }
            } catch (Exception e) {
                LOGE(TAG, "[getNetworkType] Unable to get cell location", e);
            }
            return cellLoc;
        }

        private Object getObjectBySlot(String predictedMethodName, int slotId) {
            Object ob_phone = null;
            try {
                Class<?> telephonyClass = Class.forName(telephonyClassName);
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
                Object[] obParameter = new Object[1];
                obParameter[0] = slotId;
                ob_phone = getSimID.invoke(telephony, obParameter);
            } catch (Exception e) {
                LOGE(TAG, "[getNetworkType] Unable to get slot info", e);
            }
            return ob_phone;
        }

        private boolean invokeMethodBoolean(String className, int slotNumber, String methodName, String simVariant) {
            boolean val = false;
            try {
                Class<?> telephonyClass = Class.forName(telephonyClassName);
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Object ob_phone = null;
                try {
                    Method getSimID = telephonyClass.getMethod(methodName
                            + simVariant, parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = slotNumber;
                    ob_phone = getSimID.invoke(obj, obParameter);
                } catch (Exception e) {
                    if (slotNumber == 0) {
                        Method getSimID = telephonyClass.getMethod(methodName
                                + simVariant, parameter);
                        Object[] obParameter = new Object[1];
                        obParameter[0] = slotNumber;
                        ob_phone = getSimID.invoke(obj);
                    }
                }

                if (ob_phone != null) {
                    val = (boolean) Boolean.parseBoolean(ob_phone.toString());
                }
            } catch (Exception e) {
                invokeOldMethod(className, slotNumber, methodName, simVariant);
            }

            return val;
        }

        public boolean isTelephonyClassExists(String className) {

            boolean isClassExists = false;
            try {
                Class<?> telephonyClass = Class.forName(className);
                isClassExists = true;
            } catch (ClassNotFoundException e) {
                LOGE(TAG, "[isTelephonyClassExists] Unable to get telephony class", e);
            } catch (Exception e) {
                LOGE(TAG, "[isTelephonyClassExists] Unable to get telephony class", e);
            }
            return isClassExists;
        }

        /**
         * Here we are identify sim slot number
         */
        public void getValidSlotFields(String className) {

            String value = null;
            try {
                Class<?> telephonyClass = Class.forName(className);
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                StringBuffer sbf = new StringBuffer();
                Field[] fieldList = telephonyClass.getDeclaredFields();
                for (int index = 0; index < fieldList.length; index++) {
                    sbf.append("\n\n" + fieldList[index].getName());
                    Class<?> type = fieldList[index].getType();
                    Class<?> type1 = int.class;
                    if (type.equals(type1)) {
                        String variableName = fieldList[index].getName();
                        if (variableName.contains("SLOT")
                                || variableName.contains("slot")) {
                            if (variableName.contains("1")) {
                                slotName1 = variableName;
                            } else if (variableName.contains("2")) {
                                slotName2 = variableName;
                            } else if (variableName.contains("" + slotNumber1)) {
                                slotName1 = variableName;
                            } else if (variableName.contains("" + slotNumber2)) {
                                slotName2 = variableName;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGE(TAG, "[getValidSlotFields] Unable to get slot info", e);
            }
        }

        /**
         * Some device assign different slot number so here code execute
         * to get slot number
         */
        public void getSlotNumber(String className) {
            try {
                Class<?> c = Class.forName(className);
                Field fields1 = c.getField(slotName1);
                fields1.setAccessible(true);
                slotNumber1 = (Integer) fields1.get(null);
                Field fields2 = c.getField(slotName2);
                fields2.setAccessible(true);
                slotNumber2 = (Integer) fields2.get(null);
            } catch (Exception e) {
                slotNumber1 = 0;
                slotNumber2 = 1;
            }
        }

        private String invokeMethod(String className, int slotNumber, String methodName, String simVariant) {
            String value = "";

            try {
                Class<?> telephonyClass = Class.forName(className);
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Object ob_phone = null;
                try {
                    Method getSimID = telephonyClass.getMethod(methodName + simVariant, parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = slotNumber;
                    ob_phone = getSimID.invoke(obj, obParameter);
                } catch (Exception e) {
                    if (slotNumber == 0) {
                        Method getSimID = telephonyClass.getMethod(methodName + simVariant, parameter);
                        Object[] obParameter = new Object[1];
                        obParameter[0] = slotNumber;
                        ob_phone = getSimID.invoke(obj);
                    }
                }

                if (ob_phone != null) {
                    value = ob_phone.toString();
                }
            } catch (Exception e) {
                invokeOldMethod(className, slotNumber, methodName, simVariant);
            }

            return value;
        }

        private String invokeLongMethod(String className, long slotNumber, String methodName, String simVariant) {
            String value = "";

            try {
                Class<?> telephonyClass = Class.forName(className);
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = long.class;
                Object ob_phone = null;
                try {
                    Method getSimID = telephonyClass.getMethod(methodName + simVariant, parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = slotNumber;
                    ob_phone = getSimID.invoke(obj, obParameter);
                } catch (Exception e) {
                    if (slotNumber == 0) {
                        Method getSimID = telephonyClass.getMethod(methodName + simVariant, parameter);
                        Object[] obParameter = new Object[1];
                        obParameter[0] = slotNumber;
                        ob_phone = getSimID.invoke(obj);
                    }
                }

                if (ob_phone != null) {
                    value = ob_phone.toString();
                }
            } catch (Exception e) {

            }

            return value;
        }

        public String invokeOldMethod(String className, int slotNumber, String methodName, String simVariant) {
            String val = "";
            try {
                Class<?> telephonyClass = Class.forName("android.telephony.TelephonyManager");
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Object ob_phone = null;
                try {
                    Method getSimID = telephonyClass.getMethod(methodName + simVariant, parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = slotNumber;
                    ob_phone = getSimID.invoke(obj, obParameter);
                } catch (Exception e) {
                    if (slotNumber == 0) {
                        Method getSimID = telephonyClass.getMethod(methodName + simVariant, parameter);
                        Object[] obParameter = new Object[1];
                        obParameter[0] = slotNumber;
                        ob_phone = getSimID.invoke(obj);
                    }
                }

                if (ob_phone != null) {
                    val = ob_phone.toString();
                }
            } catch (Exception e) {
                LOGE(TAG, "[getDeviceIdBySlot] Unable to get device info");
            }
            return val;
        }

        public String getDeviceIdBySlot(String predictedMethodName, int slotId) {

            String imei = null;
            try {
                Class<?> telephonyClass = Class.forName(telephonyClassName);
                Constructor[] cons = telephonyClass.getDeclaredConstructors();
                cons[0].getName();
                cons[0].setAccessible(true);
                Object obj = cons[0].newInstance();
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Method getSimID = telephonyClass.getMethod(predictedMethodName,
                        parameter);
                Object[] obParameter = new Object[1];
                obParameter[0] = slotId;
                Object ob_phone = getSimID.invoke(obj, obParameter);

                if (ob_phone != null) {
                    imei = ob_phone.toString();
                }
            } catch (Exception e) {
                LOGE(TAG, "[getDeviceIdBySlot] Unable to get device info", e);
            }

            return imei;
        }

        public String getDeviceIdBySlotOld(String predictedMethodName, int slotId) {

            String value = null;
            try {
                Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;
                Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

                Object[] obParameter = new Object[1];
                obParameter[0] = slotId;
                Object ob_phone = getSimID.invoke(telephony, obParameter);

                if (ob_phone != null) {
                    value = ob_phone.toString();
                }
            } catch (Exception e) {
                LOGE(TAG, "[getDeviceIdBySlotOld] Unable to get device info", e);
            }
            return value;
        }
    }
}
