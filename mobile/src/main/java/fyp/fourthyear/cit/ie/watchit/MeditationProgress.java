package fyp.fourthyear.cit.ie.watchit;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fyp.fourthyear.cit.ie.watchit.Services.DataCollectorService;
import layout.Meditate;


public class MeditationProgress extends Fragment {
    private MediaPlayer mediaPlayer;
    private DataCollectorService DCService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d("WatchitDebug Meditation Progress", "found service");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DataCollectorService.LocalBinder binder = (DataCollectorService.LocalBinder) service;
            DCService = binder.getService();
            DCService.collectMeditationData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    };
    public MeditationProgress(){}



    @Override
    public void onDestroyView() {
        mediaPlayer.stop();
        super.onDestroyView();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_meditation_progress, container, false);
        final TextView timeRemainingTV = v.findViewById(R.id.time_remaining);

        int time = getArguments().getInt("time");
        long timeInMilliseconds  = time*60000;
        Button endMeditationBTN = v.findViewById(R.id.EndMeditationBTN);
        endMeditationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                DCService.stopCollectingMeditationData();
                Toast.makeText(getContext(),"You ended the meditation early. To get details about your meditation session you must complete a full session",Toast.LENGTH_LONG).show();
                pushFragment(new Meditate());
            }
        });
        Intent intent = new Intent(getContext(), DataCollectorService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.progress);
        mediaPlayer.start();

        new CountDownTimer(timeInMilliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                long secondsLeft = (millisUntilFinished % 60000) / 1000;
                long minutesLeft =millisUntilFinished/60000;
                String timeRemaining;
                if(minutesLeft>0) {
                    if(minutesLeft>=10)
                        timeRemaining = ""+minutesLeft+":";
                    else
                        timeRemaining = "0"+minutesLeft+":";
                }
                else
                    timeRemaining="00:";
                if(secondsLeft>=10)
                    timeRemaining+=secondsLeft;
                else
                    timeRemaining+="0"+secondsLeft;
                timeRemainingTV.setText(timeRemaining);
            }
            public void onFinish() {
                ArrayList<Integer> meditationData = DCService.stopCollectingMeditationData();
                mediaPlayer.stop();
                MeditationResult meditationResult = new MeditationResult();
                Bundle resultsData = new Bundle();
                resultsData.putIntegerArrayList("results", meditationData);
                meditationResult.setArguments(resultsData);
                pushFragment(meditationResult);

            }
        }.start();


        return v;
    }
    private void pushFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.content, fragment);
                ft.commit();
            }
        }
    }




}
