package fyp.fourthyear.cit.ie.watchit;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends WearableActivity {
    private SensorDataService dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sensorButton = findViewById(R.id.SensorBtn);
        dataService = new SensorDataService(this);
        Log.d("TAG",""+isAmbient());

        if(dataService.areSensorsOn()){
            //turn off
            sensorButton.setText("Stop listeners");
        }

        else if (!dataService.areSensorsOn()){

            sensorButton.setText("Start listeners");
        }



        sensorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button sensorButton = (Button)v;
                if(dataService.areSensorsOn()){
                    //turn off
                    dataService.detachListeners();
                    sensorButton.setText("Start listeners");
                }

                else{
                    dataService.attachListeners();
                    sensorButton.setText("Stop listeners");
                }


            }
        });

    }




}
