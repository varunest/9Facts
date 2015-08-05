package com.varunest.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.parse.ConfigCallback;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.varunest.interfaces.ModelToController;
import com.varunest.numberfacts.Constants;
import com.varunest.numberfacts.Fact;
import com.varunest.numberfacts.Model;
import com.varunest.numberfacts.MyApplication;
import com.varunest.numberfacts.NotificationHandler;
import com.varunest.numberfacts.R;
import com.varunest.numberfacts.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Varun on 28/03/15.
 */
public class DailyBroadcastReciever extends BroadcastReceiver implements ModelToController{

    private static final String tag = "DEBUG_BROADCASTRECEIVER";
    private Context context;
    SharedPreferences sharedPrefences;
    SharedPreferences.Editor editor;
    Model model;



    @Override
    public void onReceive(final Context context, final Intent intent) {
        model = new Model(this);
        this.context = context;
        Log.d(tag, "DailyBroadCastReciever Called by alarm Manager");

        sharedPrefences = context.getSharedPreferences(context.getString(R.string.SP_shared_prefereces), Context.MODE_PRIVATE);
        editor = sharedPrefences.edit();
        if (Utilities.getSharedInstance().isNetworkAvailable(context)) {
            parseConfig();  // get Config file from the parse

            if (sharedPrefences.getBoolean(context.getString(R.string.SP_allow_daily_facts), true)) {
                if (!MyApplication.isActivityVisible()) {
                  /* call the api to fetch the date fact */
                    Calendar c = Calendar.getInstance();
                    model.apiCall(String.valueOf(c.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(c.get(Calendar.MONTH)+1), Constants.TYPE_DATE);
                }
            }
        } else {
            new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        Thread.sleep(1000 * 60 * 60);
                        onReceive(context, intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void parseConfig() {
        Parse.initialize(context, "mcvLWqaV2Q5TIEZXuJZP8f1yA8G3C0xZh7ykx9xR", "QcaWoABTCGPTa43tNxqZ4tZUar3b9deddSQEI6Wa");
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig parseConfig, ParseException e) {
                if (e == null) {
                    Log.d(tag, "Config Fetched");
                } else {
                    Log.e(tag, "Using cached Config");
                    parseConfig = ParseConfig.getCurrentConfig();
                }
                if (parseConfig != null) {

                    if (parseConfig.getBoolean("if_application_message")) {
                        if (sharedPrefences.getInt(context.getString(R.string.SP_application_message_counter), 0) < parseConfig.getInt("application_message_counter")){
                            editor.putString(context.getString(R.string.SP_application_message), parseConfig.getString("application_message"));
                            editor.putInt(context.getString(R.string.SP_application_message_counter), parseConfig.getInt("application_message_counter"));
                            if (parseConfig.getBoolean("if_url")) {
                                editor.putString(context.getString(R.string.SP_url_link), parseConfig.getString("url"));
                            } else {
                                editor.putString(context.getString(R.string.SP_url_link), "");
                            }
                        }
                    }
                    if (parseConfig.getBoolean("if_notification_message")) {
                        if (sharedPrefences.getInt(context.getString(R.string.SP_notification_message_counter), 0) < parseConfig.getInt("notification_message_counter")) {
                        /* Call nofification handler to show the notification message here */
                            editor.putInt(context.getString(R.string.SP_notification_message_counter), parseConfig.getInt("notification_message_counter"));
                            new NotificationHandler(context, "Greeting", parseConfig.getString("notification_message"), 0);
                        }
                    }
                    editor.commit();
                }

            }
        });
    }

    @Override
    public void processingHttpRequest() {

    }

    @Override
    public void onSuccess(Fact fact) {
        /* send notification handler the fact to display */
        String title;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-");
        String formattedDate = df.format(c.getTime());
        if (fact.getFactYear() != 0)
            title = "Date: "+formattedDate+fact.getFactYear();
        else
            title = "Date: "+formattedDate+"-Unknown";

        new NotificationHandler(context, title, fact.getFact() , 1);
    }

    @Override
    public void onFailure() {
        Toast.makeText(context, context.getResources().getString(R.string.failure), Toast.LENGTH_LONG).show();
    }
}
