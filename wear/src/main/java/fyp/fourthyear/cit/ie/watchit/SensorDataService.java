package fyp.fourthyear.cit.ie.watchit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class SensorDataService extends WearableListenerService implements SensorEventListener  {
    private SensorManager sensorManager;
    private Sensor heartRate;
    private Sensor breathRate;
    private Sensor stress;
    private Sensor EDA;


    private final long sensorTimer = 30000;//30 seconds
    /*timeSent is used to detect when was the last time a sensor sent data.
    index 0 = heart rate sensor
    index 1 = breath rate sensor
    index 2 = EDA sensor
    index 3 = Stress sensor
    Could expand this with enums*/
    private long[] timeSent;
    private GoogleApiClient client;
    private Context context;
    private final String TAG = "SENSOR";
    private boolean sensorsOn;
    public SensorDataService(){}
    public SensorDataService(Context context){
        this.context = context;
        //Create google api client that can handle the connections
        client = new GoogleApiClient.Builder(this.context)
                .addApi(Wearable.API)
                .build();
        client.connect();
        setupSensors();
        sensorsOn=false;
    }

    public boolean areSensorsOn(){
        return this.sensorsOn;
    }

    private void setupSensors(){
        //Set up the sensor manager and the default HR sensor
        sensorManager = (SensorManager)context.getSystemService(SENSOR_SERVICE);
        heartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        //Check if there are stress, breath rate or EDA sensors
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor : deviceSensors)
        {
            if(sensor.getName().contains("Stress") || sensor.getName().contains("stress"))
                stress = sensor;
            else if(sensor.getName().contains("Breath") || sensor.getName().contains("breath"))
                breathRate = sensor;
            else if(sensor.getName().contains("EDA") || sensor.getName().contains("Electrodermal") || sensor.getName().contains("electrodermal"))
                EDA = sensor;
        }
        timeSent = new long[4];
    }
    public void detachListeners(){
        logger(TAG,"Detach");
        sensorManager.unregisterListener(this);
        sensorsOn=false;
    }
    public void attachListeners(){
        boolean isRegistered;
        if(null != heartRate) {
            isRegistered = sensorManager.registerListener(this, heartRate, SensorManager.SENSOR_DELAY_NORMAL);
            logger(TAG, "HR listener on: " + isRegistered);
        }
        else
            logger(TAG,"No HR sensor");

        if(null != stress)
        {
            isRegistered = sensorManager.registerListener(this, stress, SensorManager.SENSOR_DELAY_NORMAL);
            logger(TAG,"Stress listener on: " + isRegistered);

        }
        else
            logger(TAG,"No stress sensor");

        if(null != breathRate)
        {
            isRegistered = sensorManager.registerListener(this, breathRate, SensorManager.SENSOR_DELAY_NORMAL);
            logger(TAG,"breathRate listener on: " + isRegistered);
        }
        else
            logger(TAG,"No breath sensor");

        if(null != EDA)
        {
            isRegistered = sensorManager.registerListener(this, EDA, SensorManager.SENSOR_DELAY_NORMAL);
            logger(TAG,"EDA listener on: " + isRegistered);
        }
        else
            logger(TAG,"No EDA sensor");
        sensorsOn=true;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        logger(TAG , "Sensor "+sensor.getName() + " accuracy changed to "+accuracy);
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.accuracy<=1)
            return;


        logger(TAG +"EVENT", "SENSOR:" + event.sensor.getName() + "\nDATA:" + event.values[0]);
        long currentTime = System.currentTimeMillis();
        //Do checks to see if data for a given sensor has been sent in the last minute

        if(null != heartRate && event.sensor.getName().equals(heartRate.getName()))
        {
            if(timeSent[0]+sensorTimer<currentTime)
                timeSent[0] = currentTime;
            else
                return;

        }
        else if(null != breathRate && event.sensor.getName().equals(breathRate.getName()))
        {
            if(timeSent[1]+sensorTimer<currentTime)
                timeSent[1] = currentTime;
            else
                return;
        }
        else if(null != EDA && event.sensor.getName().equals(EDA.getName()))
        {
            if(timeSent[2]+sensorTimer<currentTime)
                timeSent[2] = currentTime;
            else
                return;
        }
        else if(null != stress && event.sensor.getName().equals(stress.getName()))
        {
            if(timeSent[3]+sensorTimer<currentTime)
                timeSent[3] = currentTime;
            else
                return;
        }

        logger(TAG +"EVENT","TIME:" + currentTime + "\nACCURACY:" + event.accuracy + "\nSENSOR:" + event.sensor.getName() + "\nDATA:" + event.values[0]);
        sendSensorData(currentTime,event.values[0],event.sensor.getStringType());

    }
    public void sendSensorData(long timestamp, float data, String sensor){
        //Create the put request map that will be used to propagate data
        PutDataMapRequest requestMap  = PutDataMapRequest.create("/SensorData");
        //Fill the request map with the sensor data
        requestMap.getDataMap().putString("sensorType",sensor);
        requestMap.getDataMap().putLong("time",timestamp);
        requestMap.getDataMap().putFloat("data",data);

        //Create a request from our request map
        PutDataRequest request = requestMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(client,request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        //Note: dataItemResult will only give back data on if the data has been sent or not
                        //and does not give data on whether or not the data has arrived or not.
                        if(dataItemResult.getStatus().isSuccess())
                            logger(TAG,"Data sent");
                        else
                            logger(TAG,"Data failed to send, " +dataItemResult.getStatus().getStatusMessage());
                    }
                });
    }
    private void logger(String tag , String message){
        Log.d(tag,message);
    }

}
