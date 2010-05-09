package cn.shooter.client.andriod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.SearchManager;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

      
        menu.add(ShooterClientAndroid.MENU_GROUP_SEARCH, ShooterClientAndroid.MENU_SUBSCRIBE, Menu.NONE, R.string.subscribe_label) //
        	.setIcon(R.drawable.ic_menu_add);

        if(mSubscribed) {
        	menu.add(ShooterClientAndroid.MENU_GROUP_SEARCH, ShooterClientAndroid.MENU_UNSUBSCRIBE, Menu.NONE, R.string.unsubscribe_label) //
        	.setIcon(R.drawable.ic_menu_remove);
	
        }
        return true;
    }
   

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case ShooterClientAndroid.MENU_UNSUBSCRIBE:
	        {
	        	mSubscribed = false;
	        	
            	SharedPreferences.Editor sharedata = getSharedPreferences ( "data", 0). edit ();
            		
            	sharedata.remove( "subscribe1");

	            sharedata.commit ();
            	
	        }
                return true;
            case ShooterClientAndroid.MENU_SUBSCRIBE:
                
            {
            
            	mSubscribed = true;
            	
            	SharedPreferences.Editor sharedata = getSharedPreferences ( "data", 0). edit ();
            	
            	sharedata.putString ( "subscribe1", mKeyword );

	            sharedata.commit ();
            	
            }
                return true;
          
        }
        return super.onOptionsItemSelected(item);
    }
}
