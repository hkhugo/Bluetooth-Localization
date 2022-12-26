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
import android.widget.EditText;
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
        navigationTV.setText("Localization");
        loadFragment(new localizationFragment());
    }

    //Device List button function
    public void ClickListDevice(View view){
        closeDrawer(drawerLayout);
        Intent intent;
        intent = new Intent(getApplicationContext(), LeDeviceListAdapter.class);
        startActivity(intent);
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

    //
    public void onButtonClicked(View view) {

        EditText ptTx = findViewById(R.id.ptTx);
        EditText ptx1 = findViewById(R.id.ptx1);
        EditText ptx2 = findViewById(R.id.ptx2);
        EditText ptx3 = findViewById(R.id.ptx3);
        EditText pty1 = findViewById(R.id.pty1);
        EditText pty2 = findViewById(R.id.pty2);
        EditText pty3 = findViewById(R.id.pty3);
        EditText ptRssi1 = findViewById(R.id.ptRSSI1);
        EditText ptRSSI2 = findViewById(R.id.ptRSSI2);
        EditText ptRSSI3 = findViewById(R.id.ptRSSI3);
        EditText ptRssi = findViewById(R.id.ptRssi);

        int txPower = Integer.parseInt(ptTx.getText().toString());
        double[] result;
        double[] x = new double[4];
        x[1] = Integer.parseInt(ptx1.getText().toString());
        x[2] = Integer.parseInt(ptx2.getText().toString());
        x[3] = Integer.parseInt(ptx3.getText().toString());
        double[] y = new double[4];
        y[1] = Integer.parseInt(pty1.getText().toString());
        y[2] = Integer.parseInt(pty2.getText().toString());
        y[3] = Integer.parseInt(pty3.getText().toString());
        double[] rssi = new double[4];
        rssi[1] = Integer.parseInt(ptRssi1.getText().toString());
        rssi[2] = Integer.parseInt(ptRSSI2.getText().toString());
        rssi[3] = Integer.parseInt(ptRSSI3.getText().toString());


        switch (view.getId()) {
            case R.id.btSubmit:

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }

    }


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
