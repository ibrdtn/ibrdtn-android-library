package de.tubs.ibr.dtn.example;

import android.content.Intent;
import android.util.Log;
import de.tubs.ibr.dtn.api.BundleID;
import de.tubs.ibr.dtn.api.DTNClient;
import de.tubs.ibr.dtn.api.DTNClient.Session;
import de.tubs.ibr.dtn.api.DTNIntentService;
import de.tubs.ibr.dtn.api.DataHandler;
import de.tubs.ibr.dtn.api.Registration;
import de.tubs.ibr.dtn.api.ServiceNotAvailableException;
import de.tubs.ibr.dtn.api.SessionDestroyedException;
import de.tubs.ibr.dtn.api.SimpleDataHandler;
import de.tubs.ibr.dtn.api.SingletonEndpoint;

/**
 * IntentService to send and receive Bundles via IBR-DTN
 */

public class MyDtnIntentService extends DTNIntentService {
	
	private static final String TAG = "MyDtnIntentService";
	private DTNClient.Session mSession = null;
	
	public static final String ACTION_SEND_MESSAGE = "de.tubs.ibr.dtn.example.SEND_MESSAGE";
	public static final String ACTION_RECV_MESSAGE = "de.tubs.ibr.dtn.example.RECV_MESSAGE";
	
	private static final String ACTION_MARK_DELIVERED = "de.tubs.ibr.dtn.example.DELIVERED";
	private static final String EXTRA_BUNDLEID = "de.tubs.ibr.dtn.example.BUNDLEID";
	
	public static final String EXTRA_SOURCE = "de.tubs.ibr.dtn.example.SOURCE";
	public static final String EXTRA_DESTINATION = "de.tubs.ibr.dtn.example.DESTINATION";
	public static final String EXTRA_PAYLOAD = "de.tubs.ibr.dtn.example.PAYLOAD";
	
	public MyDtnIntentService() {
		super(TAG);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		//register this Service at IBR-DTN
		Registration reg = new Registration("minimal-example");
		try {
			initialize(reg);
		} catch (ServiceNotAvailableException e) {
			Log.e(TAG, "Service not available", e);
		}
	}

	/**
	 * Filter the intent-action and do corresponding work
	 * @param intent received intent
     */
	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();

		//if a new bundle was received by IBR-DTN
		if (de.tubs.ibr.dtn.Intent.RECEIVE.equals(action)) {
			try {
				//query all available bundles so it can be processed by the DataHandler
				while (mSession.queryNext());
			} catch (SessionDestroyedException e) {
				Log.e(TAG, "session destroyed", e);
			}
		}
		//if a message has to be send
		else if (ACTION_SEND_MESSAGE.equals(action)) {
			try {
				//create destination endpoint
				SingletonEndpoint destination = new SingletonEndpoint(intent.getStringExtra(EXTRA_DESTINATION));
				//send the given payload to destination, set bundle lifetime to 1 hour (3600 seconds)
				mSession.send(destination, 3600, intent.getByteArrayExtra(EXTRA_PAYLOAD));
			} catch (SessionDestroyedException e) {
				Log.e(TAG, "session destroyed", e);
			}
		}
		//if a received bundle should be marked as delivered
		else if (ACTION_MARK_DELIVERED.equals(action)) {
			try {
				//get id of bundle to mark
				BundleID id = intent.getParcelableExtra(EXTRA_BUNDLEID);
				if (id != null) mSession.delivered(id);
			} catch (SessionDestroyedException e) {
				Log.e(TAG, "session destroyed", e);
			}
		}
	}

	/**
	 * called if service is connected to IBR-DTN
	 * saves the session and sets DataHandler processing bundles
	 *
	 * @param session current Session
     */
	@Override
	protected void onSessionConnected(Session session) {
		mSession = session;
		mSession.setDataHandler(mDataHandler);
	}

	@Override
	protected void onSessionDisconnected() {
		mSession = null;
	}
	
	/**
	 * Notice: The SimpleDataHandler only supports messages with
	 * a maximum size of 4096 bytes. Bundles of other sizes are
	 * not going to be delivered.
	 */
	private DataHandler mDataHandler = new SimpleDataHandler() {
		@Override
		protected void onMessage(BundleID id, byte[] data) {
			Log.d(TAG, "Message received from " + id.getSource());
			
			// forward message to an activity
			Intent mi = new Intent(ACTION_RECV_MESSAGE);
			mi.putExtra(EXTRA_SOURCE, id.getSource().toString());
			mi.putExtra(EXTRA_PAYLOAD, data);
			sendBroadcast(mi);
			
			// mark the bundle as delivered
			Intent i = new Intent(MyDtnIntentService.this, MyDtnIntentService.class);
			i.setAction(ACTION_MARK_DELIVERED);
			i.putExtra(EXTRA_BUNDLEID, id);
			startService(i);
		}
	};
}
