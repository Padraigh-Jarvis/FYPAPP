package layout;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import fyp.fourthyear.cit.ie.watchit.R;

public class Home extends Fragment {
    private FirebaseAuth mAuth;
    public Home() {
        // Required empty public constructor
        mAuth = FirebaseAuth.getInstance();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button logout =  view.findViewById(R.id.home_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
        return view;
    }


}
