package com.golwado.coronavirusscanner.Design;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.golwado.coronavirusscanner.R;

import java.util.ArrayList;

public class CustomSlider extends LinearLayout{
    private ArrayList<Slider> sliders = new ArrayList<>();
    private WindowManager windowManager;
    private float sensitivity;
    private int viewIndex = 0;
    public CustomSlider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        updateContent();
        this.setOrientation(LinearLayout.VERTICAL);
    }

    public void addSlider(Slider slider){
        this.sliders.add(slider);
        if(sliders.size() == 1) {
            updateContent();
        }
    }

    public void addSwipeHandler(WindowManager windowManager , float sensitivity){
        this.windowManager = windowManager;
        this.sensitivity = sensitivity;
        this.setOnTouchListener(new SwipeHandler());
    }

    private void updateContent(){
        try {
            this.removeAllViews();
            Slider slider = sliders.get(viewIndex);
            if(slider.label != null){
                TextView labelView = new TextView(getContext());
                labelView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT));
                labelView.setText(slider.label);
                labelView.setTextColor(getResources().getColor(R.color.whiteColor));
                labelView.setTypeface(Typeface.DEFAULT_BOLD);
                labelView.setGravity(Gravity.CENTER);
                labelView.setTextSize(15);
                this.addView(labelView);
            }
            View sliderContent = slider.content;
            sliderContent.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT));
            this.addView(sliderContent);
        } catch (IndexOutOfBoundsException e){
            TextView textView = new TextView(this.getContext());
            textView.setText("No Data Found");
            textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT));
            this.addView(textView);
        }
    }
    public class SwipeHandler implements View.OnTouchListener {
        private float xDown;
        private float xUp;
        private float minimumDistance;
        private boolean handled = true;
        public SwipeHandler(){
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            sensitivity = 1.f - sensitivity;
            minimumDistance = sensitivity * outMetrics.widthPixels;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action){
                case MotionEvent.ACTION_DOWN : {
                    xDown = xUp = event.getX();
                    handled = false;
                    return true;
                }
                case MotionEvent.ACTION_UP:{
                    xUp = event.getX();
                    if(! handled)
                        handleResult();
                }
                case MotionEvent.ACTION_MOVE: {
                    xUp = event.getX();
                    if(! handled)
                        handleResult();
                }
            }
            return false;
        }

        private void handleResult(){
            if(Math.abs(xDown - xUp) >= 35){
                if(xDown > xUp)
                    handleRightToLeftSwipe();
                if(xUp > xDown)
                    handleLeftToRightSwipe();
                handled = true;
                updateContent();
            }
        }
        private void handleRightToLeftSwipe(){
            if(viewIndex < sliders.size()-1)
                viewIndex++;
        }

        private void handleLeftToRightSwipe(){
            if(viewIndex > 0)
                viewIndex--;
        }
    }

    public static class Slider{
        View content;
        String label = null;
        public Slider(View content){
            this.content = content;
        }

        public Slider(View content , String label){
            this.content = content;
            this.label = label;
        }
    }
}
