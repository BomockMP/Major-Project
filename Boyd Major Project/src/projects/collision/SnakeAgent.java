package projects.collision;

import java.util.ArrayList;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;
import voxelTools.VoxelGrid;
import core.Agent;
import core.Environment;

public class SnakeAgent extends Agent{

	VoxelGrid voxelGrid;
	PApplet parent;
	public float TWO_PI = 6.28318530717958647693f;

	
	//springs and strings
	//	public ArrayList<VerletParticle3D> particleList = new ArrayList<VerletParticle3D>();
	//	public ArrayList<VerletSpring3D> springList = new ArrayList<VerletSpring3D>();

		//physics
		public VerletPhysics3D physics;
		
		public boolean snakeComplete = false;
		
	
	//CONSTRUCTOR
	public SnakeAgent(Vec3D _o, boolean _f, PApplet _parent, VerletPhysics3D _physics) {
		super(_o, _f);

		parent = _parent;

		physics = _physics;

	}
	
	
	

	@Override
	public void run(Environment environment){
	

		//RESET POSITION OF PARTICLE IF OUT OF BOUNDS//MIN AND MAX.
		if(!inBounds(-800, 2300)){
			reset();
		}
		
//		if (this.y <100){
//			addForce(new Vec3D(0f,-10f,0f));
//		}
		
		
		if (age<100){
		addLink(5f);
		}else{
			snakeComplete = true;
		}
		
		
		windMovement();
		
		lockHead();

		update();
		
	
	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR MOVEMENT
	//------------------------------------------------------
	@SuppressWarnings("static-access")
	public void windMovement(){
		 Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (0.4*parent.cos(TWO_PI*parent.noise(0.3f*this.x,0.1f*this.y,0.1f*this.z))); //increasing value before X is good - 0.2
		 //wind.y = (float) 0.4*parent.sin(TWO_PI*parent.noise(0.3f*this.x,0.1f*this.y,0.1f*this.z));   //increasing value before X is good
		 wind.z = (float) 0.4*parent.cos(TWO_PI*parent.noise(0.3f*this.x,0.01f*this.y,0.01f*this.z));
		 
		 
		 wind.scaleSelf(100);
		addForce(wind.getInverted());
		 
			 wind.scaleSelf(-100);
				addForce(wind.getInverted());
		 
	}
	
	

	//------------------------------------------------------
	//Spring Trail stuff
	//------------------------------------------------------	
	
	
	
	
	
	//function to add lists to physics engine
	@Override
	public void addListsToPhysicsEngine(VerletPhysics3D physics){
	for (VerletParticle3D p : particleList){physics.addParticle(p);}
	for (VerletSpring3D s : springList){physics.addSpring(s);}
	}
	
	
	
	
	
	
	
	
	
	@Override
	public void addLink(float rate){
		if (age%rate==0){ addParticle();  
		}
	}
		

		//add a particle at agents position
@Override
		public void addParticle(){
			
			VerletParticle3D particle = new VerletParticle3D(this, 2f);
			
			//make sure always a particle in list
			if (particleList.size() == 0){
			particleList.add(particle);
			
			}

			//check theres not already a particle there
			if (particleList.size()>=1){
				//get distance from agent pos to last particle in list
				float a = particleList.get(particleList.size()-1).distanceTo(this);
				if (a!=0){
					particleList.add(particle);
				}		
			}
			
			if (particleList.size()>=2){
	
				for (VerletParticle3D p : particleList) {
					
					
					VerletParticle3D p1 = particleList.get(0);
					VerletParticle3D p2 = particleList.get(1);
					
					float springRestLength = p2.distanceTo(p1);
					
					VerletSpring3D springSection = new VerletSpring3D(p2, p1, springRestLength*0.6f, 0.1f);
					springList.add(springSection);
					particleList.remove(0);
					addListsToPhysicsEngine(physics);
					
					
					
			}
			}
	
		}
	


public void lockHead(){
	
	if (snakeComplete == true){
		
VerletParticle3D a = physics.particles.get(physics.particles.size()-1);

a.set(this);
	}
	}

	
@Override
public void addSprings(float str){

		if (particleList.size()>10){
		for (int i = 1; i < particleList.size(); i++){
			VerletParticle3D p2 = particleList.get(i);
			VerletParticle3D p1 = particleList.get(i-1);
			
			float springRestLength = p2.distanceTo(p1);
	
			VerletSpring3D springSection = new VerletSpring3D(p2, p1, springRestLength, str);
			springList.add(springSection);
		}

		addListsToPhysicsEngine(physics);
	}
	}



//------------------------------------------------------
//FUNCTION FOR RESETTING POSITION IF OUT OF BOUNDS
//------------------------------------------------------
	
	public void windDirection(Vec3D windDirection){

		windDirection.normalize();
		addForce(windDirection);
	}
	

	//------------------------------------------------------
	//FUNCTION FOR RESETTING POSITION IF OUT OF BOUNDS
	//------------------------------------------------------
	
	public void reset(){


		resetTrail();
	}
	
	

	///----------------------------------------------------------------------------------------
	//// Check if agent is in bounds
	///----------------------------------------------------------------------------------------		  			
		
		public boolean inBounds(int boundsMin, int boundsMax) {
			if (  x< boundsMin ||  y < boundsMin || z< boundsMin )return false;
			if (x > boundsMax || y > boundsMax ||  z> boundsMax )return false;
			return true;
		}
	
	
}
