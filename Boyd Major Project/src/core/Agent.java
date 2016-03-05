package core;
import java.awt.List;
import java.util.ArrayList;

import javax.print.attribute.Size2DSyntax;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import toxi.geom.*;
import toxi.math.InterpolateStrategy;
import toxi.physics2d.behaviors.AttractionBehavior2D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;
import voxelTools.VoxelGrid;



/*------------------------------------

Class that handles flocking, neighbour search
and trails

------------------------------------*/

public class Agent extends Plane3D {
	public ArrayList neighbours = new ArrayList();
	public ArrayList<Link> trail = new ArrayList<Link>();
	public VoxelGrid voxelGrid;
	
	
	public boolean initialDirection = true;
	public boolean move = true;
	public Vec3D direction;
	public Vec3D startPos;
	
	//springs and strings
	public ArrayList<VerletParticle3D> particleList = new ArrayList<VerletParticle3D>();
	public ArrayList<VerletSpring3D> springList = new ArrayList<VerletSpring3D>();

	//physics
	public VerletPhysics3D physics;


	//-------------------------------------------------------------------------------------

	//Constructors

	//-------------------------------------------------------------------------------------
	
	public Agent (Vec3D _o, Vec3D _x, Vec3D _y, boolean _f) {
		super(_o, _x, _y);
		f=_f;
		resetTrail();
	}

	public Agent (Vec3D _o, boolean _f){
		super (_o);
		f=_f;
		resetTrail();
	}

	public Agent (Vec3D _o, Vec3D _x, boolean _f){
		super (_o,_x);
		f = _f;
		resetTrail();
	}

	public Agent (Plane3D _b, boolean _f) {
		super(_b);
		f=_f;
		resetTrail();
	}
	
	//boyd constructors//
	
	public Agent (Vec3D _o, boolean _f, VoxelGrid _voxelGrid) {
		super (_o);
		f=_f;
		resetTrail();
		voxelGrid = _voxelGrid;
	}
	
	
	public Agent (Vec3D _o, boolean _f, VoxelGrid _voxelGrid, VerletPhysics3D _physics) {
		super (_o);
		f=_f;
		resetTrail();
		voxelGrid = _voxelGrid;
		physics = _physics;
	}
	
	

	//-------------------------------------------------------------------------------------

	//Default Run Function - extend or override
	//TODO kill this default function
	
	//-------------------------------------------------------------------------------------
//@Override
	public void run(Environment environment) {
		
	
	//flocking
	getNeighbours(this, 50, environment); //get nearby agents
	for(Plane3D a:(ArrayList<Plane3D>)neighbours){
		//repel(a, 0, 10, 0.01f, "exponential");
		//cohere(a, 30, 80, 0.01f, "exponential");
		//align(this, a, 0, 50, 0.1f, "exponential");
	}	
		
		
	attractToVoxels(voxelGrid);
	
	//repelFromParticles();
	
	addSprings(1f);
	
	
	
	
	
	
	update();

	}


	//-------------------------------------------------------------------------------------

	//Functions for reading and writing to voxels

	//-------------------------------------------------------------------------------------
	
	
	
	public void attractToVoxels(VoxelGrid voxelGrid){
		
		if (initialDirection == true){
		startPos = this.copy();
		Vec3D toVoxel = new Vec3D();
		toVoxel = voxelGrid.findClosest(this, 18, 0);
		direction = toVoxel;
		initialDirection = false;
	}
		
		

		if (move == true) {
		addForce(direction);
		
		//run trail
		addLink(1f);
		}
		float currentVal = voxelGrid.getValue(this);
		if(currentVal>0){
		move = false;
		vel.set(0,0,0);
		}
		
		if(this.z<-2f){
		particleList.clear();
		springList.clear();
		set(startPos.x+5, startPos.y+5, startPos.z);
		startPos = this.copy();
		}
		
		
}
	


	
	//-------------------------------------------------------------------------------------

	//Functions for reading and writing to the environment

	//-------------------------------------------------------------------------------------
	
	public void getNeighbours(Vec3D p, float rad, Environment environment) {
		neighbours = new ArrayList<Plane3D>();
		ArrayList addList = environment.getWithinSphere(p, rad);
		if(addList!=null){
			neighbours.addAll(addList);
		}
	}
	
	public void addPhe2D(DataMap pheMap, Vec3D pt, float val){
		pheMap.addToPoint(pt, val);
	}
	
	public void addPhe3D(PointOctree pheCollection, Plane3D pt, float val){
		pheCollection.add(new Phe(pt, val));
	}
	
