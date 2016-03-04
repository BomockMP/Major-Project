package examples.extendingClasses;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import core.Particle;

public class Ball extends Particle{

	PApplet parent;
	
	public Ball(Vec3D _o, PApplet _parent) {
		super(_o);
		parent = _parent;
	}


	public void render(){
		
		parent.stroke(255);
		parent.strokeWeight(5);
		parent.point(x, y,z);
		
	}

}
