package client.android.network;

import org.json.JSONException;
import org.json.JSONObject;

/***************************************************************
 * A shell encapsulating JSON command object to be executed later
 * after queueing it in the command scheduler.
 * 
 * @author Akram Kassay
 ***************************************************************/
public interface ICommand 
{
	/***************************************************************
	 * Definition of the JSON object command that will be sent.
	 * Ex. JSONObject object = new JSONObject(); 
	 * 	   object.put("Name","Akram");
	 ***************************************************************/
	public JSONObject BuildJSONCommand() throws JSONException;
}
