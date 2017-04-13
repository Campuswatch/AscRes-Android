package com.campuswatch.ascres_android.root;

import android.content.Context;
import android.content.SharedPreferences;

import com.campuswatch.ascres_android.UserRepository;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("985357666838-kl53o6sdepdbv625pd3lj2jpqq95qevv.apps.googleusercontent.com")
                .build();

        return new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Provides
    SharedPreferences provideUserSharedPreferences(Context context){
        return context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
    }

}
