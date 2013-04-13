package client.android.winterfell;

import client.android.winterfell.network.Network;
import client.android.winterfell.network.NetworkListenerRegister;

/***************************************************************
 * Access point class to register all network listeners.
 * 
 * Ex. ApplicationListenerRegister.RegisterListeners(network);
 * 
 * @author Akram Kassay
 ***************************************************************/
public final class ApplicationListenerRegister 
{
	/*****************************************************************
	 * Private constructor to disable instantiation of this class.
	 *****************************************************************/
	private ApplicationListenerRegister(){}
	
	/*****************************************************************
	 * Static accessor for registering all the application listeners
	 * from the different packages.
	 * 
	 * @param network the network to register the network listeners with
	 *****************************************************************/
	public static final void RegisterListeners(Network network)
	{
		NetworkListenerRegister.RegisterListeners(network);
	}
}
