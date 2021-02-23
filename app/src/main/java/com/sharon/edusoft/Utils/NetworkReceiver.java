package com.sharon.edusoft.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        if(checkInternet(context))
        {
            Toast.makeText(context, "Network Available Do operations",Toast.LENGTH_LONG).show();
        }

    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        return serviceManager.isNetworkAvailable();
    }
}
