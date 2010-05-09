package cn.shooter.client.andriod;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.TabSpec;

public class ShooterClientAndroid extends TabActivity {
	public static final String TAG = "ShooterClientAndroidMainActivity";
    private TabHost mTabHost;
    public String Subscribe1;
    public String Subscribe2;
    public String Subscribe3;
    public String Subscribe4;
    public static final int MENU_SEARCH = 0;
    public static final int MENU_REFRESH = 1;
    public static final int MENU_SUBSCRIBE = 2;
    public static final int MENU_UNSUBSCRIBE = 3;
    public static final int MENU_CLOSE_TAB = 4;
    public static final int MENU_GROUP_SEARCH = 0;
    private final Map<String, TabSpec> tabList = new LinkedHashMap<String,TabSpec>();  
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        setContentView(R.layout.main);
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        initTabHost();
        
        final Intent queryIntent = getIntent();
        final String queryAction = queryIntent.getAction();
        if (Intent.ACTION_SEARCH.equals(queryAction)) {
        	addTabSearch(queryIntent.getStringExtra(SearchManager.QUERY), false);
        }

   
    }
    private void initTabHost() {
        if (mTabHost != null) {
            //throw new IllegalStateException("Trying to intialize already initializd TabHost");
        	return;
        }

        mTabHost = getTabHost();
        
        addTabLatest();
        addTabHotest();
        //addTabBooked(false);
        
        SharedPreferences sharedata = getSharedPreferences ( "data", 0);
        for(int i = 1; i <= 4; i++ ){
	        Subscribe1 = sharedata.getString("subscribe"+i, null);
			if(Subscribe1 != null && Subscribe1.trim() != "")
				addTabSearch(Subscribe1, true);
        }

    	
        setTabHeight();
        
        
        mTabHost.setCurrentTab(0);
    }
    private void setTabHeight(){
    	TabWidget mainTabWidget = mTabHost.getTabWidget();
    	for(int i = 0; i < mainTabWidget.getChildCount(); i++){
    		mainTabWidget.getChildAt(i).setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , 35));
    	}
    }
    @Override
    public void onNewIntent(Intent intent) {
        
        String action = intent.getAction();
        String query = intent.getStringExtra(SearchManager.QUERY);

        if (intent == null) {
        	addTabSearch(query, false);
        } else if (Intent.ACTION_SEARCH.equals(action) && query != null) {
        	addTabSearch(query, false);
        } 
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Always show these.
        menu.add(MENU_GROUP_SEARCH, MENU_SEARCH, Menu.NONE, R.string.search_label) //
                .setIcon(R.drawable.ic_menu_search) //
                .setAlphabeticShortcut(SearchManager.MENU_KEY);
      
        menu.add(MENU_GROUP_SEARCH, MENU_REFRESH, Menu.NONE, R.string.refresh_label) //
        .setIcon(R.drawable.ic_menu_refresh);

        return true;
    }
   

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SEARCH:
                onSearchRequested();
                return true;
          
            case MENU_CLOSE_TAB:
            	//close current tab
            	mTabHost.getCurrentTab();
            	String tagThatNeedRemove = mTabHost.getCurrentTabTag();
            	mTabHost.setCurrentTab(0);
            	tabList.remove( tagThatNeedRemove );
            	mTabHost.clearAllTabs();
            	
            	for ( TabSpec specData : tabList.values()) {
            		mTabHost.addTab(specData);  
            	}
            	setTabHeight();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public  void addTabSearch(String keyword, boolean isSubscribed)
    {
    	Intent searchIntent = new Intent(this, SearchResultsActivity.class); 
    	searchIntent.putExtra("keyword", keyword);
    	searchIntent.putExtra("subscribed", isSubscribed);
    	String tabTag = "search_"+keyword;
    	TabSpec tabSpecData = mTabHost.newTabSpec(tabTag);
    	mTabHost.addTab( tabSpecData.setIndicator(keyword, null) // the tab icon//  getResources().getDrawable(R.drawable.icon)
                .setContent(searchIntent));// The  contained  activity
    	tabList.put(tabTag, tabSpecData);
    	setTabHeight();
    	mTabHost.setCurrentTab(mTabHost.getTabWidget().getChildCount() - 1);
    }
    private void addTabLatest() {
        // Latest Sub tab
    	String tabTag = "latest";
    	TabSpec tabSpecData = mTabHost.newTabSpec(tabTag);
       mTabHost.addTab(tabSpecData.setIndicator(getString(R.string.latest_label), null) // the tab icon//  getResources().getDrawable(R.drawable.icon)
               .setContent(new Intent(this, LatestResultsActivity.class)) );// The  contained  activity
       tabList.put(tabTag, tabSpecData);  
   }
    private void addTabHotest() {
        // Hotest Sub tab
    	String tabTag = "hotest";
    	TabSpec tabSpecData = mTabHost.newTabSpec(tabTag);
       mTabHost.addTab(tabSpecData.setIndicator(getString(R.string.hotest_label), null) // the tab icon//  getResources().getDrawable(R.drawable.icon)
               .setContent(new Intent(this, HotestResultsActivity.class)) );// The  contained  activity
       tabList.put(tabTag, tabSpecData);
   }

}