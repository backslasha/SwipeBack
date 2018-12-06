package hb.swipeback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class SwipeBackSupport implements Application.ActivityLifecycleCallbacks {

    private static SwipeBackSupport INSTANCE;
    private LinkedList<WeakReference<Activity>> mActivities = new LinkedList<>();

    private SwipeBackSupport() {

    }

    static SwipeBackSupport getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SwipeBackSupport();
        }
        return INSTANCE;
    }

    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(getInstance());
    }

    Activity getPenultimateActivity() {
        return mActivities.get(mActivities.size() - 2).get();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivities.add(new WeakReference<>(activity));
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivities.removeLast();
    }
}
