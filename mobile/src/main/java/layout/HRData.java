package layout;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fyp.fourthyear.cit.ie.watchit.DAO.DAO;
import fyp.fourthyear.cit.ie.watchit.Domain.HRDataAdapter;
import fyp.fourthyear.cit.ie.watchit.R;

public class HRData extends Fragment {
    private DAO dao = DAO.getInstance();
    public HRData(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_hr_data, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new HRDataAdapter(dao.getHeartRates(), R.layout.hr_data_row));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        return v;
    }



}
