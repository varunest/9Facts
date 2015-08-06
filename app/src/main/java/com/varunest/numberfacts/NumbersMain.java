package com.varunest.numberfacts;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Region;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.varunest.interfaces.ModelToController;
import com.varunest.receivers.DailyBroadcastReciever;
import com.varunest.ui.Animator;
import com.varunest.ui.CroppedRelativeLayout;
import com.vinayrraj.flipdigit.lib.Flipmeter;

import io.fabric.sdk.android.Fabric;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.drakeet.materialdialog.MaterialDialog;


public class NumbersMain extends FragmentActivity implements ModelToController {
    PlaceholderFragment placeholderFragment;
    static Animator animator;
    static Model model;
    static Fact factStatic;
    private static String query = "";
    private static boolean isLoading = false;
    private static SharedPreferences sharedPref;
    private static Activity activity;
    private static String tag = "DEBUG_NUMBERSMAIN";

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.activityPaused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.activityPaused();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        MyApplication.activityResumed();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_numbers_main);

        if (savedInstanceState == null) {
            animator = new Animator(this);
            model = new Model(this);
            placeholderFragment = new PlaceholderFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, placeholderFragment)
                    .commit();

        }

        activity = this;
        sharedPref = this.getSharedPreferences(getString(R.string.SP_shared_prefereces), Context.MODE_PRIVATE);
        Parse.initialize(this, "mcvLWqaV2Q5TIEZXuJZP8f1yA8G3C0xZh7ykx9xR", "QcaWoABTCGPTa43tNxqZ4tZUar3b9deddSQEI6Wa");
        setBroadcastReceiver();
        checkIfThereIsMessage();
    }



    private void checkIfThereIsMessage() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!sharedPref.getString(getString(R.string.SP_application_message), "").isEmpty()) {
                    Log.d(tag, "Passed the check for activity Message");
                    showDialogBox();
                }
            }
        };

        handler.postDelayed(runnable, 13000);
    }

    private void showDialogBox() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(activity);

                mMaterialDialog.setTitle("Greetings")
                .setMessage(sharedPref.getString(activity.getString(R.string.SP_application_message),""))
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!sharedPref.getString(activity.getString(R.string.SP_url_link), "").isEmpty()) {
                            String url = sharedPref.getString(activity.getString(R.string.SP_url_link), "");
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(activity.getString(R.string.SP_application_message), "");
                        editor.commit();
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(activity.getString(R.string.SP_application_message), "");
                        editor.commit();
                        mMaterialDialog.dismiss();
                    }
                });

        mMaterialDialog.show();
    }

    private void setBroadcastReceiver() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(this, DailyBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                4*1000*60*60,
                pendingIntent);
    }

    @Override
    public void processingHttpRequest() {
        isLoading = true;
        animator.loadingAnimation(placeholderFragment.getFactsButton);
    }

    @Override
    public void onSuccess(Fact fact) {
        factStatic = fact;
        if (fact.getFactType().equals("date")){
            animator.animateFlipmeterNumbers(placeholderFragment.flipMeter,
                    String.valueOf(fact.getFactYear()));
        } else {
            animator.animateFlipmeterNumbers(placeholderFragment.flipMeter,
                    String.valueOf(fact.getFactNumber()));
        }


        Thread thread = new Thread() {
            @Override
            public void run () {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    isLoading = false;
                }

            }
        };
        thread.start();
        animator.showFactsAnimation(placeholderFragment.croppedLayout,
                placeholderFragment.getFactsButton,
                placeholderFragment.radioGroup,
                placeholderFragment.backButton,
                placeholderFragment.factText,
                placeholderFragment.shareButton);

        setFact(fact);
    }

    private void setFact(Fact f) {
        switch (f.getFactType()) {
            case "date" :
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-");
                String formattedDate = df.format(c.getTime());
                if (f.getFactYear() != 0)
                    placeholderFragment.factText.setText("Date: "+formattedDate+f.getFactYear()+"\r\n\r\n"+f.getFact());
                else
                    placeholderFragment.factText.setText("Date: "+formattedDate+"-Unknown\r\n\r\n"+f.getFact());
                break;

            case "year" :
                placeholderFragment.factText.setText("Date: "+f.getFactDate()+"\r\n\r\n"+f.getFact());
                break;

            default :
                placeholderFragment.factText.setText(f.getFact());
                break;
        }

    }

    @Override
    public void onFailure() {
        isLoading = false;
        animator.stopLoadingAnim(placeholderFragment.getFactsButton);
        Toast.makeText(this, getResources().getString(R.string.failure), Toast.LENGTH_LONG).show();
    }


    public void backButtonPress (View backButtonView) {
        animator.showBackButtonPressAnimation(backButtonView,
                placeholderFragment.croppedLayout,
                placeholderFragment.radioGroup,
                placeholderFragment.getFactsButton,
                placeholderFragment.factText,
                placeholderFragment.shareButton);
    }

    public void onShareButtonPress(View shareButtonView) {
        animator.shareButtonPressAnimation(placeholderFragment.shareButton);
        String textBody;

        switch (factStatic.getFactType()) {
            case "date":
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-");
                String formattedDate = df.format(c.getTime());
                if (factStatic.getFactYear() != 0)
                    textBody = "Date: " + formattedDate + factStatic.getFactYear() + "\r\n\r\n" + factStatic.getFact();
                else
                    textBody = "Date: " + formattedDate + "-Unknown\r\n\r\n" + factStatic.getFact();
                break;

            case "year":
                textBody = "Date: " + factStatic.getFactDate() + " " + factStatic.getFactNumber() + "\r\n\r\n" + factStatic.getFact();
                break;

            default:
                textBody = "Number : " + factStatic.getFactNumber() + "\r\n\r\n" + factStatic.getFact();
                break;
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, textBody+"\r\n\r\nhttps://play.google.com/store/apps/details?id="+activity.getPackageName());

        startActivity(Intent.createChooser(i, getResources().getString(R.string.share_using)));
    }

    public void getFacts (final View getFactsButtonView) {
        if (!isLoading) {
            if(placeholderFragment.radioGroup.getCheckedRadioButtonId() == R.id.radioButton_random_fact) {
                model.apiCall("",Constants.TYPE_RANDOM);
            }
            else if (placeholderFragment.radioGroup.getCheckedRadioButtonId() == R.id.radioButton_daily_fact){
                Calendar c = Calendar.getInstance();
                model.apiCall(String.valueOf(c.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(c.get(Calendar.MONTH)+1), Constants.TYPE_DATE);
            }else {
                animator.animateShowNumberInput(getFactsButtonView,
                        placeholderFragment.numberInput,
                        placeholderFragment.blurBg,
                        getApplicationContext(), getFactTypeSelected());

                placeholderFragment.numberInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                        Log.d(tag, "NumberInput TextView Listener callback");
                        Log.d(tag, String.valueOf(actionId));
                        if (actionId == EditorInfo.IME_NULL
                                || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                            Log.d(tag, "NumberInput TextView Listener IME_ACTION_DONE event");

                            animator.animateHideNumberInput(getFactsButtonView,
                                    placeholderFragment.numberInput,
                                    placeholderFragment.blurBg,
                                    getApplicationContext(),
                                    false);
                            query = textView.getText().toString();
                            if (query != null && !query.isEmpty()) {
                                if (Utilities.getSharedInstance().isNetworkAvailable(getApplicationContext())){
                                    model.apiCall(query, getFactTypeSelected());
                                }
                            } else {
                                Toast.makeText(placeholderFragment.getActivity(), getResources().getString(R.string.invalid_input), Toast.LENGTH_SHORT);
                            }

                        }
                        return false;
                    }
                });

                placeholderFragment.blurBg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        animator.animateHideNumberInput(getFactsButtonView,
                                placeholderFragment.numberInput,
                                placeholderFragment.blurBg,
                                getApplicationContext(),
                                true);
                    }
                });
            }
        }
    }

    private String getFactTypeSelected() {
        switch (placeholderFragment.radioGroup.getCheckedRadioButtonId()){
            case R.id.radioButton_date_fact:
                return Constants.TYPE_YEAR;

            case R.id.radioButton_math_fact:
                return Constants.TYPE_MATH;

            case R.id.radioButton_random_fact:
                return Constants.TYPE_RANDOM;

            case R.id.radioButton_daily_fact:
                return Constants.TYPE_DATE;
        };

        return null;
    }

    public String getQuery() {
        return query;
    }

    /**
     * A placeholder fragment containing a main view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Flipmeter flipMeter;
        private CroppedRelativeLayout fullCrop;
        private TextView titleText, subtitleText;
        private CroppedRelativeLayout croppedLayout;
        private CheckBox dailyBtn;
        private RadioButton yearBtn, mathBtn, randomBtn, todayBtn;
        private RadioGroup radioGroup;
        private View blurBg;
        private EditText numberInput;
        private TextView factText;
        private ImageView getFactsButton;
        private ImageView backButton;
        private Button shareButton;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_numbers_main, container, false);
            initViews(rootView);
            animator.onCreateAnimation(titleText, subtitleText, fullCrop, randomBtn, mathBtn, todayBtn, yearBtn, getFactsButton, croppedLayout);
            return rootView;
        }

        private void initViews(View rootView) {
            flipMeter = (Flipmeter) rootView.findViewById(R.id.Flipmeter);
            animator.animateFlipmeterNumbers(flipMeter, null);
            croppedLayout = (CroppedRelativeLayout) rootView.findViewById(R.id.custom_layout);
            croppedLayout.setOperation(Region.Op.INTERSECT);
            numberInput = (EditText) rootView.findViewById(R.id.number_input);
            blurBg = rootView.findViewById(R.id.blur_bg);
            getFactsButton = (ImageView) rootView.findViewById(R.id.get_facts_button);
            dailyBtn = (CheckBox) rootView.findViewById(R.id.daily_checkbox);
            dailyBtn.setChecked(sharedPref.getBoolean(getString(R.string.SP_allow_daily_facts), true));
            dailyBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.SP_allow_daily_facts), isChecked);
                    editor.commit();
                }
            });
            yearBtn = (RadioButton) rootView.findViewById(R.id.radioButton_date_fact);
            mathBtn = (RadioButton) rootView.findViewById(R.id.radioButton_math_fact);
            randomBtn = (RadioButton) rootView.findViewById(R.id.radioButton_random_fact);
            todayBtn = (RadioButton) rootView.findViewById(R.id.radioButton_daily_fact);
            titleText = (TextView) rootView.findViewById(R.id.title_text);
            fullCrop = (CroppedRelativeLayout) rootView.findViewById(R.id.full_crop);
            subtitleText = (TextView) rootView.findViewById(R.id.subtitle_text);
            radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
            backButton = (ImageView) rootView.findViewById(R.id.back_button);
            factText = (TextView) rootView.findViewById(R.id.fact_text);
            shareButton = (Button) rootView.findViewById(R.id.share_button);
        }
    }
}
