package com.varunest.ui;


import android.content.Context;
import android.graphics.Region;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.google.android.gms.ads.AdView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.varunest.numberfacts.R;
import com.vinayrraj.flipdigit.lib.Flipmeter;

/**
 * Created by Varun on 23/03/15.
 */
public class Animator {
    private static ObjectAnimator rotateAnimator;
    private float screenWidth, screenHeight;
    private final static String tag = "DEBUG_ANIMATOR";
    private Context context;

    public Animator (Context context) {
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    public void onCreateAnimation (final View titleText, final View subtitleText, final CroppedRelativeLayout fullCrop, final View randomBtn, final View mathBtn, final View todayBtn, final View yearBtn, final View getBtn, final CroppedRelativeLayout halfCrop) {

        getBtn.setAlpha(0f);
        randomBtn.setAlpha(0f);
        mathBtn.setAlpha(0f);
        todayBtn.setAlpha(0f);
        yearBtn.setAlpha(0f);
        subtitleText.setAlpha(0f);
        fullCrop.setFraction(10f);
        fullCrop.setOperation(Region.Op.INTERSECT);
        fullCrop.setCenter(screenWidth/2, screenHeight);
        titleText.setVisibility(View.INVISIBLE);

        ValueAnimator colorAnim = ValueAnimator.ofInt(context.getResources().getColor(R.color.dark_pink), context.getResources().getColor(R.color.pink));
        colorAnim.setDuration(2000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fullCrop.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        colorAnim.start();

        final Handler handler = new Handler();
        Runnable runnableTitle = new Runnable() {
            @Override
            public void run() {
                SpringAnimator(titleText, 1, 0, true);
                ObjectAnimator anim = ObjectAnimator.ofFloat(subtitleText, "alpha", 0f, 1f);
                anim.setDuration(1000);
                anim.setStartDelay(500);
                anim.setInterpolator(new DecelerateInterpolator());
                anim.start();
            }
        };
        Runnable runnableCrop = new Runnable(){
            @Override
            public void run() {
                ObjectAnimator fullCropAnim = ObjectAnimator.ofFloat(fullCrop, "fraction", 1.6f, 0f);
                fullCropAnim.setDuration(800);
                ObjectAnimator getBtnY = ObjectAnimator.ofFloat(getBtn, "translationY", 500, 0);
                ObjectAnimator randomBtnY = ObjectAnimator.ofFloat(randomBtn, "translationY", 10, 0).setDuration(100);
                ObjectAnimator dateBtnY = ObjectAnimator.ofFloat(yearBtn, "translationY", 10, 0).setDuration(100);
                ObjectAnimator mathBtnY = ObjectAnimator.ofFloat(mathBtn, "translationY", 10, 0).setDuration(100);
                ObjectAnimator funBtnY = ObjectAnimator.ofFloat(todayBtn, "translationY", 10, 0).setDuration(100);

                AnimatorSet animSet = new AnimatorSet();
                animSet.play(getBtnY).after(fullCropAnim);
                animSet.play(randomBtnY).after(funBtnY);
                animSet.play(dateBtnY).after(mathBtnY);
                animSet.play(mathBtnY).after(randomBtnY);
                animSet.play(funBtnY).after(fullCropAnim);
                animSet.playTogether(randomBtnY, ObjectAnimator.ofFloat(randomBtn, "alpha", 0f, 1f));
                animSet.playTogether(mathBtnY, ObjectAnimator.ofFloat(mathBtn, "alpha", 0f, 1f));
                animSet.playTogether(funBtnY, ObjectAnimator.ofFloat(todayBtn,"alpha",0f,1f));
                animSet.playTogether(dateBtnY, ObjectAnimator.ofFloat(yearBtn,"alpha",0f,1f));
                animSet.playTogether(getBtnY, ObjectAnimator.ofFloat(getBtn, "alpha", 0f, 1f));
                animSet.start();
            }
        };

        handler.postDelayed(runnableTitle, 1000);
        handler.postDelayed(runnableCrop, 1800);
    }

    public void animateFlipmeterNumbers(final Flipmeter flipMeter, final String query) {

                if (query != null && !query.isEmpty()){
                    flipMeter.setValue(Integer.parseInt(query), true);
                } else {
                    flipMeter.setValue(999999, true);
                }
    }

    public void animateShowNumberInput(View button, final TextView numberInput, View blurBg, Context context, String factType) {

        SpringAnimator(numberInput, 2, 0, true);

        numberInput.setFocusableInTouchMode(true);
        numberInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(numberInput, InputMethodManager.SHOW_IMPLICIT);

        Log.d(tag,"Fact Type : " + factType);
        blurBg.setVisibility(View.VISIBLE);
        if (factType.equals("/year?fragment=true&json=true")) {
            numberInput.setHint(context.getString(R.string.hint_year));
        } else if (factType.equals("/math?fragment=true&json=true")) {
            numberInput.setHint(context.getString(R.string.hint_number));
        }
        AnimatorSet animSet = new AnimatorSet();

        animSet.playTogether(
                ObjectAnimator.ofFloat(button, "rotation", 0 , 360),
                ObjectAnimator.ofFloat(blurBg, "alpha", 0, .7f)
        );

        animSet.start();


    }

    public void animateHideNumberInput(View button, TextView numberInput, View blurBg, Context context, boolean force){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(numberInput.getWindowToken(), 0);
        if (force) {
            button.setRotation(0);
            button.setAlpha(1);
            numberInput.setVisibility(View.GONE);
            blurBg.setAlpha(0f);
            blurBg.setVisibility(View.GONE);
        } else {
            AnimatorSet animSet2 = new AnimatorSet();
            animSet2.playTogether(
                    ObjectAnimator.ofFloat(button, "rotation", button.getRotation() , 0),
                    ObjectAnimator.ofFloat(blurBg, "alpha", .4f, 0)
            );
            animSet2.start();
            SpringAnimator(numberInput, 0, 2, false);
        }

    }

    public void loadingAnimation(final View view) {
        view.setVisibility(View.VISIBLE);
        rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.setDuration(400);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.start();
        SpringAnimator(view, 0, .2f, true);
    }

    public void showFactsAnimation(final CroppedRelativeLayout croppedLayout, final View factButton, final View radioGroup, final View backButton, final View factText, final View shareButton) {
        rotateAnimator.cancel();
        croppedLayout.setOperation(Region.Op.INTERSECT);
        croppedLayout.setCenter(factButton.getLeft() + factButton.getWidth()/2, factButton.getTop() - 1.3f * factButton.getHeight());

        ObjectAnimator expandViewAnim = ObjectAnimator.ofFloat(croppedLayout, "fraction", 0f, 1.2f);
        expandViewAnim.setInterpolator(new AccelerateInterpolator(1.5f));
        expandViewAnim.setDuration(500);

        ValueAnimator colorAnim = ValueAnimator.ofInt(context.getResources().getColor(R.color.pink), context.getResources().getColor(R.color.violet));
        colorAnim.setDuration(2000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                croppedLayout.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        colorAnim.start();

        ObjectAnimator fadeOutAnim = ObjectAnimator.ofFloat(factButton, "alpha", 1f, 0f);
        fadeOutAnim.setDuration(200);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(expandViewAnim).with(fadeOutAnim);
        animSet.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animator) {
            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animator) {
                showFact(factButton, radioGroup, croppedLayout, backButton, factText, shareButton);
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animator) {
            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animator) {
            }
        });
        animSet.start();
}


