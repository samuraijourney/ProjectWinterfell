package client.android.winterfell.command;

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
public final class ApplicationCommands 
{
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
}
