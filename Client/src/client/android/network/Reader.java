package client.android.network;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.http.ParseException;

import client.android.logging.Logger;

/***************************************************************
 * Continuous reading class which is activated once a network
 * connection has been established.
 * 
 * @author Akram Kassay
 ***************************************************************/
public final class Reader implements INetworkSessionListener
{
	/** Single instance of our reader class to be returned **/
	private static Reader _reader;
	/** Network object which to communicate through **/
	private static Network _network;
	/** The thread which controls all reading from the network connection **/
	private static Thread _readerThread;
	
	/*****************************************************************
	 * Constructor for reader object. This is private because this object 
	 * can NOT be instantiated. It is a singleton and only returns one 
	 * declared instance which can be statically accessed via the "Instance" 
	 * method.
	 *****************************************************************/
	private Reader(){}
	
	/*****************************************************************
	 * Singleton method to access single instance of "Reader".
	 *****************************************************************/
	protected final static Reader Instance()
	{
		if(_reader == null)
		{
			_reader = new Reader();
		}
		return _reader;
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public final void NetworkConnected(Network network) 
	{
		_network = network;
		
		if(_readerThread == null)
		{
			_readerThread = new Thread(new ReaderThread(),"Reader Thread");
			_readerThread.start();
		}
		else
		{
			_readerThread.resume();
		}
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public final void NetworkDisconnected() 
	{
		_readerThread.interrupt();
		_network = null;
	}
	
	/***************************************************************
	 * Thread that continually reads from the network.
	 * 
	 * Ex. Thread thread = new Thread(new ReaderThread(), "Reader Thread");
	 * 
	 * @author Akram Kassay
	 ***************************************************************/
	private final class ReaderThread implements Runnable
	{
		/** The sleep interval between the scheduler threads iterations **/
		private static final int _sleepInterval = 10;
		
		/*****************************************************************
		 * Executable function for the thread.
		 *****************************************************************/
		public void run() 
		{
			try 
			{
				while(true)
				{
					_network.Read();
					Thread.sleep(_sleepInterval);
				}
			} 
			catch (ParseException e) 
			{
				Assert.fail("Information has been missed in reader thread due to parse exception");
			} 
			catch (IOException e) 
			{
				Assert.fail("Information has been missed in reader thread due to IO exception");
			} 
			catch (InterruptedException e) 
			{
				Logger.Log.Error("Could not wait in reader thread",e);
			}
		}
	}
}
