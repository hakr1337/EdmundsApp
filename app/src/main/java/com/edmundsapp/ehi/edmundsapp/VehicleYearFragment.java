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

/***
 * Fragment class for handling vehicle year selection
 *  Extends a ListFragment for easy override of list functions on click
 *  Also allows for dynamic updating of list when needed
 *  Author: James Bradshaw
 *  Date: 5/24/16
 */

public class VehicleYearFragment extends ListFragment{

    ListView lv;
    ArrayAdapter<String> ad;
    String[] years;
    String selected;

    @Nullable
    @Override//inflate layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.option_select_fragment, container, false);
    }

    @Override//populate list with years string in resources
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        years  = getResources().getStringArray(R.array.years);
        ad = new ArrayAdapter<String>(getActivity(), R.layout.list_row, R.id.row_item, years);
        setListAdapter(ad);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int pos, long id){
        //hide all lower frags on click to avoid errors
        CarSearch.hideFrag("mk");
        CarSearch.hideFrag("st");
        CarSearch.hideFrag("dt");
        CarSearch.hideFrag("md");

        v.setSelected(true);
        selected = (String)lv.getItemAtPosition(pos);
        new GetMakes().execute(selected);
        CarSearch.showFrag("mk");
    }

    /**
     * GetMakes calls the edmunds.com API using the Java net package
     * handles responses depending on HTTP codes then if all is well
     * uses a buffered reader to read in the returned data into a string
     * to pass on to the onPostExecute method which parses the data using
     * the json library to get the correct data and display it. This class
     * extends the AsyncTask class to avoid freezing up the UI
     */

    private class GetMakes extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {//create API url using year to find makes in that year
                URL url = new URL("http://api.edmunds.com/api/vehicle/v2/makes?fmt=json&year=" + params[0] + "&api_key=jskft3jqdm9wrhvj3fba2qwg");
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Accept", "application/json");

                if(c.getResponseCode() != 200){//handle failure
                    throw new RuntimeException("HTTP failed with error: " + c.getResponseCode() +" Request: " + url.toString());
                }

                //buffered reader to read stream
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
                JSONObject json = new JSONObject(ret);//create json object with data

                JSONArray a = json.getJSONArray("makes");//retrieve makes array
                String makes = "";
                for(int i = 0; i<a.length(); i++){
                    makes += a.getJSONObject(i).getString("name");//get names of makes
                    makes+="^";
                }

                if(!makes.isEmpty()) {//handle if year doesnt have makes
                    CarSearch.mk.setList(makes);
                }else{
                    CarSearch.mk.setList("None");
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
