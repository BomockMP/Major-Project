package projects.Purnululu;

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


	
	
	//CONSTRUCTOR
	public WindAgent(Vec3D _o, boolean _f, VoxelGrid _voxelGrid, PApplet _parent) {
		super(_o, _f);
		voxelGrid = _voxelGrid;
		parent = _parent;
		

	}
	
	
	

	@Override
	public void run(Environment environment){
	
		 int bounds = voxelGrid.getH()*(int)voxelGrid.s.y;
		 int boundScale = (int) (bounds + (0.9f*bounds));
		 
		setStartPos();
		
		//RESET POSITION OF PARTICLE IF OUT OF BOUNDS//MIN AND MAX.
		if(!inBounds(-boundScale, boundScale)){
			reset();
		}
		
		
		
		//FUNCTION FOR SETTING VOXEL VALUE TO 255 AT CURRENT POSITION//change this to just reduce val
		voxelGrid.setValue(this, 0);
		
		//FUNCTION FOR AVOIDING STRUCTURAL VOXELS (PAINTED) BETWEEN THE VALS AND WITHIN A SEARCH RADIUSS
		avoidVoxels(voxelGrid, 15, 1f, 255f);
		
		
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
		 wind.x = (float) (0.1*parent.cos(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z)));
		 wind.y = (float) 0.1*parent.sin(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z));
		 wind.z = (float) 0.1*parent.sin(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z));
		 wind.scaleSelf(100);
		addForce(wind.getInverted());

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
			awayFromVoxel.normalizeTo(1);
			addForce(awayFromVoxel);
		}
		

	}
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR RESETTING POSITION IF OUT OF BOUNDS
	//------------------------------------------------------
	
	public void reset(){

		//set(new Vec3D(300f, (float) (float)Math.random()*(voxelGrid.getW()*voxelGrid.s.y), (float) Math.random()*(voxelGrid.getD()*voxelGrid.s.z)));
		//System.out.println("Reset");
		set(startPos.x, startPos.y, startPos.z);
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
