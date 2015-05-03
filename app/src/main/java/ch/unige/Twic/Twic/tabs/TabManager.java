package ch.unige.Twic.Twic.tabs;

import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost;

import ch.unige.Twic.MainActivity;
import ch.unige.Twic.Twic.Exceptions.TwicException;

public class TabManager implements TabHost.OnTabChangeListener {

    FragmentTabHost tabHost;
    MainActivity mainActivity;

    public TabManager(FragmentTabHost tabHost, MainActivity mainActivity) {
        this.tabHost = tabHost;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onTabChanged(String tabId) {
        update();
    }

    public void update(){
        if(mainActivity != null) {
            mainActivity.cleanFlash();
            try {
                String currentTab = tabHost.getCurrentTabTag();
                android.support.v4.app.FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                android.support.v4.app.Fragment fragmentById = fragmentManager.findFragmentByTag(currentTab);
                ManagableTab fragmentById1 = (ManagableTab) fragmentById;

                fragmentById1.update();
            } catch (TwicException e) {
                e.printStackTrace();
                mainActivity.flash(e.getMessageId());
            }
        }
    }
}