package projects.distribution;

import java.util.ArrayList;

import core.Agent;
import core.Plane3D;
import pointCloudTools.Plane3DOctree;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;

public class SpringManager {

	
	
	
	public VerletPhysics3D springPhysics;
	
	public Plane3DOctree pts;
	public float bounds;
	
	public ArrayList<ParticleAgent> pop;
	public ArrayList<ParticleAgent> removeAgents;
	public ArrayList<ParticleAgent> addAgents;
	
	//CONSTRUCTOR
	public SpringManager(VerletPhysics3D _springPhysics, float _bounds){
		springPhysics = _springPhysics;
		bounds = _bounds;
		pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2);
		pop = new ArrayList<ParticleAgent>();
		removeAgents = new ArrayList<ParticleAgent>();
		addAgents = new ArrayList<ParticleAgent>();
	}
	
	
	//add springs if particles are within certain distance to each other
	
	//run function for particles other functions
	public void run(){

		for (ParticleAgent a: pop)a.run(this);
	}
	
	
	
	
	public void update(){
		
			//System.out.println(pts.getSize());
			for (ParticleAgent p:addAgents){
				pop.add(p);
			}
			for (ParticleAgent p: removeAgents){
				pop.remove(p); 
			}
			
			pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2);
			for(VerletParticle3D a:springPhysics.particles){
				pts.addPoint(a);
			}
	}
	
	
	
	//add springs if particles are within certain distance to each other
	//lets do this in particleAgent
//	public void addSpringsWithinRange(){
//		
//		
//		if (springPhysics.particles.size()>0){
//			
//			for (int i = 0; i < springPhysics.particles.size(); i++){
//			VerletParticle3D a = springPhysics.particles.get(i);
//			
//			//check distance between particles
//			Vec3D thisPos = a.abs();
//			
//			
//			
//		}
//}
//	}
	
	
	//Getting neighbours
	
	
public ArrayList getWithinSphere(Vec3D p, float rad){
		
		return pts.getPointsWithinSphere(p,rad);
		
	}
	
	
	
	
//-------------------------------------------------------------------------------------

//Functions for creating and removing Agents

//-------------------------------------------------------------------------------------
public void addAgents(ArrayList<ParticleAgent>agents){
	for(ParticleAgent a:agents){
		addAgent(a);
	}
}

public void addAgent(ParticleAgent a){
	addAgents.add(a);
}


public void remove(ParticleAgent a){
	removeAgents.add(a);
}

public void removeAll(){
	removeAgents.addAll(pop);
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//END
}
