package cn.shooter.client.andriod.app;

//Use this service to fetch and check the data from server

import java.util.Timer;
import java.util.TimerTask;

import cn.shooter.client.andriod.ShooterDataManager;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class ShooterClientService extends Service {
	private static final String TAG = "ShooterClientService";
	private Timer checkTimer = new Timer();
	private ShooterDataManager shooterDataManager;
	
	@Override
    public void onStart(Intent intent, int startId) {
		SharedPreferences settings = getSharedPreferences("shooterClientPerfs", 0);
		boolean hasThingsToCheck = settings.getBoolean("hasThingsToCheck", false);

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            updateWidgets();
        }
        if(hasThingsToCheck){
        	TimerTask backgroundChecker = new TimerTask(){
                public void run() {
                	shooterDataManager.scheduleCheck();
                }
              };

        	
        	checkTimer.scheduleAtFixedRate( backgroundChecker, 0, 300000);

        }else{
        	stopSelfResult(startId);
        }
    }
	@Override
	public void  onDestroy  () {
		if (checkTimer != null){
			 checkTimer.cancel();
		} 
	}
	private void updateWidgets() {
		 //TODO: Update Widget Icon
		
		/*
		  // Save user preferences. We need an Editor object to
	      // make changes. All objects are from android.context.Context
	      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putBoolean("silentMode", mSilentMode);
	
	      // Don't forget to commit your edits!!!
	      editor.commit();
		*/
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
