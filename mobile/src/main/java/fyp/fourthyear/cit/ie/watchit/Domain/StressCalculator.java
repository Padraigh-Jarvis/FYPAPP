package fyp.fourthyear.cit.ie.watchit.Domain;



import java.util.ArrayList;
import java.util.Calendar;

import fyp.fourthyear.cit.ie.watchit.DAO.DAO;

public class StressCalculator {

    private DAO dao = DAO.getInstance();
    private final int BUFFER = 10;

    public StressCalculator(){}



    public boolean determineStress(ArrayList<Float> hourData,long time){
        double baseline = dao.getHeartRateBaseline();
        if (0==baseline){
            dao.uploadStressTest(hourData,time);
            return false;
        }
        float stressCounter=0;
        for(int index = 0; index<hourData.size();index++){
            if(index == 0 && hourData.get(index) > baseline+BUFFER)
                stressCounter++;
            else if(hourData.get(index) > baseline+BUFFER && hourData.get(index-1) > baseline+BUFFER)
                stressCounter++;
        }
        double result = (stressCounter/hourData.size())*100;
        boolean stressed=false;
        if(result>50)
            stressed = true;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        dao.uploadStressTest(hourData,time);
        dao.uploadStressData(result,c.getTime(),stressed);
        return stressed;
    }






}
