package projects.Boyd;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;
import core.Agent;
import core.Environment;



//Put in function reset in here, not in environment


public class ToxicAgent extends Agent {
	
	VoxelGrid pheremone;
	VoxelGrid structure;
	
	float TWO_PI = 6.28318530717958647693f;
	PApplet parent;

	public ToxicAgent(Vec3D _o, boolean _f, VoxelGrid _structure, VoxelGrid _pheremone, PApplet _parent) {
		super(_o, _f);
		
		pheremone = _pheremone;
		structure = _structure;
	
		parent = _parent;

	}

	@SuppressWarnings("static-access")
	@Override
	public void run(Environment environment){
	
		
		///WIND SIMULATION OF PARTICLE MOVEMENT USING PERLIN NOISE
		
		 Vec3D wind = new Vec3D(0,0,0);
		 wind.x = (float) (0.1*parent.cos(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z)));
		 wind.y = (float) 0.1*parent.sin(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z));
		 wind.z = (float) 0.1*parent.sin(TWO_PI*parent.noise(0.01f*this.x,0.01f*this.y,0.01f*this.z));
		addForce(wind);
		
		//RESET POSITION OF PARTICLE IF OUT OF BOUNDS//MIN AND MAX.
		if(!inBounds(-50, pheremone.getH()*(int)pheremone.s.y)){
			reset();
		}
		
		
		//FUNCTION FOR SETTING VOXEL VALUE TO 255 AT CURRENT POSITION
		pheremone.setValue(this, 255);
		
		//FUNCTION FOR AVOIDING STRUCTURAL VOXELS (PAINTED) BETWEEN THE VALS AND WITHIN A SEARCH RADIUSS
		avoidStructuralVoxels(structure, 5, 100f, 255f);
		
		update();
		
		
		//TRAIL FUNCTION 
		//if(age%1==0)addToTrail(this);
	}
	
	//------------------------------------------------------
	//FUNCTION FOR AVOIDING THE STRUCTURALVOXELGRID
	//------------------------------------------------------
	public void avoidStructuralVoxels(VoxelGrid structure, int searchRadius, float minVal, float maxVal){
		
		//get position of agent in voxel grid
		int[] v = structure.map(this);
		//get the vector to structural voxels with a val  between those defined within the search radius. Note: not the position of the voxel.
		Vec3D toVoxel = structure.findValInRange(v[0], v[1], v[2], searchRadius, minVal, maxVal);
		//System.out.println(toVoxel);
		
		float distance = toVoxel.magnitude();
		if (distance < searchRadius){
			Vec3D awayFromVoxel = toVoxel.getInverted();
			awayFromVoxel.normalizeTo(1);
			addForce(awayFromVoxel);
		}
		
		
//		float distance = toPheremone.magnitude();
//		
//		if (distance < maxRepelRadius){
//		Vec3D awayFromPheremone = toPheremone.getInverted();
//		awayFromPheremone.normalizeTo(1).scale(0.5f);
//		addForce(awayFromPheremone);
//		}
	}
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR RESETTING POSITION IF OUT OF BOUNDS
	//------------------------------------------------------
	
	public void reset(){
		//set((float) Math.random()*300, (float) Math.random()*300, (float) Math.random()*300);
		//should reset in a position which is +300 in the x direction, and randomly across the z/y planes
		set(new Vec3D(300f, (float) (float)Math.random()*(pheremone.getW()*pheremone.s.y), (float) Math.random()*(pheremone.getD()*pheremone.s.z)));

		resetTrail();
	}
	
	///----------------------------------------------------------------------------------------
	//// Check if agent is in bounds
	///----------------------------------------------------------------------------------------		  			
		
		public boolean inBounds(int boundsMin, int boundsMax) {
			if (  x< boundsMin ||  y < boundsMin || z< boundsMin )return false;
			if ( y > boundsMax ||  z> boundsMax )return false;
			return true;
		}
	
}
