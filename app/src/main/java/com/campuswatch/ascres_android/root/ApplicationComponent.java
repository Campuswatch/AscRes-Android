package com.campuswatch.ascres_android.root;

import com.campuswatch.ascres_android.chat.ChatActivity;
import com.campuswatch.ascres_android.chat.ChatModule;
import com.campuswatch.ascres_android.map.MapsActivity;
import com.campuswatch.ascres_android.map.MapsModule;
import com.campuswatch.ascres_android.login.LoginActivity;
import com.campuswatch.ascres_android.splash.SplashActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Thought of by samwyz for the most part on 4/12/17.
 */

@Singleton
@Component(modules = {ApplicationModule.class, MapsModule.class, ChatModule.class})
public interface ApplicationComponent {

    void inject(MapsActivity target);
    void inject(SplashActivity target);
    void inject(LoginActivity target);
    void inject(ChatActivity target);
}
