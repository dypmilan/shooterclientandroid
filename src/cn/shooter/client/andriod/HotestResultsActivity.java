package cn.shooter.client.andriod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class HotestResultsActivity extends ResultsActivity {
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
		  fetchURL = "https://www.shooter.cn/index.xml";
		  
	      super.onCreate(savedInstanceState);
	      

	      ListView listView = getListView();
	      listView.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            	ItemDataStruct itemData = (ItemDataStruct) parent.getAdapter().getItem(position);
	                if (itemData.keyword != null) {
	                    //add new search tab result
	                	ShooterClientAndroid shtc = (ShooterClientAndroid)getParent();
	                	shtc.addTabSearch(itemData.keyword, false);
	                }
	            }
	        });
	  }
}
