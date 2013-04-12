package client.android.winterfell.command;

import java.io.IOException;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import client.android.logging.Logger;
import client.android.winterfell.network.INetworkListener;
import client.android.winterfell.network.Network;

/***************************************************************
 * Executable item which possesses the ability to store a command
 * and sends it over the network.
 * 
 * Ex. Command command = new Command(new ICommand()
 * 		{
 * 			public JSONObject BuildJSONCommand() throws JSONException
 *			{
 *				JSONObject command = new JSONObject();
 *				command.put("name", "Akram");
 *				return command;
 *			}
 * 		});
 * 
 * @author Akram Kassay
 ***************************************************************/
public final class Command implements INetworkListener
{
	/** Network object which to communicate through **/
	private Network _network;
	/** Command to send over the network to server **/
	private JSONObject _command;
	
	/*****************************************************************
	 * Constructor defining an executable command.
	 * 
	 * @param command the JSONObject shell which can be sent over the network
	 *****************************************************************/
	public Command(ICommand command)
	{
		if(command == null)
		{
			Assert.assertEquals("Cannot build JSON command object because ICommand is null",command, null);
			return;
		}
		
		try
		{
			_command = command.BuildJSONCommand();
		}
		catch(JSONException e)
		{
			String message = "Cannot build JSON command object";
			Logger.Log.Error(message,e);
		}
	}
	
	/*****************************************************************
	 * Sends the command through the network to the server.
	 *****************************************************************/
	protected final void Execute()
	{
		try 
		{
			if(_command == null || _network == null)
			{
				Assert.assertEquals("The network is null!",_network, null);
				Assert.assertEquals("The command is null!",_command, null);
				return;
			}
			_network.Send(_command.toString());
		} 
		catch (IOException e) 
		{
			Logger.Log.Error("Cannot send command", e);
		}
	}
	
	/*****************************************************************
	 * Updates command's JSONObject to be sent over the IO stream.
	 * 
	 * @param newCommand the new JSONObject to send
	 *****************************************************************/
	public final void ModifyCommand(JSONObject newCommand)
	{
		_command = newCommand;
	}
	
	/*****************************************************************
	 * Updates command's JSONObject argument which is passed in.
	 * 
	 * @param newCommand the new JSONObject to send
	 *****************************************************************/
	public final boolean ModifyCommandArgument(String title, Object newValue)
	{
		if(_command.has(title))
		{
			try 
			{
				_command.put(title, newValue);
			} 
			catch (JSONException e) 
			{
				String message = "Cannot modify JSON argument";
				Logger.Log.Error(message,e);
			}
			return true;
		}
		return false;
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
	
	public final void InformationReceived(Object info) {}
	public final void InformationSent(Object info) {}
}
