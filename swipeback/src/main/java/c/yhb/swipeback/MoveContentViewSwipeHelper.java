package c.yhb.swipeback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import c.yhb.util.ScreenUtils;

public class MoveContentViewSwipeHelper implements SwipeBackActivity.SwipeBackHelper {

    private static final String TAG = "MoveContentView";
    private static final int SHADOW_WIDTH = 30; //阴影宽度

    private AppCompatActivity mCurActivity, mPreActivity;
    private int mScreenWidth;
    private boolean mDragging;
    private boolean mAnimating;
    private ViewGroup mCurRootView, mPreRootView;
    private View mShadowView; // 阴影

    public MoveContentViewSwipeHelper(AppCompatActivity curActivity) {
        this.mCurActivity = curActivity;
        this.mScreenWidth = ScreenUtils.getScreenWidth(curActivity);
    }


    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        float rawX = event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mAnimating || mDragging) {
                    return false;
                }
                if (downInLeftEdge(event)) {
                    long time = System.currentTimeMillis();
                    readyDragging();
                    Log.d(TAG, getClass().getSimpleName() + ":readyDragging: " + (System.currentTimeMillis() - time) + " ms");
                    return true; // consumed and stop dispatching
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mAnimating) {
                    return false;
                }
                if (mDragging) {
                    if (!(Math.abs(mCurRootView.getX() - rawX) < 3)) {
                        draggingTo(rawX);
                    }
                    return true; // consumed and stop dispatching
                }
                break;
            case MotionEvent.ACTION_POINTER_UP: // prevent from mess of second figure
            case MotionEvent.ACTION_UP:
                if (mDragging) {
                    long time = System.currentTimeMillis();
                    stopDraggingAt(rawX);
                    Log.d(TAG, getClass().getSimpleName() + ":stopDraggingAt: " + (System.currentTimeMillis() - time) + " ms");
                    return true; // consumed and stop dispatching
                }
                break;
        }

        return false; // don't care, please dispatch as usual
    }

    private boolean downInLeftEdge(MotionEvent event) {
        return event.getRawX() < mScreenWidth / 5;
    }

    private void readyDragging() {
        mDragging = true;

        mPreActivity = (AppCompatActivity) SwipeBackSupport.getInstance().getPenultimateActivity(mCurActivity);

        if (mPreActivity == null || mPreActivity == mCurActivity) {
            mDragging = false;
        }

        ViewGroup curDecorView = (ViewGroup) mCurActivity.getWindow().getDecorView();
        mCurRootView = (ViewGroup) curDecorView.getChildAt(0);

        addPreRootView(curDecorView);
        addShadow(curDecorView);

        mShadowView.setX(-SHADOW_WIDTH);
        mPreRootView.setX(-mScreenWidth / 3);

        Log.d(TAG, "readyDragging: ");
    }

    private void addShadow(ViewGroup viewGroup) {
        if (mShadowView == null) {
            mShadowView = new ShadowView(mCurActivity);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(SHADOW_WIDTH, FrameLayout.LayoutParams.MATCH_PARENT);
        viewGroup.addView(mShadowView, params);
    }

    private void addPreRootView(ViewGroup curDecorView) {
        ViewGroup preDecorView = (ViewGroup) mPreActivity.getWindow().getDecorView();
        mPreRootView = (ViewGroup) preDecorView.getChildAt(0);
        preDecorView.removeView(mPreRootView);
        curDecorView.addView(mPreRootView, 0);
    }

    private void draggingTo(float x) {
        mCurRootView.setX(x);
        mShadowView.setX(x - SHADOW_WIDTH);
        mPreRootView.setX(-mScreenWidth / 3 + x / 3);
        Log.d(TAG, "draggingTo: " + x);
    }

    private void stopDraggingAt(float x) {
        mDragging = false;
        boolean shouldFinishedCurActivity = x >= mScreenWidth / 3;
        if (shouldFinishedCurActivity) {
            animateToFinishCurActivity(x);
        } else {
            animateToRestoreEverything(x);
        }
        Log.d(TAG, "stopDraggingAt: " + x);
    }

    private void animateToFinishCurActivity(float from) {
        ValueAnimator animator = ValueAnimator.ofInt((int) from, mScreenWidth);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                slidingTo(x);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                restoreEverything();
                finishCurActivity();
                mAnimating = false;
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        mAnimating = true;
    }

    private void finishCurActivity() {

        // 更改当前 activity 的底 View 为 preView, 防止cur activity finish时的白屏闪烁
        CacheView cacheView = new CacheView(mCurActivity);
        cacheView.cacheView(mPreRootView);
        ViewGroup curDecorView = (ViewGroup) mCurRootView.getParent();
        curDecorView.addView(cacheView, 0);

        mCurActivity.finish();
        mCurActivity.overridePendingTransition(0, 0);
    }

    private void animateToRestoreEverything(float from) {
        ValueAnimator animator = ValueAnimator.ofInt((int) from, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                slidingTo(x);
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

    private void slidingTo(int x) {
        draggingTo(x);
    }

    private void restoreEverything() {
        ViewGroup curDecorView = (ViewGroup) mCurActivity.getWindow().getDecorView();
        curDecorView.removeView(mPreRootView);
        curDecorView.removeView(mShadowView);

        ViewGroup preDecorView = (ViewGroup) mPreActivity.getWindow().getDecorView();
        preDecorView.addView(mPreRootView, 0);
        mPreRootView.setX(0);

        mDragging = false;
        mAnimating = false;
        mPreActivity = null;
    }

    // 用于防止白屏闪烁
    private class CacheView extends View {

        private View mView;

        public CacheView(Context context) {
            super(context);
        }

        public void cacheView(View view) {
            mView = view;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mView != null) {
                mView.draw(canvas);
                mView = null;
            }
        }
    }
}
