package com.campuswatch.ascres_android.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.campuswatch.ascres_android.R;
import com.campuswatch.ascres_android.SmartFragmentStatePagerAdapter;
import com.campuswatch.ascres_android.map.MapsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int COUNT = 5;

    @BindView(R.id.tutorial_pager)
    ViewPager pager;

    @BindView(R.id.next_button)
    FloatingActionButton nextButton;

    @BindView(R.id.back_button) FloatingActionButton backButton;

    public final int[] mImageArray = new int[] {
            R.drawable.tut1, R.drawable.tut2, R.drawable.tut3, R.drawable.tut4, R.drawable.tut5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        ButterKnife.bind(this);

        pager.setAdapter(new TutorialAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(pageChangeListener);

        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.next_button:
                if (pager.getCurrentItem() < 4) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                } else {
                    startActivity(new Intent(TutorialActivity.this, MapsActivity.class));
                    finish();
                }
                break;
            case R.id.back_button:
                if (pager.getCurrentItem() > 0) {
                    pager.setCurrentItem(pager.getCurrentItem() - 1, true);
                }
        }
    }

    private ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                backButton.setVisibility(View.GONE);
                return;
            }

            if (backButton.getVisibility() == View.GONE) {
                backButton.setVisibility(View.VISIBLE);
            }
        }
    };

    private class TutorialAdapter extends SmartFragmentStatePagerAdapter {


        TutorialAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TutorialFragment.newInstance(mImageArray[position]);
        }

        @Override
        public int getCount() {
            return COUNT;
        }
    }
}

