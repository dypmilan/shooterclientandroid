package cn.shooter.client.andriod;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

public class ShooterDataManager {
	private boolean scheduleCheckIsRunning = false;
	public void scheduleCheck(){
		if(scheduleCheckIsRunning)
			return;
		scheduleCheckIsRunning = true;
		
		
		scheduleCheckIsRunning = false;
	}
	private boolean FetchData(String uri)
	{
		try {  
            URL url = new URL(uri);  
            URLConnection con = url.openConnection();  
              
            InputStream is = con.getInputStream();  
            BufferedInputStream bis = new BufferedInputStream(is);  
            ByteArrayBuffer bab = new ByteArrayBuffer(32);  
            int current = 0;  
            while ( (current = bis.read()) != -1 ){  
                bab.append((byte)current);  
            }  
            //String result = EncodingUtils.getString(bab.toByteArray(), HTTP.UTF_8);  
              
            bis.close();  
            is.close();  
  
            return true;
        } catch (Exception e) {  
             
        }  
        return false;
	}
}
