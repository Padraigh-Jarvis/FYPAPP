package fyp.fourthyear.cit.ie.watchit;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fyp.fourthyear.cit.ie.watchit.DAO.DAO;


public class MeditationResult extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        DAO dao = DAO.getInstance();
        View v =inflater.inflate(R.layout.fragment_meditation_result, container, false);

        ArrayList<Integer> results = getArguments().getIntegerArrayList("results");

        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.done);
        mediaPlayer.start();

        TextView resultsTV = v.findViewById(R.id.resultsBlurb);

        if(results==null) {
            Log.d("WatchitDebug MeditationResults", "There was an issue getting the data bundle!");
        }
        else{
            if(results.size()!=0) {
                int total=0;
                for(int hr : results){
                    total+=hr;
                }
                total=total/results.size();


                resultsTV.setText("You recorded a average BMP of "+ total+" during this meditation session. For reference  your " +
                        "baseline BMP is " + dao.getHeartRateBaseline());
            }
            else {
                resultsTV.setText("No data was recorded during this meditation session. If you wish to know how your meditation affected your " +
                        "heart rate please turn on the Watchit app on your smart watch");
            }
        }


        return v;
    }

}
