
package com.edmundsapp.ehi.edmundsapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.TextView;

/***
 * Main Activity Class
 *  extends FragmentActivity for use of FragmentManager and FragmentTransaction
 *  Creates and assigns fragments to their spot in the layout
 *  Author: James Bradshaw
 *  Date: 5/24/16
 */
public class CarSearch extends FragmentActivity {

    //static vars bc shouldnt change for proper usage
    public static FragmentManager fm;
    public static FragmentTransaction ft;

    //create all fragments
    public static VehicleYearFragment yr = new VehicleYearFragment();
    public static VehicleMakeFragment mk = new VehicleMakeFragment();
    public static VehicleModelFragment md = new VehicleModelFragment();
    public static VehicleStyleFragment st = new VehicleStyleFragment();
    public static VehicleDetailsFragment dt = new VehicleDetailsFragment();

    @Override//on create assign frags to their layouts
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_search);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        //hide all besides year for error avoidance
        ft.add(R.id.vhyr, yr);
        ft.add(R.id.vhmk, mk).hide(mk);
        ft.add(R.id.vhmd, md).hide(md);
        ft.add(R.id.vhst, st).hide(st);
        ft.add(R.id.vhdt, dt).hide(dt);
        //commit adds
        ft.commit();
    }

    //method to show an individual frag
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

    //method to hide an individual frag
    public static void hideFrag(String frag){
        ft = fm.beginTransaction();
        switch(frag){
            case "mk":
                ft.hide(mk);
                ft.commit();
                break;
            case "md":
                ft.hide(md);
                ft.commit();
                break;
            case "st":
                ft.hide(st);
                ft.commit();
                break;
            case "dt":
                ft.hide(dt);
                ft.commit();
                break;
        }
    }


}

