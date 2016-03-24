package projects.Purnululu;

import java.util.ArrayList;

import javax.naming.ldap.StartTlsResponse;

import com.jogamp.newt.event.MouseAdapter;
import com.vividsolutions.jts.math.MathUtil;

import controlP5.CheckBox;
import processing.core.PApplet;
import toxi.color.HistEntry;
import toxi.geom.Vec3D;
import toxi.math.MathUtils;
import voxelTools.VoxelGrid;
import core.Agent;
import core.Environment;
import core.Plane3D;

public class WindAgent extends Agent {

	
	VoxelGrid voxelGrid;
	PApplet parent;
	public float TWO_PI = 6.28318530717958647693f;

	public boolean startpBoolean = true;
	public boolean direction = true;
	public float randomStartingDirection = 1;
	public boolean bounds = true;
	
	public float sandBank = 0;
	
	
	public float theta = 0.0f;
	public float spiralRate = 7;
	public boolean spiral = false;
	
	//CONSTRUCTOR
	public WindAgent(Vec3D _o, boolean _f, VoxelGrid _voxelGrid, PApplet _parent, boolean _direction) {
		super(_o, _f);
		voxelGrid = _voxelGrid;
		parent = _parent;
		direction = _direction;
		
		spiral = false;
		//randomStartingDirection = parent.randomGaussian();

	}
	
	
	

