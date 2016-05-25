package com.edmundsapp.ehi.edmundsapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

public class CarSearch extends FragmentActivity {
    String selected;
    FragmentManager fm;
    FragmentTransaction ft;
    public static VehicleYearFragment yr = new VehicleYearFragment();
    public static VehicleMakeFragment mk = new VehicleMakeFragment();
    public static VehicleModelFragment md = new VehicleModelFragment();
    public static VehicleMakeFragment st = new VehicleMakeFragment();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_search);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        ft.add(R.id.vhyr, yr);
        ft.add(R.id.vhmk, mk);
        ft.add(R.id.vhmd, md);
        ft.add(R.id.vhst, st);
        ft.commit();
    }

}