 public void showBackButtonPressAnimation(final View backButtonView, final CroppedRelativeLayout halfCrop, final View radioGroup, final View getFactButton, final View factText, final View shareButton) {
     AnimatorSet animSet = new AnimatorSet();
     halfCrop.bringToFront();
     animSet.playTogether(ObjectAnimator.ofFloat(backButtonView, "translationX", 0, -150),
             ObjectAnimator.ofFloat(backButtonView, "translationY", 0, -150),
             ObjectAnimator.ofFloat(backButtonView, "scaleX", 1, 1.3f),
             ObjectAnimator.ofFloat(backButtonView, "scaleY", 1, 1.3f));
     animSet.setInterpolator(new DecelerateInterpolator(2.5f));
     animSet.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
         @Override
         public void onAnimationStart(com.nineoldandroids.animation.Animator animator) {
         }

         @Override
         public void onAnimationEnd(com.nineoldandroids.animation.Animator animator) {
            backButtonView.setVisibility(View.GONE);
            backButtonExpand(halfCrop, backButtonView, getFactButton, radioGroup, factText, shareButton);
         }

         @Override
         public void onAnimationCancel(com.nineoldandroids.animation.Animator animator) {
         }
         @Override
         public void onAnimationRepeat(com.nineoldandroids.animation.Animator animator) {

         }
     });
    animSet.start();
 }

    private void backButtonExpand(final CroppedRelativeLayout halfCrop, View backButtonView, final View getFactButton, final View radioGroup, final View factText, final View shareButton) {
        halfCrop.setCenter(backButtonView.getX() + backButtonView.getWidth()/2, backButtonView.getY() - 1.7f * backButtonView.getHeight());
        halfCrop.setOperation(Region.Op.INTERSECT);
        halfCrop.setBackgroundColor(context.getResources().getColor(R.color.violet));


        AnimatorSet expandAnimSet = new AnimatorSet();

        ValueAnimator colorAnim = ValueAnimator.ofInt(context.getResources().getColor(R.color.dark_violet), context.getResources().getColor(R.color.pink));
        colorAnim.setDuration(1000);
        colorAnim.setInterpolator(new AccelerateInterpolator(2.5f));
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                halfCrop.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        colorAnim.start();

        ObjectAnimator expandAnim = ObjectAnimator.ofFloat(halfCrop, "fraction", 0, 1.4f);
        expandAnim.setDuration(800);
        expandAnimSet.playTogether(expandAnim,
                ObjectAnimator.ofFloat(backButtonView, "alpha", 1, 0),
                ObjectAnimator.ofFloat(shareButton, "alpha", 1, 0));
        expandAnimSet.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animator) {

            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animator) {
                factText.setVisibility(View.GONE);
                factText.setScaleY(1);
                factText.setScaleX(1);
                showFrontPage(halfCrop, getFactButton, radioGroup, shareButton);
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animator) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animator) {

            }
        });
        expandAnimSet.start();
    }

    private void showFrontPage(CroppedRelativeLayout halfCrop, final View getFactsButton, View radioGroup, View shareButton) {
        shareButton.setVisibility(View.GONE);
        halfCrop.setCenter(screenWidth/2, screenHeight);
        halfCrop.setOperation(Region.Op.INTERSECT);
        halfCrop.setFraction(1.4f);

        radioGroup.setVisibility(View.VISIBLE);

        ObjectAnimator halfCropAnim = ObjectAnimator.ofFloat(halfCrop, "fraction", 1.4f, 0f).setDuration(480);
        halfCropAnim.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animator) {

            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animator) {
                getFactsButton.setVisibility(View.VISIBLE);
                getFactsButton.setAlpha(1);
                ViewHelper.setScaleX(getFactsButton, 1);
                ViewHelper.setRotation(getFactsButton, 0);
                ViewHelper.setScaleY(getFactsButton, 1);
                ObjectAnimator translateIn = ObjectAnimator.ofFloat(getFactsButton, "translationY", 250, 0).setDuration(300);
                translateIn.setInterpolator(new DecelerateInterpolator(1.4f));
                translateIn.start();
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animator) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animator) {

            }
        });
        halfCropAnim.start();
    }

    private void showFact(View factButton, View radioGroup, CroppedRelativeLayout croppedLayout, final View backButton, final View factText, final View shareButton) {
        croppedLayout.bringToFront();
        shareButton.setVisibility(View.VISIBLE);
        factButton.setVisibility(View.GONE);
        radioGroup.setVisibility(View.GONE);
        factText.setVisibility(View.VISIBLE);

        AnimatorSet animSet = new AnimatorSet();

        animSet.playTogether(
                ObjectAnimator.ofFloat(factText, "alpha", 0 , 1),
                ObjectAnimator.ofFloat(factText, "scaleX", 1, .9f),
                ObjectAnimator.ofFloat(factText, "scaleY", 1, .9f),
                ObjectAnimator.ofFloat(shareButton, "alpha", 0, 1),
                ObjectAnimator.ofFloat(shareButton, "scaleY", 1.1f, 1),
                ObjectAnimator.ofFloat(shareButton, "scaleX", 1.1f, 1)
                );
        animSet.setDuration(950);
        animSet.setInterpolator(new AccelerateInterpolator(2.4f));
        animSet.start();
        SpringAnimator(shareButton, 1, 0, true);

        croppedLayout.setCenter(screenWidth, screenHeight);

        ObjectAnimator cropAnim = ObjectAnimator.ofFloat(croppedLayout, "fraction", 1.2f, 0f).setDuration(700);
        cropAnim.setInterpolator(new AccelerateInterpolator(2.5f));
        cropAnim.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animator) {

            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animator) {
                backButton.setAlpha(1);
                backButton.setScaleX(1);
                backButton.setScaleY(1);
                backButton.setVisibility(View.VISIBLE);
                AnimatorSet animSet = new AnimatorSet();
                animSet.playTogether(ObjectAnimator.ofFloat(backButton, "translationX", 100, 0),
                        ObjectAnimator.ofFloat(backButton, "translationY", 100, 0)
                );
                animSet.start();
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animator) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animator) {

            }
        });
        cropAnim.start();
    }


    private void SpringAnimator (final View view, float currentValue, float endValue, final boolean visible) {
        // Create a system to run the physics loop for a set of springs.
        SpringSystem springSystem = SpringSystem.create();

        // Add a spring to the system.
        Spring spring = springSystem.createSpring();
        spring.setCurrentValue(currentValue);
        // Add a listener to observe the motion of the spring.
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                // You can observe the updates in the spring
                // state by asking its current value in onSpringUpdate.
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                view.setScaleX(scale);
                view.setScaleY(scale);
            }

            @Override
            public void onSpringAtRest(Spring spring) {
                super.onSpringAtRest(spring);
                if (!visible) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSpringEndStateChange(Spring spring) {
                super.onSpringEndStateChange(spring);
                if (visible) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
        // Set the spring in motion; moving from 0 to 1
        spring.setEndValue(endValue);
    }

    public void stopLoadingAnim(ImageView getFactsButton) {

        rotateAnimator.cancel();
        getFactsButton.setScaleX(1);
        getFactsButton.setScaleY(1);
        getFactsButton.setRotation(0);
    }

    public void shareButtonPressAnimation(Button shareButton) {
        SpringAnimator(shareButton, 1f, 0, true);
    }

    public void animateAdView(AdView adView) {
        adView.setPivotX(screenWidth/2);
        final ObjectAnimator rotateObject = ObjectAnimator.ofFloat(adView, "rotationY", 0, 360);
        rotateObject.setDuration(2500);
        rotateObject.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateObject.setStartDelay(10000);
        rotateObject.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animator) {

            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animator) {
                rotateObject.start();
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animator) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animator) {

            }
        });
        rotateObject.start();
    }
}
