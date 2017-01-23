package jp.ac.it_college.std.s15012.jinrouapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import static android.os.Looper.getMainLooper;

/**
 * Created by s15012 on 17/01/23.
 */

public class WifiDirectActivity {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
    }
}
