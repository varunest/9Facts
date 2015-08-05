package com.varunest.interfaces;

import com.varunest.numberfacts.Fact;

/**
 * Created by Varun on 26/03/15.
 */
public interface ModelToController {

    public void processingHttpRequest ();
    public void onSuccess(Fact fact);
    public void onFailure ();
}