	public void addPt(Environment environment, Plane3D p){
		environment.addPlane(p);
	}

	//-------------------------------------------------------------------------------------

	//Functions for attraction/repulsion from particle/springs

	//-------------------------------------------------------------------------------------
	
	//avoid existing particle trails
	
	public void repelFromParticles(){
		
		for (Agent a:(ArrayList<Agent>)neighbours){

		//access particle list - get latest particle - repel from it
			if (!a.particleList.isEmpty()){
			
			VerletParticle3D repelP = a.particleList.get(a.particleList.size()-1);
			repel(repelP, 0, 10, 0.001f, "exponential");
			}
			}
	
	}
	
	
	
	
	
	//-------------------------------------------------------------------------------------

	//Creating, destroying and manipulating Trails

	//-------------------------------------------------------------------------------------

	//BOYDS
	//TODO reset trail when pos resets
	
	
	//add a particle over time to agents path
public void addLink(float rate){
	if (age%rate==0){ addParticle();  }
}
	

	//add a particle at agents position
	public void addParticle(){
		
		VerletParticle3D particle = new VerletParticle3D(this, 2f);
		
		//make sure always a particle in list
		if (particleList.size() == 0){
		particleList.add(particle);
		}

		//check theres not already a particle there
		if (particleList.size()>0){
			//get distance from agent pos to last particle in list
			float a = particleList.get(particleList.size()-1).distanceTo(this);
			if (a!=0){
				particleList.add(particle);
				
			//	System.out.println(particle.getWeight());
			}
		}
	}
		
	//add springs when motion has stopped
	
	
	public void addSprings(float str){
		if (particleList.size()>0 && move == false){
			
			// if the springList is empty then add strings
			if (springList.isEmpty()){
			
			for (int i = 1; i < particleList.size(); i++){
				VerletParticle3D p2 = particleList.get(i);
				VerletParticle3D p1 = particleList.get(i-1);
				
				float springRestLength = p2.distanceTo(p1);
		
				VerletSpring3D springSection = new VerletSpring3D(p2, p1, springRestLength, str);
				springList.add(springSection);
				
				//test
				
			}
			//should only run once once lists are full
			anchorEnds(springList,particleList);
			addListsToPhysicsEngine(physics);
			
			}

		}
		}

	
	
	//add behaviour to particle. not sure best place to do this. dont know if particle behaviour will carry through.
	public void addForceToParticles(){
		//for (VerletParticle3D p : particleList){p.add      }
	}
	
	
	
	//function to add lists to physics engine
	public void addListsToPhysicsEngine(VerletPhysics3D physics){
	for (VerletParticle3D p : particleList){physics.addParticle(p);}
	for (VerletSpring3D s : springList){physics.addSpring(s);}
	}
	
	
	//System.out.println(springList.size());
		
