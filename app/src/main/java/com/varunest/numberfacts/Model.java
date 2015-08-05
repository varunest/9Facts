package com.varunest.numberfacts;

import android.os.AsyncTask;
import android.util.Log;

import com.varunest.interfaces.ModelToController;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Varun on 26/03/15.
 */
public class Model {

    private final static String API_KEY = "VNw2tZXk8qmshS2Id6Ld0nj2mhJap1euha4jsnlGJiOXyY1JHG";
    private final static String tag = "DEBUG_MODEL";
    private ModelToController controllerCallback;

    public Model(ModelToController controllerCallback) {
        this.controllerCallback = controllerCallback;
    }

    public void apiCall (String query, String type) {
        HttpGetAsyncTask apiTask = new HttpGetAsyncTask();
        apiTask.execute(query, type);
    }

    public boolean validateInput (String input) {
        return true;
    }

    /* Async task to get api response using query and type */
    public class HttpGetAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            controllerCallback.processingHttpRequest();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Log.d(tag, result);
                try {
                    controllerCallback.onSuccess(new Fact(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag, "Json Exception : "+result);
                }
            } else {
                controllerCallback.onFailure();
                Log.e(tag, "Failed to retrieve data");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String query = params[0];
            String type = params[1];
            String response = null;
            String url = "https://numbersapi.p.mashape.com/"+query+type;
            Log.d(tag, url);

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 8000);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("X-Mashape-Key", API_KEY);
            httpGet.setHeader("Accept","text/plain");



            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                InputStream responseStream = httpResponse.getEntity().getContent();
                BufferedReader br = null;
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    br = new BufferedReader(new InputStreamReader(responseStream));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    response = sb.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }
    }
}
