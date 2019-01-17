package auto.kotlin.com.waveapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private WaveHelper mWaveHelper;
    public static final int CACHE_UNSTART = 1;
    public static final int CACHE_DOWNLOADING = 2;
    public static final int CACHE_PAUSE = 3;
    public static final int CACHE_COMPLETE = 4;
    public static final int CACHE_ERROR = 5;
    public static final int CACHE_SPACE_NOT_ENOUGH = 6;
    public static final int CACHE_WAITING = 7;
    public static final int CACHE_DISABLE = 8;

    private TextView tv_pause;
    private TextView tv_dowloading;
    private TextView tv_complete;
    private TextView tv_error;
    private TextView tv_continue;
    private TextView tv_init_data;
    private int mBorderColor = Color.parseColor("#44FFFFFF");
    private int mBorderWidth = 10;

    private Thread mMyThread;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //do something
                mWaveHelper.setLevelHeight(msg.arg1);
                Log.i("MainActivity","===="+msg.arg1);
                if (mMyThread != null && msg.arg1 == 100) {
                    mMyThread.interrupt();
                }
            }
            super.handleMessage(msg);

        }
    };

    private int index = 0;

    class MyThread extends Thread {//这里也可用Runnable接口实现
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);//每隔1s执行一次
                    Message msg = new Message();
                    msg.what = 1;
                    index++;
                    msg.arg1 = index;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void updateAnimationState(int cacheStatus) {
        switch (cacheStatus) {
            case CACHE_UNSTART:

                break;
            case CACHE_DOWNLOADING:
                mWaveHelper.resume();

                break;
            /**点击暂停 更新UI*/
            case CACHE_PAUSE:
                mWaveHelper.pause();
                if(mMyThread != null){
                    mMyThread.interrupt();
                }
                break;
            case CACHE_COMPLETE:

                break;
            case CACHE_ERROR:

                break;
            case CACHE_SPACE_NOT_ENOUGH:

                break;
            default:

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pause:
                updateAnimationState(CACHE_PAUSE);
                break;
            case R.id.tv_dowloading:
                updateAnimationState(CACHE_DOWNLOADING);
                break;
            case R.id.tv_complete:
                updateAnimationState(CACHE_COMPLETE);
                break;
            case R.id.tv_error:
                updateAnimationState(CACHE_ERROR);
                break;
            case R.id.tv_continue:
                if(mMyThread != null ){
                    mMyThread.start();
                }
                updateAnimationState(CACHE_DOWNLOADING);
                break;
            case R.id.tv_init_data:
                index = 0 ;
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        waveView.setShapeType(WaveView.ShapeType.SQUARE);
        waveView.setWaveColor(
                Color.parseColor("#28f16d7a"),
                Color.parseColor("#3cf16d7a"));
        waveView.setWaterLevelRatio(0);
        mWaveHelper = new WaveHelper(waveView);

        mBorderColor = Color.parseColor("#44f16d7a");

        mMyThread = new MyThread();
        mMyThread.start();
        index = 0;
        tv_pause = findViewById(R.id.tv_pause);
        tv_dowloading = findViewById(R.id.tv_dowloading);
        tv_complete = findViewById(R.id.tv_complete);
        tv_error = findViewById(R.id.tv_error);
        tv_continue = findViewById(R.id.tv_continue);
        tv_init_data = findViewById(R.id.tv_init_data);
        tv_pause.setOnClickListener(this);
        tv_dowloading.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
        tv_error.setOnClickListener(this);
        tv_continue.setOnClickListener(this);
        tv_init_data.setOnClickListener(this);

        /*((RadioGroup) findViewById(R.id.shapeChoice))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.shapeCircle:
                                waveView.setShapeType(WaveView.ShapeType.CIRCLE);
                                break;
                            case R.id.shapeSquare:
                                waveView.setShapeType(WaveView.ShapeType.SQUARE);
                                break;
                            default:
                                break;
                        }
                    }
                });

        ((SeekBar) findViewById(R.id.seekBar))
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        mBorderWidth = i;
//                        waveView.setmProgress(i*3);
                        float waterLevel = (float) i / 100;//i 增加数量，mBNumber 总数
                        waveView.setWaterLevelRatio(waterLevel);
                        waveView.setWaveShiftRatio(waterLevel);
                        waveView.setAmplitudeRatio(waterLevel);
                        Log.i("MainActivity",waterLevel+"");
//                        waveView.setBorder(mBorderWidth, mBorderColor);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

        CompoundButtonCompat.setButtonTintList(
                (RadioButton) findViewById(R.id.colorDefault),
                getResources().getColorStateList(android.R.color.white));
        CompoundButtonCompat.setButtonTintList(
                (RadioButton) findViewById(R.id.colorRed),
                getResources().getColorStateList(R.color.red));
        CompoundButtonCompat.setButtonTintList(
                (RadioButton) findViewById(R.id.colorGreen),
                getResources().getColorStateList(R.color.green));
        CompoundButtonCompat.setButtonTintList(
                (RadioButton) findViewById(R.id.colorBlue),
                getResources().getColorStateList(R.color.blue));

        ((RadioGroup) findViewById(R.id.colorChoice))
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.colorRed:
                                waveView.setWaveColor(
                                        Color.parseColor("#28f16d7a"),
                                        Color.parseColor("#3cf16d7a"));
                                mBorderColor = Color.parseColor("#44f16d7a");
                                waveView.setWaterLevelRatio(0);
//                                waveView.setBorder(mBorderWidth, mBorderColor);
                                break;
                            case R.id.colorGreen:
                                waveView.setWaveColor(
                                        Color.parseColor("#40b7d28d"),
                                        Color.parseColor("#80b7d28d"));
                                mBorderColor = Color.parseColor("#B0b7d28d");
                                waveView.setBorder(mBorderWidth, mBorderColor);
                                break;
                            case R.id.colorBlue:
                                waveView.setWaveColor(
                                        Color.parseColor("#88b8f1ed"),
                                        Color.parseColor("#b8f1ed"));
                                mBorderColor = Color.parseColor("#b8f1ed");
                                waveView.setBorder(mBorderWidth, mBorderColor);
                                break;
                            default:
                                waveView.setWaveColor(
                                        WaveView.DEFAULT_BEHIND_WAVE_COLOR,
                                        WaveView.DEFAULT_FRONT_WAVE_COLOR);
                                mBorderColor = Color.parseColor("#44FFFFFF");
                                waveView.setBorder(mBorderWidth, mBorderColor);
                                break;
                        }
                    }
                });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaveHelper.start();
    }


}
