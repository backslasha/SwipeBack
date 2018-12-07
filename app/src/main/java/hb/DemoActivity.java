package hb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import c.yhb.swipeback.MoveContentViewSwipeHelper;
import c.yhb.swipeback.SwipeBackActivity;
import c.yhb.swipeback.TransparentThemeSwipeHelper;
import hb.swipeback.R;

public class DemoActivity extends SwipeBackActivity {

    private int mCount = 0;

    public static Intent newIntent(Context context, int count) {
        Intent intent = new Intent(context, DemoActivity.class);
        intent.putExtra("count", count);
        return intent;
    }

    @Override
    public void setTheme(int resid) {
        switch (MainActivity.sType) {
            case MainActivity.TYPE_MOVE_CONTENT_VIEW:
                super.setTheme(R.style.AppTheme);
                return;
            case MainActivity.TYPE_DYNAMIC_TRANSPARENT:
                super.setTheme(R.style.AppTheme_TransparentTheme);
                return;
        }
        super.setTheme(resid);
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

            String implementType = "";
            if (MainActivity.sType==MainActivity.TYPE_DYNAMIC_TRANSPARENT) {
                implementType = "dynamic_transparent";
            } else if (MainActivity.sType == MainActivity.TYPE_MOVE_CONTENT_VIEW) {
                implementType = "move_content_view";
            }

            actionBar.setTitle("The " + mCount + " activity." + "\\(" + (implementType) + "\\)");
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

        VideoView videoView = findViewById(R.id.player);
        videoView.setVideoURI(Uri.parse("http://vfx.mtime.cn/Video/2018/12/03/mp4/181203104656117576.mp4"));
        videoView.setMediaController(new MediaController(this, true));
        videoView.start();
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
