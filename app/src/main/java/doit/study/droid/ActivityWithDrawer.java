package doit.study.droid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ActivityWithDrawer extends AppCompatActivity {
    private DrawerLayout mDrawer;
    // Shia LaBeouf - Just Do it! (Auto-tuned)
    private final String URL = "http://www.youtube.com/watch?v=gJscrxxl_Bg";
    protected FrameLayout mFrameLayout;
    private static String version;

    private String getVersion() {
        if (version != null)
            return version;

        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version = "buggy";
        }
        return version;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mFrameLayout = (FrameLayout) findViewById(R.id.flContent);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView nvView = (NavigationView) findViewById(R.id.nvView);
        View header = nvView.getHeaderView(0);
        TextView tv = (TextView) header.findViewById(R.id.version_num_header);
        tv.setText(getVersion());
        nvView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent = null;
                switch(menuItem.getItemId()) {
                    case R.id.nav_first_fragment:
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(URL));
                        startActivity(intent);
                        break;
                    case R.id.nav_second_fragment:
                        intent = new Intent(ActivityWithDrawer.this, TopicsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_third_fragment:
                        intent = new Intent(ActivityWithDrawer.this, QuestionsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
