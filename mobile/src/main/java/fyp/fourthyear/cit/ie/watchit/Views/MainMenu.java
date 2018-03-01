package fyp.fourthyear.cit.ie.watchit.Views;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("TAG","TEST");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        //new DataCollectorService(this);



        Intent service = new Intent(this,DataCollectorService.class);
        startService(service);
        DAO.getInstance();





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

}
