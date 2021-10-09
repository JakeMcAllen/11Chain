package chain.test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

public class MyTimeTask extends TimerTask {

	
	public MyTimeTask() {
		// TODO Auto-generated constructor stub
		
		System.err.println("Heyyyyyyyyyyyy");
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Date calendar1 = Calendar.getInstance().getTime();

		System.err.println("TestTime: " + calendar1);
		
	}
	
}
