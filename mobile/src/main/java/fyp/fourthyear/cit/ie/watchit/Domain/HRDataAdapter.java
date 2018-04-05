package fyp.fourthyear.cit.ie.watchit.Domain;


import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;


import fyp.fourthyear.cit.ie.watchit.R;

public class HRDataAdapter extends RecyclerView.Adapter<HRDataAdapter.ViewHolder>{

    private ArrayList<HeartRate> hrList;
    private int layout;
    public HRDataAdapter(ArrayList<HeartRate> hrList, int layout){
        this.hrList=hrList;
        this.layout = layout;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView day;
        private TextView hour;
        private TextView data;
        private ImageView stressed;


        ViewHolder(View view) {
            super(view);
            this.day = view.findViewById(R.id.day);
            this.hour = view.findViewById(R.id.hour);
            this.data = view.findViewById(R.id.data);
            this.stressed = view.findViewById(R.id.stressed);

        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(HRDataAdapter.ViewHolder holder, int position) {
        HeartRate hrData = hrList.get(position);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(hrData.getTime());
        holder.day.setText(getDay(c.get(Calendar.DAY_OF_WEEK)));
        holder.hour.setText(Integer.toString(c.get(Calendar.HOUR_OF_DAY)));
        holder.data.setText(Integer.toString(hrData.getHeartRate()));
        if(hrData.getStressed())
            holder.stressed.setBackgroundResource(R.drawable.ic_red_circle);
        else
            holder.stressed.setBackgroundResource(R.drawable.ic_green_circle);
    }


    @Override
    public int getItemCount() {
        return hrList.size();
    }



    private String getDay(int value)
    {
        switch (value)
        {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return null;
        }

    }
}
