package com.varunest.numberfacts;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Varun on 28/03/15.
 */
public class Fact {

    private String factText;
    private String factType;
    private String factDate;

    private int factNumber;
    private boolean factFound;
    private int factYear;

    private JSONObject jsonObj;

    public Fact(String json) throws JSONException {
        jsonObj = new JSONObject(json);
        factText = jsonObj.getString("text");
        factNumber = jsonObj.getInt("number");
        factType = jsonObj.getString("type");
        factFound = jsonObj.getBoolean("found");
        try {
            if (factType.equals("year"))
                factDate = jsonObj.getString("date");
            if (factType.equals("date"))
                factYear = jsonObj.getInt("year");
        } catch (JSONException e) {
            factDate = "Not Known";
            factYear = 0;
        }
    }

    public String getFact() {
        return factText.substring(0, 1).toUpperCase() + factText.substring(1);

    }

    public String getFactType() {
        return this.factType;
    }

    public String getFactDate() {
        return this.factDate;
    }

    public boolean factFound(){
        return this.factFound;
    }

    public int getFactNumber (){
        return this.factNumber;
    }

    public int getFactYear () {
        return this.factYear;
    }
}
