package com.amphibian.ffz;

import android.app.Application;
import android.content.Context;

/**
 * Extending Application to make Context available everywhere.
 * Passing it around is getting tedious. Not sure if this is the
 * best solution or not...
 * 
 * @author Casey
 *
 */
public class App extends Application {

	public final static String name = "ffz"; 
	
	private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
    
}
