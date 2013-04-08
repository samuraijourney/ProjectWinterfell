package client.android.winterfell.network;

import java.io.IOException;
import java.net.ConnectException;

import android.app.Activity;

public abstract class Network 
{
	private Activity _activity;
	
	public void Network(Activity activity)
	{
		_activity = activity;
	}
	
	public abstract void Connect() throws ConnectException, IOException;
	
	public abstract void Disconnect() throws ConnectException, IOException;
	
	public abstract Network Instance();
	
	public abstract boolean IsConnected();
	
	public abstract String ReadLine() throws IOException;
	
	public abstract void Send(String command) throws IOException;
	
	protected Activity GetActivity()
	{
		return _activity;
	}
}
