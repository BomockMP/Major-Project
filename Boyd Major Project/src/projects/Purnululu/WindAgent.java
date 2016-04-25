package projects.Purnululu;

import java.security.PublicKey;
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
	public String direction;
	public float randomStartingDirection = 1;
	public boolean bounds = true;
	
	public float sandBank = 0;
	
	
	public float theta = 0.0f;
	public float spiralRate = 3;
	public boolean spiral = false;
	public float randomSpiral;
	public float XdepositPosition;
	public float XdepositPositionMax;
	public float YXdepositPosition;
	public float YdepositPositionMax;
	
	float randomX;
	float randomZ;
	public float windLateral = 0.3f;
	public float windForward = 5.9f;
	//CONSTRUCTOR
	public WindAgent(Vec3D _o, boolean _f, VoxelGrid _voxelGrid, PApplet _parent, String _direction) {
		super(_o, _f);
		voxelGrid = _voxelGrid;
		parent = _parent;
		direction = _direction;
		
		spiral = false;
		
		//randomStartingDirection = parent.randomGaussian();

	}
	
	
	

	@Override
	public void run(Environment environment){
		
		
		
		randomZ = MathUtils.random(-1,1);
		
		getNeighbours(this, 50, environment); //get nearby agents
		for(Plane3D a:(ArrayList<Plane3D>)neighbours){
			//repel(a, 0, 10, 0.01f, "exponential");
		//	cohere(a, 30, 80, 0.5f, "exponential");
			//align(this, a, 0, 50, 0.5f, "exponential");
		}	
				 
		setStartPos();
		
		//RESET POSITION OF PARTICLE IF OUT OF BOUNDS//MIN AND MAX.
		if(!inBounds(-80, 300)){
			reset();
		}

		windErosion(0.1f);
		windAddition(1f, 1f, 4, 2);
		//FUNCTION FOR AVOIDING STRUCTURAL VOXELS (PAINTED) BETWEEN THE VALS AND WITHIN A SEARCH RADIUSS
		avoidVoxels(voxelGrid, 4, 10f, 255f, 8f); //radius 4

		//if particle moved past voxel grid start getting weighed down by sand
		
		
if (this.x > 5 && this.z > 2 && this.x < 245 ){
		sandWeight(-0.03f);
	
}
		


//		if (this.x > randomX && randomSpiral > 0.25f)
//		{
//		spiral = true;
//		}
//	if(spiral == false){
//		
//		windMovement();
//	}	
//		if (spiral == true){
//		
//		if (age%spiralRate==0){
//			spiralMotion(1000f, 0.1f, 100f, 0.01f);
//			if(age%100==0){
//				if (spiralRate>0){
//				spiralRate--;
//				}else{
//					spiral = false;
//					this.vel.set(0,0,0);
//					windMovement();
//				}
//			}
//		}
//		}

		windMovement(direction, 1f);
		update();

	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR MOVEMENT
	//------------------------------------------------------
	@SuppressWarnings("static-access")
	public void windMovement(String direction, float scaleForce){
		

		//wind
		 Vec3D wind = new Vec3D(0,0,0);
		 
		 
//		 switch (direction) {
//		case "posX": wind = windXPos();
//		case "posY": wind = windYPos();	
//		case "negX": wind = windXNeg();	
//		case "negY": wind = windYNeg();	
//		}
		 
		 if (direction == "posX"){
			 wind = windXPos(); 
		 }
		 
		 if (direction == "posY"){
			 wind = windYPos(); 
		 }
		 
		 if (direction == "negX"){
			 wind = windXNeg(); 
		 }
		 
		 if (direction == "negY"){
			 wind = windYNeg(); 
		 }
		 
		 wind.scaleSelf(scaleForce);
		 addForce(wind);
	}
	
	
	//WIND DIRECTION CASES
	
	public Vec3D windXPos(){
		Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (windForward*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z))); //increasing value before X is good - 0.2
		 wind.y = (float) windLateral*parent.sin(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z));   //increasing value before X is good
		 wind.z = (float) 0.005*randomZ*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.01f*this.y,0.01f*this.z));
		return wind.getInverted();
	}
	
	public Vec3D windYPos(){
		Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (windLateral*parent.sin(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z))); //increasing value before X is good - 0.2
		 wind.y = (float)(windForward*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z)));   //increasing value before X is good
		 wind.z = (float) (0.005*randomZ*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.01f*this.y,0.01f*this.z)));
		return wind.getInverted();
	}
	
	public Vec3D windXNeg(){
		Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (windForward*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z))); //increasing value before X is good - 0.2
		 wind.y = (float) (windLateral*parent.sin(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z)));   //increasing value before X is good
		 wind.z = (float) 0.05*randomZ*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.01f*this.y,0.01f*this.z));
		return wind;
	}
	
	public Vec3D windYNeg(){
		Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (windLateral*parent.sin(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z))); //increasing value before X is good - 0.2
		 wind.y = (float) windForward*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.1f*this.y,0.1f*this.z));   //increasing value before X is good
		 wind.z = (float) 0.05*randomZ*parent.cos(TWO_PI*parent.noise(0.1f*this.x,0.01f*this.y,0.01f*this.z));
		return wind;
	}

	
	
	
	public void setStartPos(){
		
		if (startpBoolean == true){
			startPos = this.copy();
			randomSpiral = parent.random(0,1);
			XdepositPosition = parent.random(2,8);
			XdepositPositionMax = parent.random(115,145);	
			YXdepositPosition = parent.random(2,8);
			YdepositPositionMax = parent.random(115,120);
			
			
			
			
			randomX = parent.random(70, 110);
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

			Vec3D circleOffSet = new Vec3D(radius* PApplet.cos(theta),radius* PApplet.sin(theta),-10f);
			Vec3D target = circleloc.add(circleOffSet);
			this.accel.addSelf(target);
		}
		
		

	
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR AVOIDING THE STRUCTURALVOXELGRID
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
			
			if (awayFromVoxel.z < 0){
			//awayFromVoxel.scaleSelf(1, 1, -1); //make flow up only
			}
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


		
//		if ( voxVal > 254){
//			//float newVal = voxVal*erosionFactor;
//			//voxelGrid.setValue(this, newVal);
//			//if (spiral == false){
//			reset();
//			//}
//			
//			} 
		
		
		if ( voxVal >= 10 && voxVal  <= 255 && this.z>1){
		float newVal = voxVal*erosionFactor;
		voxelGrid.setValue(this, newVal);
		//if (spiral == false){
		reset();
		//}
		
		} 
		
		if (voxVal < 10 && voxVal > 0 && this.z>1) {
			voxelGrid.setValue(this, 0);	
			//System.out.println("eaten");
			sandBank++;
		}
	}
	
