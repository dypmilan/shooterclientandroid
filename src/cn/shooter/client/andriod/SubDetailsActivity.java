package cn.shooter.client.andriod;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SubDetailsActivity extends Activity  implements Runnable{
	
    private ProgressBar mEmptyProgress;
    private TextView mEmptyText;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	     setContentView(R.layout.loadable_detail_activity);
	     
	     mEmptyProgress = (ProgressBar)findViewById(R.id.emptyProgress);
	     mEmptyText = (TextView)findViewById(R.id.emptyText);
	     setLoadingView();
	     
	     Thread thread = new Thread(this);
	     thread.start();
	        
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	private Handler handler = new Handler() {
		
	     @Override
	     public void handleMessage(Message msg) {
	    	 
	    	setEmptyView();
	     }
	};
	
	public void setEmptyView() {
        mEmptyProgress.setVisibility(ViewGroup.GONE);
        mEmptyText.setText(R.string.no_search_results);
    }

    public void setLoadingView() {
        mEmptyProgress.setVisibility(ViewGroup.VISIBLE);
        mEmptyText.setText(R.string.loading);
    }
}
