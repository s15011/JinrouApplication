package jp.ac.it_college.std.s15012.jinrouapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler mHandler = new Handler();
    private ScheduledExecutorService mScheduledExecutor;

    private Button startButton;    //ゲーム開始ボタン
    private Button playHostButton; //ホストボタン
    private Button playGuestButton;//ゲストボタン

    private boolean sbBool; //アニメーション用


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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && sbBool == true) {
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
                    Toast.makeText(this, "ばーかばーか！", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.play_guest_button://ゲストボタン-- TODO
                    Toast.makeText(this, "うんちうんち！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
}
