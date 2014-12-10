package com.btrax.mywatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.ImageView;

import java.util.Calendar;

/**
 * Activity for WatchFace. please set AndroidManifest.xml
 * 盤面用のActivity。AndroidManifest.xmlに設定すること。
 */
public class WatchFaceActivity extends Activity {

    private static IntentFilter mTimeIntentFilter;
    private ImageView mMinuteHand;
    private ImageView mHourHand;
    private ImageView mBackground;

    /**
     * called when this app is selected as a watch face.
     * 盤面に設定されたときに呼ばれる
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_face);

        mTimeIntentFilter = new IntentFilter();
        mTimeIntentFilter.addAction(Intent.ACTION_TIME_TICK);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // retrieve view
                // ビューの取得
                mMinuteHand = (ImageView) stub.findViewById(R.id.minute_hand);
                mHourHand = (ImageView) stub.findViewById(R.id.hour_hand);
                mBackground = (ImageView) stub.findViewById(R.id.background);

                // initiate watch face
                // 盤面初期化
                onTimeUpdated(Calendar.getInstance());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mTimeInfoReceiver, mTimeIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mTimeInfoReceiver);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onTimeUpdated(Calendar.getInstance()); // 再度盤面が表示されるときに時間を更新
    }

    private BroadcastReceiver mTimeInfoReceiver = new BroadcastReceiver() {

        /**
         * called every minute.
         * このメソッドが1分ごとに呼ばれる
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            onTimeUpdated(Calendar.getInstance());
        }
    };

    /**
     * call this method when time is changed.
     * 時間が更新されたときに実行する
     */
    private void onTimeUpdated(Calendar cal) {
        updateHands(cal);
        updateBackground(cal);
    }

    /**
     * update hands.
     * 長針、短針を更新する
     */
    private void updateHands(Calendar cal) {
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        mMinuteHand.setRotation(min * 360.0f / 60);
        mHourHand.setRotation((hour * 60 + min) * (360.0f / (12 * 60)));
    }

    /**
     * update background of watch face.
     * 盤面の背景を更新する
     */
    private void updateBackground(Calendar cal) {

        int[] imgResIds = {
                R.drawable.image_0,
                R.drawable.image_1,
                R.drawable.image_2,
        };

        int min = cal.get(Calendar.MINUTE);
        mBackground.setImageResource(imgResIds[min % imgResIds.length]);
    }
}
