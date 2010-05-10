package cn.shooter.client.andriod;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.admob.android.ads.*;


public class SubDetailsActivity extends ListActivity   implements OnClickListener, Runnable{
	
    private ProgressBar mEmptyProgress;
    private TextView mEmptyText;
    private RatingBar mRatingBar; 
    private Button  mDownloadButton;
    private WebView mIntroView;
    
    private static final int DIALOG_COMMENTADD = 1;
    private static final int DIALOG_LOGON = 2;
    
    private String mMyComment;
    private String mSubid;

    private String mLoc;
    private SubDetailStruct subDetail;
    
    public CommentsListAdapter mListAdapter = null;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	     setContentView(R.layout.loadable_detail_activity);
	     
	     Bundle extras = getIntent().getExtras();
	     mSubid = extras.getString("subid");
	    /*
	     AdManager.setTestDevices( new String[] {
                 AdManager.TEST_EMULATOR, // Android emulator
                 "10A1B7E9E780292FC509449E259476B2", // My  Test Phone
	     } ); 
	     */
	     mEmptyProgress = (ProgressBar)findViewById(R.id.emptyProgress);
	     mEmptyText = (TextView)findViewById(R.id.emptyText);
	     mRatingBar = (RatingBar)findViewById(R.id.rating_bar);
	     mDownloadButton =  (Button)findViewById(R.id.detailDownloadButton);
	     mDownloadButton.setOnClickListener( this );

	     
	     mIntroView = (WebView)findViewById(R.id.item_intros);
	     
	     setLoadingView();
	     
	     if( mListAdapter == null) {
	    	 mListAdapter = new CommentsListAdapter(this);
	     }
	     ListView listView = getListView();
	     listView.setAdapter(mListAdapter);
	     
	     Thread thread = new Thread(this);
	     thread.start();
	        

