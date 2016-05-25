package com.edmundsapp.ehi.edmundsapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.TextView;

public class CarSearch extends FragmentActivity {
    String selected;
    public static FragmentManager fm;
    public static FragmentTransaction ft;
    public static VehicleYearFragment yr = new VehicleYearFragment();
    public static VehicleMakeFragment mk = new VehicleMakeFragment();
    public static VehicleModelFragment md = new VehicleModelFragment();
    public static VehicleStyleFragment st = new VehicleStyleFragment();
    public static VehicleDetailsFragment dt = new VehicleDetailsFragment();
    TextView name;
    TextView price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_search);
        name = (TextView) findViewById(R.id.mdlnm);
        price = (TextView) findViewById(R.id.mdlpc);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        ft.add(R.id.vhyr, yr);
        ft.add(R.id.vhmk, mk).hide(mk);
        ft.add(R.id.vhmd, md).hide(md);
        ft.add(R.id.vhst, st).hide(st);
        ft.add(R.id.vhdt, dt).hide(dt);
        ft.commit();
    }

    public static void showFrag(String frag){
        ft = fm.beginTransaction();
        switch(frag){
            case "mk":
                ft.show(mk);
                ft.commit();
                break;
            case "md":
                ft.show(md);
                ft.commit();
                break;
            case "st":
                ft.show(st);
                ft.commit();
                break;
            case "dt":
                ft.show(dt);
                ft.commit();
                break;
        }
    }

}


