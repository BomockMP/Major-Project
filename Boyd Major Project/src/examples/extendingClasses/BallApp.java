package examples.extendingClasses;

import controlP5.ControlP5;
import core.Particle;
import peasy.PeasyCam;
import processing.core.PApplet;
import toxi.geom.Vec3D;

public class BallApp extends PApplet{
	
	PeasyCam cam;
	Ball b;
	
	BallAgent bAgent;
	
	
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);	
		b = new Ball(new Vec3D(), this);
		bAgent = new BallAgent(new Vec3D(50,0,0),false);
		
	}
	
	
	public void draw(){
		background(0);
		//box(40);
		bAgent.run();
		bAgent.render(this);
		
	}
	
	

}
