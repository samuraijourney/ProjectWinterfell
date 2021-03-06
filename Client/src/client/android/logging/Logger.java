package client.android.logging;

/***************************************************************
 * Controller for all the log manipulation operations.
 * 
 * Ex. Logger.DisableLogging();
 * 
 * @author Akram Kassay
 ***************************************************************/
public class Logger 
{
	/** Flag indicating whether or not to log entries **/
	private static boolean _enableLogging = true;
	
	/*****************************************************************
	 * Enables log collection.
	 *****************************************************************/
	public static void EnableLogging()
	{
		_enableLogging = true;
	}
	
	/*****************************************************************
	 * Disables log collection.
	 *****************************************************************/
	public static void DisableLogging()
	{
		_enableLogging = false;
	}
	
	/*****************************************************************
	 * Definition of all different logging types
	 *****************************************************************/
	private enum LogType
	{
		INFO,
		DEBUG,
		ERROR
	}
	
	/***************************************************************
	 * Controller for all the logging to the ADB (Android Debug Bridge).
	 * 
	 * Ex. Logger.Log.Info("I am logging");
	 * 	   Logger.Log.Error("There has been an error",exception);
	 * 
	 * @author Akram Kassay
	 ***************************************************************/
	public static class Log
	{	
		/** Stack trace index of the function which called to log information **/
		private static int _stackTraceIndex = 0;

		/*****************************************************************
		 * Logs a debug message to the ADB. Synchronized method to allow for
		 * safe multithreaded log calls.
		 * 
		 * @param message the information to be logged
		 *****************************************************************/
		public synchronized static void Debug(String message)
		{
			_stackTraceIndex++;
			
			if(!LogMessage(message,null,LogType.DEBUG))
			{
				_stackTraceIndex--;
			}
		}
		
		/*****************************************************************
		 * Logs a debug message and an exception to the ADB. Synchronized 
		 * method to allow for safe multithreaded log calls.
		 * 
		 * @param message the information to be logged
		 * @param exception the error to be logged
		 *****************************************************************/
		public synchronized static void Debug(String message, Throwable exception)
		{
			_stackTraceIndex++;
			
			if(!LogMessage(message,exception,LogType.DEBUG))
			{
				_stackTraceIndex--;
			}
		}
		
		/*****************************************************************
		 * Logs an error message to the ADB. Synchronized method to allow for
		 * safe multithreaded log calls.
		 * 
		 * @param message the information to be logged
		 *****************************************************************/
		public synchronized static void Error(String message)
		{
			_stackTraceIndex++;
			
			if(!LogMessage(message,null,LogType.ERROR))
			{
				_stackTraceIndex--;
			}
		}
		
		/*****************************************************************
		 * Logs an error message and an exception to the ADB. Synchronized 
		 * method to allow for safe multithreaded log calls.
		 * 
		 * @param message the information to be logged
		 * @param exception the error to be logged
		 *****************************************************************/
		public synchronized static void Error(String message, Throwable exception)
		{
			_stackTraceIndex++;
			
			if(!LogMessage(message,exception,LogType.ERROR))
			{
				_stackTraceIndex--;
			}
		}
		
		/*****************************************************************
		 * Looks through stack trace and pulls the information about the
		 * class and function who made the call to the logger.
		 * 
		 * @return string containing the class and function name of the
		 * 		   function that called the logger
		 *****************************************************************/
		private static String GetCallingMethodStackTraceElementInfo()
		{
			_stackTraceIndex++;
			
			StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[_stackTraceIndex];
			_stackTraceIndex = 0;
			
			return stackTrace.getClassName() + ":" + stackTrace.getMethodName();
		}
		
		/*****************************************************************
		 * Logs a info message to the ADB. Synchronized method to allow for
		 * safe multithreaded log calls.
		 * 
		 * @param message the information to be logged
		 *****************************************************************/
		public synchronized static void Info(String message)
		{
			_stackTraceIndex++;
			
			if(!LogMessage(message,null,LogType.INFO))
			{
				_stackTraceIndex--;
			}
		}
		
		/*****************************************************************
		 * Logs a info message and an exception to the ADB. Synchronized 
		 * method to allow for safe multithreaded log calls.
		 * 
		 * @param message the information to be logged
		 * @param exception the error to be logged
		 *****************************************************************/
		public synchronized static void Info(String message, Throwable exception)
		{
			_stackTraceIndex++;
			
			if(!LogMessage(message,exception,LogType.INFO))
			{
				_stackTraceIndex--;
			}
		}
		
		/*****************************************************************
		 * Logs a message to the ADB based on the exception, message and 
		 * log type.
		 * 
		 * @param message the information to be logged
		 * @param exception the error to be logged
		 * @param logType the type of information to log (DEBUG,ERROR,INFO)
		 *****************************************************************/
		private static boolean LogMessage(String message, Throwable exception, LogType logType)
		{
			_stackTraceIndex++;
			
			if(message == null || _enableLogging == false)
			{
				_stackTraceIndex--;
				return false;
			}
			
			String tag = GetCallingMethodStackTraceElementInfo();
			
			if(exception == null)
			{
				switch(logType)
				{
					case INFO: 	android.util.Log.i(tag,message);
								break;
					case DEBUG: android.util.Log.d(tag,message);
								break;
					case ERROR: android.util.Log.e(tag,message);
								break;
				}
			}
			else
			{
				switch(logType)
				{
					case INFO: 	android.util.Log.i(tag,message,exception);
								break;
					case DEBUG: android.util.Log.d(tag,message,exception);
								break;
					case ERROR: android.util.Log.e(tag,message,exception);
								break;
				}
			}
			
			return true;
		}
	}
}
