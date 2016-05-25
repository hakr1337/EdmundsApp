package com.edmundsapp.ehi.edmundsapp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by James on 5/24/2016.
 */

public class VehicleStyleFragment extends ListFragment{

    ListView lv;
    ArrayAdapter<String> ad;
    String[] models = {"none"};
    String selected;
    String ids;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_vehicle_style_fragment, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        ad = new ArrayAdapter<String>(getActivity(), R.layout.style_list, R.id.style_row, models);
        setListAdapter(ad);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int pos, long id){
        v.setSelected(true);
        String[] t = ids.split(", ");
        CarSearch.showFrag("dt");
        new GetTrims().execute(t[pos]);
    }

    public void setList(String m){
        ad = new ArrayAdapter<String>(getActivity(), R.layout.list_row, R.id.row_item, m.split(","));
        setListAdapter(ad);
    }

    public void setIds(String i){
        ids = i;
    }


    private class GetTrims extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://api.edmunds.com/v1/api/tmv/tmvservice/calculatenewtmv?styleid="+params[0]+"&zip=84124&fmt=json&api_key=jskft3jqdm9wrhvj3fba2qwg");
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Accept", "application/json");

                if(c.getResponseCode() != 200){
                    throw new RuntimeException("HTTP failed with error: " + c.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));

                String response;
                String ret = "";
                while((response = br.readLine()) != null) {
                    ret += response + " ";
                }
                return ret;
            }catch(MalformedURLException e){
                Log.v("TAG", "malformed URL exception");
            }catch(IOException e){
                Log.v("TAG", "IO exception");
            }

            return "none";
        }

        @Override
        protected void onPostExecute(String ret){
            try {
                JSONObject json = new JSONObject(ret);

                String price = json.getJSONObject("tmv").getJSONObject("nationalBasePrice").getString("baseMSRP");

                CarSearch.dt.setName("hello");
                CarSearch.dt.setPrice(price);

            }catch(JSONException e){

            }
        }
    }

}