	public void anchorEnds(ArrayList<VerletSpring3D> springList, ArrayList<VerletParticle3D> particleList){
		
		//lock particle ends
		if (particleList.isEmpty() == false){
			VerletParticle3D p1 = particleList.get(0);
			p1.lock();
			VerletParticle3D p2 = particleList.get(particleList.size()-1);
			p2.lock();
		}
		
		
		
		//lock spring ends - this fucks it up,  its locking every spring end

//		if (springList.isEmpty() == false){
//			
//			//lock a particle of first spring
//			VerletSpring3D s1 = springList.get(0);
//			s1.lockA(true);
//			
//			//lock b particle of last spring
//			VerletSpring3D s2 = springList.get(springList.size()-1);
//			s2.lockB(true);
//		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//GWYLLS
	//------------------------------------------------------------------------------------
	
	
	public void addToTrail(Plane3D p){
		Link lastLink = trail.get(trail.size()-1);
		//if last link is same as this link..
		if(lastLink.b==this){
			lastLink.setB(new Plane3D(p));
			if(p.locked())lastLink.b.lock();
			lastLink.updateLength();
		}else{
			Link next = new Link(lastLink.b,new Plane3D(p), true);
			if(p.locked())next.b.lock();
			trail.add(next);
		}
		
	}
	
	public void addToTrail(Environment environment, Plane3D p){
		Link lastLink = trail.get(trail.size()-1);
		if(lastLink.b==this){
			lastLink.setB(new Plane3D(p));
			lastLink.updateLength();
		}else{
			trail.add(new Link(lastLink.b,new Plane3D(p), true));
			addPt(environment,lastLink.b);
		}

	}
	
	
	public void removeFromTrail(int index){
		if(trail.size()!=1){
			trail.remove(index);
		}
	}
	
	public void resetTrail(){
		trail = new ArrayList<Link>();
		Link l =new Link(new Plane3D(this), this,true);
		trail.add(l);
	}
	
	public void deleteTrail(){
		trail = new ArrayList<Link>();
	}
	
	public void updateTrail(){
		updateTrail(1);
	}
	
	public void updateTrail(float damp){
		for(int i =0;i<trail.size();i++){
			Link l = trail.get(i);
			if(l.spr)l.spring();
			if(!l.a.locked())l.a.update(damp);
			if(i==trail.size()-1){
				if(!l.b.locked() && l.b!=this)l.b.update(damp);
			}
		}
	}
	
	public void addForceToTrail(Vec3D force){
		for(Link l:trail){
			l.a.addForce(force);
		}
	}

	protected void stiffenTrail(float bendResistance) {
		if(trail.size()>1){
			for(int i =1;i<trail.size();i++){
				Link prev = trail.get(i-1);
				Link next = trail.get(i);
				float currentAngle = prev.angleBetween(next,true);
				//if(currentAngle<parent.PI/3){
					
					Vec3D ab = prev.a.add(next.b).scale((float) 0.5);
					
					float targetAngle = prev.linkAngle;
					
					float diff = (targetAngle)-currentAngle/(float)Math.PI; //max is 1, min is -1
	
					//float sf = (float) (1+(0-1)*(0.5+0.5*parent.cos(diff*parent.PI))); //cosine interpolation
					
					Vec3D avg = ab.sub(prev.b).scale(bendResistance*diff);
					prev.b.addForce(avg);
				//}
			}
		}
	}
	
	//-------------------------------------------------------------------------------------

	//Interpolation

	//-------------------------------------------------------------------------------------
	
	//toxi circ interpolator
	
	public float interpCirc(float a, float b, float f, boolean isFlipped) {
        if (isFlipped) {
                return a - (b - a) * ((float) Math.sqrt(1 - f * f) - 1);
        } else {
                f = 1 - f;
                return a + (b - a) * ((float) Math.sqrt(1 - f * f));
        }
	}
	
	//toxi cosine interpolator 
	
	public float interpCos(float a, float b, float f) {
        return b+(a-b)*(float)(0.5+0.5*Math.cos(f*Math.PI));
	}
	
	//toxi sigmoid interpolator
	
	public float interpSigmoid(float a, float b, float f, float sharpPremult) {
        f = (f * 2 - 1) * sharpPremult * 5;
        f = (float) (1.0f / (1.0f + Math.exp(-f)));
        return a + (b - a) * f;
	}
	
	public float interpExp(float a, float b, float f){
		return(a+(b-a)*(f*f));
	}
	
	public float getInterpolatedVal(String type, float a, float b, float f){
		float sf = 0;
		switch(type){
			case "exponential": sf = interpExp(a, b, f);
			case "circle": sf = interpCirc(a, b, f,false);
			case "cosine": sf = interpCos(a, b, f);
			case "sigmoid": sf = interpSigmoid(a, b, f,1);
		}
		return sf;
	}
	
	//-------------------------------------------------------------------------------------

	//Interpolated Boid Behaviours

	//-------------------------------------------------------------------------------------
	public void cohere(Vec3D c2, float minDist, float maxDist, float maxForce, String interpolatorType){
		Vec3D to = c2.sub(this);
		float dist = to.magnitude();
		if(dist>minDist && dist<maxDist){	
			float f = ((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
			float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
			addForce(to.normalizeTo(sf));
		}
	}
	
	public void align(Plane3D c1, Plane3D c2, float minDist, float maxDist, float maxForce, String interpolatorType){
		Vec3D to = c2.sub(this);
		float dist = to.magnitude();
		if(dist>minDist && dist<maxDist){
			float f = 1-((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
			float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
			interpolateToPlane3D(c2,maxDist,sf);
		}
	}
	
	public void repel(Vec3D c2,float minDist, float maxDist, float maxForce, String interpolatorType){
		Vec3D to = c2.sub(this);
		float dist = to.magnitude();
		if(dist>=minDist && dist<=maxDist){
			float f = 1-((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
			float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
			addForce(to.normalizeTo(-sf));
		}
	}
	
	
	
	//-------------------------------------------------------------------------------------

	//Voxel functions

	//-------------------------------------------------------------------------------------
	

	
	
	
	
	
	
	
	
	
}