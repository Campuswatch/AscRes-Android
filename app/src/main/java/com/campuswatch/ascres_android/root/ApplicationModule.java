package com.campuswatch.ascres_android.root;

import android.content.Context;
import android.content.SharedPreferences;

import com.campuswatch.ascres_android.UserRepository;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.campuswatch.ascres_android.Constants.USER_DATA;

/**
 * Thought of by samwyz for the most part on 4/12/17.
 */

@Module
public class ApplicationModule {

    private final App app;

    public ApplicationModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return app;
    }

    @Provides
    @Singleton
    public UserRepository provideUserRepository(SharedPreferences prefs){
        return new UserRepository(prefs);
    }

    @Provides
    @Singleton
    GoogleApiClient providesGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();
    }

    @Provides
    SharedPreferences provideUserSharedPreferences(Context context){
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
    }

}
