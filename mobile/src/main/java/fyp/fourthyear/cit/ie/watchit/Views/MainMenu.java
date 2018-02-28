package fyp.fourthyear.cit.ie.watchit.Views;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import fyp.fourthyear.cit.ie.watchit.DAO.DAO;
import fyp.fourthyear.cit.ie.watchit.R;
import fyp.fourthyear.cit.ie.watchit.Services.DataCollectorService;
import layout.Associate;
import layout.Home;
import layout.Meditate;
import layout.ViewData;

public class MainMenu extends AppCompatActivity {
    private DAO dao = DAO.getInstance();
    private AlertDialog baselinePopup;
    @Override
    public void onResume(){
        super.onResume();

        if (!baselinePopup.isShowing())
            new AsyncBaselinePoller().execute("");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        new DataCollectorService(this);



        AlertDialog.Builder builder  = new AlertDialog.Builder(this,R.style.Watchit_actionDialog);

        builder.setMessage("Watch it requires your resting heart rate to function properly." +
                "\n\nTo facilitate this please enable the application on your SmartWatch right after you wake up." +
                "\n\nYou will be asked to do this once a week for the most accurate results")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }})
                .setTitle("Resting heart rate");
        baselinePopup = builder.create();







        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        //Set up a listener for a menu item event
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //Reset icons to unfocused.
                        Menu menu = bottomNavigationView.getMenu();

                        menu.findItem(R.id.menu_home).setIcon(R.drawable.home);
                        menu.findItem(R.id.menu_data).setIcon(R.drawable.data);
                        menu.findItem(R.id.menu_meditation).setIcon(R.drawable.meditation);
                        menu.findItem(R.id.menu_associate).setIcon(R.drawable.association);

                        switch (item.getItemId()) {
                            case R.id.menu_home:

                                item.setIcon(R.drawable.home_selected);
                                pushFragment(new Home());
                                break;
                            case R.id.menu_data:
                                item.setIcon(R.drawable.data_selected);
                                pushFragment(new ViewData());
                                break;
                            case R.id.menu_meditation:
                                item.setIcon(R.drawable.meditation_selected);
                                pushFragment(new Meditate());
                                break;
                            case R.id.menu_associate:
                                item.setIcon(R.drawable.association_selected);
                                pushFragment(new Associate());
                                break;
                        }
                        return true;
                    }
                });
        //Make home the default fragment
        bottomNavigationView.getMenu().getItem(0).setIcon(R.drawable.home_selected);
        pushFragment(new Home());


    }
    private void pushFragment(Fragment fragment)
    {
        //Change content of the fragment to the one selected
        if (fragment == null)
            return;
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.content, fragment);
                ft.commit();
            }
        }
    }

    private class AsyncBaselinePoller extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... strs) {
            do{
                try {
                    Thread.sleep(2000);

                }catch(Exception e){
                    Log.e("TAG" , "A async error occured " + e);
                }
            }while (!dao.getRequestBaseline() && !dao.getBaselinePolling());
            return dao.getRequestBaseline();
        }
        @Override
        protected void onPostExecute(Boolean result){
            if(result)
                baselinePopup.show();
        }
    }

}
