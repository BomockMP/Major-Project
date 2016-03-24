package projects.Purnululu;

import java.util.ArrayList;

import javax.naming.ldap.StartTlsResponse;

import com.jogamp.newt.event.MouseAdapter;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;
import toxi.physics3d.behaviors.AttractionBehavior3D;
import toxi.physics3d.constraints.ParticleConstraint3D;
import toxi.physics3d.constraints.PlaneConstraint;
import voxelTools.VoxelGrid;
import core.Agent;
import core.Environment;
import core.Plane3D;

public class AnchorAgent extends Agent {

	
	VoxelGrid voxelGrid;
	PApplet parent;
	public float TWO_PI = 6.28318530717958647693f;

	public Boolean startpBoolean = true;
	public boolean Anchored = false;

	//physics
			public VerletPhysics3D physics;
	
	
	//CONSTRUCTOR
	public AnchorAgent(Vec3D _o, boolean _f, VoxelGrid _voxelGrid, VerletPhysics3D _physics, PApplet _parent) {
		super(_o, _f);
		voxelGrid = _voxelGrid;
		parent = _parent;
		physics = _physics;

	}
	
	
	

	@Override
	public void run(Environment environment){
		
		setStartPos(); //store starting position 
		
		//System.out.println(Anchored);
		
		
		getNeighbours(this, 50, environment); //get nearby agents
		for(Plane3D a:(ArrayList<Plane3D>)neighbours){
			//repel(a, 0, 10, 0.01f, "exponential");
			//cohere(a, 30, 80, 0.5f, "exponential");
			//align(this, a, 0, 50, 0.5f, "exponential");
		}	
			

		//RESET POSITION OF PARTICLE IF OUT OF BOUNDS//MIN AND MAX.
		if(!inBounds(-800, 2300)){
			reset(); 
		}
		
		
		
		//FUNCTION FOR AVOIDING  VOXELS (PAINTED) BETWEEN THE VALS AND WITHIN A SEARCH RADIUSS
		//avoidVoxels(voxelGrid, 3, 1f, 255f, 0.1f); //radius 4
		
		
		
		windMovement();
		//windErosion(0.01f);
		FindBestVoxel(voxelGrid, 10, 150);
		attachParticles(); //keep particle and agent position related
		update();
		

	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR MOVEMENT
	//------------------------------------------------------
	@SuppressWarnings("static-access")
	public void windMovement(){
		

		if (!Anchored){
			
		 Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (0.05f*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z))); //increasing value before X is good - 0.2
		// wind.y = (float) 0.05f*parent.sin(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z));   //increasing value before X is good
		// wind.z = (float) 0.000f*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.01f*this.y,0.01f*this.z));

		 wind.scaleSelf(1);
		addForce(wind.getInverted());
		}
		
