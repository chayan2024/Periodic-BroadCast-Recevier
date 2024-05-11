package com.example.periodic_broadcast_recevier;
import android.app.Dialog;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static final long INACTIVITY_DELAY = 3000; // 3 seconds for testing purposes
    private Dialog mDialog;
    private long mLastInteractionTime = 0;

    // SharedPreferences keys
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_LAST_INTERACTION_TIME = "last_interaction_time";

    // Broadcast receiver to receive custom action
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long currentTime = SystemClock.elapsedRealtime();
            long inactiveDuration = currentTime - mLastInteractionTime;
            if (inactiveDuration >= INACTIVITY_DELAY) {
                // User is inactive, show custom dialog
                showCustomDialog(BaseActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
        mLastInteractionTime = getLastInteractionTime();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        resetTimer();
        return super.onTouchEvent(event);
    }

    private void resetTimer() {
        mLastInteractionTime = SystemClock.elapsedRealtime();
        saveLastInteractionTime(mLastInteractionTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }

    // Register BroadcastReceiver to listen for custom action
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter("com.example.ACTION_CHECK_INACTIVITY");
        registerReceiver(mBroadcastReceiver, filter);
    }

    // Unregister BroadcastReceiver
    private void unregisterBroadcastReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

    protected void showCustomDialog(Activity activity) {
        mDialog = new Dialog(activity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_dialog_layout, null);
        mDialog.setContentView(dialogView);

        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.setCancelable(false);
        mDialog.show();
    }

    // Save last interaction time to SharedPreferences
    private void saveLastInteractionTime(long time) {
        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(KEY_LAST_INTERACTION_TIME, time);
        editor.apply();
    }

    // Retrieve last interaction time from SharedPreferences
    private long getLastInteractionTime() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_LAST_INTERACTION_TIME, 0);
    }
}


