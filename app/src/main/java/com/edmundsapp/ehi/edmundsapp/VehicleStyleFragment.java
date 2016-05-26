package com.edmundsapp.ehi.edmundsapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.Locale;
import java.util.jar.Manifest;

/***
 * Fragment class for handling vehicle style selection
 *  Extends a ListFragment for easy override of list functions on click
 *  Also allows for dynamic updating of list when needed
 *  Author: James Bradshaw
 *  Date: 5/24/16
 */
public class VehicleStyleFragment extends ListFragment{

    ListView lv;
    ArrayAdapter<String> ad;

    String[] styles = {"Loading"};//initial styles array
    String selected; //style selected
    String ids; //style ids

    @Nullable
    @Override //Inflate option_select_fragment when view is created
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.option_select_fragment, container, false);
    }

    @Override //create list from styles array when activity created
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        ad = new ArrayAdapter<String>(getActivity(), R.layout.style_list, R.id.style_row, styles);
        setListAdapter(ad);
    }



    @Override //handle click of an item on list
    public void onListItemClick(ListView lv, View v, int pos, long id){
        v.setSelected(true);
        selected = (String) lv.getItemAtPosition(pos); //set selected item for later use

        //split ids for access to the one needed also turn into array list for removing bad data
        List<String> t = new ArrayList(Arrays.asList(ids.split("\\^")));
        t.removeAll(Arrays.asList("", " ", null));//remove bad data
        CarSearch.showFrag("dt");//unhide details frag
        new GetDetails().execute(t.get(pos));//call for async detail collection using style id
    }

    //sets list of styles when new model selected
    public void setList(String m){
        List<String> l = new ArrayList(Arrays.asList(m.split("\\^")));
        l.removeAll(Arrays.asList(""," ", null));
        ad = new ArrayAdapter<String>(getActivity(), R.layout.style_list, R.id.style_row, l);
        setListAdapter(ad);
    }

    //set ids string
    public void setIds(String i){
        ids = i;
    }

    /**
     * GetDetails calls the edmunds.com API using the Java net package
     * handles responses depending on HTTP codes then if all is well
     * uses a buffered reader to read in the returned data into a string
     * to pass on to the onPostExecute method which parses the data using
     * the json library to get the correct data and display it. This class
     * extends the AsyncTask class to avoid freezing up the UI
     */
    private class GetDetails extends AsyncTask<String, Void, String> {

        boolean n = true;

        @Override
        protected String doInBackground(String... params) {
            try {//attempt to connect to the API through the total market value service to find the MSRP

                URL url = new URL("http://api.edmunds.com/v1/api/tmv/tmvservice/calculatenewtmv?styleid=" + params[0] + "&zip=84124&fmt=json&api_key=jskft3jqdm9wrhvj3fba2qwg");

                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Accept", "application/json");

                //if HTTP response is not returned as OK(code 200)
                if(c.getResponseCode() != 200){
                    //if current year selected is '17
                    if(CarSearch.yr.selected.equals("2017") && c.getResponseCode() == 400){
                        return "UNK";//vehicle info not released yet
                    }else {//if not current year bad request throw error

                        url = new URL("https://api.edmunds.com/v1/api/tmv/tmvservice/calculatetypicallyequippedusedtmv?styleid="+params[0]+"&zip=84124&fmt=json&api_key=jskft3jqdm9wrhvj3fba2qwg");
                        c = (HttpURLConnection) url.openConnection();
                        c.setRequestMethod("GET");
                        c.setRequestProperty("Accept", "application/json");
                        n = false;
                        if(c.getResponseCode() != 200) {
                            throw new RuntimeException("HTTP failed with error: " + c.getResponseCode() + " Request: " + url.toString());
                        }
                    }
                }
                //read input stream
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));

                String response;
                String ret = "";
                while((response = br.readLine()) != null) {
                    ret += response + " ";
                }
                return ret;
            }catch(MalformedURLException e){//handle exceptions and print appropriate messages
                Log.v("TAG", "malformed URL exception");
            }catch(IOException e){
                Log.v("TAG", "IO exception");
            }
            return "none";
        }

        @Override //after execution finished parse returned JSON in post execute
        protected void onPostExecute(String ret){
            if(ret.equals("UNK")) {//if info unknown display
                CarSearch.dt.setName("Car details unavailable.");
                CarSearch.dt.setPrice("MSRP:\n Unknown");
            }else {
                if(n) {
                    try {
                        JSONObject json = new JSONObject(ret);//create json object for reading

                        String price = json.getJSONObject("tmv").getJSONObject("nationalBasePrice").getString("baseMSRP");//retrieve MSRP from object

                        CarSearch.dt.setName("Vehicle Selected:\n" + CarSearch.yr.selected + " " + CarSearch.mk.selected + " " + CarSearch.md.selected + " " + CarSearch.st.selected);//display selected vehicles name
                        CarSearch.dt.setPrice("MSRP:\n $" + price);//display MSRP

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        JSONObject json = new JSONObject(ret);//create json object for reading

                        String price = json.getJSONObject("tmv").getJSONObject("nationalBasePrice").getString("usedTmvRetail");//retrieve MSRP from object

                        CarSearch.dt.setName("Vehicle Selected:\n" + CarSearch.yr.selected + " " + CarSearch.mk.selected + " " + CarSearch.md.selected + " " + CarSearch.st.selected);//display selected vehicles name
                        CarSearch.dt.setPrice("True Market Value:\n $" + price);//display MSRP

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
