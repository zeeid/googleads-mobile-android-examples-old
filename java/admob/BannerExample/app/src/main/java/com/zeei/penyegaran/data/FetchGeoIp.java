package com.zeei.penyegaran.data;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.zeei.penyegaran.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchGeoIp extends AsyncTask<Void,Void,Void> {
    private String data="";
    private String dataAll="";
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://ip-api.com/json/?fields=status,message,country,countryCode,region,regionName,city,zip,lat,lon,timezone,offset,currency,isp,org,as,asname,reverse,mobile,proxy,hosting,query");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            data = stringBuilder.toString();

            JSONObject JO = new JSONObject(data);
            dataAll = "IP : "+JO.getString("query")+"\n"+
                    "Country Code : "+JO.getString("countryCode")+"\n"+
                    "Country Name : "+JO.getString("country")+"\n"+
                    "Region Code : "+JO.getString("region")+"\n"+
                    "Region Name : "+JO.getString("regionName")+"\n"+
                    "City : "+JO.getString("city")+"\n"+
                    "Zip Code : "+JO.getString("zip")+"\n"+
                    "Time Zone : "+JO.getString("timezone")+"\n"+
                    "Latitude : "+JO.getString("lat")+"\n"+
                    "Longitude : "+JO.getString("lon")+"\n"+
                    "mobile : "+JO.getBoolean("mobile")+"\n"+
                    "proxy : "+JO.getBoolean("proxy")+"\n"+
                    "hosting : "+JO.getBoolean("hosting")+"\n"+
                    "ISP : "+JO.getString("isp")+"\n\n\n";
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.data.setText("Loading...");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (dataAll.isEmpty()) {
            MainActivity.data.setText("Tidak dapat memuat data. Periksa koneksi internet Anda.");
        } else {
            Log.d("IP1", "onPostExecute: " + data);
            Log.d("IP2", "onPostExecute: " + dataAll);
            MainActivity.data.setText(this.dataAll);
        }
    }
}
