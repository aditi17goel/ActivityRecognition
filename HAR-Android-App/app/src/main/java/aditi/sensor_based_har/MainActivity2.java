package aditi.sensor_based_har;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle b = getIntent().getExtras();
        int id = b.getInt("id");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_biking);
        progressBar.setProgress(id);
    }


}