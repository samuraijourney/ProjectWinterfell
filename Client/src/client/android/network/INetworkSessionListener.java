package client.android.network;

/***************************************************************
 * Definition of the network listener events related to the 
 * network connectivity.
 * 
 * @author Akram Kassay
 ***************************************************************/
public interface INetworkSessionListener 
{
	/***************************************************************
	 * Called when a network connection has been made.
	 ***************************************************************/
	public void NetworkConnected(Network network);
	
	/***************************************************************
	 * Called when the network connection has been broken.
	 ***************************************************************/
	public void NetworkDisconnected();
}
