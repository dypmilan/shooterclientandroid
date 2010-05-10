package cn.shooter.client.andriod;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

public class SubDetailsActivity extends ListActivity   implements Runnable{
	
    private ProgressBar mEmptyProgress;
    private TextView mEmptyText;
    private RatingBar mRatingBar; 
    private ImageButton  mDownloadButton;
    private WebView mIntroView;
    
    private String mSubid;
    private SubDetailStruct subDetail;
    
    public CommentsListAdapter mListAdapter = null;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	     setContentView(R.layout.loadable_detail_activity);
	     
	     Bundle extras = getIntent().getExtras();
	     mSubid = extras.getString("subid");
	    	
	     mEmptyProgress = (ProgressBar)findViewById(R.id.emptyProgress);
	     mEmptyText = (TextView)findViewById(R.id.emptyText);
	     mRatingBar = (RatingBar)findViewById(R.id.rating_bar);
	     mDownloadButton =  (ImageButton)findViewById(R.id.detailDownloadButton);
	     mIntroView = (WebView)findViewById(R.id.item_intros);
	     
	     setLoadingView();
	     
	     if( mListAdapter == null) {
	    	 mListAdapter = new CommentsListAdapter(this);
	     }
	     ListView listView = getListView();
	     listView.setAdapter(mListAdapter);
	     
	     Thread thread = new Thread(this);
	     thread.start();
	        
	}

	@Override
	public void run() {
		//http://shooter.cn/xml/sub/140/140323.xml
		int did = Integer.parseInt(mSubid)/1000;
		fetchContent("https://www.shooter.cn/xml/sub/"+did+"/"+mSubid+".xml");
		
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		
	     @Override
	     public void handleMessage(Message msg) {
	    	 
	    	 if (subDetail != null) {
		    	 mEmptyProgress.setVisibility(ViewGroup.GONE);
		         mEmptyText.setVisibility(ViewGroup.GONE);
		         
		    	 showDetailInfo();
		    	 
		    	 if(mListAdapter.getCount() > 0)
		    		 mListAdapter.notifyDataSetChanged();
		    	 
	    	 } else 
	    		 setEmptyView();
	     }
	};
	
	public void fetchContent(String uri) {
		
		try {
			 URL url = new URL(uri);
			 
		     URLConnection con = url.openConnection();  
		          
		     InputStream is = con.getInputStream();  
		 
		    
		     try    {  
		            SAXParserFactory saxFactory = SAXParserFactory.newInstance();  
		            SAXParser parser = saxFactory.newSAXParser();  
		            XMLReader reader = parser.getXMLReader();  
		              
		            SHTDetailResultsSAXHandler handlerSAX = new SHTDetailResultsSAXHandler(mListAdapter);
		            reader.setContentHandler(handlerSAX);  
		            reader.parse(new InputSource(is));  
		            
		            subDetail = handlerSAX.itemData;
		      } catch (Exception e) {  
		    	   Log.v("fuck", e.getMessage());
		      }  
		     
		     is.close();  
		} catch (Exception e) {
			Log.v("fuck2", e.getMessage());
		}
		
		
	}
	public void showDetailInfo() {
		findViewById(R.id.detailInfoSec).setVisibility(ViewGroup.VISIBLE);
		
		TextView tempTextView = (TextView)findViewById(R.id.detailTitleName);
		tempTextView.setText(subDetail.sTitle);
		
		tempTextView = (TextView)findViewById(R.id.detailInfo);
		tempTextView.setText(subDetail.sInfo);
		
		tempTextView = (TextView)findViewById(R.id.upTimeTextView);
		tempTextView.setText(subDetail.timeStamp);
		
		if( mListAdapter.getCount() > 0 ) {
        	TextView totalPostsView  = (TextView) findViewById(R.id.totalPosts);
        	totalPostsView.setText("("+mListAdapter.getCount()+")");
        }
		if (subDetail.sRating != null) {
			float rate = ((float)Integer.parseInt(subDetail.sRating)) / 2 ;
			
			mRatingBar.setVisibility(ViewGroup.VISIBLE);
			mRatingBar.setRating(rate);
		}
		if( subDetail.sIntro != null ) {
			mIntroView.loadData(subDetail.sIntro, "text/html", "utf-8");
			mIntroView.setVisibility(ViewGroup.VISIBLE);
		}

		
		
		//TODO user VOTE
		
		
		//TODO user comment

	}
	public void setEmptyView() {
        mEmptyProgress.setVisibility(ViewGroup.GONE);
        mEmptyText.setText(R.string.no_search_results);
    }

    public void setLoadingView() {
        mEmptyProgress.setVisibility(ViewGroup.VISIBLE);
        mEmptyText.setText(R.string.loading);
    }
}