public void windAddition(float scaleFactor, float dropRate, int dropRadius, float VoxelBelowRequirement){

	if (sandBank > 0){

		
		//  && age%dropRate==0 
		Vec3D currentPos = new Vec3D (this.x, this.y, this.z);

		//bounds for dropping a particle
		if (currentPos.x > XdepositPosition && currentPos.x < 110 && currentPos.y > 5 && currentPos.y < 115 && currentPos.z > 1){

		//find a close empty voxel
		Vec3D target = voxelGrid.findValPosition(currentPos, dropRadius, 2f, this, 0f);
		
		//find the voxels below it value
		float voxelBelow = voxelGrid.getValue((int)target.x, (int)target.y, (int)target.z-1);
		
		//store the value of the voxel surrounding the voxel below to avoid forming towers only
		float voxelBelowSurroundValue = 0;
		float voxelBelowSurroundsCount = 0;
		
		
		//If the boxel below value is > 0 and the target is within bounds
		if (voxelBelow > 0 && target.x > 0){
			
			//check surrounding of voxel below
			for (int i = -1; i<=1; i++){
				for (int k = -1; k <=1; k++){
				//check it doesnt equal 0. if not surrounds = > 0
				
				float surroundVal = voxelGrid.getValue((int)target.x+i, (int)target.y+k, (int)target.z-1);
				
				
				
				if (surroundVal > 0){
					voxelBelowSurroundValue = surroundVal;
					voxelBelowSurroundsCount++;
				}
				}
			}
			
			
			
			if(voxelBelowSurroundValue > 0 && voxelBelowSurroundsCount >= VoxelBelowRequirement && target.x > 5){
				
			
		//check the targets value
		float TargetValue = voxelGrid.getValue(target);

		
		if (target.magnitude() !=0 && TargetValue < 1){
		//if its not at origin and the value is 0 then
			
			//System.out.println("painted");
			voxelGrid.setValue(target, 254);
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
	
	
	public void sandWeight(float strength){
		
		//grav
		addForce(new Vec3D(0,0,strength));
		
		if (sandBank>0){
			addForce(new Vec3D(0,0,strength));
		}
	}
	
	
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR RESETTING POSITION IF OUT OF BOUNDS
	//------------------------------------------------------
	
	public void reset(){

		spiralRate = 0;
		theta = 0.0f;
		spiral = false;
		
	if (startPos.z > 5){
		//System.out.println(startPos.z);
		//float r = parent.random(-1, 1);
			//startPos.z = startPos.z-r;
			
			
		}
		
	
		set(startPos.x, startPos.y, startPos.z);
		//spiral = false;
		resetTrail();
	}
	
	
	
	
	
	
	///----------------------------------------------------------------------------------------
	//// Check if agent is in bounds
	///----------------------------------------------------------------------------------------		  			
		
		public boolean inBounds(int boundsMin, int boundsMax) {
			if (  x< boundsMin ||  y < boundsMin || z< -3 )return false;
			if (x > boundsMax || y > boundsMax ||  z> 45 )return false;
			return true;
		}
	
}
