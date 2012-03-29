package com.and.game;

import java.util.Timer;
import java.util.TimerTask;

public class Cool {
	private boolean go;
	private Timer timer;
	private long delay = 100;
	private static Cool instance = null;
	
	public static Cool shareCool()
	{
		if(instance == null)
		{
			instance = new Cool();
		}
		return instance;
	}
	// private constructor , singleton class
	private Cool()
	{
		timer = new Timer();
		go = true;
	}
	 public boolean checkValidity() {
		         if (go) {
		             go = false;
		             timer.schedule(new Task(), delay);
		             return true;
		         }
		         return false;
		     }
		     class Task extends TimerTask {
		         public void run() {
		             go = true;
		 
		         }
		     }
}
