package com.campuswatch.ascres_android.chat;

import com.campuswatch.ascres_android.UserRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

@Module
public class ChatModule {

    @Provides
    @Singleton
    ChatActivityMVP.Presenter providesChatPresenter(ChatActivityMVP.Model model, UserRepository repo){
        return new ChatPresenter(model, repo);
    }

    @Provides
    @Singleton
    ChatActivityMVP.Model providesChatActivityModel(){
        return new ChatModel();
    }
}
