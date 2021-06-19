package aditi.sensor_based_har;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import aditi.sensor_based_har.HARClassifier;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private static final int N_SAMPLES = 100;
    private static int prevIdx = -1;


    public static int prog_walk=0;
    public int prog_sit=0;
    public int prog_stand=0;
    public int prog_jog=0;
    public static int prog_bik=0;
    public int prog_upst=0;
    public int prog_down=0;

    private static List<Float> ax;
    private static List<Float> ay;
    private static List<Float> az;

    private static List<Float> lx;
    private static List<Float> ly;
    private static List<Float> lz;

    private static List<Float> gx;
    private static List<Float> gy;
    private static List<Float> gz;

    private static List<Float> ma;
    private static List<Float> ml;
    private static List<Float> mg;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLinearAcceleration;

    private TableRow bikingTableRow;
    private TableRow downstairsTableRow;
    private TableRow joggingTableRow;
    private TableRow sittingTableRow;
    private TableRow standingTableRow;
    private TableRow upstairsTableRow;
    private TableRow walkingTableRow;

    private TextToSpeech textToSpeech;
    private float[] results;
    private aditi.sensor_based_har.HARClassifier classifier;

    private String[] labels = {"Biking", "Downstairs", "Jogging", "Sitting", "Standing", "Upstairs", "Walking"};
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#2D3047"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
        button = (Button) findViewById(R.id.but_bike);
        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity2(prog_bik);
            }
        });
        button = (Button) findViewById(R.id.but_down);
        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity2(prog_down);
            }
        });

        button = (Button) findViewById(R.id.but_up);
        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity2(prog_upst);
            }
        });

        button = (Button) findViewById(R.id.but_stand);
        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity2(prog_stand);
            }
        });

        button = (Button) findViewById(R.id.but_sit);
        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity2(prog_sit);
            }
        });

        button = (Button) findViewById(R.id.but_jog);
        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity2(prog_jog);
            }
        });

        button = (Button) findViewById(R.id.but_walk);
        button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity2(prog_walk);
            }
        });
        SharedPreferences getShared = getSharedPreferences("demo", MODE_PRIVATE);
        Integer val1 = getShared.getInt("walk", 0);
        Integer val2 = getShared.getInt("sit", 0);
        Integer val3 = getShared.getInt("stand", 0);
        Integer val4 = getShared.getInt("bike", 0);
        Integer val5 = getShared.getInt("jog", 0);
        Integer val6 = getShared.getInt("upst", 0);
        Integer val7 = getShared.getInt("down", 0);
        prog_walk = val1;
        prog_sit = val2;
        prog_stand = val3;
        prog_bik = val4;
        prog_jog = val5;
        prog_upst = val6;
        prog_down = val7;
        ax = new ArrayList<>(); ay = new ArrayList<>(); az = new ArrayList<>();
        lx = new ArrayList<>(); ly = new ArrayList<>(); lz = new ArrayList<>();
        gx = new ArrayList<>(); gy = new ArrayList<>(); gz = new ArrayList<>();
        ma = new ArrayList<>(); ml = new ArrayList<>(); mg = new ArrayList<>();


        bikingTableRow = (TableRow) findViewById(R.id.biking_row);
        downstairsTableRow = (TableRow) findViewById(R.id.downstairs_row);
        joggingTableRow = (TableRow) findViewById(R.id.jogging_row);
        sittingTableRow = (TableRow) findViewById(R.id.sitting_row);
        standingTableRow = (TableRow) findViewById(R.id.standing_row);
        upstairsTableRow = (TableRow) findViewById(R.id.upstairs_row);
        walkingTableRow = (TableRow) findViewById(R.id.walking_row);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);

        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mLinearAcceleration , SensorManager.SENSOR_DELAY_FASTEST);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope , SensorManager.SENSOR_DELAY_FASTEST);

        classifier = new HARClassifier(getApplicationContext());

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
    }
    public void openMainActivity2(Integer prog) {

        Intent i = new Intent(MainActivity.this, MainActivity2.class);
        i.putExtra("id", prog);
        startActivity(i);
    }
    @Override
    public void onInit(int status) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (results == null || results.length == 0) {
                    return;
                }
                float max = -1;
                int idx = -1;
                for (int i = 0; i < results.length; i++) {
                    if (results[i] > max) {
                        idx = i;
                        max = results[i];
                    }
                }

                if(max > 0.50 && idx != prevIdx) {
                    textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null,
                            Integer.toString(new Random().nextInt()));
                    prevIdx = idx;
                }
            }
        }, 1000, 3000);
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        activityPrediction();

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);

        } else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            lx.add(event.values[0]);
            ly.add(event.values[1]);
            lz.add(event.values[2]);

        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void activityPrediction() {

        List<Float> data = new ArrayList<>();

        if (ax.size() >= N_SAMPLES && ay.size() >= N_SAMPLES && az.size() >= N_SAMPLES
                && lx.size() >= N_SAMPLES && ly.size() >= N_SAMPLES && lz.size() >= N_SAMPLES
                && gx.size() >= N_SAMPLES && gy.size() >= N_SAMPLES && gz.size() >= N_SAMPLES
        ) {
            double maValue; double mgValue; double mlValue;

            for( int i = 0; i < N_SAMPLES ; i++ ) {
                maValue = Math.sqrt(Math.pow(ax.get(i), 2) + Math.pow(ay.get(i), 2) + Math.pow(az.get(i), 2));
                mlValue = Math.sqrt(Math.pow(lx.get(i), 2) + Math.pow(ly.get(i), 2) + Math.pow(lz.get(i), 2));
                mgValue = Math.sqrt(Math.pow(gx.get(i), 2) + Math.pow(gy.get(i), 2) + Math.pow(gz.get(i), 2));

                ma.add((float)maValue);
                ml.add((float)mlValue);
                mg.add((float)mgValue);
            }

            data.addAll(ax.subList(0, N_SAMPLES));
            data.addAll(ay.subList(0, N_SAMPLES));
            data.addAll(az.subList(0, N_SAMPLES));

            data.addAll(lx.subList(0, N_SAMPLES));
            data.addAll(ly.subList(0, N_SAMPLES));
            data.addAll(lz.subList(0, N_SAMPLES));

            data.addAll(gx.subList(0, N_SAMPLES));
            data.addAll(gy.subList(0, N_SAMPLES));
            data.addAll(gz.subList(0, N_SAMPLES));

            data.addAll(ma.subList(0, N_SAMPLES));
            data.addAll(ml.subList(0, N_SAMPLES));
            data.addAll(mg.subList(0, N_SAMPLES));

            results = classifier.predictProbabilities(toFloatArray(data));

            float max = -1;
            int idx = -1;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > max) {
                    idx = i;
                    max = results[i];
                }
            }
            sendData(idx);
            setRowsColor(idx);
            ax.clear(); ay.clear(); az.clear();
            lx.clear(); ly.clear(); lz.clear();
            gx.clear(); gy.clear(); gz.clear();
            ma.clear(); ml.clear(); mg.clear();
        }
    }

    private void sendData(int idx) {
        String activity = "Biking";
        if(idx == 1)activity = "Downstairs";
        else if(idx == 2)activity = "Jogging";
        else if(idx == 3)activity = "Sitting";
        else if(idx == 4)activity = "Standing";
        else if(idx == 5)activity = "Upstairs";
        else if(idx == 6)activity = "Walking";
        Call<ResponseModel> call = RetrofitClient.getInstance().getAPI().sendData(activity);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                //This method will be called on successful server call

                ResponseModel obj = response.body();

                Toast.makeText(MainActivity.this, obj.getRemarks(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

                //This method will called in case of failure

                Toast.makeText(MainActivity.this, "Network Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setRowsColor(int idx) {
        bikingTableRow.setBackgroundColor(Color.parseColor("#CF9823"));
        downstairsTableRow.setBackgroundColor(Color.parseColor("#CF9823"));
        joggingTableRow.setBackgroundColor(Color.parseColor("#CF9823"));
        sittingTableRow.setBackgroundColor(Color.parseColor("#CF9823"));
        standingTableRow.setBackgroundColor(Color.parseColor("#CF9823"));
        upstairsTableRow.setBackgroundColor(Color.parseColor("#CF9823"));
        walkingTableRow.setBackgroundColor(Color.parseColor("#CF9823"));
        SharedPreferences shrd = getSharedPreferences("demo", MODE_PRIVATE);
        SharedPreferences.Editor editor = shrd.edit();

        if(idx == 0){
            bikingTableRow.setBackgroundColor(Color.parseColor("#586F6B"));
            prog_bik += 1;
            editor.putInt("bike", prog_bik);
            editor.apply();
        }
        else if (idx == 1){
            downstairsTableRow.setBackgroundColor(Color.parseColor("#586F6B"));
            prog_down += 1;
            editor.putInt("down", prog_down);
            editor.apply();
        }
        else if (idx == 2){
            joggingTableRow.setBackgroundColor(Color.parseColor("#586F6B"));
            prog_jog += 1;
            editor.putInt("jog", prog_jog);
            editor.apply();
        }
        else if (idx == 3){
            sittingTableRow.setBackgroundColor(Color.parseColor("#586F6B"));
            prog_sit += 1;
            editor.putInt("sit", prog_sit);
            editor.apply();
        }
        else if (idx == 4){
            standingTableRow.setBackgroundColor(Color.parseColor("#586F6B"));
            prog_stand += 1;
            editor.putInt("stand", prog_stand);
            editor.apply();
        }
        else if (idx == 5){
            upstairsTableRow.setBackgroundColor(Color.parseColor("#586F6B"));
            prog_upst += 1;
            editor.putInt("upst", prog_upst);
            editor.apply();
        }
        else if (idx == 6){
            walkingTableRow.setBackgroundColor(Color.parseColor("#586F6B"));
            prog_walk += 1;
            editor.putInt("walk", prog_walk);
            editor.apply();
        }
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }

}
