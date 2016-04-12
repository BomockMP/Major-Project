package projects.distribution;

import java.util.ArrayList;
import java.util.Iterator;

import core.Agent;
import core.Plane3D;
import pointCloudTools.Plane3DOctree;
import processing.core.PApplet;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletMinDistanceSpring3D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;

public class SpringManager {

	//todo remove long springs
	
	
	public VerletPhysics3D springPhysics;
	
	public Plane3DOctree pts;
	public float bounds;
	public PApplet parent;
	
	public ArrayList<ParticleAgent> pop;
	public ArrayList<ParticleAgent> removeAgents;
	public ArrayList<ParticleAgent> addAgents;
	
	//CONSTRUCTOR
	public SpringManager(VerletPhysics3D _springPhysics, float _bounds, PApplet _parent){
		springPhysics = _springPhysics;
		bounds = _bounds;
		parent = _parent;
		pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2);
		pop = new ArrayList<ParticleAgent>();
		removeAgents = new ArrayList<ParticleAgent>();
		addAgents = new ArrayList<ParticleAgent>();
	}
	
	
	//add springs if particles are within certain distance to each other
	
	//run function for particles other functions
	public void run(){

		for (ParticleAgent a: pop)a.run(this);
		
		//addMinDistanceSprings(springPhysics);
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
	
	
	

	
	
	//Getting neighbours
public ArrayList getWithinBox(Vec3D p, float rad){
		
		return pts.getPointsWithinBox(new AABB(p, rad));
		
		
		
	}
	
public ArrayList getWithinSphere(Vec3D p, float rad){
		
		return pts.getPointsWithinSphere(p,rad);
		
		
		
	}
	

public void addMinDistanceSprings(VerletPhysics3D springPhysics){


	for (int k = 0; k < springPhysics.particles.size(); k++){
	
		VerletParticle3D pp = springPhysics.particles.get(k);
		
		
		
	Iterator i=springPhysics.particles.iterator();
	
	//VerletParticle3D p = (VerletParticle3D)i.next();
	
	
	
	while( i.hasNext()){
	
		//System.out.println("test");
		
	VerletParticle3D p1 = (VerletParticle3D)i.next();
	
	VerletMinDistanceSpring3D s = new VerletMinDistanceSpring3D(p1, pp, 400, 100f);
	
	springPhysics.addSpring(s);
	//p = p1;
	}
	}

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
	
	
//-------------------------------------------------------------------------------------

//Save spring linework

//-------------------------------------------------------------------------------------
public void saveSpringParticles(int framecount){
	ArrayList<String>lineList = new ArrayList<String>();
	if(springPhysics.springs.size()>0){
	for (VerletSpring3D a: springPhysics.springs) {
		
			String c = "";
			//for(int i = 0; i<springPhysics.springs.size();i++){
				//VerletSpring3D l = springPhysics.springs.get(i);
				
				c = c+a.a.x  +"," + a.a.y  + "," + a.a.z  +"/";
				c = c+a.b.x  +"," + a.b.y  + "," + a.b.z  +"/";
			//}
			lineList.add(c);
		}
	}
	String[] skin = new String[lineList.size()];
	for (int i =0;i<lineList.size()-1;i++) {
		skin[i]=lineList.get(i);
	}
	parent.saveStrings("trails_"+framecount+".txt", skin);
}
	
	
	
	
	
//	
//	
////-------------------------------------------------------------------------------------
//public void saveSpringParticles(int framecount){
//	ArrayList<String>lineList = new ArrayList<String>();
//	for (VerletSpring3D a: springPhysics.springs) {
//		if(springPhysics.springs.size()>0){
//			String c = "";
//			//for(int i = 0; i<springPhysics.springs.size();i++){
//				VerletSpring3D l = springPhysics.springs.get(i);
//				
//				c = c+l.a.x  +"," + l.a.y  + "," + l.a.z  +"/";
//				//c = c+l.b.x  +"," + l.b.y  + "," + l.b.z  +"/";
//			//}
//			lineList.add(c);
//		}
//	}
//	String[] skin = new String[lineList.size()];
//	for (int i =0;i<lineList.size()-1;i++) {
//		skin[i]=lineList.get(i);
//	}
//	parent.saveStrings("trails_"+framecount+".txt", skin);
//}
//	
//	
//	
//	
//	
	
	
	//END
}
