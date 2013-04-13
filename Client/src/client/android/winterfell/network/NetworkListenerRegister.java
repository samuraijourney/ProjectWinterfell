package client.android.winterfell.network;

/***************************************************************
 * Access point class to register all network listeners.
 * 
 * Ex. NetworkListenerRegister.RegisterListeners(network);
 * 
 * @author Akram Kassay
 ***************************************************************/
public final class NetworkListenerRegister 
{
	/*****************************************************************
	 * Private constructor to disable instantiation of this class.
	 *****************************************************************/
	private NetworkListenerRegister(){}
	
	/*****************************************************************
	 * Static accessor for registering all network listeners.
	 * 
	 * @param network the network to register listeners with
	 *****************************************************************/
	public static final void RegisterListeners(Network network)
	{
		network.AddNetworkInfoListener(CommandScheduler.Instance());
		network.AddNetworkSessionListener(CommandScheduler.Instance());
		network.AddNetworkSessionListener(Reader.Instance());
		network.AddNetworkSessionListener(ApplicationCommands.Instance());
	}
}
