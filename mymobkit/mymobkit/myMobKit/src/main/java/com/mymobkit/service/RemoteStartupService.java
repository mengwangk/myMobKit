package com.mymobkit.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.ServiceUtils;
import com.mymobkit.common.StringUtils;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import java.lang.ref.WeakReference;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class RemoteStartupService extends Service {

	private static final String TAG = makeLogTag(RemoteStartupService.class);

	private static final String START_COMMAND = "start";

	private static final String STOP_COMMAND = "stop";

	private static PowerManager.WakeLock startingService;

	private static final Object startingServiceSync = new Object();

	private ServiceHandler serviceHandler;

	private Looper serviceLooper;

	private SmsMessage sms;

	private Context context;

	@Override
	public void onCreate() {
		HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		context = getApplicationContext();
		serviceLooper = thread.getLooper();
		serviceHandler = new ServiceHandler(this, serviceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LOGD(TAG, "Remote start up service started");

		Message msg = serviceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.obj = intent;
		serviceHandler.sendMessage(msg);

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;

	}

	@Override
	public void onDestroy() {
		serviceLooper.quit();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private static class ServiceHandler extends Handler {

		private final WeakReference<RemoteStartupService> remoteStartupServiceRef;

		public ServiceHandler(RemoteStartupService remoteStartupService, Looper looper) {
			super(looper);
			this.remoteStartupServiceRef = new WeakReference<RemoteStartupService>(remoteStartupService);
		}

		@Override
		public void handleMessage(Message msg) {
			RemoteStartupService remoteStartupService = remoteStartupServiceRef.get();
			if (remoteStartupService != null) {
				int serviceId = msg.arg1;
				Intent intent = (Intent) msg.obj;
				if (intent != null) {
					// String action = intent.getAction();
					remoteStartupService.handleSmsReceived(intent);
				}
				finishStartingService(remoteStartupService, serviceId);
			}
		}
	}

	/**
	 * Handle receiving SMS message
	 */
	protected void handleSmsReceived(Intent intent) {
		String body;
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			SmsMessage[] messages = getMessagesFromIntent(intent);
			sms = messages[0];
			if (messages != null) {
				if (messages.length == 1 || sms.isReplace()) {
					body = sms.getDisplayMessageBody();

				} else {
					StringBuilder bodyText = new StringBuilder();
					for (int i = 0; i < messages.length; i++) {
						bodyText.append(messages[i].getMessageBody());
					}
					body = bodyText.toString();
				}

				if (!TextUtils.isEmpty(body)) {
					LOGD(TAG, "Received msg: " + body);

					// Get the password from the msg
					String password = StringUtils.EMPTY;
					String command = StringUtils.EMPTY;
					String[] values = body.split(" ");
					if (values.length > 2) {
						password = values[1].trim();
						command = values[2].toLowerCase().trim();

						boolean allowedRemoteStartup = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_REMOTE_STARTUP, Boolean.valueOf(this.getString(R.string.default_remote_startup)));
						String systemPassword = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_REMOTE_STARTUP_PASSWORD, this.getString(R.string.default_remote_startup_password));

						if (allowedRemoteStartup && password.equals(systemPassword)) {
							if (START_COMMAND.equals(command)) {
								// Start the service
								startControlPanelService();

								// Start video surveillance mode
								startSurveillance();

							} else if (STOP_COMMAND.equals(command)) {
								stopControlPanelService();
								shutdownSurveillance();
							}
						}
					}

				}
			}
		}
	}

	private void startControlPanelService() {
		try {

			boolean isRunning = ServiceUtils.isServiceRunning(context, HttpdService.class);
			if (isRunning) return;
			ServiceUtils.startHttpdService(context);
		} catch (Exception ex) {
			LOGE(TAG, "[startControlPanelService] Error starting control panel service", ex);
		}
	}

	private void stopControlPanelService() {
		boolean isRunning = ServiceUtils.isServiceRunning(context, HttpdService.class);
		if (!isRunning) return;

		// Stop the service
		//boolean isStopped = context.stopService(new Intent(context, HttpdService.class));
		boolean isStopped = ServiceUtils.stopHttpdService(context);
		if (!isStopped) {
			LOGW(TAG, "[stopService] Unable to stop service");
		}
	}

	private void startSurveillance() {
		boolean mode = AppController.isSurveillanceMode();
		if (mode) {
			LOGI(TAG, "[startSurveillance] Already in surveillance mode");
			return;
		}

		WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
		try {
			screenLock.acquire();
			LOGI(TAG, "[startSurveillance] Starting surveillance mode");
			ServiceUtils.startWebcam(getBaseContext());
			/*
			Intent dialogIntent = new Intent(getBaseContext(), WebcamActivity.class);
			dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			getApplication().startActivity(dialogIntent);
			*/
		} catch (Exception e) {
			LOGE(TAG, "[startSurveillance] Failed to start surveillance mode", e);
		} finally {
			try {
				screenLock.release();
			} catch (Throwable th) {
				// ignoring this exception, probably wakeLock was already released
			}
		}
	}

	private void shutdownSurveillance() {
		boolean isShutdown = AppController.isSurveillanceShutdown();
		if (isShutdown) {
			LOGI(TAG, "[shutdownSurveillance] Surveillance camera is already shutdown.");
			return;
		}

		try {
			LOGI(TAG, "[shutdownSurveillance] Shutting down surveillance camera");
			Intent intent = new Intent(AppConfig.INTENT_SHUTDOWN_SURVEILLANCE_ACTION);
			sendBroadcast(intent);

			int counter = 0;
			while (true) {
				Thread.sleep(1000);
				isShutdown = AppController.isSurveillanceShutdown();
				if (isShutdown || counter == 5)
					break;
				else
					counter++;
			}
		} catch (Exception e) {
			LOGE(TAG, "[shutdownSurveillance] Failed to shutdown surveillance camera", e);
		}
	}

	/**
	 * Get the SMS message.
	 * 
	 * @param intent
	 *            - The SMS message intent.
	 * @return SmsMessage
	 */
	public static final SmsMessage[] getMessagesFromIntent(Intent intent) {

		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

		if (messages == null) {
			return null;
		}

		if (messages.length == 0) {
			return null;
		}

		byte[][] pduObjs = new byte[messages.length][];

		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}

		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;

		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		return msgs;
	}

	/**
	 * Start the service to process the current event notifications.
	 * 
	 * @param context
	 *            - The context of the calling activity.
	 * @param intent
	 *            - The calling intent.
	 * @return void
	 */
	public static void beginStartingService(Context context, Intent intent) {
		LOGI(TAG, "Starting mymobkit service");
		synchronized (startingServiceSync) {

			if (startingService == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				startingService = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
				startingService.setReferenceCounted(false);
			}
			startingService.acquire();
			context.startService(intent);
		}
	}

	/*
	 * Called back by the service when it has finished processing notifications, releasing the wake lock and wifi lock if the service is now stopping.
	 * 
	 * @param service - The calling service.
	 * 
	 * @param startId - The service start id.
	 * 
	 * @return void
	 */
	public static void finishStartingService(Service service, int startId) {
		synchronized (startingServiceSync) {
			if (startingService != null) {
				if (service.stopSelfResult(startId)) {
					try {
						startingService.release();
					} catch (Throwable th) {
						// ignoring this exception, probably wakeLock was already released
					}
				}
			}
		}
	}
}
