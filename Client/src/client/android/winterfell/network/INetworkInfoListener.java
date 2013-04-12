package client.android.winterfell.network;

/***************************************************************
 * Definition of the network listener events related to the 
 * transfer of information.
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
	
	/***************************************************************
	 * Called when information is being sent through the IO connection.
	 * 
	 * @param info the information sent
	 ***************************************************************/
	public void InformationSent(Object info);
}
