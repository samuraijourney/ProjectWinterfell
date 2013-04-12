package client.android.winterfell.network;

import android.bluetooth.BluetoothDevice;

/***************************************************************
 * Definition of the bluetooth discovery listener events.
 * 
 * @author Akram Kassay
 ***************************************************************/
public interface IBluetoothDiscoveryListener {

	/***************************************************************
	 * Called when a bluetooth device has been discovered.
	 * 
	 * @param device the newly discovered device
	 ***************************************************************/
	public void BluetoothDeviceDiscovered(BluetoothDevice device);
	
	/***************************************************************
	 * Called when the bluetooth discovery process has started.
	 ***************************************************************/
	public void BluetoothDiscoveryStart();
	
	/***************************************************************
	 * Called when the bluetooth discovery process has ended.
	 ***************************************************************/
	public void BluetoothDiscoveryEnd();
}
