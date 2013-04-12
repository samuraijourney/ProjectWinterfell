package client.android.winterfell.command;

import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

import client.android.logging.Logger;
import client.android.winterfell.network.INetworkListener;
import client.android.winterfell.network.Network;

/***************************************************************
 * Stores commands and executes them when it is it's turn in the
 * queue of commands.
 * 
 * Ex. CommandScheduler scheduler = CommandScheduler.Instance();
 * 	   scheduler.QueueCommand(command);
 * 
 * @author Akram Kassay
 ***************************************************************/
public final class CommandScheduler implements INetworkListener
{
	/** A lock permitting only one thread to access the commands queue at a time **/
	private static final ReentrantLock _queueLock = new ReentrantLock();
	/** The commands queue data structure **/
	private static final PriorityQueue<Command> _commandsQueue = new PriorityQueue<Command>();
	/** Object to be waited on until a response is returned from server **/
	private static final Object _commandResponseWaitObject = new Object();
	/** The thread which controls all command scheduling **/
	private static Thread _schedulerThread;
	/** Singleton instance of this class **/
	private static CommandScheduler _commandScheduler;
	
	/*****************************************************************
	 * Constructor for command scheduler object. This is private because
	 * this object can NOT be instantiated. It is a singleton and only
	 * returns one declared instance which can be statically accessed via
	 * the "Instance" method.
	 *****************************************************************/
	private CommandScheduler(){};
	
	/*****************************************************************
	 * Adds the command to the scheduling queue to be sent to the server.
	 * 
	 * @param command the command to be added
	 *****************************************************************/
	public final void QueueCommand(Command command)
	{
		if(command != null)
		{
			_queueLock.lock();
			{
				_commandsQueue.add(command);
			}
			_queueLock.unlock();
		}
	}
	
	/*****************************************************************
	 * Singleton method to access single instance of "CommandScheduler".
	 *****************************************************************/
	public static final CommandScheduler Instance()
	{
		if(_commandScheduler == null)
		{
			_commandScheduler = new CommandScheduler();
		}
		return _commandScheduler;
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public final void InformationReceived(Object info) 
	{
		if(!(info instanceof String))
		{
			return;
		}
		
		String response = (String)info;
		if(response.equals("OK"))
		{
			_commandResponseWaitObject.notify();
		}
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public final void NetworkConnected(Network network) 
	{
		if(_schedulerThread == null)
		{
			_schedulerThread = new Thread(new SchedulerThread(), "Scheduler Thread");
			_schedulerThread.start();
		}
		else
		{
			_schedulerThread.resume();
		}
	}

	/*****************************************************************
	 * {@inheritDoc}
	 *****************************************************************/
	public final void NetworkDisconnected() 
	{
		_commandResponseWaitObject.notify();
		_schedulerThread.interrupt();
		_queueLock.lock();
		{
			_commandsQueue.removeAll(_commandsQueue);
		}
		_queueLock.unlock();
	}
	
	public final void InformationSent(Object info) {}
	
	/***************************************************************
	 * Thread that sends commands to the robot and awaits a response
	 * before sending another command.
	 * 
	 * Ex. Thread thread = new Thread(new SchedulerThread(), "Scheduler Thread");
	 * 
	 * @author Akram Kassay
	 ***************************************************************/
	private final class SchedulerThread implements Runnable
	{
		/** The sleep interval between the scheduler threads iterations **/
		private static final int _sleepInterval = 50;
		
		public void run() 
		{
			while(true)
			{
				try 
				{
					if(_commandsQueue.size() > 0)
					{
						_queueLock.lock();
						{
							Command command = _commandsQueue.poll();
							command.Execute();
						}
						_queueLock.unlock();

						_commandResponseWaitObject.wait();
					}
					
					Thread.sleep(_sleepInterval);
				}
				catch (InterruptedException e)
				{
					Logger.Log.Error("Could not wait in command scheduler thread",e);
				}
			}
		}
	}
}
