package client.android.winterfell.network;

import java.io.IOException;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import client.android.winterfell.logging.Logger;

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
public final class Command
{
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
			if(_command == null || ApplicationCommands.GetNetwork() == null)
			{
				Assert.assertEquals("The network is null!",ApplicationCommands.GetNetwork(), null);
				Assert.assertEquals("The command is null!",_command, null);
				return;
			}
			ApplicationCommands.GetNetwork().Send(_command);
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
	 * @param title the name of the new command argument
	 * @param value the value of the new command argument
	 * @return indicator as to whether or not the argument was modified
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
	 * Appends an argument to a JSONObject.
	 * 
	 * @param title the name of the new command argument
	 * @param value the value of the new command argument
	 * @return indicator as to whether or not the command argument was appended
	 *****************************************************************/
	public final boolean AppendCommandArgument(String title, Object value)
	{
		if(!_command.has(title))
		{
			try 
			{
				_command.accumulate(title, value);
				return true;
			} 
			catch (JSONException e) 
			{
				String message = "Cannot append command argument to JSON";
				Logger.Log.Error(message,e);
			}
		}
		return false;
	}
}
