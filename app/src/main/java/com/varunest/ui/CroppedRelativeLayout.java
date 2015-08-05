package com.varunest.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class CroppedRelativeLayout extends RelativeLayout {

    Path clipPath=null;

    private Region.Op operation = Region.Op.INTERSECT;
    private float centerX, centerY;
    public CroppedRelativeLayout(Context context) {
        super(context);
    }

    public CroppedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CroppedRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    float radius=0;
    public float fraction = 0;

    @Override
    public void draw(Canvas canvas) {
        try{
            if(clipPath==null){
                radius = getWidth()>getHeight()?getWidth():getHeight();
                radius*=fraction;
                radius*=1.5f;
                clipPath = new Path();
                clipPath.addCircle(centerX, centerY, radius, Path.Direction.CW);
            }
            canvas.clipPath(clipPath, operation);
            super.draw(canvas);
        }catch(Exception e){
            super.draw(canvas);
        }



    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        clipPath=null;
        invalidate();
    }

    public void setCenter(float x, float y) {
        centerX = x;
        centerY = y;
    }

    public void setFraction(float fraction){
        this.fraction = fraction;
        this.clipPath = null;
        invalidate();
    }
    public void setOperation (Region.Op operation) {
        this.operation = operation;
    }
}
