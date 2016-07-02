package de.tubs.ibr.dtn.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * BroadcastReceiver for Intents from IBR-DTN
 */

public class DtnBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(de.tubs.ibr.dtn.Intent.RECEIVE))
		{
			Intent i = new Intent(context, MyDtnIntentService.class);
			i.setAction(de.tubs.ibr.dtn.Intent.RECEIVE);
			context.startService(i);
		}
	}
}
