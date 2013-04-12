package client.android.winterfell.network;

/***************************************************************
 * Definition of the network listener events related to connectivity
 * and the transfer of information.
 * 
 * @author Akram Kassay
 ***************************************************************/
public interface INetworkListener {

	/***************************************************************
	 * Called when information is read from the IO connection.
	 * 
	 * @param info the information received
	 ***************************************************************/
	public void InformationReceived(Object info);
	
	/***************************************************************
	 * Called when information is being sent through the IO connection.
	 * 
	 * @param info the information sent
	 ***************************************************************/
	public void InformationSent(Object info);
	
	/***************************************************************
	 * Called when a network connection has been made.
	 ***************************************************************/
	public void NetworkConnected(Network network);
	
	/***************************************************************
	 * Called when the network connection has been broken.
	 ***************************************************************/
	public void NetworkDisconnected();
}
