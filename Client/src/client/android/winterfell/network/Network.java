package client.android.winterfell.network;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;


/***************************************************************
 * Governs the communications with any remote device, this is only
 * the base class and must be extended to allow for more specific
 * networks.
 * 
 * @author Akram Kassay
 ***************************************************************/
public abstract class Network 
{	
	private static ArrayList<INetworkListener> _listeners = new ArrayList<INetworkListener>();
	
	/*****************************************************************
	 * Tries to establish a connection to the target object provided.
	 * Run this in a separate thread, it will hold up the UI.
	 * 
	 * @param target the target device or channel to try and communicate with
	 * @throws IOException could not form connection to target
	 * @throws IllegalArgumentException invalid target object was provided
	 *****************************************************************/
	public abstract void Connect(Object target) throws IOException, IllegalArgumentException;
	
	/*****************************************************************
	 * Breaks current connection cleanly.
	 * 
	 * @throws ConnectException no connection was available to disconnect
	 * @throws IOException could not close streams or socket
	 *****************************************************************/
	public abstract void Disconnect() throws ConnectException, IOException;
	
	/*****************************************************************
	 * Indicates whether or not there is currently an active connection.
	 * 
	 * @return true or false
	 *****************************************************************/
	public abstract boolean IsConnected();
	
	/*****************************************************************
	 * Reads stream until a CRLF character is hit.
	 * 
	 * @return the info read from the stream
	 * @throws IOException no available input stream to read from
	 *****************************************************************/
	protected abstract String Read() throws IOException;
	
	/*****************************************************************
	 * Sends the given command to the robot.
	 * 
	 * @param command the command to send
	 * @throws IOException no available output stream to send too
	 *****************************************************************/
	public abstract void Send(String command) throws IOException;
	
	/*****************************************************************
	 * Adds a network information listener.
	 * 
	 * @param listener the network information listener to add
	 *****************************************************************/
	public void AddNetworkInfoListener(INetworkListener listener)
	{
		if(!_listeners.contains(listener))
		{
			_listeners.add(listener);
		}
	}
	
	/*****************************************************************
	 * Removes a network information listener.
	 * 
	 * @param listener the network information listener to remove
	 *****************************************************************/
	public void RemoveNetworkInfoListener(INetworkListener listener)
	{
		_listeners.remove(listener);
	}
	
	/*****************************************************************
	 * Launches network info received event for all listeners.
	 * 
	 * @param info the information received from the IO connection
	 *****************************************************************/
	protected void FireNetworkInfoReceivedEvent(Object info)
	{
		for(INetworkListener listener : _listeners)
		{
			listener.InformationReceived(info);
		}
	}
	
	/*****************************************************************
	 * Launches network info sent event for all listeners.
	 * 
	 * @param info the information sent through the IO connection
	 *****************************************************************/
	protected void FireNetworkInfoSentEvent(Object info)
	{
		for(INetworkListener listener : _listeners)
		{
			listener.InformationSent(info);
		}
	}
	
	/*****************************************************************
	 * Launches event notifying all listeners that a connection is present.
	 *****************************************************************/
	protected void FireNetworkConnectedEvent(Network network)
	{
		for(INetworkListener listener : _listeners)
		{
			listener.NetworkConnected(network);
		}
	}
	
	/*****************************************************************
	 * Launches event notifying all listeners that the connection has
	 * been broken.
	 *****************************************************************/
	protected void FireNetworkDisconnectedEvent()
	{
		for(INetworkListener listener : _listeners)
		{
			listener.NetworkDisconnected();
		}
	}
}
