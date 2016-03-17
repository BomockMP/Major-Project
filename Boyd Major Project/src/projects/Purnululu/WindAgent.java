package projects.Purnululu;

import javax.naming.ldap.StartTlsResponse;

import com.jogamp.newt.event.MouseAdapter;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import voxelTools.VoxelGrid;
import core.Agent;
import core.Environment;

public class WindAgent extends Agent {

	
	VoxelGrid voxelGrid;
	PApplet parent;
	public float TWO_PI = 6.28318530717958647693f;

	public Boolean startpBoolean = true;
	public boolean direction = true;


	
	
	//CONSTRUCTOR
	public WindAgent(Vec3D _o, boolean _f, VoxelGrid _voxelGrid, PApplet _parent, boolean _direction) {
		super(_o, _f);
		voxelGrid = _voxelGrid;
		parent = _parent;
		direction = _direction;
		

	}
	
	
	

	@Override
	public void run(Environment environment){
	
		 int bounds = voxelGrid.getH()*(int)voxelGrid.s.y;
		 int boundScale = (int) (bounds + (0.3f*bounds));
		 
		setStartPos();
		
		//RESET POSITION OF PARTICLE IF OUT OF BOUNDS//MIN AND MAX.
		if(!inBounds(-50, 150)){
			reset();
		}
		
		
		
		//FUNCTION FOR SETTING VOXEL VALUE TO 255 AT CURRENT POSITION//change this to just reduce val
		//voxelGrid.setValue(this, 0);
		windErosion(0.95f);
		//FUNCTION FOR AVOIDING STRUCTURAL VOXELS (PAINTED) BETWEEN THE VALS AND WITHIN A SEARCH RADIUSS
		avoidVoxels(voxelGrid, 6, 1f, 255f); //radius 4
		//bounceFromVoxels();
		
		///WIND SIMULATION OF PARTICLE MOVEMENT USING PERLIN NOISE
		windMovement();
		
	
		
		update();
		

	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR MOVEMENT
	//------------------------------------------------------
	@SuppressWarnings("static-access")
	public void windMovement(){
		 Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (0.4*parent.cos(TWO_PI*parent.noise(0.3f*this.x,0.1f*this.y,0.1f*this.z))); //increasing value before X is good - 0.2
		 wind.y = (float) 0.4*parent.sin(TWO_PI*parent.noise(0.3f*this.x,0.1f*this.y,0.1f*this.z));   //increasing value before X is good
		 wind.z = (float) 0.01*parent.cos(TWO_PI*parent.noise(0.3f*this.x,0.01f*this.y,0.01f*this.z));
		 
		 if (direction == true){
		 wind.scaleSelf(100);
		addForce(wind.getInverted());
		 }else{
			 wind.scaleSelf(-100);
				addForce(wind.getInverted());
		 }
	}
	
	
	public void setStartPos(){
		
		if (startpBoolean == true){
			startPos = this.copy();
			startpBoolean = false;
			
		}
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
			awayFromVoxel.scaleSelf(10);
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
			reset();
			}
		
		if (voxVal < 125 && voxVal >= 60){
			float newVal = voxVal*(erosionFactor*1);
			voxelGrid.setValue(this, newVal);
			//bounceFromVoxels();
			reset();
			}
		
		
		if (voxVal < 60 && voxVal >= 10){
		float newVal = voxVal*erosionFactor;
		voxelGrid.setValue(this, newVal);
		reset();
		//bounceFromVoxels();
		
		} if (voxVal < 10 && voxVal > 0) {
			voxelGrid.setValue(this, 0);	
			reset();
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

		//set(new Vec3D(300f, (float) (float)Math.random()*(voxelGrid.getW()*voxelGrid.s.y), (float) Math.random()*(voxelGrid.getD()*voxelGrid.s.z)));
		//System.out.println("Reset");
		if (startPos.z > 6){
			startPos.z = startPos.z-1;
		}
		set(startPos.x, startPos.y, startPos.z);
		resetTrail();
	}
	
	///----------------------------------------------------------------------------------------
	//// Check if agent is in bounds
	///----------------------------------------------------------------------------------------		  			
		
		public boolean inBounds(int boundsMin, int boundsMax) {
			if (  x< boundsMin ||  y < -30 || z< -10 )return false;
			if (x > boundsMax || y > 130 ||  z> 70 )return false;
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
