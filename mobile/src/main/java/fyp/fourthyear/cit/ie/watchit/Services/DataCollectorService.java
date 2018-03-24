package fyp.fourthyear.cit.ie.watchit.Services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.DataClient;

import java.util.ArrayList;
import java.util.Calendar;

import fyp.fourthyear.cit.ie.watchit.DAO.DAO;
import fyp.fourthyear.cit.ie.watchit.Domain.HeartRate;
import fyp.fourthyear.cit.ie.watchit.Domain.StressCalculator;

public class DataCollectorService extends Service implements  DataClient.OnDataChangedListener,SensorEventListener {
    private final String TAG = "Data Collector Service";
    private final int motionCap = 2;

    private StressCalculator stressCalculator;
    private ArrayList<Float> lastHourData;
    private int hourStart;
    private DAO dao= DAO.getInstance();
    private long lastUpdate;
    private long millsecondsStart;
    private double gravityValues[];
    private boolean exercise;
    private long exerciseTime;
    public DataCollectorService(){}
    @Override
    public void onCreate(){
        Wearable.getDataClient(this).addListener(this);
        stressCalculator = new StressCalculator();

        SensorManager sensorManager;
        Sensor accelerometer;
        Sensor gravity;
        sensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravityValues = new double[3];
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        boolean isRegistered = sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        logger(TAG,"gravity listener on: " + isRegistered);

        isRegistered = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        logger(TAG,"accelerometer listener on: " + isRegistered);

        lastUpdate= 0;
        lastHourData = new ArrayList<>();
        exercise=false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        for(DataEvent event : dataEventBuffer)
        {
            if(event.getType() == DataEvent.TYPE_CHANGED)
            {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                String path = event.getDataItem().getUri().getPath();
                if(path.equals("/SensorData"))
                {
                    long timeStamp = dataMap.getLong("time");
                    float data = dataMap.getFloat("data");
                    String type = dataMap.getString("sensorType");
                    logger(TAG, "Event received\nTime:"+timeStamp+"\nData:"+data+"\nType:"+type);
                    if(type.contains("heart") || type.contains("rate"))
                         updateData(timeStamp,data);

                }
            }
        }
    }
    private void uploadData(){

        boolean stressed = stressCalculator.determineStress(lastHourData,millsecondsStart);
        float hourHRData = 0;
        for (float hrData : lastHourData)
            hourHRData += hrData;
        hourHRData = hourHRData / lastHourData.size();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millsecondsStart);
        dao.uploadData(new HeartRate(millsecondsStart, (int) hourHRData,stressed,lastHourData.size()),c.getTime().toString());
        dao.setHrBaseline();
        lastHourData = new ArrayList<>();
        dao.clearData();
    }
    private void updateData(long time, float data){
        if(!exercise) {
            logger(TAG,"No exercise");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            int hourValue = c.get(Calendar.HOUR_OF_DAY);
             if (lastHourData.isEmpty()) {
                lastHourData.add(data);
                hourStart = hourValue;
                millsecondsStart = time;
            }
            else {
                if (hourValue != hourStart)
                    uploadData();
                else
                    lastHourData.add(data);

            }
        }
        else{

            logger(TAG,"Exercise");
            if(exerciseTime+1200000<Calendar.getInstance().getTimeInMillis())
                exercise=false;
        }
    }



    private void logger(String tag, String message)
    {
        Log.d(tag,message);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        if(event.sensor.getName().contains("Gravity"))
        {
            //Log.d(TAG,event.sensor.getName() + "\nX:" + event.values[0] + "\nY:" + event.values[1] + "\nZ:" + event.values[2]);
            gravityValues[0]=event.values[0];
            gravityValues[1]=event.values[1];
            gravityValues[2]=event.values[2];
            return;
        }
        if(currentTime >lastUpdate+60000 && gravityValues[2]!=0) {
            //logger(TAG, event.sensor.getName() + "\nX:" + event.values[0] + "\nY:" + event.values[1] + "\nZ:" + event.values[2]);
            lastUpdate=currentTime;


            final double alpha = 0.8;

            gravityValues[0] = alpha * gravityValues[0] + (1 - alpha) * event.values[0];
            gravityValues[1] = alpha * gravityValues[1] + (1 - alpha) * event.values[1];
            gravityValues[2] = alpha * gravityValues[2] + (1 - alpha) * event.values[2];

            ArrayList<Double> data = new ArrayList<>();


            data.add(event.values[0] - gravityValues[0]);
            data.add(event.values[1] - gravityValues[1]);
            data.add(event.values[2] - gravityValues[2]);
            for(Double value : data)
            {
                if(value<-motionCap || value>motionCap)
                {
                    exercise = true;
                    exerciseTime = Calendar.getInstance().getTimeInMillis();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        logger(TAG,sensor.getStringType() + accuracy);
    }
}
