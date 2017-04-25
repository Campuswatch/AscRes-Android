package com.campuswatch.ascres_android.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.campuswatch.ascres_android.R;
import com.campuswatch.ascres_android.map.MapsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorialActivity extends AppCompatActivity {

    private static final int COUNT = 5;

    @BindView(R.id.tutorial_pager)
    ViewPager pager;

    @BindView(R.id.next_button)
    Button button;

    public final int[] mImageArray = new int[] {
            R.drawable.tut1, R.drawable.tut2, R.drawable.tut3, R.drawable.tut4, R.drawable.tut5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        ButterKnife.bind(this);

        pager.setAdapter(new TutorialAdapter(getSupportFragmentManager()));
        button.setOnClickListener(v -> {
            if (pager.getCurrentItem() < 4) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            } else {
                startActivity(new Intent(TutorialActivity.this, MapsActivity.class));
                finish();
            }

            if (pager.getCurrentItem() == 4) {
                button.setText("DONE");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class TutorialAdapter extends FragmentPagerAdapter {


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

