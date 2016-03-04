package examples.flocking;

import java.util.ArrayList;

import toxi.geom.Vec3D;
import core.Agent;
import core.Environment;
import core.Link;
import core.Plane3D;

public class DemoAgent extends Agent{

	public DemoAgent(Vec3D _o, boolean _f) {
		super(_o, _f);
		// TODO Auto-generated constructor stub
	}
	
	//demo run function 
	
	public void run(Environment environment){
		getNeighbours(this, 60, environment); //get nearby agents
		for(Plane3D a:(ArrayList<Plane3D>)neighbours){
			repel(a, 0, 40, 0.03f, "exponential");
			cohere(a, 30, 80, 0.01f, "exponential");
			align(this, a, 0, 50, 0.1f, "exponential");
		}
		interpolateToXX(vel, 0.5f); //align the plane of the agent with its velocity for funsies
		addForce(xx.scale(0.01f)); //then push in that dir
		update(); //moves the agent
		//updateTrail(); //spring behaviour
		if(age%5==0){
			addToTrail(this); //add to trail and to map of pts
		}
		if(trail.size()>40)removeFromTrail(0); //fix length
		if(!inBounds(500)){
			set(Vec3D.randomVector().scale((float)Math.random()*100)); //reset if outa bounds
			set(x,y,0);
			resetTrail(); //reset the trail
			//vel = new Vec3D();
			//addForce(new Vec3D(0,0,10));
		}
		
	}
	
}
