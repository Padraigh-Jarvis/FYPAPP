package layout;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fyp.fourthyear.cit.ie.watchit.DAO.DAO;
import fyp.fourthyear.cit.ie.watchit.R;

public class ViewData extends Fragment {
    DAO dao = DAO.getInstance();

    public ViewData() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_data, container, false);
        TextView stressInfo = view.findViewById(R.id.ViewDataStressedValues);

//        dao.clearData();

        int dataAvailable = dao.getDataAvailable();
        int hoursStressed = dao.getHoursStressed();



        String stressedString = hoursStressed+" out of "+dataAvailable +" hours";
        stressInfo.setText(stressedString);

        TextView restingHR = view.findViewById(R.id.Resting_HR);
        String restingHRString = dao.getHeartRateBaseline()+" bmp";
        restingHR.setText(restingHRString);

        Button HRbtn = view.findViewById(R.id.HRDataButton);
        HRbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pushFragment(new HRData());
            }
        });
        return view;
    }

    private void pushFragment(Fragment fragment)
    {
        //Change content of the fragment to the one selected
        if (fragment == null)
            return;
        System.gc();
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
