package ch.unige.Twic.tabs;

import android.support.v4.app.FragmentTabHost;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.exceptions.TwicException;

/**
 * Handle tab updates.
 */
public class TabManager {

    FragmentTabHost tabHost;
    MainActivity mainActivity;

    public TabManager(FragmentTabHost tabHost, MainActivity mainActivity) {
        this.tabHost = tabHost;
        this.mainActivity = mainActivity;
    }

    /**
     * Update the current tab.
     */
    public void update(){
        if(mainActivity != null) {
            MainActivity.cleanFlash();
            try {
                String currentTab = tabHost.getCurrentTabTag();
                android.support.v4.app.FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                ManageableTab fragmentById = (ManageableTab) fragmentManager.findFragmentByTag(currentTab);
                fragmentById.update();
            } catch (TwicException e) {
                e.printStackTrace();
                MainActivity.flash(e.getMessageId());
            }
        }
    }
}