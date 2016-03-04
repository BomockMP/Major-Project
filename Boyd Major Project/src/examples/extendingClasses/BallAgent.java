package examples.extendingClasses;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import core.Agent;
import core.Link;

public class BallAgent extends Agent{

	public BallAgent(Vec3D _o, boolean _f) {
		super(_o, _f);
		// TODO Auto-generated constructor stub
	}
	

	public void run(){
		
		addForce(Vec3D.randomVector().scale(5));
		
		update();
		addToTrail(this);
		stiffenTrail(0.05f);
		updateTrail();
		
	}
	
	
	
	public void render(PApplet parent){
		parent.stroke(255);
		for(Link l:trail){
			parent.line(l.a.x, l.a.y, l.a.z, l.b.x, l.b.y, l.b.z); 

		}
		
	}

}
