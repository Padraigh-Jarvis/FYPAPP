package fyp.fourthyear.cit.ie.watchit.DAO;


import android.provider.CalendarContract;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    private ArrayList<String> myTherapists;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<HeartRate> heartRates;
    private int hrBaseline;
    private long hrBaselineTime;
    private boolean requestBaseline;
    public static DAO getInstance() {
        return ourInstance;
    }
    private int dataAvailable;
    private int hoursStressed;
    private boolean baselinePolling = true;


    private DAO() {
        dataAvailable = 0;
        hoursStressed=0;
        requestBaseline = false;
        myTherapists = new ArrayList<>();
        heartRates = new ArrayList<>();
        myTherapists();
        getPhysiologicalAttributes();
        readHRBaseline();
        getBasicHistoricalData();
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
                myTherapists = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    myTherapists.add(child.getKey());
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
    public void addAssociate(String id){
        //Add wearer ID to therapist
        ref = DATABASE.getReference("Therapists");
        ref.child(id).child("patients").child(auth.getUid()).setValue(true);
        //add therapist ID to wearer
        ref = DATABASE.getReference("Wearers");
        ref.child(auth.getUid()).child("Therapists").child(id).setValue(true);
    }
    public void uploadData(HeartRate heartRate,String key){
        ref = DATABASE.getReference("Wearers");
        ref.child(auth.getUid()).child("PhysiologicalAttributes").child("HR").child(key).setValue(heartRate);
    }
    public void uploadBaseline(int baseline,long time ,String type) {
        ref = DATABASE.getReference("Wearers").child(auth.getUid()).child("PhysiologicalAttributes").child(type).child("Baseline");
        ref.child("time").setValue(time);
        ref.child("HR").setValue(baseline);
    }
    private void checkBaseline(String type){
        Calendar c = Calendar.getInstance();
        Calendar current  =Calendar.getInstance();
        if(type.equals("HR")){
            c.setTimeInMillis(hrBaselineTime);
            c.add(Calendar.DATE,7); //Change to 7
            if(c.getTime().compareTo(current.getTime())<0)
                requestBaseline=true;
            else
                requestBaseline = false;
        }

    }
    private void readHRBaseline() {
        baselinePolling=false;
        ref = DATABASE.getReference("Wearers").child(auth.getUid()).child("PhysiologicalAttributes").child("HR").child("Baseline");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.hasChildren() && dataSnapshot.hasChild("time") && dataSnapshot.hasChild("HR")) {
                    hrBaselineTime = dataSnapshot.child("time").getValue(Long.class);
                    hrBaseline = dataSnapshot.child("HR").getValue(Integer.class);
                    checkBaseline("HR");
                    baselinePolling=true;
                }
                else {
                    requestBaseline = true;
                    baselinePolling=true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public boolean getRequestBaseline(){return requestBaseline;}
    private void getPhysiologicalAttributes(){
        ref = DATABASE.getReference("Wearers").child(auth.getUid()).child("PhysiologicalAttributes").child("HR");
        ref.orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                heartRates = new ArrayList<>();
                for(DataSnapshot dataEntry :dataSnapshot.getChildren()){
                    Log.d(TAG,dataEntry.toString());
                    if(dataEntry.child("time").exists() && dataEntry.child("heartRate").exists())
                        heartRates.add(dataEntry.getValue(HeartRate.class));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public ArrayList<HeartRate> getHeartRates(){
        return this.heartRates;
    }
    public int getHeartRateBaseline(){return this.hrBaseline;}
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
    public boolean getBaselinePolling(){return this.baselinePolling;}
    public void clearData(){
        ref = DATABASE.getReference("Wearers").child(auth.getUid()).child("PhysiologicalAttributes").child("HR");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-7);
        final long boundaryTime = c.getTimeInMillis();
        ref.orderByChild("time").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.getKey().equals("Baseline")) {
                    if(dataSnapshot.child("time").getValue(Long.class) < boundaryTime){
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(dataSnapshot.child("time").getValue(Long.class));
                        DatabaseReference deleteRef= dataSnapshot.getRef();
                        deleteRef.setValue(null);
                        deleteRef=DATABASE.getReference("Wearers").child(auth.getUid()).child("Detailed_Stress_Data").child(cal.getTime().toString());
                        deleteRef.setValue(null);
                        deleteRef=DATABASE.getReference("Wearers").child(auth.getUid());
                        dataAvailable--;
                        deleteRef.child("Data_Available").setValue(dataAvailable);
                        if(dataSnapshot.hasChild("stressed")) {
                            hoursStressed--;
                            deleteRef.child("Hours_Stressed").setValue(hoursStressed);
                        }
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}