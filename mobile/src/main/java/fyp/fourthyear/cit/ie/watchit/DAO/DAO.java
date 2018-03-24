package fyp.fourthyear.cit.ie.watchit.DAO;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fyp.fourthyear.cit.ie.watchit.Domain.HeartRate;

public class DAO {
    private final String TAG = "DAO";
    private static final DAO ourInstance = new DAO();
    private final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private String myName;
    private ArrayList<String> myTherapists;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<HeartRate> heartRates;
    private double hrBaseline;
    public static DAO getInstance() {
        return ourInstance;
    }
    private int dataAvailable;
    private int hoursStressed;


    private DAO() {
        dataAvailable = 0;
        hoursStressed=0;
        myTherapists = new ArrayList<>();
        heartRates = new ArrayList<>();
        myTherapists();
        getPhysiologicalAttributes();
        getBasicHistoricalData();
        getName();
    }

    public FirebaseDatabase getDATABASE(){
        return DATABASE;
    }
    public void uploadStressTest(ArrayList<Float> data , long time ){
        ref = DATABASE.getReference("Wearers").child(auth.getUid()).child("STRESSTEST");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        ref.child(c.getTime().toString()).setValue(data);
    }
    public ArrayList<String> getMyTherapists(){
        return this.myTherapists;
    }
    private void myTherapists(){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        ref = DATABASE.getReference("Wearers").child(auth.getUid()).child("Therapists");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myTherapists = new ArrayList<String>();
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                   // String[] data = {child.getKey(),child.child("email").getValue().toString()};
                    myTherapists.add(child.getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void disassociate(String id) {
        ref = DATABASE.getReference("Therapists");
        ref.child(id).child("patients").child(auth.getUid()).setValue(null);
        ref = DATABASE.getReference("Wearers");
        ref.child(auth.getUid()).child("Therapists").child(id).setValue(null);
    }
    public void addAssociate(String[] id){
        //Add wearer ID to therapist
        ref = DATABASE.getReference("Therapists");
        ref.child(id[0]).child("patients").child(auth.getUid()).setValue(myName);
        //add therapist ID to wearer
        ref = DATABASE.getReference("Wearers");
        ref.child(auth.getUid()).child("Therapists").child(id[0]).setValue(id[1]);
    }
    public void uploadData(HeartRate heartRate,String key){
        heartRates.add(heartRate);
        ref = DATABASE.getReference("Wearers");
        ref.child(auth.getUid()).child("PhysiologicalAttributes").child("HR").child(key).setValue(heartRate);
    }
    public void setHrBaseline(){
        int counter=0;
        double dataTotal=0;
        for(HeartRate hr : heartRates){
            if(0!=hr.getDataNum()){
                counter+=hr.getDataNum();
                dataTotal+=hr.getHeartRate()*hr.getDataNum();
            }
        }
        hrBaseline = Math.floor(dataTotal/counter);
        Log.d(TAG, "Baseline:"+hrBaseline+"\tdata total:"+dataTotal +"\tcounter:"+counter);
    }
    private void getName(){
        ref = DATABASE.getReference("Wearers").child(auth.getUid()).child("Name");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    myName = dataSnapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void getPhysiologicalAttributes(){
        ref = DATABASE.getReference("Wearers").child(auth.getUid()).child("PhysiologicalAttributes").child("HR");
        ref.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                heartRates = new ArrayList<>();
                for(DataSnapshot dataEntry :dataSnapshot.getChildren()){
                    if(dataEntry.child("time").exists() && dataEntry.child("heartRate").exists())
                        heartRates.add(dataEntry.getValue(HeartRate.class));
                }
                setHrBaseline();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public ArrayList<HeartRate> getHeartRates(){
        return this.heartRates;
    }
    public double getHeartRateBaseline(){return this.hrBaseline;}
    private void getBasicHistoricalData(){
        ref = DATABASE.getReference("Wearers").child(auth.getUid());
        ref.child("Data_Available").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    dataAvailable = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        ref.child("Hours_Stressed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    hoursStressed = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
    public void uploadStressData(double result, Date time,boolean stressed){
        ref = DATABASE.getReference("Wearers").child(auth.getUid());
        dataAvailable++;
        ref.child("Data_Available").setValue(dataAvailable);
        if(stressed){
            hoursStressed++;
            ref.child("Hours_Stressed").setValue(hoursStressed);
        }
        ref.child("Detailed_Stress_Data").child(time.toString()).setValue(result);
    }
    public int getHoursStressed(){return this.hoursStressed;}
    public int getDataAvailable(){return this.dataAvailable;}
    public void clearData(){
        ref = DATABASE.getReference("Wearers").child(auth.getUid()).child("PhysiologicalAttributes").child("HR");
        //This will set the removal time to 7 days
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-7);
        final long boundaryTime = c.getTimeInMillis();

        ref.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG , dataSnapshot.toString());
                for(DataSnapshot dataPoint : dataSnapshot.getChildren()) {
                    //If true then the datapoint is 1 week or more old
                    if(dataPoint.getChildrenCount()!=4){
                        Log.e(TAG, "Error physiological attribute for HR at time "+ dataPoint.getKey() + " does not have enough children");
                        continue;
                    }
                    long dataPointTime = dataPoint.child("time").getValue(Long.class);
                    if(dataPointTime < boundaryTime){
                        String timeToRemove = dataPoint.getKey();
                        //Remove data from physiological attributes
                        dataPoint.getRef().setValue(null);
                        //Remove data from detailed stress data
                        DATABASE.getReference("Wearers").child(auth.getUid()).child("Detailed_Stress_Data").child(timeToRemove).setValue(null);
                        //Remove the data from the local heart rate data
                        for(int index = 0; index<heartRates.size();index++){
                            if(heartRates.get(index).getTime() == dataPointTime){
                                heartRates.remove(index);
                                break;
                            }

                        }

                    }


                }
                //Check to see if the number of hours stressed has changed, if so upload the new amount of hours stressed
                int hoursStressedRecount=0;
                for(HeartRate heartRate : heartRates){
                    if(heartRate.getStressed())
                        hoursStressedRecount++;
                }
                if(hoursStressed != hoursStressedRecount) {
                    hoursStressed = hoursStressedRecount;
                    DATABASE.getReference("Wearers").child(auth.getUid()).child("Hours_Stressed").setValue(hoursStressed);
                }

                //The amount of data available will have changed, so upload the new value
                dataAvailable = heartRates.size();
                DATABASE.getReference("Wearers").child(auth.getUid()).child("Data_Available").setValue(dataAvailable);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
