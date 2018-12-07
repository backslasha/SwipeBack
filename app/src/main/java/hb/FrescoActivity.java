package hb;

import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import common.CommonAdapter;
import common.CommonViewHolder;
import hb.swipeback.R;


public class FrescoActivity extends AppCompatActivity {

    private static int index = 0;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresco);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    public void onClick(View view) {
        int currentItem = mViewPager.getCurrentItem();
        PlaceholderFragment currentFragment = (PlaceholderFragment) mSectionsPagerAdapter.getItem(currentItem);
        currentFragment.loadMore10Item();
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_fresco, container, false);

            mCommonAdapter = new CommonAdapter<String>(R.layout.item_image_card, new ArrayList<String>()) {
                @Override
                public void convert(CommonViewHolder holder, String entity) {
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
