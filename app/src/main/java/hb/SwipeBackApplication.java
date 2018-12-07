package hb;

import android.app.Application;

import c.yhb.swipeback.SwipeBackSupport;


public class SwipeBackApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SwipeBackSupport.init(this);
    }

}
