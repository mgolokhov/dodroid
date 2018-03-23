package doit.study.droid.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import doit.study.droid.R;

public class DrawerHelper {

    public static Drawer getDrawer(final Activity activity, Toolbar toolbar){
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.android_side)
                .build();

        Drawer drawer = new DrawerBuilder()
                .withToolbar(toolbar)
                .withActivity(activity)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.need_motivation_button).withIcon(R.drawable.test2).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.set_topic_button).withIcon(R.drawable.death_star_icon).withIdentifier(2).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.doit_button).withIcon(R.drawable.test2).withIdentifier(3).withSelectable(false)
                )
                .withShowDrawerOnFirstLaunch(true)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    //check if the drawerItem is set.
                    //there are different reasons for the drawerItem to be null
                    //--> click on the header
                    //--> click on the footer
                    //those items don't contain a drawerItem

                    if (drawerItem != null) {
                        Intent intent = null;
                        if (drawerItem.getIdentifier() == 1) {
                            Toast.makeText(activity, "test1", Toast.LENGTH_SHORT).show();
                        } else if (drawerItem.getIdentifier() == 2) {
                            Toast.makeText(activity, "test2", Toast.LENGTH_SHORT).show();
                        } else if (drawerItem.getIdentifier() == 3) {
                            Toast.makeText(activity, "test3", Toast.LENGTH_SHORT).show();
                        }
                        if (intent != null) {
                            //activity.startActivity(intent);
                        }
                    }

                    return false;
                })
                .withSelectedItem(2)
                .build();
        return drawer;
    }
}
