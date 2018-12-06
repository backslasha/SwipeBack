package hb.swipeback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import hb.util.ScreenUtils;

public class TransparentThemeSwipeHelper implements SwipeBackActivity.SwipeBackHelper {

    private static final String TAG = "TransparentSwipeHelper";

    private AppCompatActivity mCurActivity, mPreActivity;
    private int mScreenWidth;
    private boolean mDragging;
    private boolean mInConverting; // window is in process of converting to transparent or reversely
    private View mCurRootView;

    public TransparentThemeSwipeHelper(AppCompatActivity curActivity) {
        this.mCurActivity = curActivity;
        this.mScreenWidth = ScreenUtils.getScreenWidth(curActivity);
        convertActivityFromTranslucent(curActivity);
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (downInLeftEdge(event)) {
                    readyDragging();
                    return true; // consumed and stop dispatching
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInConverting) {
                    return true; // to avoid see the black behind the cur activity, allow swipe only after cur activity is completely transparent
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

        convertActivityToTranslucent(mCurActivity);

        Log.d(TAG, "readyDragging: ");
    }

    private boolean downInLeftEdge(MotionEvent event) {
        return event.getRawX() < mScreenWidth / 5;
    }

    private void convertActivityToTranslucent(AppCompatActivity activity) {
        mInConverting = false;
        Log.d(TAG, "convertActivityToTranslucent: ");
        mInConverting = true;
    }

    private void convertActivityFromTranslucent(AppCompatActivity activity) {
        Log.d(TAG, "convertActivityFromTranslucent: ");
    }

    private void draggingTo(float x) {
        mCurRootView.setX(x);
        Log.d(TAG, "draggingTo: ");
    }

    private void stopDraggingAt(float x) {
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
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finishCurActivity();
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
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
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                restoreEverything();
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void restoreEverything() {
        convertActivityFromTranslucent(mCurActivity);
    }

}
