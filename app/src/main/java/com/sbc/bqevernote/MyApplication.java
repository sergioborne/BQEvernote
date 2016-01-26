package com.sbc.bqevernote;

import android.app.Application;

import com.evernote.client.android.EvernoteSession;

/**
 * Created by Sergio on 26/01/2016.
 */
public class MyApplication extends Application{

    private static final String CONSUMER_KEY = "Your consumer key";
    private static final String CONSUMER_SECRET = "Your consumer secret";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

    private EvernoteSession mEvernoteSession;

    @Override
    public void onCreate() {
        super.onCreate();

        mEvernoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
    }
}
