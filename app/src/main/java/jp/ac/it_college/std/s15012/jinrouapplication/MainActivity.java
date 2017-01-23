package jp.ac.it_college.std.s15012.jinrouapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Handler mHandler = new Handler();
    private ScheduledExecutorService mScheduledExecutor;

    private Button startButton;    //ゲーム開始ボタン
    private Button playHostButton; //ホストボタン
    private Button playGuestButton;//ゲストボタン

    private boolean sbBool; //アニメーション用

    //WifiDirectAcitivity
//    private final String TAG = "WiFiDirectTestAppActivity";

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

//    private boolean isWifiP2pEnabled = false;
    //〆

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.start_button);
        playHostButton  = (Button) findViewById(R.id.play_host_button);
        playGuestButton = (Button) findViewById(R.id.play_guest_button);
        sbBool = true; //アニメーション起動

        startButton.setOnClickListener(this);
        startMeasure(); //startボタンのアニメーション
        playHostButton.setOnClickListener(this);
        playGuestButton.setOnClickListener(this);


        playHostButton.setVisibility(View.GONE); //村ホスト・ゲストボタンの無効化
        playGuestButton.setVisibility(View.GONE);

        //WifiDirect TODO
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

//    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
//        this.isWifiP2pEnabled = isWifiP2pEnabled;
//    }

    public void discoverPeers (WifiP2pManager.Channel channel) {
        mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "成功したよ", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainActivity.this, "faeuirgietaertbaeth", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void connect (WifiP2pManager.Channel channel,
                         WifiP2pConfig config) {
        WifiP2pDevice device = new WifiP2pDevice();
        config.deviceAddress = device.deviceAddress;

        mManager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic
                Toast.makeText(MainActivity.this, "接続ーーー", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
                Toast.makeText(MainActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void startMeasure() {  //startbuttonの点滅アニメーション
        /**
         * 第一引数: 繰り返し実行したい処理
         * 第二引数: 指定時間後に第一引数の処理を開始
         * 第三引数: 第一引数の処理完了後、指定時間後に再実行
         * 第四引数: 第二、第三引数の単位
         *
         * new Runnable（無名オブジェクト）をすぐに（0秒後に）実行し、完了後1700ミリ秒ごとに繰り返す。
         * （ただしアニメーションの完了からではない。Handler#postが即時実行だから？？）
         */

        mScheduledExecutor = Executors.newScheduledThreadPool(2);
        mScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startButton.setVisibility(View.VISIBLE);

                        // HONEYCOMBより前のAndroid SDKがProperty Animation非対応のため
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && sbBool) {
                            animateAlpha();
                        }
                    }
                });
            }


            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            private void animateAlpha() {

                // 実行するAnimatorのリスト
                List<Animator> animatorList = new ArrayList<Animator>();

                // alpha値を0から1へ1000ミリ秒かけて変化させる。
                ObjectAnimator animeFadeIn = ObjectAnimator.ofFloat(startButton, "alpha", 0f, 1f);
                animeFadeIn.setDuration(1000);

                // alpha値を1から0へ600ミリ秒かけて変化させる。
                ObjectAnimator animeFadeOut = ObjectAnimator.ofFloat(startButton, "alpha", 1f, 0f);
                animeFadeOut.setDuration(600);

                // 実行対象Animatorリストに追加。
                animatorList.add(animeFadeIn);
                animatorList.add(animeFadeOut);

                final AnimatorSet animatorSet = new AnimatorSet();

                // リストの順番に実行
                animatorSet.playSequentially(animatorList);

                animatorSet.start();
            }
        }, 0, 1700, TimeUnit.MILLISECONDS);

    }




    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.start_button: //開始ボタンクリック時、村ホスト・ゲストボタン有効化（開始ボタン無効化）
                    sbBool = false;
                    startButton.setVisibility(View.GONE);
                    startButton.setEnabled(false);

                    playHostButton.setVisibility(View.VISIBLE);
                    playHostButton.setEnabled(true);
                    playGuestButton.setVisibility(View.VISIBLE);
                    playGuestButton.setEnabled(true);

                    break;
                case R.id.play_host_button: //ホストボタン-- TODO
                    discoverPeers(this.mChannel);
//                    Toast.makeText(this, "ばーかばーか！", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.play_guest_button://ゲストボタン-- TODO
//                    Toast.makeText(this, "うんちうんち！", Toast.LENGTH_SHORT).show();

                    break;
            }
        }

    }


}
