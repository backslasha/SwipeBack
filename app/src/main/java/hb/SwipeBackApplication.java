package hb;

import android.app.Application;

import hb.swipeback.SwipeBackSupport;

public class SwipeBackApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SwipeBackSupport.init(this);
    }

}
