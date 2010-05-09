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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ResultsActivity extends ListActivity{

    private int mNoSearchResultsString = getNoSearchResultsStringId();

    private ProgressBar mEmptyProgress;
    private TextView mEmptyText;
    
    public String fetchURL;
    public int nextPageId = 1;
    
    private SeparatedListAdapter mListAdapter = null;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.loadable_list_activity);
        mEmptyProgress = (ProgressBar)findViewById(R.id.emptyProgress);
        mEmptyText = (TextView)findViewById(R.id.emptyText);
        setLoadingView();
        
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
	                }
	            }
	        });
    }
	public void fetchContent(String uri) {
		try {
			 URL url = new URL(uri);
			 
		     URLConnection con = url.openConnection();  
		          
		     InputStream is = con.getInputStream();  
		 
		     if( mListAdapter == null) {
		    	 mListAdapter = new SeparatedListAdapter(this);
		     }
		    
		     try    {  
		            SAXParserFactory saxFactory = SAXParserFactory.newInstance();  
		            SAXParser parser = saxFactory.newSAXParser();  
		            XMLReader reader = parser.getXMLReader();  
		              
		            SHTSearchResultsSAXHandler handler = new SHTSearchResultsSAXHandler(mListAdapter);
		            reader.setContentHandler(handler);  
		            reader.parse(new InputSource(is));  
		            
		      } catch (Exception e) {  
		    	  setEmptyView();
		      }  
		      ListView listView = getListView();
		      listView.setAdapter(mListAdapter);
		      
		     is.close();  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			setEmptyView();
		}
		if( mListAdapter.getCount() < 0 )
			setEmptyView();
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
    
    public void fetchMore() {
    	String nextUrl = fetchURL + "page=" + nextPageId;
    	nextPageId++;
    	
    }
}
