package examples.vision;

import examples.extendingClasses.Ball;
import examples.extendingClasses.BallAgent;
import peasy.PeasyCam;
import pointCloudTools.KinectScanner;
import processing.core.PApplet;
import toxi.geom.Vec3D;

public class ScanningApp extends PApplet {
	PeasyCam cam;
	KinectScanner kinect;
	
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);	
		kinect = new KinectScanner(this);
		
	}
	
	
	public void draw(){
		background(0);
		box(40);
		kinect.renderColours();
		
	}

}
