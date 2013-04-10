package client.android.winterfell.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import client.android.logging.Logger;
import client.android.winterfell.ApplicationInstance;

/***************************************************************
 * Definition of the bluetooth network and all its options.
 * 
 * Ex. Network network = BluetoothNetwork.Instance();
 * 
 * @author Akram Kassay
 ***************************************************************/
public class BluetoothNetwork implements INetwork
{
	/** The list of all already paired devices with the phone **/
	private static ArrayList<BluetoothDevice> _pairedDevices = new ArrayList<BluetoothDevice>();
	/** The list of all devices that have been discovered **/
	private static ArrayList<BluetoothDevice> _discoveredDevices = new ArrayList<BluetoothDevice>();
	/** Object to wait on while the device discovery process completes **/
	private static final Object _deviceDiscoveryWaitObject = new Object();
	/** Lock allowing for only one item to access socket connection at a time **/
	private final ReentrantLock _ioLock = new ReentrantLock();
	/** Flag indicating whether or not there is an active connection **/
	private boolean _connected = false;
	/** Single instance of our bluetooth network class to be returned **/
	private INetwork _bluetoothNetwork;
	/** Input stream for reading responses from robot **/
	private BufferedReader _inputStream;
	/** Output stream for sending commands to the robot **/
	private BufferedOutputStream _outputStream;
	/** Socket connection to communicate through during a session **/
	private BluetoothSocket _socket;
	/** Unique pin identifier that must be matched on server and client in order to pair connection **/
	private UUID _pin = UUID.fromString("winterfell");
	
