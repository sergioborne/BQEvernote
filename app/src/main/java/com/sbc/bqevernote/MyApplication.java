package com.sbc.bqevernote;

import android.app.Application;

import com.evernote.client.android.EvernoteSession;

public class MyApplication extends Application{

    public final static String LOG_TAG = "BQEvernote";

    private static final String CONSUMER_KEY = "";
    private static final String CONSUMER_SECRET = "";

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
