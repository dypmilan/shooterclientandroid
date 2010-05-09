package cn.shooter.client.andriod;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class LatestResultsActivity extends ResultsActivity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	fetchURL = "https://www.shooter.cn/sub/";
    	
        super.onCreate(savedInstanceState);
      
    }
    @Override
	public void fetchMore(){
    	//https://shooter.cn/xml/list/sub/14/14030.xml
    	
    	 int curPageId = (maxSubId - nextPageId*10) / 10;
    	 if(curPageId > 0) {
	    	 fetchURL = "https://www.shooter.cn/xml/list/sub/"+curPageId/1000+"/"+curPageId+".xml";
	    	 //Log.v("Fetch "+nextPageId, fetchURL);
	    	 nextPageId++;
	    	 
    	 } 
    	 super.fetchMore();
    }
}
