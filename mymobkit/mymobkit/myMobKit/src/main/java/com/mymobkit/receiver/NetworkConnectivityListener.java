package com.mymobkit.receiver;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

import java.util.HashMap;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

/**
 * A wrapper for a broadcast receiver that provides network connectivity state information,
 * independent of network type (mobile, Wi-Fi, etc.). {@hide}
 */
public class NetworkConnectivityListener {
	
	private static final String TAG = makeLogTag(NetworkConnectivityListener.class);

	private static final boolean DBG = false;

	private Context context;

	private HashMap<Handler, Integer> handlers = new HashMap<Handler, Integer>();

	private State state;

	private boolean listening;

	private String reason;

	private boolean isFailover;

	/** Network connectivity information */
	private NetworkInfo networkInfo;

	/**
	 * In case of a Disconnect, the connectivity manager may have already established, or may be
	 * attempting to establish, connectivity with another network. If so, {@code mOtherNetworkInfo}
	 * will be non-null.
	 */
	private NetworkInfo otherNetworkInfo;

	private ConnectivityBroadcastReceiver receiver;

	private class ConnectivityBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || listening == false) {
				LOGW(TAG, "onReceived() called with " + state.toString() + " and " + intent);
				return;
			}

			boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			if (noConnectivity) {
				state = State.NOT_CONNECTED;
			} else {
				state = State.CONNECTED;
			}

			networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

			reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
			isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

			if (DBG) {
				LOGD(TAG, "onReceive(): mNetworkInfo=" + networkInfo + " mOtherNetworkInfo = "
						+ (otherNetworkInfo == null ? "[none]" : otherNetworkInfo + " noConn=" + noConnectivity)
						+ " mState=" + state.toString());
			}

			// Notify any handlers.
			Iterator<Handler> it = handlers.keySet().iterator();
			while (it.hasNext()) {
				Handler target = it.next();
				Message message = Message.obtain(target, handlers.get(target));
				target.sendMessage(message);
			}
		}
	};

	public enum State {
		UNKNOWN,

		/** This state is returned if there is connectivity to any network **/
		CONNECTED,
		/**
		 * This state is returned if there is no connectivity to any network. This is set to true
		 * under two circumstances:
		 * <ul>
		 * <li>When connectivity is lost to one network, and there is no other available network to
		 * attempt to switch to.</li>
		 * <li>When connectivity is lost to one network, and the attempt to switch to another
		 * network fails.</li>
		 */
		NOT_CONNECTED
	}

	/**
	 * Create a new NetworkConnectivityListener.
	 */
	public NetworkConnectivityListener() {
		state = State.UNKNOWN;
		receiver = new ConnectivityBroadcastReceiver();
	}

	/**
	 * This method starts listening for network connectivity state changes.
	 * 
	 * @param context
	 */
	public synchronized void startListening(Context context) {
		if (!listening) {
			this.context = context;

			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			context.registerReceiver(receiver, filter);
			listening = true;
		}
	}

	/**
	 * This method stops this class from listening for network changes.
	 */
	public synchronized void stopListening() {
		if (listening) {
			context.unregisterReceiver(receiver);
			context = null;
			networkInfo = null;
			otherNetworkInfo = null;
			isFailover = false;
			reason = null;
			listening = false;
		}
	}

	/**
	 * This methods registers a Handler to be called back onto with the specified what code when the
	 * network connectivity state changes.
	 * 
	 * @param target The target handler.
	 * @param what The what code to be used when posting a message to the handler.
	 */
	public void registerHandler(Handler target, int what) {
		handlers.put(target, what);
	}

	/**
	 * This methods unregisters the specified Handler.
	 * 
	 * @param target
	 */
	public void unregisterHandler(Handler target) {
		handlers.remove(target);
	}

	public State getState() {
		return state;
	}

	/**
	 * Return the NetworkInfo associated with the most recent connectivity event.
	 * 
	 * @return {@code NetworkInfo} for the network that had the most recent connectivity event.
	 */
	public NetworkInfo getNetworkInfo() {
		return networkInfo;
	}

	/**
	 * If the most recent connectivity event was a DISCONNECT, return any information supplied in
	 * the broadcast about an alternate network that might be available. If this returns a non-null
	 * value, then another broadcast should follow shortly indicating whether connection to the
	 * other network succeeded.
	 * 
	 * @return NetworkInfo
	 */
	public NetworkInfo getOtherNetworkInfo() {
		return otherNetworkInfo;
	}

	/**
	 * Returns true if the most recent event was for an attempt to switch over to a new network
	 * following loss of connectivity on another network.
	 * 
	 * @return {@code true} if this was a failover attempt, {@code false} otherwise.
	 */
	public boolean isFailover() {
		return isFailover;
	}

	/**
	 * An optional reason for the connectivity state change may have been supplied. This returns it.
	 * 
	 * @return the reason for the state change, if available, or {@code null} otherwise.
	 */
	public String getReason() {
		return reason;
	}
}