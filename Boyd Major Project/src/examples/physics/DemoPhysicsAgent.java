package examples.physics;

import java.util.ArrayList;

import toxi.geom.Vec3D;
import core.Agent;
import core.Environment;
import core.Link;
import core.Plane3D;

public class DemoPhysicsAgent extends Agent{
	
	boolean running = true;
	
	public DemoPhysicsAgent(Vec3D _o, boolean _f) {
		super(_o, _f);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run(Environment environment){
		if(running){
			getNeighbours(this, 40, environment); //get nearby agents
			for(Plane3D a:(ArrayList<Plane3D>)neighbours){
				repel(a, 0, 20, 0.003f, "exponential");
				cohere(a, 10, 50, 0.002f, "exponential");
				align(this, a, 0, 50, 0.1f, "exponential");
			}
			interpolateToXX(vel, 0.5f); //align the plane of the agent with its velocity for funsies
			addForce(xx.scale(0.01f)); //then push in that dir
			if(age%5==0){
				addToTrail(environment,this); //add to trail and to map of pts
			}
			addForce(new Vec3D(0,0,0.1f));
			update(); //moves the agent
			updateTrail(0.4f); //spring behaviour
			//if(trail.size()>40)removeFromTrail(0); //fix length
			if(!inBounds(500)){
				running = false;
				DemoPhysicsAgent a = new DemoPhysicsAgent(new Vec3D((float)Math.random()*100,(float)Math.random()*100,1), false);
				a.addForce(new Vec3D(0,0,1));
				environment.addAgent(a);
				//set(new Vec3D((float)Math.random()*100,(float)Math.random()*100,1)); //reset if outa bounds
				//resetTrail(); //reset the trail
			}
		}
		
	}

	
	@Override
	public void resetTrail(){

		trail = new ArrayList<Link>();
		Link l =new Link(new Plane3D(this), this,true);
		l.a.lock();
		trail.add(l);

	}
}
