package com.campuswatch.ascres_android.map;

import com.campuswatch.ascres_android.UserRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

@Module
public class MapsModule {

    @Provides
    @Singleton
    MapsActivityMVP.Presenter providesMapsPresenter(MapsActivityMVP.Model model,
                                                            UserRepository repo) {

        return new MapsPresenter(model, repo);
    }

    @Provides
    @Singleton
    MapsActivityMVP.Model providesMapsActivityModel(){
        return new MapsModel();
    }

    @Provides
    @Singleton
    MapsActivityMVP.Client providesClientHelper(MapsActivityMVP.Presenter presenter) {
        return new ClientHelper(presenter);
    }

}
