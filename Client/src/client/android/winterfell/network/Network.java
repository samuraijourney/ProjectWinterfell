package client.android.winterfell.network;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import org.apache.http.ParseException;
import org.json.JSONObject;


/***************************************************************
 * Governs the communications with any remote device, this is only
 * the base class and must be extended to allow for more specific
 * networks.
 * 
 * @author Akram Kassay
 ***************************************************************/
public abstract class Network 
{	
	private static ArrayList<INetworkInfoListener> _infoListeners = new ArrayList<INetworkInfoListener>();
	private static ArrayList<INetworkSessionListener> _sessionListeners = new ArrayList<INetworkSessionListener>();
	
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
	 * @return the info read from the stream in a JSONObject
	 * @throws IOException no available input stream to read from
	 *****************************************************************/
	protected abstract JSONObject Read() throws IOException, ParseException;
	
	/*****************************************************************
	 * Sends the given command to the robot.
	 * 
	 * @param command the command to send
	 * @throws IOException no available output stream to send too
	 *****************************************************************/
	public abstract void Send(JSONObject command) throws IOException;
	
	/*****************************************************************
	 * Adds a network information listener.
	 * 
	 * @param listener the network information listener to add
	 *****************************************************************/
	public void AddNetworkInfoListener(INetworkInfoListener listener)
	{
		if(!_infoListeners.contains(listener))
		{
			_infoListeners.add(listener);
		}
	}
	
	/*****************************************************************
	 * Adds a network session listener.
	 * 
	 * @param listener the network session listener to add
	 *****************************************************************/
	public void AddNetworkSessionListener(INetworkSessionListener listener)
	{
		if(!_sessionListeners.contains(listener))
		{
			_sessionListeners.add(listener);
		}
	}
	
	/*****************************************************************
	 * Removes a network information listener.
	 * 
	 * @param listener the network information listener to remove
	 *****************************************************************/
	public void RemoveNetworkInfoListener(INetworkInfoListener listener)
	{
		_infoListeners.remove(listener);
	}
	
	/*****************************************************************
	 * Removes a network session listener.
	 * 
	 * @param listener the network session listener to remove
	 *****************************************************************/
	public void RemoveNetworkSessionListener(INetworkSessionListener listener)
	{
		_sessionListeners.remove(listener);
	}
	
	/*****************************************************************
	 * Launches network info received event for all listeners.
	 * 
	 * @param info the information received from the IO connection
	 *****************************************************************/
	protected void FireNetworkInfoReceivedEvent(Object info)
	{
		for(INetworkInfoListener listener : _infoListeners)
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
		for(INetworkInfoListener listener : _infoListeners)
		{
			listener.InformationSent(info);
		}
	}
	
	/*****************************************************************
	 * Launches event notifying all listeners that a connection is present.
	 *****************************************************************/
	protected void FireNetworkConnectedEvent(Network network)
	{
		for(INetworkSessionListener listener : _sessionListeners)
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
		for(INetworkSessionListener listener : _sessionListeners)
		{
			listener.NetworkDisconnected();
		}
	}
}