		if (Anchored){
			
			//this.vel.scaleSelf(0.99f);
			//remove z component
			//this.vel.scaleSelf(1, 1, 0.01f);
		}
		
		
		
	}
	
	
	
	public void circularMovement(){
		
	}
	
	
	
	
	
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR STORING START POSITION
	//------------------------------------------------------
	
	
	public void setStartPos(){
		if (startpBoolean == true){
			startPos = this.copy();
			startpBoolean = false;
		}
	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR FINDING THE VOXELGRID
	//------------------------------------------------------
	
	public void FindBestVoxel(VoxelGrid voxelGrid, int searchRadius, float Val){
		
		//if agent is inside the voxel grid
		if (this.x > 75 || this.y > 75){
		
		//System.out.println("inPosition");
		//find voxel position with closest value
		Vec3D desiredVoxel = voxelGrid.findValPosition(this, searchRadius, 1, this, 255); 
		//check that the magnitude is >0 so it doesnt throw us the origin
		if (desiredVoxel.magnitude() != 0 && desiredVoxel.z > 8f)
		{
			
		//run function
		addParticleAtPositionAndAgent(desiredVoxel);
		}
		}
	}
	

	//------------------------------------------------------
	//FUNCTION FOR ADDING A PARTICLE AT VOXEL GRID POSITION
	//------------------------------------------------------
	
	public void addParticleAtPositionAndAgent(Vec3D otherParticlePosition){
		
		Vec3D anchorPoint = new Vec3D();
		
		//create new particle at agents current position if the particleList is empty.
		if (particleList.size() == 0){
		VerletParticle3D AgentPos = new VerletParticle3D(this, 1f);
		

		
		//AgentPos.addConstraint(arg0)
		
		particleList.add(AgentPos);

		//particle added at other position (anchor). 
	anchorPoint =	otherParticlePosition.copy();
		VerletParticle3D particleOther = new VerletParticle3D(anchorPoint, 1f);
		particleOther.lock();
		particleList.add(particleOther);
		
					
							VerletParticle3D p1 = particleList.get(0); //at agent
							VerletParticle3D p2 = particleList.get(1); //at other
						
							System.out.println(p1);
							System.out.println(p2);
							float springRestLength = p2.distanceTo(p1);
						
							VerletSpring3D springSection = new VerletSpring3D(p2, p1, springRestLength*0.5f, 0.1f);
							springList.add(springSection);
							addListsToPhysicsEngine(physics);
							
							this.vel.scaleSelf(1, 1, 0.01f); //stop wild motion
							Anchored = true;
	}
	}

	
public void attachParticles(){
	
	if (physics.particles.size() > 0){
		VerletParticle3D a = physics.particles.get(0);
		AttractionBehavior3D attractionToAgent = new AttractionBehavior3D(this, 50, 0.01f);
		a.addBehavior(attractionToAgent);
		a.add(this);
		cohere(a, 0, 150, 10, "exponential");
		}
	
}
		
		
		

	//------------------------------------------------------
	//FUNCTION FOR AVOIDING THE VOXELGRID
	//------------------------------------------------------
	public void avoidVoxels(VoxelGrid voxelGrid, int searchRadius, float minVal, float maxVal, float force){
		
		//get position of agent in voxel grid
		int[] v = voxelGrid.map(this);
		//get the vector to structural voxels with a val  between those defined within the search radius. Note: not the position of the voxel.
		Vec3D toVoxel = voxelGrid.findValInRange(v[0], v[1], v[2], searchRadius, minVal, maxVal);
		//System.out.println(toVoxel);
		
		float distance = toVoxel.magnitude();
		if (distance < searchRadius){
			Vec3D awayFromVoxel = toVoxel.getInverted();
			//System.out.println(awayFromVoxel);
			awayFromVoxel.scaleSelf(force);
			//awayFromVoxel.normalize();
			//System.out.println(awayFromVoxel);
			addForce(awayFromVoxel);
		}
	}
	
	
	public void bounceFromVoxels(){
		Vec3D rand = randomVector().scaleSelf(12);
		addForce(new Vec3D(-rand.x,-rand.y,rand.z));
	}
	
	
	
public void windErosion(float erosionFactor){

		//dissolve voxels when in grid
		
		float voxVal = voxelGrid.getValue(this);

		if (voxVal >= 125){
			float newVal = voxVal*(erosionFactor*1);
			voxelGrid.setValue(this, newVal);
			//bounceFromVoxels();
			//reset();
			}
		
		if (voxVal < 125 && voxVal >= 60){
			float newVal = voxVal*(erosionFactor*1);
			voxelGrid.setValue(this, newVal);
			//bounceFromVoxels();
			//reset();
			}
		
		
		if (voxVal < 60 && voxVal >= 10){
		float newVal = voxVal*erosionFactor;
		voxelGrid.setValue(this, newVal);
		//reset();
		//bounceFromVoxels();
		
		} if (voxVal < 10 && voxVal > 0) {
			voxelGrid.setValue(this, 0);	
			//reset();
		}
	}
	
	
	

	//------------------------------------------------------
	//FUNCTION FOR RESETTING POSITION IF OUT OF BOUNDS
	//------------------------------------------------------
	
	public void reset(){

		
		if (startPos.z > 3){
			startPos.z = startPos.z-1;
		}
		
		set(startPos.x, startPos.y, startPos.z);
		resetTrail();
	}
	
	
	
	
	
	
	///----------------------------------------------------------------------------------------
	//// Check if agent is in bounds
	///----------------------------------------------------------------------------------------		  			
		
		public boolean inBounds(int boundsMin, int boundsMax) {
			if (  x< boundsMin ||  y <0 || z< -150 )return false;
			if (x > boundsMax || y > 150 ||  z> 150 )return false;
			return true;
		}
	
}
