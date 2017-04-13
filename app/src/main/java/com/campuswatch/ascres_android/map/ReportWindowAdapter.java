package com.campuswatch.ascres_android.map;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.campuswatch.ascres_android.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class ReportWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity activity;

    public ReportWindowAdapter(Activity activity){
        this.activity = activity;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        View view = activity.getLayoutInflater().inflate(R.layout.report_info_window, null);
        TextView timestamp = (TextView) view.findViewById(R.id.incident_time_text);
        TextView title = (TextView) view.findViewById(R.id.incident_text);

        title.setText(marker.getTitle());
        timestamp.setText(marker.getSnippet());

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