         mLoc = "@" + Build.MODEL ;
         /*LocationManager llm = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
         Location cLoc = llm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        
         if( cLoc != null ){
         	mLoc += "@" + cLoc.getProvider();
         }
         */
	}

	@Override
	public void run() {
		//http://shooter.cn/xml/sub/140/140323.xml
		int did = Integer.parseInt(mSubid)/1000;
		fetchContent("https://www.shooter.cn/xml/sub/"+did+"/"+mSubid+".xml");
		
		handler.sendEmptyMessage(0);
	}
	
	public Handler handler = new Handler() {
		
	     @Override
	     public void handleMessage(Message msg) {
	    	 switch(msg.what) {
	    	 	case 0:
		    	 if (subDetail != null) {
			    	 mEmptyProgress.setVisibility(ViewGroup.GONE);
			         mEmptyText.setVisibility(ViewGroup.GONE);
			         
			    	 showDetailInfo();
			    	 
			    	 if(mListAdapter.getCount() > 0)
			    		 mListAdapter.notifyDataSetChanged();
			    	 
		    	 } else 
		    		 setEmptyView();
		    	 break;
	    	 	case 1:
	    	 		refreshActivity();
	    	 		break;
	    	 }
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
		
		try {
			//http://www.shooter.cn/discuz/mobileposts.php?threadid=156617
			
			 URL url = new URL("https://www.shooter.cn/discuz/mobileposts.php?threadid="+subDetail.sThreadID+"&ptype=sub&aboutid="+mSubid);
			 
		     URLConnection con = url.openConnection();  
		          
		     InputStream is = con.getInputStream();  
		 
		    
		     try    {  
		            SAXParserFactory saxFactory = SAXParserFactory.newInstance();  
		            SAXParser parser = saxFactory.newSAXParser();  
		            XMLReader reader = parser.getXMLReader();  
		              
		            SHTDetailResultsSAXHandler handlerSAX = new SHTDetailResultsSAXHandler(mListAdapter);
		            reader.setContentHandler(handlerSAX);  
		            reader.parse(new InputSource(is));  
		            
		            
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
			mIntroView.loadDataWithBaseURL (null, subDetail.sIntro, "text/html", "utf-8", "about:blank");
			mIntroView.setVisibility(ViewGroup.VISIBLE);
		}

		
		
		//TODO user VOTE
		
		

	}
	public void setEmptyView() {
		findViewById(R.id.detailInfoSec).setVisibility(ViewGroup.GONE);
    	mEmptyProgress.setVisibility(ViewGroup.GONE);
        mEmptyText.setText(R.string.no_search_results);
    }

    public void setLoadingView() {
    	findViewById(R.id.detailInfoSec).setVisibility(ViewGroup.GONE);
    	mEmptyProgress.setVisibility(ViewGroup.VISIBLE);
        mEmptyText.setText(R.string.loading);
    }

	@Override
	public void onClick(View v) {
		// Download SubFile
		if(subDetail != null && subDetail.sFileID != null ) {
			mIntroView.loadDataWithBaseURL (null, "正在读取……", "text/html", "utf-8", "about:blank");
			mIntroView.setVisibility(ViewGroup.VISIBLE);
			
			mIntroView.getSettings().setJavaScriptEnabled(true);

			mIntroView.loadUrl("https://www.shooter.cn/files/mobile.html?fileid=" + subDetail.sFileID);
			mIntroView.setVisibility(ViewGroup.VISIBLE);
		}
	}
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	
		menu.clear();
		
    	super.onPrepareOptionsMenu(menu);

		 menu.add(ShooterClientAndroid.MENU_GROUP_SEARCH, ShooterClientAndroid.MENU_REFRESH, Menu.NONE, R.string.refresh_label) //
		 .setIcon(R.drawable.ic_menu_refresh);
		 
        MenuItem mi = menu.add(ShooterClientAndroid.MENU_GROUP_SEARCH, ShooterClientAndroid.MENU_POST_COMMENT, Menu.NONE, R.string.post_comment_label) ;
    	mi.setIcon(R.drawable.ic_menu_comment);
    	
	    return true;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	return super.onCreateOptionsMenu(menu);
    }
   
    public void refreshActivity() {
    	setLoadingView();
 	     
 	     mListAdapter = new CommentsListAdapter(this);
 	     
 	     ListView listView = getListView();
 	     listView.setAdapter(mListAdapter);
 	     
 	     Thread thread = new Thread(this);
 	     thread.start();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case ShooterClientAndroid.MENU_POST_COMMENT:
	        {
	        	//SharedPreferences sharedata = getSharedPreferences ( "data", 0);
	            //String shash = sharedata.getString("shash", null);
	            //if(shash != null) 
	            	showDialog(DIALOG_COMMENTADD);
	            //else
	            //	showDialog(DIALOG_LOGON);
	        }
            return true;
	        case ShooterClientAndroid.MENU_REFRESH:
	        {
	        	refreshActivity();
	        }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    

    @Override
    public Dialog onCreateDialog(int id) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout;

        switch (id) {
	        case DIALOG_LOGON:
	        {
	            layout = inflater.inflate(R.layout.logon_dialog,
	                    (ViewGroup) findViewById(R.id.layout_root));
	
	            final EditText userName = (EditText) layout.findViewById(R.id.userName);
	            final EditText userPass = (EditText) layout.findViewById(R.id.userPass);
	 
	            
	            return new AlertDialog.Builder(this) //
	                    .setView(layout) //
	                    .setIcon(android.R.drawable.ic_dialog_alert) // icon
	                    .setTitle("登录") // title
	                    .setPositiveButton("登录 ", new Dialog.OnClickListener()
	                    {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            String u = userName.getText().toString();
	                            String p = userPass.getText().toString();
	                            
	                            if(uLogOn(u, p))
	                            {
	                            	showDialog(DIALOG_COMMENTADD);
	                            }else{
	                            	
	                            }
	                            //post tip
	                            //new TipAddTask().execute(tip, type);
	                            //setLoadingView();
	                            //new CommentAddTask(SubDetailsActivity.this, comm, mSubid , subDetail.sThreadID, mLoc).execute(comm, mSubid);
	                            
	                            
	                        }
	                    }) //
	                    .setNegativeButton("Cancel", new Dialog.OnClickListener() {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            
	                            dismissDialog(DIALOG_LOGON);
	                        }
	                    }).create();
	        }
            case DIALOG_COMMENTADD:
            {
                layout = inflater.inflate(R.layout.comment_add_dialog,
                        (ViewGroup) findViewById(R.id.layout_root));

                final EditText editText = (EditText) layout.findViewById(R.id.editText);
                final EditText userNameText = (EditText) layout.findViewById(R.id.userName);
                SharedPreferences sharedata = getSharedPreferences ( "data", 0);
                String postName = sharedata.getString("postname", "匿名");
        		/*
                 LocationManager llm = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
                Location cLoc = llm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
               
                if( cLoc != null ){
                	mLoc += "@" + cLoc.getProvider();
                }
                */
                
                userNameText.setText(postName);
                
                return new AlertDialog.Builder(this) //
                        .setView(layout) //
                        .setIcon(android.R.drawable.ic_dialog_alert) // icon
                        .setTitle("发议论 ") // title
                        .setPositiveButton("发表", new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String comm = editText.getText().toString();
                                editText.setText("");
                                
                                String poName = userNameText.getText().toString();
                                SharedPreferences sharedata = getSharedPreferences ( "data", 0);
                                SharedPreferences.Editor sharedataeditor = sharedata. edit ();
                        		sharedataeditor.putString( "postname", poName);
                        		sharedataeditor.commit ();
                                //post tip
                                //new TipAddTask().execute(tip, type);
                                setLoadingView();
                                new CommentAddTask(SubDetailsActivity.this, comm, mSubid , subDetail.sThreadID, poName + mLoc).execute(comm, mSubid);
                            }
                        }) //
                        .setNegativeButton("Cancel", new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editText.setText("");
                                
                                dismissDialog(DIALOG_COMMENTADD);
                            }
                        }).create();
            }
        }
        return null;
    }
    
    public boolean uLogOn(String uname, String pass){
    	 try {
    		   DefaultHttpClient httpclient = new DefaultHttpClient();  
		 	   HttpResponse response;  
		 	   HttpPost httpost = new HttpPost("http://www.shooter.cn/user/logon.php");  
		 	   List <NameValuePair> nvps = new ArrayList <NameValuePair>();  
		 	   nvps.add(new BasicNameValuePair("useremail", uname));   
		 	   nvps.add(new BasicNameValuePair("passwd1", pass));
		 	  
		 	   httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			
		 	   //httpost.setHeader(name, value)
			   
		 	   response = httpclient.execute(httpost);  
		 	   HttpEntity entity = response.getEntity();  
		 	   //entity = response.getEntity();  
		 	     
		 	   //Log.d(TAG, "HTTP POST getStatusLine: " + response.getStatusLine());  
		 	   
		 	   /* HTML POST response BODY */  
		 	   String strRet = EntityUtils.toString(entity);  
		 	   //Log.i(TAG, strRet);  
		 	   strRet = strRet.trim().toLowerCase();  
		 	     
		 	   List<Cookie> cookies = httpclient.getCookieStore().getCookies();  
		 	   if (entity != null)  
		 	   {  
		 	     entity.consumeContent();  
		 	   }  
		 	     
		 	   //Log.d(TAG, "HTTP POST Initialize of cookies.");   
		 	   cookies = httpclient.getCookieStore().getCookies();   
		 	   if (cookies.isEmpty())  
		 	   {  
		 	     //Log.d( "HTTP POST Cookie not found.",  "HTTP POST Cookie not found.");  
		 	     //Log.i(TAG, entity.toString());  
		 	   }  
		 	   else  
		 	   {  
		 		  SharedPreferences sharedata = getSharedPreferences ( "data", 0);
		 	      SharedPreferences.Editor sharedataeditor = sharedata. edit ();
		 					sharedataeditor.remove( "subscribe1");
		 	     boolean successsLogon = false;
		 	     for (int i = 0; i < cookies.size(); i++)  
		 	     {  
		 	    	String cookieName = cookies.get(i).getName();
		 	    	String cookieValue = cookies.get(i).getValue();
		 	    	
		 	    	if(cookieName == "uid")
		 	    		sharedataeditor.putString("uid",  cookieValue);
		 	    	
		 	    	if(cookieName == "shash"){
		 	    		sharedataeditor.putString("shash",  cookieValue);
		 	    		successsLogon = true;
		 	    	}
		 	     }   
		 	    sharedataeditor.commit();
		 	    return successsLogon;
		 	   }  
    	 } catch (Exception e) {
    			// TODO Auto-generated catch block
    		 //Log.d( "HTTP ", e.getMessage());
    	 }  
    	 	
    	return false;
    }
    public boolean postComm(String comm , String subid, String threadid, String mobileUser) {
    	try  
    	 {  
    	   DefaultHttpClient httpclient = new DefaultHttpClient();  
    	   HttpResponse response;  
    	   HttpPost httpost = new HttpPost("https://www.shooter.cn/discuz/mobileposthandle.php");  
    	   List <NameValuePair> nvps = new ArrayList <NameValuePair>();  
    	   nvps.add(new BasicNameValuePair("threadid", threadid));   
    	   nvps.add(new BasicNameValuePair("aboutid", subid));
    	   nvps.add(new BasicNameValuePair("ptype", "sub"));
    	   nvps.add(new BasicNameValuePair("postid", ""));
    	   nvps.add(new BasicNameValuePair("post", comm));
    	   nvps.add(new BasicNameValuePair("mobile", mobileUser));
    	   nvps.add(new BasicNameValuePair("submit", "发表评论"));
    	   nvps.add(new BasicNameValuePair("mobileuser", "android"));
    	   httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));  
    	
    	   //httpost.setHeader(name, value)
   	   
    	   response = httpclient.execute(httpost);  
    	   HttpEntity entity = response.getEntity();  
    	   //entity = response.getEntity();  
    	     
    	   //Log.d(TAG, "HTTP POST getStatusLine: " + response.getStatusLine());  
    	   
    	   /* HTML POST response BODY */  
    	   String strRet = EntityUtils.toString(entity);  
    	   //Log.i(TAG, strRet);  
    	   strRet = strRet.trim().toLowerCase();  
    	     
    	   List<Cookie> cookies = httpclient.getCookieStore().getCookies();  
    	   if (entity != null)  
    	   {  
    	     entity.consumeContent();  
    	   }  
    	     
    	   //Log.d(TAG, "HTTP POST Initialize of cookies.");   
    	   cookies = httpclient.getCookieStore().getCookies();   
    	   if (cookies.isEmpty())  
    	   {  
    	     //Log.d(TAG, "HTTP POST Cookie not found.");  
    	     //Log.i(TAG, entity.toString());  
    	   }  
    	   else  
    	   {  
    	     for (int i = 0; i < cookies.size(); i++)  
    	     {  
    	       //Log.d(TAG, "HTTP POST Found Cookie: " + cookies.get(i).toString());   
    	     }   
    	   }  
    	     
    	   if(strRet.equals("y"))  
    	   {  
    	     //Log.i("TEST", "YES");  
    	     return true;  
    	   }  
    	   else  
    	   {  
    	     //Log.i("TEST", "NO");  
    	     return false;  
    	   }  
    	 }  
    	 catch(Exception e)  
    	 {  
    	   //e.printStackTrace();  
    	   return false;  
    	 }  
    }
    @Override
    public void onPrepareDialog(int id, Dialog dialog) {
        // If the tip add was a success we must have set mStateHolder.tip. If
        // that is the case, then
        // we clear the dialog because clearly they're looking to add a new tip
        // and not post the
        // same one again.
        if (id == DIALOG_COMMENTADD && mMyComment != null) {
            ((EditText) dialog.findViewById(R.id.editText)).setText("");
            
        }
    }

}
