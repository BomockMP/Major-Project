package examples.robotics;

import core.Canvas;
import core.Plane3D;
import peasy.PeasyCam;
import processing.core.PApplet;
import robotTools.GoToPlane;
import robotTools.RobotWorkspace;
import toxi.geom.Vec3D;

public class RobotControlApp extends PApplet{
	
	PeasyCam cam;
	Canvas canvas;
	RobotWorkspace rs;
	
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);	
		canvas = new Canvas (this.g);
		rs = new RobotWorkspace(this,true);
		//Plane3D pose = new Plane3D(rs.robot.homePos.add(new Vec3D(-100,0,0)), new Vec3D(0,0,1), new Vec3D(0,-1,0));
		//rs.addTask(new GoToPlane(pose, rs.robot));
		rs.addTask(new GoToPlane(rs.robot.homePos.add(new Vec3D(0,100,0)), rs.robot));
		//rs.addTask(new GoToPlane(pose, rs.robot));
		rs.addTask(new GoToPlane(rs.robot.homePos.add(new Vec3D(200,-100,0)), rs.robot));
	}
	
	
	public void draw(){
		background(0);
		box(40);
		//rs.simulate(); // simulates tasks
		canvas.drawPlane(rs.robot, 20,2);
		canvas.drawPlane(rs.robot.targetPos, 10,2);
		rs.run("10.220.199.112"); // WARNING! will run the robot
	}
}
