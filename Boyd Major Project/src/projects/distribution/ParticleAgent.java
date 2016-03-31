package projects.distribution;

import java.util.ArrayList;

import core.Agent;
import core.Environment;
import core.Plane3D;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletMinDistanceSpring3D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;

public class ParticleAgent extends VerletParticle3D {
	
	public ArrayList neighbours = new ArrayList();

	public ParticleAgent(float x, float y, float z) {
		super(x, y, z);
		
	}

	
	
	public void run(SpringManager springManager){
		
		//springManager.springPhysics.springs.size();
		
		getNeighbours(this, 10, springManager);
		addSpringsToCloseParticles(springManager.springPhysics);
		
	}
	
	

	//function for adding springs to close agents. Trying it here.
	public void addSpringsToCloseParticles(VerletPhysics3D springPhysics){

		for (VerletParticle3D a:(ArrayList<VerletParticle3D>)neighbours){
			
			//springPhysics.springs.clear();
		
		
		VerletSpring3D s = new VerletSpring3D(this, a, 60, 0.001f);
		//VerletMinDistanceSpring3D s = new VerletMinDistanceSpring3D(this, a, 10, 100f);
			springPhysics.addSpring(s);
			
			
			
			
			System.out.println(springPhysics.springs.size());
	}
		
	}
	
	
	
	public void getNeighbours(Vec3D p, float rad, SpringManager springManager) {
		neighbours = new ArrayList<Plane3D>();
		ArrayList addList = springManager.getWithinSphere(p, rad);
		if(addList!=null){
			neighbours.addAll(addList);
		}
	}
	
	
}
