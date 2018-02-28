package fyp.fourthyear.cit.ie.watchit.Domain;


public class HeartRate extends PhysiologicalAttributes {
    private int heartRate;

    public HeartRate(){super();}


    public HeartRate(long time, int heartRate){
        super(time);
        this.heartRate = heartRate;
    }
    public HeartRate(long time, int heartRate,boolean stressed){
        super(time,stressed);
        this.heartRate = heartRate;
    }
    public HeartRate(long time, int heartRate,boolean stressed,int dataNum){
        super(time,stressed,dataNum);
        this.heartRate = heartRate;
    }

    public void setHeartRate(int heartRate){
        this.heartRate = heartRate;
    }
    public int getHeartRate(){
        return this.heartRate;
    }



}
