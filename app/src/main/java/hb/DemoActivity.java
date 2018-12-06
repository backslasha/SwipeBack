package hb;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hb.swipeback.R;
import hb.swipeback.SwipeBackActivity;
import hb.swipeback.TransparentThemeSwipeHelper;

public class DemoActivity extends SwipeBackActivity {

    private static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        int argb = Color.argb(
                255,
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255)
        );

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("The " + ++count + " activity.");
            actionBar.setBackgroundDrawable(new ColorDrawable(argb));
            findViewById(android.R.id.content).setBackgroundColor(argb);
        }


        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, DemoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected SwipeBackActivity.SwipeBackHelper createSwipeHelper() {
        return new TransparentThemeSwipeHelper(this);
    }
}
