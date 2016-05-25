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

public class VehicleModelFragment extends ListFragment{

    ListView lv;
    ArrayAdapter<String> ad;
    String[] models = {"none"};
    String selected;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_vehicle_model_fragment, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        ad = new ArrayAdapter<String>(getActivity(), R.layout.list_row, R.id.row_item, models);
        setListAdapter(ad);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int pos, long id){
        v.setSelected(true);
        selected = (String) lv.getItemAtPosition(pos);
        new GetTrims().execute(CarSearch.yr.selected, CarSearch.mk.selected, selected);
        CarSearch.showFrag("st");
    }

    public void setList(String m){
        ad = new ArrayAdapter<String>(getActivity(), R.layout.list_row, R.id.row_item, m.split(","));
        setListAdapter(ad);
    }



    private class GetTrims extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://api.edmunds.com/api/vehicle/v2/"+params[1].replaceAll("\\s+", "")+"/"+params[2].replaceAll("\\s+", "")+"/" + params[0] +"?fmt=json&api_key=jskft3jqdm9wrhvj3fba2qwg");
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

                JSONArray a = json.getJSONArray("styles");
                String models = "";
                String ids = "";

                for(int i = 0; i<a.length(); i++){
                    models += a.getJSONObject(i).getString("name");
                    models+=", ";

                    ids += ""+a.getJSONObject(i).getInt("id");
                    ids += ", ";

                }

                CarSearch.st.setList(models);
                CarSearch.st.setIds(ids);

            }catch(JSONException e){

            }
        }
    }

}
