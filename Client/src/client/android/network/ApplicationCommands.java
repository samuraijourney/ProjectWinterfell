package client.android.network;

import org.json.JSONException;
import org.json.JSONObject;

/***************************************************************
 * Container for all application commands which can be stated
 * and modified at any time as need be.
 * 
 * Ex. ApplicationCommands.MoveForward;
 * 
 * @author Akram Kassay
 ***************************************************************/
public final class ApplicationCommands implements INetworkSessionListener
{
	/** Network object which to communicate through **/
	private static Network _network;
	/** Singleton instance of the application commands **/
	private static ApplicationCommands _applicationCommands;
	
	/*****************************************************************
	 * Constructor for application commands object. This is private because
	 * this object can NOT be instantiated. It is a singleton and only
	 * returns one declared instance which can be statically accessed via
	 * the "Instance" method.
	 *****************************************************************/
	private ApplicationCommands(){}
	
	/** Sample moving forward command **/
	public static Command MoveForward = new Command(new ICommand()
	{
		public JSONObject BuildJSONCommand() throws JSONException
		{
			JSONObject command = new JSONObject();
			command.put("move_angle", 90);
			command.put("move_speed", 70);
			return command;
		}
	});
	/** Other potential sample commands **/
	public static Command GetFuelLevel;
	public static Command GetRobotState;
	public static Command GetImageFrame;
	
	/*****************************************************************
	 * Singleton instance. This is protected to restrict instance access
	 * to classes within the network package.
	 * 
	 * @return the network instance to send commands through
	 *****************************************************************/
	protected static final ApplicationCommands Instance()
	{
		if(_applicationCommands == null)
		{
			_applicationCommands = new ApplicationCommands();
		}
		return _applicationCommands;
	}
	
	/*****************************************************************
	 * Gets the network instance to be shared with all commands.
	 * 
	 * @return the network instance to send commands through
	 *****************************************************************/
	protected static final Network GetNetwork()
	{
		return _network;
	}
	
	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public final void NetworkConnected(Network network) 
	{
		_network = network;
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public final void NetworkDisconnected() 
	{
		_network = null;
	}
}
