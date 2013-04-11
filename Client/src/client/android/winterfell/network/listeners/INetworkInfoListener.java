package client.android.winterfell.network.listeners;

/***************************************************************
 * Definition of the network information listener events.
 * 
 * @author Akram Kassay
 ***************************************************************/
public interface INetworkInfoListener {

	/***************************************************************
	 * Called when information is read from the IO connection.
	 * 
	 * @param info the information received
	 ***************************************************************/
	public void InformationReceived(Object info);
}
