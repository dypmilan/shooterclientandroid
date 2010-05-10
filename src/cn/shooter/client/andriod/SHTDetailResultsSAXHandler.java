package cn.shooter.client.andriod;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SHTDetailResultsSAXHandler extends DefaultHandler {
	private CommentsListAdapter m_ListAdapter;
	
	public SubDetailStruct itemData;
	private CommentDataStruct commentData;
	
	private final int XML_TYPE_SEARCH_RESULT = 0;
	private final int XML_TYPE_HOT_WORDS = 1;
	private final int XML_TYPE_SKIP = -1;
	private int mXMLType = XML_TYPE_SEARCH_RESULT;
	public int maxSubId = -1;
	
	public SHTDetailResultsSAXHandler(CommentsListAdapter mListAdapter) {
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
        
        if (localName == "subdetail" )  {
        	itemData =  new SubDetailStruct();
        } else if (localName == "post") {
        	commentData = new CommentDataStruct();
    	} 
        else if (itemData != null && localName == "threadinfo") {
    		itemData.sThreadID = "0";
    	}
        
        
    }  
    private String myConcat(String one, String two, String Sep, String before  )
    {
    	if(two.trim().length() > 0 &&  two != "null"){
	    	if(one == null){
	    		one = "";
	    	}else{
	    		if( one.indexOf(two) >= 0 )
	    			return one;
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
    		
    		if(itemData != null ) {
		        if (localName == "orgname" || localName == "engname" || localName == "chnname" 
		        	|| localName == "hkname" || localName == "twname" || localName == "akaname"
		        		|| localName == "othname" )
		        	itemData.sTitle = myConcat(itemData.sTitle , builder.toString(), "/", "");
		        else if (localName == "uptime")  
		        	itemData.timeStamp =  builder.toString().trim();
		        else if (localName == "splitby")
		        	itemData.sInfo = myConcat(itemData.sInfo , builder.toString(), " ", "版本：");
		        else if (localName == "language")
		        	itemData.sInfo = myConcat(itemData.sInfo , builder.toString(), " ", "语种：");
		        else if (localName == "producer")
		        	itemData.sInfo = myConcat(itemData.sInfo , builder.toString(), " ", "制作：");
		        else if (localName == "verifier")
		        	itemData.sInfo = myConcat(itemData.sInfo , builder.toString(), " ", "校订：");
		        else if (localName == "uploader")
		        	itemData.sInfo = myConcat(itemData.sInfo , builder.toString(), " ", "上传：");
		        else if (localName == "intro")
		        	itemData.sIntro = myConcat(itemData.sIntro , builder.toString(), " ", "");
		        else  if (localName == "fileid")
		        	itemData.sFileID =  builder.toString().trim();
		        else if (localName == "rate")
		        	itemData.sRating = builder.toString().trim();
		        else if (localName == "totalposts")
		        	itemData.totalPosts = builder.toString().trim();
    		}
    	if (itemData != null && localName == "id" && itemData.sThreadID == "0") {
    		itemData.sThreadID = builder.toString().trim();
    	}
		       if ( commentData != null) {
		        	//comment
		        	
		        	if (localName == "post" ) {
		        		if(commentData.sPostid != null)
		        			m_ListAdapter.addSection("COMMENT_"+commentData.sPostid, commentData);
		        		
		        		commentData = null;
		        	} else if (localName == "id" && commentData.sPostid == null) {
		        		commentData.sPostid = builder.toString().trim();
		        	} else if (localName == "content") {
		        		commentData.sContent = builder.toString().trim();
		        	} else if (localName == "postername") {
		        		commentData.sPosterName = builder.toString().trim();
		        	} else if (localName == "postime") {
		        		commentData.sPosttime = builder.toString().trim();
		        	}
		        	
		        }
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
