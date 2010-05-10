package cn.shooter.client.andriod;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class CommentsListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public final Map<String, Object> itemDatas = new LinkedHashMap<String, Object>();
	

    public CommentsListAdapter(Context context) {
        super();
        mInflater =  LayoutInflater.from(context);
    }

    public CommentsListAdapter(Context context, int layoutId) {
        super();
        mInflater = LayoutInflater.from(context);
    }
    

    
    public void addSection(String section, Object dataObj) {
    	Log.v("addSection", section);
        this.itemDatas.put(section, dataObj);
    }
    
	@Override
	public int getCount() {
		Log.v("getCount", "c "+this.itemDatas.size());
		return this.itemDatas.size();
	}

	@Override
	public Object getItem(int position) {
		
		int id = 0;
	    for ( Object dataObj : this.itemDatas.values()) {
	            if ( id == position) 
	            	return dataObj;
	            if ( id > position)
	            	break;
	            id++;
	     }
	     
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CommentDataStruct itemData = (CommentDataStruct) getItem(position);
		if ( itemData != null) {
			Log.v("itemData", itemData.toString() + " " + position);
			if (convertView == null)  
			   convertView = mInflater.inflate(R.layout.comment_list_item, null);
			        
			        TextView contentLine  = (TextView) convertView.findViewById(R.id.contentLine);
			        contentLine.setText(itemData.sContent);
			        
			        TextView postTimeLine  = (TextView) convertView.findViewById(R.id.postTimeTextView);
			        postTimeLine.setText(itemData.sPosttime);
			        
			        TextView posterNameLine  = (TextView) convertView.findViewById(R.id.posterName);
			        posterNameLine.setText(itemData.sPosterName);
			        
			return  convertView;
		}
		return null;
	}


}
