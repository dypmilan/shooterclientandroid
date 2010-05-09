package cn.shooter.client.andriod;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ResultsActivity extends ListActivity implements Runnable , OnScrollListener {

    private int mNoSearchResultsString = getNoSearchResultsStringId();

    private ProgressBar mEmptyProgress;
    private TextView mEmptyText;
    
    public String fetchURL;
    private String lastFetchURL;
    private String orgFetchURL;
    
    public int maxSubId = -1;
    public int nextPageId = 1;
    public boolean mBusy = false;
    
    public SeparatedListAdapter mListAdapter = null;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.loadable_list_activity);
        mEmptyProgress = (ProgressBar)findViewById(R.id.emptyProgress);
        mEmptyText = (TextView)findViewById(R.id.emptyText);
        setLoadingView();
        
        orgFetchURL = fetchURL;
	     if( mListAdapter == null) {
	    	 mListAdapter = new SeparatedListAdapter(this);
	     }
	     ListView listView = getListView();
	     listView.setAdapter(mListAdapter);
	     listView.setOnScrollListener(this);  
	    Thread thread = new Thread(this);
        thread.start();
        
    }
	@Override
	public void run() {
		fetchContent(fetchURL);
        
        ListView listView = getListView();
	      listView.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            	ItemDataStruct itemData = (ItemDataStruct) parent.getAdapter().getItem(position);
	                if (itemData.subid != null) {
	                    //Intent intent = new Intent(LatestResultsActivity.this, SubDetailsActivity.class);
	                    //intent.putExtra(UserDetailsActivity.EXTRA_USER_PARCEL, checkin.getUser());
	                    //intent.putExtra(UserDetailsActivity.EXTRA_SHOW_ADD_FRIEND_OPTIONS, true);
	                    //startActivity(intent);
	                	//http://shooter.cn/xml/sub/140/140252.xml
	                	int did = Integer.parseInt(itemData.subid)/1000;
	                	String url = "http://www.shooter.cn/xml/sub/"+did+"/"+itemData.subid + ".xml";
	                	Intent i = new Intent(Intent.ACTION_VIEW);
	                	i.setData(Uri.parse(url));
	                	startActivity(i);
	                }else if (itemData.keyword != null) {
	                    //add new search tab result
	                	ShooterClientAndroid shtc = (ShooterClientAndroid)getParent();
	                	shtc.addTabSearch(itemData.keyword, false);
	                }
	            }
	        });
	      
	      handler.sendEmptyMessage(0);
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case ShooterClientAndroid.MENU_REFRESH:
	        {
	        	setContentView(R.layout.loadable_list_activity);
	            setLoadingView();
	            fetchURL = orgFetchURL ;
		   	   mListAdapter = new SeparatedListAdapter(this);
		   	   ListView listView = getListView();
		   	   listView.setAdapter(mListAdapter);
		   	   Thread thread = new Thread(this);
	           thread.start();
	        }
	        return true;
        }
        return super.onOptionsItemSelected(item);
	}
	private Handler handler = new Handler() {
	
	     @Override
	     public void handleMessage(Message msg) {
	    	 
		     mListAdapter.notifyDataSetChanged();
	     }
	};
	public void fetchContent(String uri) {
		mBusy = true;
		int itemCountBefore =  mListAdapter.getCount();
		try {
			 URL url = new URL(uri);
			 
		     URLConnection con = url.openConnection();  
		          
		     InputStream is = con.getInputStream();  
		 
		    
		     try    {  
		            SAXParserFactory saxFactory = SAXParserFactory.newInstance();  
		            SAXParser parser = saxFactory.newSAXParser();  
		            XMLReader reader = parser.getXMLReader();  
		              
		            SHTSearchResultsSAXHandler handlerSAX = new SHTSearchResultsSAXHandler(mListAdapter);
		            reader.setContentHandler(handlerSAX);  
		            reader.parse(new InputSource(is));  
		            
		            maxSubId = handlerSAX.maxSubId;
		      } catch (Exception e) {  
		    	    if(nextPageId > 1)
						mListAdapter.haveMoreToCome = false;
					else
						setEmptyView();
		      }  
		     
		     is.close();  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(nextPageId > 1)
				mListAdapter.haveMoreToCome = false;
			else
				setEmptyView();
		}
		if( mListAdapter.getCount() <= itemCountBefore ) {
			mListAdapter.haveMoreToCome = false;
		}
		if( mListAdapter.getCount() <= 0 )
			setEmptyView();
		
		mBusy = false;
	}
	public void setEmptyView() {
        mEmptyProgress.setVisibility(ViewGroup.GONE);
        mEmptyText.setText(mNoSearchResultsString);
    }

    public void setLoadingView() {
        mEmptyProgress.setVisibility(ViewGroup.VISIBLE);
        mEmptyText.setText(R.string.loading);
    }

    public int getNoSearchResultsStringId() {
        return R.string.no_search_results;
    }
    
    public void  fetchMore() {
    	if(lastFetchURL == fetchURL) {
    		//Log.v("Fuck ", "Fetc");
    		mListAdapter.haveMoreToCome = false;
    		mListAdapter.notifyDataSetChanged();
    		return;
    	}
    	//nextPageId++;
    	
    	//fetchURL = "http://shooter.cn/xml/list/sub/14/14030.xml";
    	Thread thread = new Thread(this);
        thread.start();
    }
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		int lastItem=firstVisibleItem+visibleItemCount;  
		if(lastItem >= totalItemCount && mBusy == false
				&& mListAdapter.getCount() > 0 && mListAdapter.haveMoreToCome ){
			//Log.v("itemData", "z "+ lastItem + " y " + totalItemCount);
			lastFetchURL = fetchURL;
			fetchMore();
		}
		
		
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
}
