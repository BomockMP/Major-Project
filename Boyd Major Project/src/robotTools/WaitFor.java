package robotTools;

public class WaitFor extends RobotTask{
	
	int counter;
	int waitTime;
	
	public WaitFor(int _waitTime, Robot _robot) {
		super(_robot);
		waitTime = _waitTime;
		counter = 0;
	}
	
	@Override
	public void run(){
		running = true;
		counter++;
		if(counter>waitTime)end();
	}
	
	@Override
	public void end(){
		//reset
		running = false;
		counter = 0;
	}

}
