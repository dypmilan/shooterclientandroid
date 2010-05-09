package cn.shooter.client.andriod;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
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
    
    private static final  String SUBSCRIBE_DATABASE_NAME = "ShooterClient.db";
    private SQLiteDatabase db = null;
    private SQLiteStatement insertStmt;
    private SQLiteStatement deleteStmt;
    
    
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
    	
    	if(this.db == null) {
	    	OpenHelper  openHelper = new OpenHelper (getBaseContext());
	        this.db = openHelper.getWritableDatabase();
	        insertStmt = db.compileStatement("INSERT INTO subscribes ( sword ) VALUES ( ? ) ");
	        deleteStmt = db.compileStatement("DELETE FROM subscribes WHERE sword  = ? ");
    	}
        
    	
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

    
    private static class  OpenHelper extends  SQLiteOpenHelper {

    	   OpenHelper(Context context) {
    	      super(context, SUBSCRIBE_DATABASE_NAME, null, 1);
    	   }

    	   @Override
    	   public void onCreate(SQLiteDatabase db) {
    	      db.execSQL("CREATE TABLE IF NOT EXISTS subscribes ( sword TEXT PRIMARY KEY, lastCheckedTs INTEGER, lastCheckedCount INTEGER)");
    	   }

    	   @Override
    	   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	      if(newVersion > oldVersion )
    	    	  db.execSQL("DROP TABLE IF EXISTS subscribes");
    	      
    	      onCreate(db);
    	   }
    	}
    
    public void addSubscribe(String Keyword) {
    	//Log.v( "INSERT" ,  Keyword);
    	try {
    		this.insertStmt.bindString(1, Keyword);
			this.insertStmt.executeInsert();
    		
	    } catch (SQLException e) {  
	    	//Log.v( "INSERT" , e.getMessage() );
       }  
    }
    public void removeSubscribe(String Keyword) {
    	//Log.v( "delete" ,  Keyword);
    	try {
    		//TODO: what if there is quote mark inside??
    		this.deleteStmt.bindString(1, Keyword);
			this.deleteStmt.execute();
    		
    	} catch (SQLException e) {  
    		//Log.v( "delete" , e.getMessage() );
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
        
       
       try {
        Cursor cursor = this.db.query("subscribes", new String[] { "sword" },  null, null, null, null, null);
        if (cursor.moveToFirst()) {
           do {
           	addTabSearch(cursor.getString(0), true);
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
       }  catch (Exception e) {  
    	   Log.v( "cursor" , e.getMessage() );
       } 
        
        //Backward compatibility
        SharedPreferences sharedata = getSharedPreferences ( "data", 0);
        String Subscribe1 = sharedata.getString("subscribe1", null);
		if(Subscribe1 != null && Subscribe1.trim() != "") {
				addTabSearch(Subscribe1, true);
				addSubscribe(Subscribe1);
				
				SharedPreferences.Editor sharedataeditor = sharedata. edit ();
				sharedataeditor.remove( "subscribe1");
				sharedataeditor.commit ();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	 menu.add(MENU_GROUP_SEARCH, MENU_SEARCH, Menu.NONE, R.string.search_label) //
         .setIcon(R.drawable.ic_menu_search) //
         .setAlphabeticShortcut(SearchManager.MENU_KEY);

		 menu.add(MENU_GROUP_SEARCH, MENU_REFRESH, Menu.NONE, R.string.refresh_label) //
		 .setIcon(R.drawable.ic_menu_refresh);
		return super.onPrepareOptionsMenu(menu);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	return super.onCreateOptionsMenu(menu);

    }
   

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SEARCH:
                onSearchRequested();
                return true;
          
            case MENU_CLOSE_TAB:
            	closeCurrentTab();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void closeCurrentTab(){
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