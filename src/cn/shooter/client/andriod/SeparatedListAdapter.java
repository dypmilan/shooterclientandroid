package cn.shooter.client.andriod;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SeparatedListAdapter extends BaseAdapter {
	public final Map<String, Object> itemDatas = new LinkedHashMap<String, Object>();
    
    private LayoutInflater mInflater;
    
    public SeparatedListAdapter(Context context) {
        super();
        mInflater =  LayoutInflater.from(context);
    }

    public SeparatedListAdapter(Context context, int layoutId) {
        super();
        mInflater = LayoutInflater.from(context);
    }
    
    public void addSection(String section, Object dataObj) {
        this.itemDatas.put(section, dataObj);
    }
    
	@Override
	public int getCount() {
		
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
		ItemDataStruct itemData = (ItemDataStruct) getItem(position);
		if ( itemData != null) {
			Log.v("itemData", itemData.toString());
			switch(itemData.itemType){
				case ItemDataStruct.TYPE_SECTION_DETAIL:
			        if (convertView == null) 
			            convertView = mInflater.inflate(R.layout.detail_list_item, null);
			        
			        TextView firstLine  = (TextView) convertView.findViewById(R.id.firstLine);
			        TextView secondLine  = (TextView) convertView.findViewById(R.id.secondLine);
			        TextView timeTextView  = (TextView) convertView.findViewById(R.id.timeTextView);
			        TextView rankTextView  = (TextView) convertView.findViewById(R.id.rankTextView);
			        
			        firstLine.setText(itemData.title);
			        if(itemData.intro != null) {
			        	secondLine.setText(itemData.intro); 
			        	firstLine.setVisibility(View.VISIBLE);
					}
			        else 
			        	secondLine.setVisibility(View.GONE);
			        
			        timeTextView.setText(itemData.timeStamp);
			        
			        if(itemData.rate != null){
			        	int rate = Integer.parseInt(itemData.rate) / 2 ;
			        	String stars = "";
			        	for(int i = 0; i< 5 ; i++) {
			        		if(i <= rate)
			        			stars += "★";
			        		else
			        			stars += "☆";
			        	}
			        	rankTextView.setText(stars);
			        }
					break;
				case ItemDataStruct.TYPE_SECTION_KEYWORDS:
					
					if (convertView == null) 
			            convertView = mInflater.inflate(R.layout.keyword_list_item, null);
					
					TextView titleLine  = (TextView) convertView.findViewById(R.id.firstLine);
					//TextView rankWordsView  = (TextView) convertView.findViewById(R.id.rankTextView);
					titleLine.setText(itemData.keyword);
					//rankWordsView.setText("Rank: "+itemData.keywordRank);
					break;
				default:
					return null;
					
			}
			return  convertView;
		}
		return null;
	}

}
