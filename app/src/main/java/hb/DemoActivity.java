package hb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hb.swipeback.MoveContentViewSwipeHelper;
import hb.swipeback.R;
import hb.swipeback.SwipeBackActivity;
import hb.swipeback.TransparentThemeSwipeHelper;

public class DemoActivity extends SwipeBackActivity {

    private int mCount = 0;

    public static Intent newIntent(Context context, int count) {
        Intent intent = new Intent(context, DemoActivity.class);
        intent.putExtra("count", count);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        Intent intent = getIntent();
        if (intent != null) {
            mCount = intent.getIntExtra("count", 1);
        }

        int argb = Color.argb(
                255,
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255)
        );

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("The " + mCount + " activity.");
            actionBar.setBackgroundDrawable(new ColorDrawable(argb));
            findViewById(android.R.id.content).setBackgroundColor(argb);
        }


        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = DemoActivity.newIntent(DemoActivity.this, mCount + 1);
                startActivity(intent);
            }
        });
    }


    @Override
    protected SwipeBackActivity.SwipeBackHelper createSwipeHelper() {
        switch (MainActivity.sType) {
            case MainActivity.TYPE_MOVE_CONTENT_VIEW:
                return new MoveContentViewSwipeHelper(this);
            case MainActivity.TYPE_DYNAMIC_TRANSPARENT:
                return new TransparentThemeSwipeHelper(this);
        }
        return new TransparentThemeSwipeHelper(this);
    }
}
