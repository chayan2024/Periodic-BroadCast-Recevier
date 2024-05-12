package com.example.periodic_broadcast_recevier;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static final long INACTIVITY_DELAY = 50000; // 50 seconds
    private static final long CHECK_INTERVAL = 10000; // 1 second
    private Dialog mDialog;
    private long mLastInteractionTime = 0;
    private boolean mIsScreenActive = true;
    private BroadcastReceiver mBroadcastReceiver;
    private Handler mHandler = new Handler();
    private Runnable mCheckActivityRunnable = new Runnable() {
        @Override
        public void run() {
            checkScreenActivity();
            mHandler.postDelayed(this, CHECK_INTERVAL);
        }
    };

    // SharedPreferences keys
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_LAST_INTERACTION_TIME = "last_interaction_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
        mHandler.postDelayed(mCheckActivityRunnable, CHECK_INTERVAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetTimer();

        // Start the service here
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBroadcastReceiver();
        mHandler.removeCallbacks(mCheckActivityRunnable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        resetTimer();
        return super.onTouchEvent(event);
    }

    private void resetTimer() {
        mLastInteractionTime = System.currentTimeMillis();
        saveLastInteractionTime(mLastInteractionTime);
    }

    // Register BroadcastReceiver to listen for custom action
    private void registerBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long currentTime = System.currentTimeMillis();
                long inactiveDuration = currentTime - mLastInteractionTime;
                showCustomDialog(BaseActivity.this);
                Log.v("@@", "showCustomDialog");
            }
        };
        IntentFilter filter = new IntentFilter("com.example.ACTION_CHECK_INACTIVITY");
        registerReceiver(mBroadcastReceiver, filter);
    }

    // Unregister BroadcastReceiver
    private void unregisterBroadcastReceiver() {
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }

    // Check whether the screen is active or inactive
    private void checkScreenActivity() {
        long currentTime = System.currentTimeMillis();
        long inactiveDuration = currentTime - mLastInteractionTime;
        if (inactiveDuration >= INACTIVITY_DELAY && mIsScreenActive) {
            // Screen is inactive
            mIsScreenActive = false;
            Log.d("@@", "Screen is inactive");
        } else if (inactiveDuration < INACTIVITY_DELAY && !mIsScreenActive) {
            // Screen is active again
            mIsScreenActive = true;
            Log.d("@@", "Screen is active again");
        }
    }

    protected void showCustomDialog(Activity activity) {
        mDialog = new Dialog(activity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_dialog_layout);

        Button btnDismiss = mDialog.findViewById(R.id.btnDismiss);
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
        return prefs.getLong(KEY_LAST_INTERACTION_TIME, System.currentTimeMillis());
    }
}
