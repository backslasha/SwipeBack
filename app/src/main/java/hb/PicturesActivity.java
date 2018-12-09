package hb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import c.yhb.swipeback.MoveContentViewSwipeHelper;
import c.yhb.swipeback.SwipeBackActivity;
import c.yhb.swipeback.TransparentThemeSwipeHelper;
import common.CommonAdapter;
import common.CommonViewHolder;
import hb.swipeback.R;


public class PicturesActivity extends SwipeBackActivity {

    private static int index = 0;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;


    private int mCount = 0;

    public static Intent newIntent(Context context, int count) {
        Intent intent = new Intent(context, PicturesActivity.class);
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
        setContentView(R.layout.activity_fresco);

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            String implementType = "";
            if (MainActivity.sType == MainActivity.TYPE_DYNAMIC_TRANSPARENT) {
                implementType = "dynamic_transparent";
            } else if (MainActivity.sType == MainActivity.TYPE_MOVE_CONTENT_VIEW) {
                implementType = "move_content_view";
            }

            actionBar.setTitle("The " + mCount + " activity." + "\\(" + (implementType) + "\\)");
            actionBar.setBackgroundDrawable(new ColorDrawable(argb));
            findViewById(android.R.id.content).setBackgroundColor(argb);
        }


//        VideoView videoView = findViewById(R.id.player);
//        videoView.setVideoURI(Uri.parse("http://vfx.mtime.cn/Video/2018/12/03/mp4/181203104656117576.mp4"));
//        videoView.setMediaController(new MediaController(this, true));
//        videoView.start();


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


    public void onClick(View view) {
//        int currentItem = mViewPager.getCurrentItem();
//        PlaceholderFragment currentFragment = (PlaceholderFragment) mSectionsPagerAdapter.getItem(currentItem);
//        currentFragment.loadMore10Item();
        Intent intent = PicturesActivity.newIntent(PicturesActivity.this, mCount + 1);
        startActivity(intent);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String TAG = "PicturesActivity";
        private RecyclerView mRecyclerView;
        private CommonAdapter<String> mCommonAdapter;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fresco, container, false);

            mCommonAdapter = new CommonAdapter<String>(R.layout.item_image_card, new ArrayList<String>()) {
                @Override
                public void convert(final CommonViewHolder holder, final String entity) {
                    Picasso.get().load(entity).centerCrop().fit().into((ImageView) holder.itemView.findViewById(R.id.iv_poster));
                }
            };

            mRecyclerView = rootView.findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {

            });
            mRecyclerView.setAdapter(mCommonAdapter);
            loadMore10Item();
            return rootView;
        }

        public void loadMore10Item() {
            List<String> uris = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                uris.add("https://bing.ioliu.cn/v1?w=1200&h=1920&d=" + index++);
            }
            mCommonAdapter.notifyDataSetChanged(uris, CommonAdapter.Op.APPEND);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                mFragments.add(PlaceholderFragment.newInstance(i));
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
