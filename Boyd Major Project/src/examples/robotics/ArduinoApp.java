package examples.robotics;

import java.net.UnknownHostException;

import peasy.PeasyCam;
import pointCloudTools.KinectScanner;
import processing.core.PApplet;
import robotTools.GoToPlane;
import robotTools.RobotClient;
import robotTools.RobotWorkspace;
import toxi.geom.Vec3D;

public class ArduinoApp extends PApplet{

	PeasyCam cam;
	RobotClient rc;
	
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);	
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
		rc.sendArduino("10", 2811, "172.20.10.6");
	}
	
}
