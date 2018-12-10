package c.yhb.swipeback;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

public abstract class SwipeBackActivity extends AppCompatActivity {

    private SwipeBackHelper mSwipeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeHelper = createSwipeHelper();
    }

    protected SwipeBackHelper createSwipeHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return new TransparentThemeSwipeHelper(this);
        } else {
            return new MoveContentViewSwipeHelper(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mSwipeHelper != null) {
            if (mSwipeHelper.handleTouchEvent(ev)) {
                return true; // consumed and stop
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    protected interface SwipeBackHelper {
        boolean handleTouchEvent(MotionEvent event);
    }
}
