package com.edmundsapp.ehi.edmundsapp;


import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/***
 * Fragment class for displaying vehicle details with previous selections
 *  Allows for dynamic updating of Name and Price TextViews
 *  Author: James Bradshaw
 *  Date: 5/24/16
 */
public class VehicleDetailsFragment extends Fragment{

    ListView lv;
    ArrayAdapter<String> ad;
    String[] models = {"none"};
    String selected;

    @Nullable
    @Override//inflate layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_vehicle_details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
    }
    //set name text
    public void setName(String m){
        TextView t = (TextView)getView().findViewById(R.id.mdlnm);
        t.setText(m);
    }
    //set price text
    public void setPrice(String m){
        TextView t = (TextView)getView().findViewById(R.id.mdlpc);
        t.setText(m);
    }

}
