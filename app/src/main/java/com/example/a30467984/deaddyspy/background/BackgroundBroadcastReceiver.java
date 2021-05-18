package com.example.a30467984.deaddyspy.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BackgroundBroadcastReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, BackgroundIntentService.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}