	/*****************************************************************
	 * Constructor for bluetooth network object. This is private because
	 * this object can NOT be instantiated. It is a singleton and only
	 * returns one declared instance which can be statically accessed via
	 * the "Instance" method.
	 *****************************************************************/
	private BluetoothNetwork(){}
	
	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public void Connect(Object target) throws IOException , IllegalArgumentException
	{
		if(_connected)
		{
			Logger.Log.Debug("There is already a connection available.");
			return;
		}
		
		if(!(target instanceof BluetoothDevice))
		{
			IllegalArgumentException e = new IllegalArgumentException("Illegal argument passed, must be of type BluetoothDevice");
			Logger.Log.Error(e.getMessage(),e);
			throw e;
		}
		
		BluetoothDevice device = (BluetoothDevice)target;
		
		Logger.Log.Info(String.format("Trying to create socket and connect to pin:%s", _pin.toString()));
		
		_socket = device.createRfcommSocketToServiceRecord(_pin);
		
		try
		{
			// Cancel any potential discovery tasks just in case because they could break the connect operation
			BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			_ioLock.lock();
			{
				_socket.connect();
			}
			_ioLock.unlock();
		}
		catch (IOException e)
		{
			_socket.close();
			_ioLock.unlock();
			Logger.Log.Error(String.format("Could not connect to pin:%s", _pin.toString()),e);
			throw e;
		}
		
		_inputStream = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
		_outputStream = new BufferedOutputStream(_socket.getOutputStream());
		_connected = true;
		
		Logger.Log.Info(String.format("Connection successful, now connected to pin:%s", _pin.toString()));
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public void Disconnect() throws ConnectException, IOException
	{
		if(!_connected)
		{
			ConnectException e = new ConnectException("No active connection available");
			Logger.Log.Error(e.getMessage(), e);
			throw e;
		}
		
		Logger.Log.Info(String.format("Trying to disconnect from pin: %s", _pin.toString()));
		
		_ioLock.lock();
		{
			try
			{
				_inputStream.close();
				_outputStream.close();
				_socket.close();
				
				_inputStream = null;
				_outputStream = null;
				_socket = null;
			}
			catch(IOException e)
			{
				_ioLock.unlock();
				Logger.Log.Error(String.format("Could not close IO streams with pin: %s", _pin.toString()),e);
				throw e;
			}
		}
		_ioLock.unlock();
		
		Logger.Log.Info(String.format("Successfully disconnected from pin: %s", _pin.toString()));
		
		_connected = false;
	}
	
	/*****************************************************************
	 * Formats the command to be sent by appending the CRLF indicator.
	 * 
	 * @param command the string to format
	 * @return the byte representation of the command to send
	 *****************************************************************/
	private byte[] FormatCommand(String command)
	{
		command = command + "\n";
		return command.getBytes();
	}
	
	/*****************************************************************
	 * Searches for all available bluetooth devices and saves them into
	 * a list. Do not run this function from the UI thread because this
	 * search can take around 12 seconds and will freeze the UI.
	 * 
	 * @return the list of all discovered bluetooth devices.
	 *****************************************************************/
	public BluetoothDevice[] GetDiscoveredDevices()
	{
		if(!BluetoothAdapter.getDefaultAdapter().startDiscovery())
		{
			return null;
		}
		
		Logger.Log.Info("Bluetooth discovery started, searching for available devices");
		
		BroadcastReceiver receiver = BluetoothDiscoverer.CreateReceiver();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		
		ApplicationInstance.GetContext().registerReceiver(receiver, filter);
		
		try 
		{
			_deviceDiscoveryWaitObject.wait();
			ApplicationInstance.GetContext().unregisterReceiver(receiver);
			// Do this just in case it hasn't stopped discovering for some strange reason
			BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			
			return _discoveredDevices.toArray(new BluetoothDevice[_discoveredDevices.size()]);
		} 
		catch (InterruptedException e) 
		{
			BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			Logger.Log.Error("Bluetooth discovery service has been interrupted",e);
			return null;
		}
	}
	
	/*****************************************************************
	 * Collects all the already paired devices with the phone.
	 * 
	 * @return the list of all paired bluetooth devices.
	 *****************************************************************/
	public BluetoothDevice[] GetPairedDevices()
	{
		Logger.Log.Info("Gathering all paired devices");
		
		Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		
		if (devices.size() == 0)
		{
			return null;
		}
		
		for(BluetoothDevice device : devices)
		{
			if(!_pairedDevices.contains(device))
			{
				_pairedDevices.add(device);
			}
		}
		
		return _pairedDevices.toArray(new BluetoothDevice[_pairedDevices.size()]);
	}
	
	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public INetwork Instance() 
	{
		if(_bluetoothNetwork == null)
			_bluetoothNetwork = new BluetoothNetwork();
		return _bluetoothNetwork;
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public boolean IsConnected() 
	{
		return _connected;
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public String ReadLine() throws IOException
	{
		if(_inputStream == null)
		{
			IOException e = new IOException("No input stream available");
			Logger.Log.Error(e.getMessage(),e);
			throw e;
		}
		
		String response;
		_ioLock.lock();
		{
			try
			{
				response = _inputStream.readLine();
			}
			catch(IOException e)
			{
				_ioLock.unlock();
				Logger.Log.Error("Could not read from input stream",e);
				throw e;
			}
		}
		_ioLock.unlock();
		
		return response;
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public void Send(String command) throws IOException
	{
		if(_outputStream == null)
		{
			IOException e = new IOException("No output stream available");
			Logger.Log.Error(e.getMessage(),e);
			throw e;
		}
		
		_ioLock.lock();
		{
			try
			{
				_outputStream.write(FormatCommand(command));
			}
			catch(IOException e)
			{
				_ioLock.unlock();
				Logger.Log.Error("Could not write to output stream",e);
				throw e;
			}
		}
		_ioLock.unlock();
	}
	
	/***************************************************************
	 * The class which creates the bluetooth discovery receiver.
	 * 
	 * Ex. BroadcastReceiver receiver = BluetoothDiscoverer.CreateReceiver();
	 * 	   activity.registerReceiver(receiver, intentFilter);
	 * 
	 * @author Akram Kassay
	 ***************************************************************/
	private static class BluetoothDiscoverer
	{
		/** The broadcast receiver to receive discovery events **/
		private static BroadcastReceiver _broadcastReceiver;
		
		/*****************************************************************
		 * Creates the broadcast receiver to register with the current activity.
		 * 
		 * @return the receiver for the bluetooth discovery events
		 *****************************************************************/
		private static BroadcastReceiver CreateReceiver()
		{
			_broadcastReceiver = new BroadcastReceiver()
			{
				// Called when the broadcast receiver receives information
				public void onReceive(Context context, Intent intent) 
				{
				   String action = intent.getAction();
				   if(BluetoothDevice.ACTION_FOUND.equals(action)) 
				   {
		             BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		             if(!_discoveredDevices.contains(device))
		             {
		            	 _discoveredDevices.add(device);
		            	 Logger.Log.Info(String.format("Discovered device: %s",device.getName()));
		             }
			       }
				   else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
				   {
					   Logger.Log.Info("Bluetooth discovery finished");
					   _deviceDiscoveryWaitObject.notify();
				   }
				}
			};
			
			return _broadcastReceiver;
		}
	}
}