	@Override
	public void run(Environment environment){
		
		
		//System.out.println(spiral);
		
		getNeighbours(this, 50, environment); //get nearby agents
		for(Plane3D a:(ArrayList<Plane3D>)neighbours){
			//repel(a, 0, 10, 0.01f, "exponential");
		//	cohere(a, 30, 80, 0.5f, "exponential");
			//align(this, a, 0, 50, 0.5f, "exponential");
		}	
				 
		setStartPos();
		
		//RESET POSITION OF PARTICLE IF OUT OF BOUNDS//MIN AND MAX.
		if(!inBounds(-80, 170)){
			reset();
		}

		windErosion(0.1f);
		windAddition(1f);
		//FUNCTION FOR AVOIDING STRUCTURAL VOXELS (PAINTED) BETWEEN THE VALS AND WITHIN A SEARCH RADIUSS
		avoidVoxels(voxelGrid, 1, 30f, 255f); //radius 4
		
		
		///WIND SIMULATION OF PARTICLE MOVEMENT USING PERLIN NOISE
		float randomX = parent.random(35, 150);
		
		if (this.x > randomX)
		{
		spiral = true;
		}
		
		
	if(spiral == false){
		
		windMovement();
	}
	 
		if (spiral == true){
		
		if (age%spiralRate==0){
			spiralMotion(1000f, 0.1f, 100f, 0.01f);
			if(age%100==0){
				if (spiralRate>0){
				spiralRate--;
				}else{
					spiral = false;
					this.vel.set(0,0,0);
					windMovement();
				}
			}
		}
		}
		
		
		
		update();
		

	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR MOVEMENT
	//------------------------------------------------------
	@SuppressWarnings("static-access")
	public void windMovement(){
		
		
		//wind
		 Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (0.4*randomStartingDirection*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z))); //increasing value before X is good - 0.2
		 wind.y = (float) 0.4*randomStartingDirection*parent.sin(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z));   //increasing value before X is good
		 wind.z = (float) 0.0001*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.01f*this.y,0.01f*this.z));

		 if (direction == true){
		 wind.scaleSelf(1);
		addForce(wind.getInverted());
		 }else{
			 wind.scaleSelf(-1);
				addForce(wind.getInverted());
		 }
	}
	
	
	
	public void setStartPos(){
		
		if (startpBoolean == true){
			startPos = this.copy();
			startpBoolean = false;
			
		}
	}
	
	
	public void spiralMotion(Float radius, Float distance, float angle, float maxForce){
		
		
		
	//	public void spiral2D(float wanderR, float wanderD, float angle) {
		theta += angle;     // Randomly change wander theta

			// Now we have to calculate the new location to steer towards on the wander circle
			Vec3D circleloc = vel.copy();  // Start with velocity
			circleloc.normalize();            // Normalize to get heading
			circleloc.scaleSelf(distance);          // Multiply by distance
			circleloc.addSelf(this.copy());               // Make it relative to boid's location

			Vec3D circleOffSet = new Vec3D(radius* PApplet.cos(theta),radius* PApplet.sin(theta),0);
			Vec3D target = circleloc.add(circleOffSet);
			this.accel.addSelf(target);

			
			
		}
		
		

	
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR AVOIDING THE STRUCTURALVOXELGRID
	//------------------------------------------------------
	public void avoidVoxels(VoxelGrid voxelGrid, int searchRadius, float minVal, float maxVal){
		
		//get position of agent in voxel grid
		int[] v = voxelGrid.map(this);
		//get the vector to structural voxels with a val  between those defined within the search radius. Note: not the position of the voxel.
		Vec3D toVoxel = voxelGrid.findValInRange(v[0], v[1], v[2], searchRadius, minVal, maxVal);
		//System.out.println(toVoxel);
		
		float distance = toVoxel.magnitude();
		if (distance < searchRadius){
			Vec3D awayFromVoxel = toVoxel.getInverted();
			//System.out.println(awayFromVoxel);
			awayFromVoxel.scaleSelf(1);
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


		
		if ( voxVal >= 10){
		float newVal = voxVal*erosionFactor;
		voxelGrid.setValue(this, newVal);
		reset();
		
		} if (voxVal < 10 && voxVal > 0) {
			voxelGrid.setValue(this, 0);	
			//System.out.println("eaten");
			sandBank++;
		}
	}
	
public void windAddition(float scaleFactor){
	//&& this.x > 0
	if (sandBank > 0 ){
	
		//System.out.println(sandBank);
		
		Vec3D currentPos = new Vec3D (this.x, this.y, this.z);
		
		
		if (currentPos.x > 25 && currentPos.y > 5 && currentPos.z > 5){
			
		
		
		//find a close empty voxel
		Vec3D target = voxelGrid.findValPosition(currentPos, 10, 2f, this, 0f);
		//System.out.println(target);
		//find the voxels below it value
		float voxelBelow = voxelGrid.getValue((int)target.x, (int)target.y, (int)target.z-1);
		float voxelBelowSurrounds = 0;
		//if the voxel is occupied
		
	//test moving target in
		
		if (voxelBelow > 0 && target.x > 10 && target.z > 20){
			
			//check surrounding of voxel below
			for (int i = -1; i<=1; i++){
				//check it doesnt equal 0. if not surrounds = > 0
				
				float surroundVal = voxelGrid.getValue((int)target.x+i, (int)target.y+i, (int)target.z-1);
				
				
				if (surroundVal > 0){
				voxelBelowSurrounds = surroundVal;
				}
				}
			
			
			
			if(voxelBelowSurrounds > 0){
				
			
		//check the targets value
		float TargetValue = voxelGrid.getValue(target);
		
		
		
		if (target.magnitude() !=0 && TargetValue < 1){
		//if its not at origin and the value is 0 then
			
			//System.out.println(TargetValue);
			voxelGrid.setValue(target, 255);
			sandBank--;
			
		}
		}
		}
		}
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

		
	if (startPos.z > 5){
		//System.out.println(startPos.z);
		float r = parent.random(-1, 1);
			startPos.z = startPos.z-r;
			
		}
		
	
		set(startPos.x, startPos.y, startPos.z);
		spiral = false;
		resetTrail();
	}
	
	
	
	
	
	
	///----------------------------------------------------------------------------------------
	//// Check if agent is in bounds
	///----------------------------------------------------------------------------------------		  			
		
		public boolean inBounds(int boundsMin, int boundsMax) {
			if (  x< boundsMin ||  y < -20 || z< 2 )return false;
			if (x > boundsMax || y > 160 ||  z> 80 )return false;
			return true;
		}
	
}
//
//Vec3D wind = new Vec3D(0,0,0);
//wind.x = (float) (0.4*parent.cos(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z)));
//wind.y = (float) 0.4*parent.sin(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z));
//wind.z = (float) 0.01*parent.sin(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z));
//wind.scaleSelf(100);
//addForce(wind.getInverted());

//float voxVal = voxelGrid.getValue(this);
//float voxelBelow = voxelGrid.getValue((int)this.x, (int)this.y, (int)this.z-1);
//if (voxVal < 1 && voxelBelow > 0){
//	voxelGrid.setValue(this, 255);
//	sandBank--;
