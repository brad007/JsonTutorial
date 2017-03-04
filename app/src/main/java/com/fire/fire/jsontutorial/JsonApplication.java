package com.fire.fire.jsontutorial;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by brad on 2017/03/04.
 */

public class JsonApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(JsonApplication.this);
    }
}
