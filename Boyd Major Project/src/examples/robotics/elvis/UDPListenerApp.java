package examples.robotics.elvis;

import java.net.UnknownHostException;

import core.Canvas;
import core.Plane3D;
import peasy.PeasyCam;
import processing.core.PApplet;
import robotTools.GoToPlane;
import robotTools.RobotClient;
import robotTools.RobotWorkspace;
import toxi.geom.Vec3D;

public class UDPListenerApp extends PApplet{
	PeasyCam cam;
	Canvas canvas;
	RobotClient rc;
	
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);	
		canvas = new Canvas (this.g);
		try {
			rc = new RobotClient(5001,5000, this);
			rc.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void draw(){
		background(0);
		box(40);
	}
}
