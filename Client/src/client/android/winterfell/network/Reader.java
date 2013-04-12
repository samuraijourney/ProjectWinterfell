package client.android.winterfell.network;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.http.ParseException;

import client.android.winterfell.logging.Logger;

public final class Reader extends Thread implements INetworkSessionListener
{
	private static Reader _reader;
	private static Network _network;
	private static Thread _readerThread;
	
	private Reader(){}
	
	private final static Reader Instance()
	{
		if(_reader == null)
		{
			_reader = new Reader();
		}
		return _reader;
	}

	public final void NetworkConnected(Network network) 
	{
		_network = network;
		
		if(_readerThread == null)
		{
			_readerThread = new Thread(new ReaderThread());
			_readerThread.start();
		}
		else
		{
			_readerThread.resume();
		}
	}

	public final void NetworkDisconnected() 
	{
		_readerThread.interrupt();
		_network = null;
	}
	
	private final class ReaderThread implements Runnable
	{
		private static final int _sleepInterval = 10;
		
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
