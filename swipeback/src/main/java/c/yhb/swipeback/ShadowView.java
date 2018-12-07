package c.yhb.swipeback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

public class ShadowView extends View {

    private Drawable mDrawable;

    public ShadowView(Context context) {
        super(context);
        int[] colors = new int[]{0x00000000, 0x17000000, 0x43000000};
        mDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mDrawable.draw(canvas);
    }
}
