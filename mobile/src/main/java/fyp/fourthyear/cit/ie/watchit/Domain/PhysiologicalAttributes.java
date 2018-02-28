package fyp.fourthyear.cit.ie.watchit.Domain;


public class PhysiologicalAttributes {
    private long time;
    private boolean stressed;
    private int dataNum;

    public PhysiologicalAttributes(){}
    public PhysiologicalAttributes(long time){
        this.time=time;
    }
    public PhysiologicalAttributes(long time,boolean stressed){
        this.time=time;
        this.stressed=stressed;
    }
    public PhysiologicalAttributes(long time,boolean stressed,int dataNum){
        this.time=time;
        this.stressed=stressed;
        this.dataNum=dataNum;
    }

    public void setTime(long time){
        this.time= time;
    }
    public long getTime(){
        return this.time;
    }
    public void setDataNum(int dataNum){
        this.dataNum= dataNum;
    }
    public int getDataNum(){
        return this.dataNum;
    }

    public void setStressed(boolean stressed){
        this.stressed= stressed;
    }
    public boolean getStressed(){
        return this.stressed;
    }

}
