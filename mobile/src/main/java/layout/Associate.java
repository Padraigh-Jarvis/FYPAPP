package layout;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fyp.fourthyear.cit.ie.watchit.DAO.DAO;
import fyp.fourthyear.cit.ie.watchit.R;
import fyp.fourthyear.cit.ie.watchit.Views.Login;

public class Associate extends Fragment {
    private DAO dao = DAO.getInstance();
    private ArrayList<String> therapistList;
    private View  view;
    private FirebaseDatabase db;
    private ArrayList<String> myTherapists;
    private Spinner spinner;
    public Associate() {
        db = dao.getDATABASE();
        myTherapists = dao.getMyTherapists();
        therapistList = new ArrayList<>();
        listTherapists();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_associate, container, false);
        this.view = view;
        if(!myTherapists.contains("None")) {
            myTherapists.add(0, "None");
        }
        spinner = this.view.findViewById(R.id.spinner);
        //Create an adapter that will be used for the spinner.
        //Note the array list is the items that will appear in the spinner and the layout file is the way they will be displayed
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.spinner_dropdown,myTherapists);
        spinner.setAdapter(adapter);

        setUpButtons();

        return view;
    }

    private void setUpButtons()
    {
        Button associate = view.findViewById(R.id.AssociateBtn);
        Button disassociate = view.findViewById(R.id.DissassocaiteBtn);



        associate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText et = view.findViewById(R.id.TherapistID);
                boolean success = checkId(et.getText().toString());
                if(success)
                    addId(et.getText().toString());



            }
        });
        disassociate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int selection = spinner.getSelectedItemPosition();
                if(myTherapists.get(selection).equals("None"))
                {
                    Toast.makeText(view.getContext(), "Please select an id", Toast.LENGTH_SHORT).show();
                    return;
                }
                dao.disassociate(myTherapists.get(selection));
                myTherapists.remove(selection);
                spinner.setSelection(0);

            }
        });

    }

    public void addId(String id)
    {
        dao.addAssociate(id);
        myTherapists.add(id);

        Toast.makeText(view.getContext(), "Therapist id was added", Toast.LENGTH_SHORT).show();
    }

    public void listTherapists(){
        DatabaseReference ref = db.getReference("Therapists");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                therapistList = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    therapistList.add(child.getKey());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
     private boolean checkId(String id){
         //Check if ID exists
         for(String therapistId : therapistList)
         {
            if(therapistId.equals(id))
              {
                  //Check if the user has already added this ID
                  for(String myTherapistID:myTherapists)
                  {
                      if(myTherapistID.equals(id))
                      {
                          Toast.makeText(this.view.getContext(), "User has already added this ID", Toast.LENGTH_SHORT).show();
                          return false;
                      }
                  }

                  return true;
              }


          }

         Toast.makeText(this.view.getContext(), "Therapist id does not exist", Toast.LENGTH_SHORT).show();
         return false;
  }

}
