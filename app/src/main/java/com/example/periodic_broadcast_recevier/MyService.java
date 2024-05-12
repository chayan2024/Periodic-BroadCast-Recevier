package com.example.periodic_broadcast_recevier;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MyService extends IntentService {

    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Simulate some task or event triggering
        Log.d("MyService", "Service is running");

        // Broadcast the action to trigger the dialog
        Intent broadcastIntent = new Intent("com.example.ACTION_CHECK_INACTIVITY");
        sendBroadcast(broadcastIntent);
    }
}
