package client.android.winterfell;

import android.app.Application;
import android.content.Context;

/***************************************************************
 * Static instance of the current application context that can
 * accessed from anywhere. Should be strictly used for publishing
 * to the UI thread if need be but should not be used to randomly
 * modify UI elements from anywhere.
 * 
 * Ex. Context context = ApplicationInstance.GetContext();
 * 
 * @author Akram Kassay
 ***************************************************************/
public class ApplicationInstance extends Application
{
	/** The current application context **/
	 private static Context _context;

		/*****************************************************************
		 * Automatically called by the application when it starts.
		 *****************************************************************/
	    public void onCreate(){
	        super.onCreate();
	        ApplicationInstance._context = getApplicationContext();
	    }

		/*****************************************************************
		 * Returns the current application context statically.
		 * 
		 * @return the current context
		 *****************************************************************/
	    public static Context GetContext() {
	        return ApplicationInstance._context;
	    }
}
