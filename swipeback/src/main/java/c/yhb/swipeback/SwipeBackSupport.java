package c.yhb.swipeback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class SwipeBackSupport implements Application.ActivityLifecycleCallbacks {

    private static SwipeBackSupport INSTANCE;
    private LinkedList<Activity> mActivities = new LinkedList<>();

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

    Activity getPenultimateActivity(Activity currentActivity) {
        Activity activity = null;
        try {
            if (mActivities.size() > 1) {
                activity = mActivities.get(mActivities.size() - 2);

                if (currentActivity.equals(activity)) {
                    int index = mActivities.indexOf(currentActivity);
                    if (index > 0) {
                        // 处理内存泄漏或最后一个 Activity 正在 finishing 的情况
                        activity = mActivities.get(index - 1);
                    } else if (mActivities.size() == 2) {
                        // 处理屏幕旋转后 mActivityStack 中顺序错乱
                        activity = mActivities.getLast();
                    }
                }
            }
        } catch (Exception e) {
        }
        return activity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivities.add(activity);
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
        mActivities.remove(activity);
    }
}
