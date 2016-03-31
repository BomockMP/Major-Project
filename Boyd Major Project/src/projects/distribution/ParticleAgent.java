package projects.distribution;

import java.util.ArrayList;

import core.Agent;
import core.Environment;
import core.Plane3D;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletMinDistanceSpring3D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;
import voxelTools.VoxelGrid;

public class ParticleAgent extends VerletParticle3D {
	
	public ArrayList neighbours = new ArrayList();
	public double random;
	public VoxelGrid voxelGrid;
	public float age = 0;
	
	
	
	public ParticleAgent(float x, float y, float z, VoxelGrid _voxelGrid) {
		super(x, y, z);
		voxelGrid = _voxelGrid;
		random = (float)Math.random(); 
	}

	
	
	public void run(SpringManager springManager){
		
	
		float searchRadius = 60*(float)random;
		getNeighbours(this, searchRadius, springManager);
		addSpringsToCloseParticles(springManager.springPhysics, 3, false);
		
		avoidVoxels(voxelGrid, 30, 1f, 255f, 100f);
		
		age++;
	}
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR FINDING CLOSE PARTICLES USING OCTREE
	//------------------------------------------------------
	//function for adding springs to close agents. Trying it here.
	
	public void addSpringsToCloseParticles(VerletPhysics3D springPhysics, int connectionCount, boolean connectAll){


			if(connectionCount < neighbours.size() && connectAll == false){
			for (int i = 0; i <= connectionCount; i++){
				VerletParticle3D a = (VerletParticle3D)neighbours.get(i);
				if (springPhysics.getSpring(a, this) == null){
				VerletSpring3D s = new VerletSpring3D(this, a, 50, 0.001f);
				springPhysics.addSpring(s);
				}
			}
			
			}else{
				
				for (VerletParticle3D a:(ArrayList<VerletParticle3D>)neighbours){
					if (springPhysics.getSpring(a, this) == null){
				VerletSpring3D s = new VerletSpring3D(this, a, 50, 0.001f);
					springPhysics.addSpring(s);
				}
				}
			}

	}
	
	
	
	
	
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR FINDING CLOSE PARTICLES USING OCTREE
	//------------------------------------------------------
	
	
	public void getNeighbours(Vec3D p, float rad, SpringManager springManager) {
		neighbours = new ArrayList<Plane3D>();
		ArrayList addList = springManager.getWithinSphere(p, rad);
		if(addList!=null){
			neighbours.addAll(addList);
		}
	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR AVOIDING THE STRUCTURALVOXELGRID
	//------------------------------------------------------
	//TODO ADD lock function if it hits desirable voxel
	//actually attracting
	
	public void avoidVoxels(VoxelGrid voxelGrid, int searchRadius, float minVal, float maxVal, float update){
		
		if (age%update==0){
			
			//lock if on white voxel
			//locking();
			
			
		//get position of agent in voxel grid
		int[] v = voxelGrid.map(this);
		//get the vector to structural voxels with a val  between those defined within the search radius. Note: not the position of the voxel.
		Vec3D toVoxel = voxelGrid.findValInRange(v[0], v[1], v[2], searchRadius, minVal, maxVal);
		//System.out.println(toVoxel);
		toVoxel.scaleSelf(1, 1, 0.0f);
		
		
		
		
		float distance = toVoxel.magnitude();
		if (distance < searchRadius){
			
			Vec3D awayFromVoxel = toVoxel; //repel Vec3D awayFromVoxel = toVoxel.getInverted();
			//System.out.println(awayFromVoxel);
			awayFromVoxel.scaleSelf(0.01f);
			//System.out.println(awayFromVoxel);
			addForce(awayFromVoxel);
		}
		
		}
	}
	
	//lock
	
	public void locking(){
		
		float voxVal = voxelGrid.getValue(this);
		
		if (voxVal > 0){
			this.lock();
		}
	}
	
	
	
}
