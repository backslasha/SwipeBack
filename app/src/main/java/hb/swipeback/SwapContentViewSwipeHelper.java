package hb.swipeback;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import hb.util.ScreenUtils;

public class SwapContentViewSwipeHelper implements SwipeBackActivity.SwipeBackHelper {

    private static final String TAG = "TranspantSwipeHelper";

    private AppCompatActivity mCurActivity, mPreActivity;
    private int mScreenWidth;
    private boolean mDragging;
    private View mCurRootView, mPreRootView;

    public SwapContentViewSwipeHelper(AppCompatActivity curActivity) {
        this.mCurActivity = curActivity;
        this.mScreenWidth = ScreenUtils.getScreenWidth(curActivity);
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

        mPreRootView = mPreActivity.findViewById(android.R.id.content);
        mCurRootView = mCurActivity.findViewById(android.R.id.content);


        Log.d(TAG, "readyDragging: ");
    }

    private void draggingTo(float x) {
        Log.d(TAG, "draggingTo: ");
    }

    private void stopDraggingAt(float x) {
        Log.d(TAG, "stopDraggingAt: ");
    }

    private boolean downInLeftEdge(MotionEvent event) {
        return event.getRawX() < mScreenWidth / 5;
    }
}
