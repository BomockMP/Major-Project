package projects.distribution;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;

import ProGAL.proteins.PDBFile.ParentRecord;
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
	public int drawRad;
	public float connectionCount = 12;
	public float springCount = 0;
	
	public ParticleAgent(float x, float y, float z, VoxelGrid _voxelGrid) {
		super(x, y, z);
		voxelGrid = _voxelGrid;
		random = (float)Math.random(); 
		drawRad = 3;
	}

	
	
	public void run(SpringManager springManager){
		
	
		float searchRadius = 36f; //60*(float)random; //15
		getNeighbours(this, searchRadius, springManager);
		
		
		for(Vec3D a:(ArrayList<Vec3D>)neighbours){
			repel(a, 0, 14, 0.07f, "exponential"); //0.0039 //0.06
			//cohere(a, 30, 80, 0.01f, "exponential");
			//align(this, a, 0, 50, 0.1f, "exponential");
		}	
		
		
		addSpringsToCloseParticles(springManager.springPhysics, (int)connectionCount, false); //3 connections
		//addSprings(springManager.springPhysics);
		//avoidVoxels(voxelGrid, 10, 0f, 1f, 100f, 0.1f);
		//removeIfOverEmptyVoxels(springManager);
		avoidVoxels(voxelGrid, 5, 1f, 255f, 1f, 0.03f);//0.006
		//avoidVoxels(voxelGrid, 5, 1f, 255f, 1f, 0.006f);
		age++;
		
		
		if (springCount > connectionCount){
		
			//lock();
		}
	}
	
	
	
	//------------------------------------------------------
	//FUNCTION FOR FINDING CLOSE PARTICLES USING OCTREE
	//------------------------------------------------------
	//function for adding springs to close agents. Trying it here.
	
	public void addSpringsToCloseParticles(VerletPhysics3D springPhysics, int connectionCount, boolean connectAll){

		

			if(connectAll == false){
				//System.out.println(springCount);
				for (ParticleAgent a:(ArrayList<ParticleAgent>)neighbours){
					//float voxVal = voxelGrid.getValue(this);
					if (springPhysics.getSpring(a, this) == null && springCount <= connectionCount/* *random && voxVal > 1*/ && a.springCount <= connectionCount ){
						VerletSpring3D s = new VerletSpring3D(this, a, (float)15, (float) (0.001f)); //35 length //0.0008
						//VerletMinDistanceSpring3D s = new VerletMinDistanceSpring3D(this, a, 20, 0.0001f);
					springPhysics.addSpring(s);
						springCount++;
					}
				}
				

			
			}else{
				
				for (VerletParticle3D a:(ArrayList<VerletParticle3D>)neighbours){
					if (springPhysics.getSpring(a, this) == null){
				VerletSpring3D s = new VerletSpring3D(this, a, 30, 0.001f);
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
	
	
	
	public void LockIfOverPaintedVoxels(SpringManager springManager){
		
			float voxVal = voxelGrid.getValue(this);
			
			
			Iterator i= springManager.springPhysics.particles.iterator();
			
			while( i.hasNext()){
			VerletParticle3D p1 = (VerletParticle3D)i.next();
			VerletSpring3D s = springManager.springPhysics.getSpring(this, p1);
			
			if (voxVal > 1 && s != null){
				
				this.lock();

			}
		}
		
	}
	
	
	//------------------------------------------------------
	//FUNCTION FOR FINDING CLOSE PARTICLES USING OCTREE
	//------------------------------------------------------
	
	
	public void getNeighbours(Vec3D p, float rad, SpringManager springManager) {
		neighbours = new ArrayList<Plane3D>();
		ArrayList addList = springManager.getWithinSphere(p, rad);
		//ArrayList addList = springManager.getWithinBox(p, rad);
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
	
	
	public void repel(Vec3D c2,float minDist, float maxDist, float maxForce, String interpolatorType){
		Vec3D to = c2.sub(this);
		float dist = to.magnitude();
		if(dist>=minDist && dist<=maxDist){
			float f = 1-((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
			float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
			addForce(to.normalizeTo(-sf));
		}
	}
	
	//-------------------------------------------------------------------------------------

		//Interpolation

		//-------------------------------------------------------------------------------------
		
		//toxi circ interpolator
		
		public float interpCirc(float a, float b, float f, boolean isFlipped) {
	        if (isFlipped) {
	                return a - (b - a) * ((float) Math.sqrt(1 - f * f) - 1);
	        } else {
	                f = 1 - f;
	                return a + (b - a) * ((float) Math.sqrt(1 - f * f));
	        }
		}
		
		//toxi cosine interpolator 
		
		public float interpCos(float a, float b, float f) {
	        return b+(a-b)*(float)(0.5+0.5*Math.cos(f*Math.PI));
		}
		
		//toxi sigmoid interpolator
		
		public float interpSigmoid(float a, float b, float f, float sharpPremult) {
	        f = (f * 2 - 1) * sharpPremult * 5;
	        f = (float) (1.0f / (1.0f + Math.exp(-f)));
	        return a + (b - a) * f;
		}
		
		public float interpExp(float a, float b, float f){
			return(a+(b-a)*(f*f));
		}
		
		public float getInterpolatedVal(String type, float a, float b, float f){
			float sf = 0;
			switch(type){
				case "exponential": sf = interpExp(a, b, f);
				case "circle": sf = interpCirc(a, b, f,false);
				case "cosine": sf = interpCos(a, b, f);
				case "sigmoid": sf = interpSigmoid(a, b, f,1);
			}
			return sf;
		}
		
		
		public boolean inBounds(int extentsMin, int extentsMax) {
			if (x<extentsMin || x>extentsMax || y<extentsMin || y>extentsMax)return false;
			return true;
		}
		
		public void reset(SpringManager springManager){
			
			Iterator i= springManager.springPhysics.particles.iterator();
			while( i.hasNext()){
			VerletParticle3D p1 = (VerletParticle3D)i.next();
			VerletSpring3D s = springManager.springPhysics.getSpring(this, p1);
			springManager.springPhysics.removeSpring(s);
			//System.out.println("remove");
			
		
		}
			float spawnptX = (float) (200);
			float spawnptY = (float) (200);
			this.set(spawnptX, spawnptY, 0 );
		}
	
}
