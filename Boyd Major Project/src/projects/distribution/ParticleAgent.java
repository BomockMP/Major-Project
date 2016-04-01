package projects.distribution;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;

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
	public boolean removeParticle = true;
	public boolean springIt = true;
	
	public float springCount = 0;
	
	public ParticleAgent(float x, float y, float z, VoxelGrid _voxelGrid) {
		super(x, y, z);
		voxelGrid = _voxelGrid;
		random = (float)Math.random(); 
	}

	
	
	public void run(SpringManager springManager){
		
	
		float searchRadius = 30f; //60*(float)random;
		getNeighbours(this, searchRadius, springManager);
		addSpringsToCloseParticles(springManager.springPhysics, 3, false);
		//addSprings(springManager.springPhysics);
		avoidVoxels(voxelGrid, 10, 0f, 1f, 100f, 0.1f);
		//removeIfOverEmptyVoxels(springManager);
		
		age++;
		
	}
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR FINDING CLOSE PARTICLES USING OCTREE
	//------------------------------------------------------
	//function for adding springs to close agents. Trying it here.
	
	public void addSpringsToCloseParticles(VerletPhysics3D springPhysics, int connectionCount, boolean connectAll){

		

			if(connectAll == false){
				//System.out.println(springCount);
				for (VerletParticle3D a:(ArrayList<VerletParticle3D>)neighbours){
					if (springPhysics.getSpring(a, this) == null && springCount < connectionCount){
						VerletSpring3D s = new VerletSpring3D(this, a, 15, 0.2f);
						//VerletMinDistanceSpring3D s = new VerletMinDistanceSpring3D(this, a, 20, 0.0001f);
						springPhysics.addSpring(s);
						springCount++;
					}
				}
				

			
			}else{
				
				for (VerletParticle3D a:(ArrayList<VerletParticle3D>)neighbours){
					if (springPhysics.getSpring(a, this) == null){
				VerletSpring3D s = new VerletSpring3D(this, a, 50, 0.001f);
				//VerletMinDistanceSpring3D s = new VerletMinDistanceSpring3D(this, a, 20, 0.0001f);
					springPhysics.addSpring(s);
			}
				}
			}

	}
	
	
	public void addSprings(VerletPhysics3D springPhysics){
		if(springIt == true){
		for (VerletParticle3D a:(ArrayList<VerletParticle3D>)neighbours){
			VerletMinDistanceSpring3D s = new VerletMinDistanceSpring3D(this, a, 0.2f, 11f);
			springPhysics.addSpring(s);
		}
		}
		springIt = false;
	}
	
	
	
	public void rise(){
		if (this.z < 0){
		//this.scaleSelf(1, 1, -1f);
			this.scaleSelf(1, 1, -0.1f);
		}
		if (this.z > 30){
			this.scaleSelf(1,1,-1f);
		}
		
			
	}
	
	
	public void removeIfOverEmptyVoxels(SpringManager springManager){
		
			float voxVal = voxelGrid.getValue(this);
			if (voxVal < 1){
				Iterator i= springManager.springPhysics.particles.iterator();
				while( i.hasNext()){
				VerletParticle3D p1 = (VerletParticle3D)i.next();
				VerletSpring3D s = springManager.springPhysics.getSpring(this, p1);
				springManager.springPhysics.removeSpring(s);
				System.out.println("remove");
			}
		}
		removeParticle = false;
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
	
	public void avoidVoxels(VoxelGrid voxelGrid, int searchRadius, float minVal, float maxVal, float update, float forceScale){
		
		if (age%update==0){
			
		//get position of agent in voxel grid
		int[] v = voxelGrid.map(this);
		//get the vector to structural voxels with a val  between those defined within the search radius. Note: not the position of the voxel.
		Vec3D toVoxel = voxelGrid.findValInRange(v[0], v[1], v[2], searchRadius, minVal, maxVal);
		//System.out.println(toVoxel);
		toVoxel.scaleSelf(1, 1, 1f);

		
		float distance = toVoxel.magnitude();
		if (distance < searchRadius){
			
		//	Vec3D awayFromVoxel = toVoxel; 
		Vec3D awayFromVoxel = toVoxel.getInverted();
			//System.out.println(awayFromVoxel);
		awayFromVoxel.scaleSelf(forceScale); //force factor
			//System.out.println(awayFromVoxel);
		addForce(awayFromVoxel);
		}
		
		}
	}
	
	
	
	
	
	//lock
	
	public void locking(){
		float voxVal = voxelGrid.getValue(this);
		float checked = 0;
		float checkDist = 1;
		
		
		if (neighbours.size() > 0){
		
		float maxSize = neighbours.size();
		
		
		for (int i = 0; i < neighbours.size(); i++){
			VerletParticle3D p = (VerletParticle3D) neighbours.get(i);
			
			if (distanceTo(p)>checkDist){
			checked++;	
			}
		}
		
		if (voxVal > 0 && checked >= maxSize){
		
		this.lock();
		System.out.println("lock:");
	}
		
		}
	}
	
	
	
}
