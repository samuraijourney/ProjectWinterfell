package client.android.winterfell.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothNetwork extends Network
{
	private static ArrayList<BluetoothDevice> _pairedDevices = new ArrayList<BluetoothDevice>();
	private static ArrayList<BluetoothDevice> _discoveredDevices = new ArrayList<BluetoothDevice>();
	private static final Object _deviceDiscoveryWaitObject = new Object();
	private boolean _connected = false;
	private BluetoothNetwork _bluetoothNetwork;
	private BluetoothDevice _activeDevice;
	private BufferedReader _inputStream;
	private BufferedOutputStream _outputStream;
	private BluetoothSocket _socket;
	private UUID _pin = UUID.fromString("winterfell");
	
	private BluetoothNetwork(){}
	
	// Run in separate thread, could take some time to complete.
	public void Connect() throws ConnectException, IOException 
	{
		if(_activeDevice == null)
		{
			throw new ConnectException("No active device to connect too.");
		}
		
		_socket = _activeDevice.createRfcommSocketToServiceRecord(_pin);
		
		try
		{
			// Cancel any potential discovery tasks just in case because they could break the connect operation.
			BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			_socket.connect();
		}
		catch (IOException e)
		{
			_socket.close();
			throw e;
		}
		
		_inputStream = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
		_outputStream = new BufferedOutputStream(_socket.getOutputStream());
		_connected = true;
	}

	public void Disconnect() throws ConnectException, IOException
	{
		if(!_connected)
		{
			throw new ConnectException("No active connection available");
		}
		
		_inputStream.close();
		_outputStream.close();
		_socket.close();
		
		_inputStream = null;
		_outputStream = null;
		_socket = null;
		
		_connected = false;
	}
	
	private byte[] FormatCommand(String command)
	{
		command = command + "\n";
		return command.getBytes();
	}
	
	public BluetoothDevice[] GetPairedDevices()
	{
		Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		
		if (devices.size() == 0)
		{
			return null;
		}
		
		for(BluetoothDevice device : devices)
		{
			_pairedDevices.add(device);
			_discoveredDevices.add(device);
		}
		
		return _pairedDevices.toArray(new BluetoothDevice[_pairedDevices.size()]);
	}
	
	// Do not run this function from the main thread, it will freeze the UI. Search takes around 12 seconds.
	public BluetoothDevice[] GetDiscoveredDevices()
	{
		if(!BluetoothAdapter.getDefaultAdapter().startDiscovery())
		{
			return null;
		}
		
		BroadcastReceiver receiver = BluetoothDiscoverer.CreateReceiver();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		
		GetActivity().registerReceiver(receiver, filter);
		
		try 
		{
			_deviceDiscoveryWaitObject.wait();
			GetActivity().unregisterReceiver(receiver);
			BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			
			return _discoveredDevices.toArray(new BluetoothDevice[_discoveredDevices.size()]);
		} 
		catch (InterruptedException e) 
		{
			// Add logging information here
			return null;
		}
	}
	
	public Network Instance() 
	{
		if(_bluetoothNetwork == null)
			_bluetoothNetwork = new BluetoothNetwork();
		return _bluetoothNetwork;
	}

	public boolean IsConnected() 
	{
		return _connected;
	}

	public String ReadLine() throws IOException
	{
		if(_inputStream == null)
		{
			throw new IOException("No input stream available");
		}
		
		return _inputStream.readLine();
	}

	public void Send(String command) throws IOException
	{
		if(_outputStream == null)
		{
			throw new IOException("No output stream available");
		}
		
		_outputStream.write(FormatCommand(command));
	}
	
	public void SetActiveBluetoothDevice(BluetoothDevice device)
	{
		_activeDevice = device;
	}
	
	public static class BluetoothDiscoverer
	{
		private static BroadcastReceiver _broadcastReceiver;
		
		private static BroadcastReceiver CreateReceiver()
		{
			_broadcastReceiver = new BroadcastReceiver()
			{
				public void onReceive(Context context, Intent intent) 
				{
				   String action = intent.getAction();
				   if(BluetoothDevice.ACTION_FOUND.equals(action)) 
				   {
		             BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		             if(!_discoveredDevices.contains(device))
		             {
		            	 _discoveredDevices.add(device);
		             }
			       }
				   else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
				   {
					   _deviceDiscoveryWaitObject.notify();
				   }
				}
			};
			
			return _broadcastReceiver;
		}
	}
}
