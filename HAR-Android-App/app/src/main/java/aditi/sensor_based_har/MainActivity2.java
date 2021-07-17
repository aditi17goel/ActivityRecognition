package aditi.sensor_based_har;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity2 extends AppCompatActivity {
    public double weight = 50;
    Button submitButton;
    TextView calset;
    double cal=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle b = getIntent().getExtras();
        int id1 = b.getInt("id");
        int secs= (id1*2)/5;
        int min = secs/60;
        cal = secs*3.5/12000;
        secs = secs%60;
        int hr = min/60;
        min = min%60;
        double met = b.getDouble("met");
        //Duration of physical activity in minutes × (MET × 3.5 × your weight in kg) / 200

        TextView duration = (TextView)findViewById(R.id.textView7);
        TextView num2 = (TextView)findViewById(R.id.textView);
        calset = (TextView)findViewById(R.id.textView5);
        final EditText w_in= (EditText)findViewById(R.id.weight);
        submitButton = (Button) findViewById(R.id.button);
        calset.setText(String.valueOf(truncateTo(cal*50,4)));

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = Integer.valueOf(w_in.getText().toString());
                calset.setText(String.valueOf(truncateTo(cal*weight,4)));
            }
        });

        //cal = cal*weight;
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_biking);
        progressBar.setProgress(id1);
        duration.setText(String.valueOf(hr)+":"+String.valueOf(min)+":"+String.valueOf(secs));
        num2.setText(String.valueOf(id1));

    }

    static double truncateTo( double unroundedNumber, int decimalPlaces ){
        int truncatedNumberInt = (int)( unroundedNumber * Math.pow( 10, decimalPlaces ) );
        double truncatedNumber = (double)( truncatedNumberInt / Math.pow( 10, decimalPlaces ) );
        return truncatedNumber;
    }

}