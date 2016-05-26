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


/***
 * Fragment class for handling vehicle make selection
 *  Extends a ListFragment for easy override of list functions on click
 *  Also allows for dynamic updating of list when needed
 *  Author: James Bradshaw
 *  Date: 5/24/16
 */
public class VehicleMakeFragment extends ListFragment{

    ListView lv;
    ArrayAdapter<String> ad;
    String[] makes = {"Loading"};
    String selected;

    @Nullable
    @Override//inflate layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.option_select_fragment, container, false);
    }

    @Override//populate default list
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        ad = new ArrayAdapter<String>(getActivity(), R.layout.list_row, R.id.row_item, makes);
        setListAdapter(ad);
    }

    @Override//handle item selection
    public void onListItemClick(ListView lv, View v, int pos, long id){

        //hide lower frags for error avoidance
        CarSearch.hideFrag("dt");
        CarSearch.hideFrag("st");
        CarSearch.hideFrag("md");

        v.setSelected(true);
        selected = (String) lv.getItemAtPosition(pos);

        if(!selected.equals("None")) {//make sure year selected has makes to select
            new GetModels().execute(CarSearch.yr.selected, selected);
            CarSearch.showFrag("md");
        }
    }

    public void setList(String m){//dynamically update list
        List<String> l = new ArrayList<String>(Arrays.asList(m.split("\\^")));
        l.removeAll(Arrays.asList(""," ", null));
        ad = new ArrayAdapter<String>(getActivity(), R.layout.list_row, R.id.row_item, l);
        setListAdapter(ad);
    }

    /**
     * GetModels calls the edmunds.com API using the Java net package
     * handles responses depending on HTTP codes then if all is well
     * uses a buffered reader to read in the returned data into a string
     * to pass on to the onPostExecute method which parses the data using
     * the json library to get the correct data and display it. This class
     * extends the AsyncTask class to avoid freezing up the UI
     */
    private class GetModels extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {//create api request URL using year and make
                URL url = new URL("http://api.edmunds.com/api/vehicle/v2/"+params[1].replaceAll("\\s+", "")+"?fmt=json&year=" + params[0] + "&api_key=jskft3jqdm9wrhvj3fba2qwg");
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Accept", "application/json");

                if(c.getResponseCode() != 200){//handle failures
                    throw new RuntimeException("HTTP failed with error: " + c.getResponseCode() +" Request: " + url.toString());
                }

                //read stream with buffered reader
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
                JSONObject json = new JSONObject(ret);//create json object from data

                JSONArray a = json.getJSONArray("models");//retrieve models array
                String models = "";
                for(int i = 0; i<a.length(); i++){
                    models += a.getJSONObject(i).getString("name");//collect model names
                    models+=", ";
                }

                CarSearch.md.setList(models);//update models list with collected names

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
