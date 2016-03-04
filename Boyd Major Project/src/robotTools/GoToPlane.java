package robotTools;

import core.Plane3D;
import toxi.geom.Vec3D;

public class GoToPlane extends RobotTask{
	Plane3D target;
	
	public GoToPlane(Plane3D _target, Robot _robot) {
		super(_robot);
		target = _target;
	}
	
	public GoToPlane(Vec3D _target, Robot _robot) {
		super(_robot);
		target = new Plane3D(_target);
	}
	
	@Override
	public void run(){
		running = true;
		robot.setTarget(target);
		if(robot.getDistToTarget()<5)end();
	}

}
