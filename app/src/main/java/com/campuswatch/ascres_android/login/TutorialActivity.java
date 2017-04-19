package com.campuswatch.ascres_android.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.campuswatch.ascres_android.R;
import com.campuswatch.ascres_android.map.MapsActivity;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class TutorialActivity extends AppIntro {

    private static final String FONT_MEDIUM = "fonts/Samuelstype - BrooklynSamuelsFive-Medium.otf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSkipButton(false);
        addSlide(AppIntroFragment.newInstance("WELCOME", FONT_MEDIUM,
                getResources().getString(R.string.welcomeTutorial), FONT_MEDIUM, R.drawable.logo_full_resize,
                ContextCompat.getColor(this, R.color.midnight), ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("HELP BUTTON", FONT_MEDIUM,
                getResources().getString(R.string.tutorial2), FONT_MEDIUM, R.drawable.logo_full_resize,
                ContextCompat.getColor(this, R.color.midnight),ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("REPORTING", FONT_MEDIUM,
                getResources().getString(R.string.tutorial3), FONT_MEDIUM, R.drawable.logo_full_resize,
                ContextCompat.getColor(this, R.color.midnight),ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("CHAT", FONT_MEDIUM,
                getResources().getString(R.string.chat), FONT_MEDIUM, R.drawable.logo_full_resize,
                ContextCompat.getColor(this, R.color.midnight),ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("PROFILE", FONT_MEDIUM,
                getResources().getString(R.string.profile), FONT_MEDIUM, R.drawable.logo_full_resize,
                ContextCompat.getColor(this, R.color.midnight), ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("STAY SAFE", FONT_MEDIUM,
                getResources().getString(R.string.finish), FONT_MEDIUM, R.drawable.logo_full_resize,
                ContextCompat.getColor(this, R.color.midnight),ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimary)));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        startActivity(new Intent(TutorialActivity.this, MapsActivity.class));
        finish();
    }
}

