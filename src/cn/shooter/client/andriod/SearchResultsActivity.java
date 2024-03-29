package cn.shooter.client.andriod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.SearchManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResultsActivity extends ResultsActivity {

	private String mKeyword;
	private boolean mSubscribed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Bundle extras = getIntent().getExtras();
    	
    	mKeyword = extras.getString("keyword");
    	mSubscribed = extras.getBoolean("subscribed");

    	try {
			fetchURL = "https://www.shooter.cn/search/Sub:"+URLEncoder.encode(mKeyword,"UTF-8")+"/";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			
		}
    	
        super.onCreate(savedInstanceState);
	   
    }
    @Override
	public void fetchMore() {
    	//https://shooter.cn/xml/list/sub/14/14030.xml
    	
    	try {
			fetchURL = "https://www.shooter.cn/search/Sub:"+URLEncoder.encode(mKeyword,"UTF-8")+"/?page="+nextPageId;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    	//Log.v("Fetch "+nextPageId, fetchURL);
    	nextPageId++;
    	super.fetchMore();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	
    	super.onPrepareOptionsMenu(menu);
        if(mSubscribed) {
        	menu.add(ShooterClientAndroid.MENU_GROUP_SEARCH, ShooterClientAndroid.MENU_UNSUBSCRIBE, Menu.NONE, R.string.unsubscribe_label) //
        	.setIcon(R.drawable.ic_menu_delete);
	
        }else {
            menu.add(ShooterClientAndroid.MENU_GROUP_SEARCH, ShooterClientAndroid.MENU_SUBSCRIBE, Menu.NONE, R.string.subscribe_label) //
            	.setIcon(R.drawable.ic_menu_add);
        }

        MenuItem mi = menu.add(ShooterClientAndroid.MENU_GROUP_SEARCH, ShooterClientAndroid.MENU_CLOSE_TAB, Menu.NONE, R.string.close_tab_label) ;
    	mi.setIcon(R.drawable.ic_menu_close);
    	
	    return true;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	return super.onCreateOptionsMenu(menu);
    }
   

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case ShooterClientAndroid.MENU_UNSUBSCRIBE:
	        {
	        	mSubscribed = false;
	        	ShooterClientAndroid shtc = (ShooterClientAndroid)getParent();
	        	shtc.removeSubscribe( mKeyword );
	        	shtc.closeCurrentTab();
	        }
                return true;
            case ShooterClientAndroid.MENU_SUBSCRIBE:
                
            {
            
            	mSubscribed = true;
            	
            	ShooterClientAndroid shtc = (ShooterClientAndroid)getParent();
	        	shtc.addSubscribe( mKeyword ); 
            	
            }
                return true;
          
        }
        return super.onOptionsItemSelected(item);
    }
}
