package com.campuswatch.ascres_android.root;

import android.app.Application;

/**
 * Thought of by samwyz for the most part on 4/12/17.
 */

public class App extends Application {

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .mapsModule(new MapsModule())
                .chatModule(new ChatModule())
                .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    } {
}
