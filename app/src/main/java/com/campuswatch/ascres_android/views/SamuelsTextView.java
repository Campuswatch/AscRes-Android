package com.campuswatch.ascres_android.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Thought of by samwyz for the most part on 4/19/17.
 */



class SamuelsMediumTextView extends AppCompatTextView {

    public SamuelsMediumTextView(Context context) {
        super(context);
        TextHelper.setTypefaceMedium(context, this);
    }

    public SamuelsMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextHelper.setTypefaceMedium(context, this);
    }

    public SamuelsMediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextHelper.setTypefaceMedium(context, this);
    }
}

class SamuelsBoldTextView extends AppCompatTextView {

    public SamuelsBoldTextView(Context context) {
        super(context);
        TextHelper.setTypefaceBold(context, this);
    }

    public SamuelsBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextHelper.setTypefaceBold(context, this);
    }

    public SamuelsBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextHelper.setTypefaceBold(context, this);
    }
}

class TextHelper {

    private static final String FONT_BOLD = "fonts/Samuelstype - BrooklynSamuelsFive-Bold.otf";
    private static final String FONT_MEDIUM = "fonts/Samuelstype - BrooklynSamuelsFive-Medium.otf";

    private static Typeface typefaceBold = null;
    private static Typeface typefaceMedium = null;

    static void setTypefaceBold(Context context, TextView textview){
        if(typefaceBold == null){
            typefaceBold = Typeface.createFromAsset(context.getAssets(), FONT_BOLD);
        } textview.setTypeface(typefaceBold);
    }

    static void setTypefaceMedium(Context context, TextView textView) {
        if (typefaceMedium == null) {
            typefaceMedium = Typeface.createFromAsset(context.getAssets(), FONT_MEDIUM);
        } textView.setTypeface(typefaceMedium);
    }

}

