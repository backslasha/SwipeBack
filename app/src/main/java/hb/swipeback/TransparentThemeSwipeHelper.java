package hb.swipeback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import hb.util.ScreenUtils;

@RequiresApi(Build.VERSION_CODES.KITKAT)
public class TransparentThemeSwipeHelper implements SwipeBackActivity.SwipeBackHelper {

    private static final String TAG = "TransparentSwipeHelper";

    private AppCompatActivity mCurActivity, mPreActivity;
    private int mScreenWidth;
    private boolean mDragging;
    private boolean isTranslucentComplete; // window is in process of converting to transparent or reversely
    private boolean mAnimating;
    private View mCurRootView, mPreRootView;

    public TransparentThemeSwipeHelper(AppCompatActivity curActivity) {
        this.mCurActivity = curActivity;
        this.mScreenWidth = ScreenUtils.getScreenWidth(curActivity);
        convertActivityFromTranslucent(curActivity);
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mAnimating || mDragging) {
                    return false;
                }
                if (downInLeftEdge(event)) {
                    readyDragging();
                    return true; // consumed and stop dispatching
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mAnimating) {
                    return false;
                }
                if (!isTranslucentComplete) {
                    return false; // to avoid see the black behind the cur activity, allow swipe only after cur activity is completely transparent
                }
                if (mDragging) {
                    draggingTo(event.getRawX());
                    return true; // consumed and stop dispatching
                }
                break;
            case MotionEvent.ACTION_POINTER_UP: // prevent from mess of second figure
            case MotionEvent.ACTION_UP:
                if (mDragging) {
                    stopDraggingAt(event.getRawX());
                    return true; // consumed and stop dispatching
                }
                break;
        }

        return false; // don't care, please dispatch as usual
    }

    private void readyDragging() {
        mDragging = true;

        mPreActivity = (AppCompatActivity) SwipeBackSupport.getInstance().getPenultimateActivity();

        if (mPreActivity == null) {
            mDragging = false;
            return;
        }

        mCurRootView = ((ViewGroup) mCurActivity.getWindow().getDecorView()).getChildAt(0);
        mPreRootView = ((ViewGroup) mPreActivity.getWindow().getDecorView()).getChildAt(0);

        mPreRootView.setX(-mScreenWidth / 3);

        convertActivityToTranslucent(mCurActivity);

        Log.d(TAG, "readyDragging: ");
    }

    private boolean downInLeftEdge(MotionEvent event) {
        return event.getRawX() < mScreenWidth / 5;
    }

    private void convertActivityToTranslucent(AppCompatActivity activity) {
        isTranslucentComplete = false;
        Log.d(TAG, "convertActivityToTranslucent: ");

        try {
            Class mTranslucentConversionListenerClass = null;
            Object translucentConversionListener = null;

            //获取透明转换回调类的class对象
            Class[] clazzArray = Activity.class.getDeclaredClasses();
            for (Class clazz : clazzArray) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    mTranslucentConversionListenerClass = clazz;
                }
            }

            //代理透明转换回调
            if (mTranslucentConversionListenerClass != null) {
                InvocationHandler invocationHandler = new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        isTranslucentComplete = true;
                        return null;
                    }
                };
                translucentConversionListener = Proxy.newProxyInstance(mTranslucentConversionListenerClass.getClassLoader(), new Class[]{mTranslucentConversionListenerClass}, invocationHandler);
            }

            //利用反射将窗口转为透明，注意 SDK21 及以上参数有所不同
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Object options = null;
                try {
                    @SuppressLint("PrivateApi")
                    Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
                    getActivityOptions.setAccessible(true);
                    options = getActivityOptions.invoke(this);
                } catch (Exception ignored) {
                }
                @SuppressLint("PrivateApi")
                Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent", mTranslucentConversionListenerClass, ActivityOptions.class);
                convertToTranslucent.setAccessible(true);
                convertToTranslucent.invoke(activity, translucentConversionListener, options);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                @SuppressLint("PrivateApi")
                Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent", mTranslucentConversionListenerClass);
                convertToTranslucent.setAccessible(true);
                convertToTranslucent.invoke(activity, translucentConversionListener);
            } else {
                Log.e(TAG, "convertActivityToTranslucent: invoked below api 19.");
            }

            if (translucentConversionListener == null) {
                isTranslucentComplete = true;
            }

        } catch (Throwable ignored) {
            isTranslucentComplete = true;
        }

        //去除窗口背景
        activity.getWindow().setBackgroundDrawable(null);
        isTranslucentComplete = true;
    }

    private void convertActivityFromTranslucent(AppCompatActivity activity) {

        try {
            @SuppressLint("PrivateApi")
            Method convertFromTranslucent = Activity.class.getDeclaredMethod("convertFromTranslucent");
            convertFromTranslucent.setAccessible(true);
            convertFromTranslucent.invoke(activity);
        } catch (Exception ignore) {

        }

        Log.d(TAG, "convertActivityFromTranslucent: ");
    }

    private void draggingTo(float x) {
        mCurRootView.setX(x);
        mPreRootView.setX(-mScreenWidth / 3 + x / 3);
        Log.d(TAG, "draggingTo: ");
    }

    private void stopDraggingAt(float x) {
        mDragging = false;
        boolean shouldFinishedCurActivity = x >= mScreenWidth / 3;
        if (shouldFinishedCurActivity) {
            animateToFinishCurActivity(x);
        } else {
            animateToRestoreEverything(x);
        }
        Log.d(TAG, "stopDraggingAt: ");
    }

    private void animateToFinishCurActivity(float from) {
        ValueAnimator animator = ValueAnimator.ofInt((int) from, mScreenWidth);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                mCurRootView.setX(x);
                mPreRootView.setX(-mScreenWidth / 3 + x / 3);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finishCurActivity();
                mAnimating = false;
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        mAnimating = true;
    }

    private void finishCurActivity() {
        mCurActivity.finish();
        mCurActivity.overridePendingTransition(0, 0);
    }

    private void animateToRestoreEverything(float from) {
        ValueAnimator animator = ValueAnimator.ofInt((int) from, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                mCurRootView.setX(x);
                mPreRootView.setX(-mScreenWidth / 3 + x / 3);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                restoreEverything();
                mAnimating = false;
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        mAnimating = true;
    }

    private void restoreEverything() {
        convertActivityFromTranslucent(mCurActivity);
        mDragging = false;
        mAnimating = false;
        isTranslucentComplete = false;
        mPreActivity = null;
    }

}
