package cn.shooter.client.andriod;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SHTSearchResultsSAXHandler extends DefaultHandler {
	private SeparatedListAdapter m_ListAdapter;
	private ItemDataStruct itemData;
	private final int XML_TYPE_SEARCH_RESULT = 0;
	private final int XML_TYPE_HOT_WORDS = 1;
	private final int XML_TYPE_SKIP = -1;
	private int mXMLType = XML_TYPE_SEARCH_RESULT;
	
	public SHTSearchResultsSAXHandler(SeparatedListAdapter mListAdapter) {
		m_ListAdapter = mListAdapter;
	}


    private StringBuilder builder;

    // 打开 xml 文档的回调函数  
    @Override  
    public void startDocument() throws SAXException {  
        // TODO Auto-generated method stub  
        super.startDocument();  
        builder = new StringBuilder();

    }  
      
    // 关闭 xml 文档的回调函数  
    @Override  
    public void endDocument() throws SAXException {  
        // TODO Auto-generated method stub  
        super.endDocument();  
    }  
      
    // 一发现元素开始标记就回调此函数  
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        
        if (localName == "subrief" && mXMLType != XML_TYPE_HOT_WORDS )  {
        	itemData =  new ItemDataStruct();
        	itemData.itemType = ItemDataStruct.TYPE_SECTION_DETAIL ;
        }
        if (localName == "hotsearchsub")
        	mXMLType = XML_TYPE_HOT_WORDS;
        
        if (localName == "ITEM" && mXMLType == XML_TYPE_HOT_WORDS )  {
        	itemData =  new ItemDataStruct();
        	itemData.itemType = ItemDataStruct.TYPE_SECTION_KEYWORDS ;
        }
        
        
    }  
    private String myConcat(String one, String two, String Sep, String before  )
    {
    	if(two.trim().length() > 0 &&  two != "null"){
	    	if(one == null){
	    		one = "";
	    	}else{
	    		one += Sep;
	    	}
	    	
	    	return one + before + two.trim();
    	}
    
    	return one;
    }
    // 一发现元素结束标记就回调此函数  
    @Override  
    public void endElement(String uri, String localName, String qName)  
            throws SAXException {  
    	super.endElement(uri, localName, qName);

    	if(builder.length() > 0){
    		if(mXMLType == XML_TYPE_HOT_WORDS) {
    			if (localName == "SWORD")
    				itemData.keyword = builder.toString().trim();
    			else if (localName == "COUNT")
    				itemData.keywordRank = builder.toString().trim();
    			else if (localName == "hotsearchsub")
    				mXMLType = XML_TYPE_SKIP;
    		} else if(mXMLType == XML_TYPE_SEARCH_RESULT) {
		        if (localName == "orgname")
		        	itemData.title = myConcat(itemData.title , builder.toString(), "/", "");
		        else if (localName == "engname")  
		        	itemData.title = myConcat(itemData.title , builder.toString(), "/", "");
		        else if (localName == "chnname")  
		        	itemData.title = myConcat(itemData.title , builder.toString(), "/", "");
		        else if (localName == "uptime")  
		        	itemData.timeStamp =  builder.toString().trim();
		        else if (localName == "splitby")
		        	itemData.intro = myConcat(itemData.intro , builder.toString(), " ", "版本：");
		        else if (localName == "language")
		        	itemData.intro = myConcat(itemData.intro , builder.toString(), " ", "语种：");
		        else if (localName == "uploader")
		        	itemData.intro = myConcat(itemData.intro , builder.toString(), " ", "上传：");
		        else if (localName == "id")
		        	itemData.subid =  builder.toString().trim();
		        else if (localName == "fileid")
		        	itemData.fileid =  builder.toString().trim();
		        else if (localName == "rate")
		        	itemData.rate = builder.toString().trim();
    		}
    	}
    	if (localName == "subrief" && mXMLType == XML_TYPE_SEARCH_RESULT && itemData.title != null)  
        	m_ListAdapter.addSection("DETAIL_"+itemData.title+itemData.subid, itemData);
    	else if (localName == "ITEM" && mXMLType == XML_TYPE_HOT_WORDS && itemData.keyword != null){
    		m_ListAdapter.addSection("HOT_"+itemData.keyword, itemData);
    	}
    		
        builder.setLength(0);    

    }  
  
    // 一发现元素值或属性值就回调此函数  
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

     
}
