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

public class VehicleStyleFragment extends ListFragment{

    ListView lv;
    ArrayAdapter<String> ad;
    String[] models = {"Loading"};
    String selected;
    String ids;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.option_select_fragment
                , container, false);
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
        selected = (String) lv.getItemAtPosition(pos);
        List<String> t = new ArrayList<String>(Arrays.asList(ids.split("\\^")));
        t.removeAll(Arrays.asList("", " ", null));
        CarSearch.showFrag("dt");
        new GetTrims().execute(t.get(pos));
    }

    public void setList(String m){
        List<String> l = new ArrayList<String>(Arrays.asList(m.split("\\^")));
        l.removeAll(Arrays.asList(""," ", null));
        ad = new ArrayAdapter<String>(getActivity(), R.layout.style_list, R.id.style_row, l);
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
                    if(CarSearch.yr.selected.equals("2017") && c.getResponseCode() == 400){
                        return "UNK";
                    }else {
                        throw new RuntimeException("HTTP failed with error: " + c.getResponseCode() + " Request: " + url.toString());
                    }
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
            if(ret.equals("UNK")) {
                CarSearch.dt.setName("Car details unavailable.");
                CarSearch.dt.setPrice("MSRP:\n Unknown");
            }else {
                try {
                    JSONObject json = new JSONObject(ret);

                    String price = json.getJSONObject("tmv").getJSONObject("nationalBasePrice").getString("baseMSRP");

                    CarSearch.dt.setName("Vehicle Selected:\n"+CarSearch.yr.selected +" "+CarSearch.mk.selected+" "+CarSearch.md.selected+" " + CarSearch.st.selected);
                    CarSearch.dt.setPrice("MSRP:\n $" + price);

                } catch (JSONException e) {

                }
            }
        }
    }

}
