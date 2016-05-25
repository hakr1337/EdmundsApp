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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by James on 5/24/2016.
 */

public class VehicleMakeFragment extends ListFragment{

    ListView lv;
    ArrayAdapter<String> ad;
    String[] makes = {"Loading"};
    String selected;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.option_select_fragment, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        ad = new ArrayAdapter<String>(getActivity(), R.layout.list_row, R.id.row_item, makes);
        setListAdapter(ad);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int pos, long id){

        CarSearch.hideFrag("dt");
        CarSearch.hideFrag("st");
        CarSearch.hideFrag("md");

        v.setSelected(true);
        selected = (String) lv.getItemAtPosition(pos);

        if(!selected.equals("None")) {
            new GetModels().execute(CarSearch.yr.selected, selected);
            CarSearch.showFrag("md");
        }
    }

    public void setList(String m){
        List<String> l = new ArrayList<String>(Arrays.asList(m.split("\\^")));
        l.removeAll(Arrays.asList(""," ", null));
        ad = new ArrayAdapter<String>(getActivity(), R.layout.list_row, R.id.row_item, l);
        setListAdapter(ad);
    }



    private class GetModels extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://api.edmunds.com/api/vehicle/v2/"+params[1].replaceAll("\\s+", "")+"?fmt=json&year=" + params[0] + "&api_key=jskft3jqdm9wrhvj3fba2qwg");
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Accept", "application/json");

                if(c.getResponseCode() != 200){
                    throw new RuntimeException("HTTP failed with error: " + c.getResponseCode() +" Request: " + url.toString());
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

                JSONArray a = json.getJSONArray("models");
                String models = "";
                for(int i = 0; i<a.length(); i++){
                    models += a.getJSONObject(i).getString("name");
                    models+=", ";
                }

                CarSearch.md.setList(models);

            }catch(JSONException e){

            }
        }
    }

}
