package client.android.winterfell.network;

import java.io.IOException;
import java.net.ConnectException;

/***************************************************************
 * Interface definition of functions that all networks should implement.
 * 
 * @author Akram Kassay
 ***************************************************************/
public interface INetwork 
{	
	/*****************************************************************
	 * Tries to establish a connection to the target object provided.
	 * Run this in a separate thread, it will hold up the UI.
	 * 
	 * @param target the target device or channel to try and communicate with
	 * @throws IOException could not form connection to target
	 * @throws IllegalArgumentException invalid target object was provided
	 *****************************************************************/
	public void Connect(Object target) throws IOException, IllegalArgumentException;
	
	/*****************************************************************
	 * Breaks current connection cleanly.
	 * 
	 * @throws ConnectException no connection was available to disconnect
	 * @throws IOException could not close streams or socket
	 *****************************************************************/
	public void Disconnect() throws ConnectException, IOException;
	
	/*****************************************************************
	 * Singleton method to access single instance of "Network"
	 *****************************************************************/
	public INetwork Instance();
	
	/*****************************************************************
	 * Indicates whether or not there is currently an active connection.
	 * 
	 * @return true or false
	 *****************************************************************/
	public boolean IsConnected();
	
	/*****************************************************************
	 * Reads stream until a CRLF character is hit.
	 * 
	 * @return the info read from the stream
	 * @throws IOException no available input stream to read from
	 *****************************************************************/
	public String ReadLine() throws IOException;
	
	/*****************************************************************
	 * Sends the given command to the robot.
	 * 
	 * @param command the command to send
	 * @throws IOException no available output stream to send too
	 *****************************************************************/
	public void Send(String command) throws IOException;
}
