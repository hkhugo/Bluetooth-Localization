package com.estimote.proximity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.internal_plugins_api.scanning.Beacon;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity.estimote.ProximityContentAdapter;
import com.estimote.proximity.estimote.ProximityContentManager;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView navigationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        navigationTV = findViewById(R.id.navigationTV);
        drawerLayout = findViewById(R.id.drawer_layout);
//        loadFragment(new HomeFragment());
    }

    //Menu
    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    //Home button function
    public void ClickHome(View view){
        closeDrawer(drawerLayout);
        navigationTV.setText("Home");
//        loadFragment(new HomeFragment());
    }

    //About us button function
    public void ClickAboutUs(View view){
        closeDrawer(drawerLayout);
        navigationTV.setText("List Device");
        loadFragment(new ListDeviceFragment());
    }

    public void ClickLogout(View view){
        logout(this);
    }

    public void ClickEmpty(View view){
        closeDrawer(drawerLayout);
    }

    //logout button function
    public void logout(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure that you want to logout");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finishAffinity();
            }
        });
        builder.show();
    }

    // buy and upload button
//    public void onButtonClicked(View view) {
//        Intent intent;
//        switch (view.getId()) {
//            case R.id.buy:
//                intent = new Intent(getApplicationContext(), ItemListActivity.class);
//                intent = new Intent(getApplicationContext(), ItemListActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.upload:
//                intent = new Intent(getApplicationContext(), UploadActivity.class);
//                startActivity(intent);
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + view.getId());
//        }
//
//    }


    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }


    private void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            logout(this);
            return true;
        }
        else
            return super.onKeyDown(keyCode, e);
    }
}
