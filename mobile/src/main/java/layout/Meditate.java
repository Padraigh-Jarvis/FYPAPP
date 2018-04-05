package layout;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import fyp.fourthyear.cit.ie.watchit.MeditationProgress;
import fyp.fourthyear.cit.ie.watchit.R;

public class Meditate extends Fragment {
    public Meditate() {
        // Required empty public constructor
    }

    TextView meditationTimeInputTV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_meditate
                , container, false);
        meditationTimeInputTV  = v.findViewById(R.id.meditationTimeInput);

        Button activateMeditation = v.findViewById(R.id.Start_Meditation_Button);
        activateMeditation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeAsString =  meditationTimeInputTV.getText().toString();
                int time;
                try {

                    time = Integer.parseInt(timeAsString);
                }catch (Exception e ){
                    Toast.makeText(getContext(),"There was an error parsing that number!",Toast.LENGTH_SHORT).show();
                    Log.d("Meditate", e.getMessage() +timeAsString );
                    return;
                }
                if(time>120){
                    Toast.makeText(getContext(),"Meditation time should be less then 2 hours",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (time<1){
                    Toast.makeText(getContext(),"Meditation time cannot be less then 1 minute",Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putInt("time", time);
                MeditationProgress meditationProgress = new MeditationProgress();
                meditationProgress.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    if (ft != null) {
                        ft.replace(R.id.content, meditationProgress);
                        ft.commit();
                    }
                }

            }
        });




        return v;

    }
}
