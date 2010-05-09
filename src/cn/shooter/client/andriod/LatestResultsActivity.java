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

    
}
