package projects.Purnululu;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import voxelTools.VoxelGrid;
import core.Agent;
import core.Environment;

public class RainAgent extends Agent {
	
	

	VoxelGrid voxelGrid;
	PApplet parent;
	public float TWO_PI = 6.28318530717958647693f;

	public Boolean startpBoolean = true;


	
	
	//CONSTRUCTOR
	public RainAgent(Vec3D _o, boolean _f, VoxelGrid _voxelGrid, PApplet _parent) {
		super(_o, _f);
		voxelGrid = _voxelGrid;
		parent = _parent;
		

	}
	
	
	

	@Override
	public void run(Environment environment){
	

		//RESET POSITION OF PARTICLE IF OUT OF BOUNDS//MIN AND MAX.
		if(!inBounds(-50, 100)){
			reset();
		}
		

		//FUNCTION FOR SETTING VOXEL VALUE TO 255 AT CURRENT POSITION//change this to just reduce val
		
		
		//FUNCTION FOR AVOIDING STRUCTURAL VOXELS (PAINTED) BETWEEN THE VALS AND WITHIN A SEARCH RADIUSS
		//avoidVoxels(voxelGrid, 15, 1f, 255f);
		
		
		rainBehaviour(0.1f);
		update();
		

	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR MOVEMENT
	//------------------------------------------------------
	@SuppressWarnings("static-access")
	public void windMovement(){
		 Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (0.1*parent.cos(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z)));
		 wind.y = (float) 0.1*parent.sin(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z));
		 wind.z = (float) 0.1*parent.sin(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z));
		 wind.normalizeTo(0.05f);
		addForce(wind);

	}
	
	
	
	public void gravityForce(){
		Vec3D gravity = new Vec3D(0,0,-0.098f);
		addForce(gravity);
		
	}
	
	
	public void rainBehaviour(float erosionFactor){
		
		gravityForce();
		
	//	windMovement();
		
		

		
		//determine if hitting(or really close to?) a voxel, and repel from it
		avoidVoxels(voxelGrid, 5, 1f, 255f);
		//dissolve voxels when in grid
		
		
		float voxVal = voxelGrid.getValue(this);
		if (voxVal > 6){
		float newVal = voxVal*erosionFactor;
		voxelGrid.setValue(this, newVal);
		bounceFromVoxels();
		} else {
			voxelGrid.setValue(this, 0);	
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
			awayFromVoxel.normalizeTo(1);
			addForce(awayFromVoxel);
		}
		

	}
	
	
	public void bounceFromVoxels(){
		Vec3D rand = randomVector().scaleSelf(10);
		addForce(new Vec3D(rand.x,rand.y,10f));
		
		
	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR RESETTING POSITION IF OUT OF BOUNDS
	//------------------------------------------------------
	
	public void reset(){
		 int bounds = voxelGrid.getH()*(int)voxelGrid.s.y;
		 int boundScale = (int) (bounds + (0.2f*bounds));
		 int minBoundsScale = (int)-0.5f*bounds;
		 
//		float spawnptX = parent.random((float)minBoundsScale, boundScale);
//		float spawnptY = parent.random((float)minBoundsScale, boundScale);
//		float spawnptZ = 100;
		 
		 float spawnptX = parent.random((float)0, 100);
			float spawnptY = parent.random((float)0, 100);
			float spawnptZ = 100;
			
		set(spawnptX, spawnptY, spawnptZ);
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
