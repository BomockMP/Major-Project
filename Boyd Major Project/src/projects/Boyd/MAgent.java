package projects.Boyd;

import java.security.PublicKey;
import java.util.ArrayList;




import java.util.Collections;
import java.util.List;

import processing.core.PApplet;
import core.Agent;
import core.Environment;
import core.Plane3D;
import toxi.geom.Vec3D;
import toxi.math.LinearInterpolation;
import toxi.volume.VolumetricBrush;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;


public class MAgent extends Agent {
	
///TODO
/* 
 *  
 * function to find and try and occupy non toxic voxels. needs to attract to non-structural, non toxic voxels
 *  get rid of the bridging function 
 * 
 * function for growth based on toxicity - branching perhaps in areas with no pheremone
 * 
 */

////////////////////////CLASS GLOBAL VARIABLES
		
	public	VoxelGrid volume;
	public	VoxelGrid pheremone;
	public	VoxelBrush brush;
	public	PApplet parent;
	
//////////////////////////VARIABLES FOR VOXEL SEARCH FUNCTION
	
	public float radius;
	public float cellRadiusX,cellRadiusY,cellRadiusZ;
	public float stretchX,stretchY,stretchZ;
	public int sf;
	public float searchRadius;
	
/////VARIABLES FOR BRANCHING
	int branch = 0;
	int maxBranchAgentCount = 100;

//Arraylist for storing the vector positions of high pheremone particles for test purposes
	ArrayList<Vec3D> pheremoneVoxelsFound;
	
//checking unpainted voxels. 
	ArrayList<Vec3D> emptyStructureVoxelsFound;
	
//Arraylist for storing neighbours as potential bridging partners
	@SuppressWarnings("rawtypes")
	public ArrayList bridgeNeighbours = new ArrayList();
	

public float pheremoneThreshold; //above this agent will be affected by pheremone. range from 1-225.
		

		public MAgent (Vec3D _o, boolean _f, VoxelGrid _volume, VoxelGrid _pheremone, VoxelBrush _brush, float _searchRadius, float _pheremoneThreshold){
			super (_o, _f);
			
			
			volume = _volume;
			pheremone = _pheremone;
			searchRadius = _searchRadius;
			brush = _brush;
			pheremoneThreshold = _pheremoneThreshold;
			
			f=_f;
			resetTrail();
			
			//Arraylist for storing the vector positions of high pheremone particles for test purposes & rendering
			 pheremoneVoxelsFound = new ArrayList<Vec3D>();
			 
			 emptyStructureVoxelsFound = new ArrayList<Vec3D>(); //arraylist for painting attracting empty voxels
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run(Environment environment){
			//GENERAL RANDOM WALKER FORCE
			//addForce(Vec3D.randomVector().scale(0.9f));
			
			
			//FLOCKING BEHAVIOURS-----------------------------------------------------
			getNeighbours(this, 60, environment); //get nearby agents//og 0.03,0.03,0.1
			
			for(Plane3D a:(ArrayList<Plane3D>)neighbours){
				repel(a, 0, 30, 0.02f, "sigmoid");
				cohere(a, 20, 50, 0.001f, "sigmoid");
				align(this, a, 0, 50, 0.001f, "sigmoid");
			}
			//--------------------------------------------------------------------------
			
			//FUNCTION FOR ATTRACTING TO UNPAINTED STRUCTURAL VOXELS. 
			
			Vec3D unpaintedVoxel = findUnpaintedVoxels(volume, pheremone, this, 30, 1, pheremoneThreshold);
			cohere(unpaintedVoxel, 0, 20, 0.9f, "sigmoid");
			
			
			
			//VOXEL SEARCH & REPEL FUNCTION
			//TO RETURN THE POSITION OF A VOXEL WITHIN SEARCH RADIUS WITH GREATEST PH VALUE & REPEL FROM IT. last float damping radius.
			//repelFromVoxels(0f, 3f, 0.3f,"sigmoid", 100f);
			
			//REPEL FROM ANY PH VALUES NOT JUST THE HIGHEST. WIP. first float = radius within which to be affected by repel. 2nd is damping radius.
			repelFromPheremoneVoxels(pheremone, 5, 15);
			
			//FUNCTION TO INITIATE GROWTH-TYPE PAINTING OF VOXELS. PAINTS TO STRUCTURAL VOXEL GRID, READS PHEREMONE GRID.
			//FIX THIS FUNCTION. DOESNT NEED TO SEARCH A RADIUS. 
			paintNonPheremoneVoxelsWithinRad(volume, pheremone, pheremoneThreshold, 1);
			
			//RESET POSITION OF AGENT IF FALLS BELOW 0 or GOES OUT OF 300 BB
			if(z<0)reset();
			
			//GROW UPWARDS
			
	
			
			
			//BOUNDS
			if(!inBounds(0,volume.getW()*(int)volume.s.x)){
				//environment.copyTrail(this); //pass in extended environment instead
				reset(); //resets position as random vector
				
				
				
			}
			
			branch++;
			update();
			
			//TRAILS
			//if(age%1==0)addToTrail(this);	//turn this on for the trails	
			

		}
	

		public void render(PApplet parent){
			parent.stroke(255);
			
			}
		

//-------------------------------------------------------------------------------------
//Interpolation
//-------------------------------------------------------------------------------------
			
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void getNeighbours(Vec3D p, float rad, Environment environment) {
			neighbours = new ArrayList();
			ArrayList addList = environment.getWithinSphere(p, rad);
			if(addList!=null)neighbours.addAll(addList);
		}
		
		
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
			
//------------------------------------------------------------------------------------
//Interpolated Boid Behaviours
//-------------------------------------------------------------------------------------
			public void cohere(Vec3D c2, float minDist, float maxDist, float maxForce, String interpolatorType){
				Vec3D to = c2.sub(this);
				float dist = to.magnitude();
				if(dist>minDist && dist<maxDist){	
					float f = ((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
					float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
					addForce(to.normalizeTo(sf));
				}
			}
			
			public void align(Plane3D c1, Plane3D c2, float minDist, float maxDist, float maxForce, String interpolatorType){
				Vec3D to = c2.sub(this);
				float dist = to.magnitude();
				if(dist>minDist && dist<maxDist){
					float f = 1-((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
					float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
					interpolateToPlane3D(c2,maxDist,sf);
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

			
///----------------------------------------------------------------------------------------
//// Function for upwards growth and branching
///----------------------------------------------------------------------------------------		  			
			
			
			
			
		public void	growUpwards(float branchingRate, MEnvironment environment){
			
			//boolean branch = false;
			//move agent upwards
			//Vec3D up = new Vec3D(0,-0.01f,0);
			//vel.addSelf(up);
			//branch when agent reaches a certain age
			Vec3D upDest = new Vec3D (0,(float)Math.random()*244,0);
			Vec3D up = this.sub(upDest);
			
			cohere(up, 50, 300, 0.001f, "sigmoid");
			
			if(branch == branchingRate && environment.pop.size() < maxBranchAgentCount){
				
				Vec3D spawnLocation	= this.copy();
				Vec3D newDirection = spawnLocation.getRotatedY(-0.3f);
				newDirection.invert();
				
				MAgent a = new MAgent(spawnLocation, false, volume, pheremone,
						brush, 5f, pheremoneThreshold);
				a.addForce(newDirection.normalizeTo(0.3f));
				
				environment.addMAgent(a);

				System.out.println("branched");
				branch = 0;
			}
			
			
			//set trajectory for branched agent
			
			//menvironment.addMAgents(b);
			
			
		}
			
			
			
			
			
			
			
			

///----------------------------------------------------------------------------------------
//// FUNCTION FOR SEARCHING OUT AND FINDING UNPAINTED STRUCTURAL GRID VOXELS
///----------------------------------------------------------------------------------------		  
			
//find voxels within a searchradius of the agent, in the strucuralVoxelgrid, with a Val = 0

public Vec3D findUnpaintedVoxels(VoxelGrid volume, VoxelGrid pheremone, Vec3D position, int rad, float maxVal, float pheremoneThreshold){
				
emptyStructureVoxelsFound = new ArrayList<Vec3D>(); //arraylist for rendering attracting empty voxels for test purposes
	
Vec3D closestVoxelPosition = new Vec3D();
float leastDistanceToVoxel = rad;
//Get grid position of voxel under current position and store as int array

int[] v = volume.map(position);

//use modified findValueInradius function to return the vector position of a voxel within the radius,
//and within the value range.
//!check with Gwyll that this is what the function was intended to  do originally or if this should be a new function!
//checking for a value between 0-1 should find the unpainted voxels only 

Vec3D emptyVoxelPos = findValueInRadius(volume, v[0], v[1], v[2], rad, 0f, maxVal);	

//get the vector from the current position to the found voxel
Vec3D toVoxel = emptyVoxelPos.sub(position);

//get the magnitude of this vector to get the distance to the found voxel
float distanceToVoxel = toVoxel.magnitude();

//get the toxicity value of the voxel to check against to. this should stop vectors to unpaintable voxels.
float phVal = pheremone.getValue(emptyVoxelPos);



//if that distance is less than the radius and less than the least distance found so far,
if (distanceToVoxel < leastDistanceToVoxel && phVal < pheremoneThreshold){

leastDistanceToVoxel = distanceToVoxel;
closestVoxelPosition = emptyVoxelPos;
//emptyStructureVoxelsFound.add(closestVoxelPosition); //add to array for rendering and testing
//System.out.println(leastDistanceToVoxel);
}

return closestVoxelPosition;

}





			
///----------------------------------------------------------------------------------------
////FUNCTION FOR SEARCHING VOXEL GRID FOR VOXELS WITHIN A RADIUS AND BETWEEN MAX-MIN VALUE
///RETURN AS THE POSITION OF THE VOXELS FOUND
///----------------------------------------------------------------------------------------				
				
public  Vec3D findValueInRadius(VoxelGrid volume, int _x, int _y, int _z, int rad, float min, float max){
	
	int w = volume.getW();
	int h = volume.getH();
	int d = volume.getD();
	Vec3D s = volume.s;
	
	int x = (int) constrain(_x, 0, w-1);
	int y = (int) constrain(_y, 0, h-1);
	int z = (int) constrain(_z, 0, d-1);
	

	Vec3D free = new Vec3D();
	//shuffle from position in grid - radius to pos in grid + radius
	
	ArrayList<Integer>ii = getShuffled(x-rad/(int)s.x,x+rad/(int)s.x);
	ArrayList<Integer>jj = getShuffled(y-rad/(int)s.y,y+rad/(int)s.y);
	ArrayList<Integer>kk = getShuffled(z-rad/(int)s.z,z+rad/(int)s.z);
	for (int i:ii) {
		for (int j:jj) {
			for (int k:kk) {

				//get value from grid position between -rad pos rad+
				float v = volume.getValue(i, j, k);
				
	
					if(v>=min&&v<=max){
						
						//scale resulting vector back to grid scale
						return new Vec3D(i*s.x,j*s.y,k*s.z);
					}
			}
		}
	}
	return free;
}			
			
			
			
///----------------------------------------------------------------------------------------
//// GETS PHEREMONE VALUE WITHIN RADIUS OF CURRENT POSITION - return POSITION OF HIGHEST PHEREMONE VALUE 
///----------------------------------------------------------------------------------------		  
			//update so the empty vector isnt passed through as an attractor

			public Vec3D getPheremoneWithinRadius (float _radius, Vec3D position, VoxelGrid _pheremoneVoxelGrid, float pheremoneThreshold) {
				
				
				//variables required from voxel grid ie scale
				Vec3D gridScale = _pheremoneVoxelGrid.s;
				
				//empty Variables for storing the maximum pheremone float value and its position
				//make sure this empty vector isnt passed through 
				Vec3D maxPheremonePosition = new Vec3D();
				float maxPheremoneValue = 0;
				
				
				
				
				//Set Search radius size. updates radiusfloat.
				setSearchRadiusSize(_radius);
				
				//Get grid position of voxels store as int array
				 int[] v = getGridPos(position, _pheremoneVoxelGrid);
				

				 // v is an array of voxel indexs. go through this array to find the pheremone values of each within the array
				 
				
				 
				 int minX = max((int) (v[0]  - cellRadiusX), 0);
					int minY = max((int) (v[1]  - cellRadiusY), 0);
					int minZ = max((int) (v[2]  - cellRadiusZ), 0);
					int maxX = min((int) (v[0] + cellRadiusX), _pheremoneVoxelGrid.getW());
					int maxY = min((int) (v[1] + cellRadiusY), _pheremoneVoxelGrid.getH());
					int maxZ = min((int) (v[2] + cellRadiusZ), _pheremoneVoxelGrid.getD());
					for (int z = minZ; z < maxZ; z++) {
						for (int y = minY; y < maxY; y++) {
							for (int x = minX; x < maxX; x++) {
								float dx = x - v[0];
								float dy = (y - v[1]) * stretchY;
								float dz = (z - v[2]) * stretchZ;
								float d = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
								if (d <= cellRadiusX) {
									//Cell idx = _pheremoneVoxelGrid.get(x, y, z);
									float phValueAtPoint = _pheremoneVoxelGrid.getValue(x, y, z);
							
									//vec3D phPos = get the vector of the voxel at index x y z
									
								if(phValueAtPoint>maxPheremoneValue && phValueAtPoint>pheremoneThreshold){
									//found a point that has a higher ph value than anything before. update the float & vector.
									maxPheremoneValue = phValueAtPoint;
									maxPheremonePosition = new Vec3D(x*gridScale.x, y*gridScale.y, z*gridScale.z); 
									//System.out.println(maxPheremonePosition);
								}
								}
							}
						}
					}
				 //add vec3d to the array for rendering/testing purposes
					if (maxPheremonePosition.magnitude() > 0){
					pheremoneVoxelsFound.add(maxPheremonePosition);
					
					
					}	 
					
					return maxPheremonePosition;

			}
			
		///----------------------------------------------------------------------------------------
		//// GETS PHEREMONE VALUE WITHIN RADIUS OF CURRENT POSITION V.2.0 - return any voxel with ph value within range
		///----------------------------------------------------------------------------------------		  	
			
			public void repelFromPheremoneVoxels(VoxelGrid pheremoneGrid, float maxRepelRadius, float dampingRadius){
				
				
				int[] v = pheremoneGrid.map(this);
				
				Vec3D toPheremone = pheremoneGrid.findValInRange(v[0], v[1], v[2], 5, pheremoneThreshold, 255);
				
				float distance = toPheremone.magnitude();
				
				if (distance < maxRepelRadius){
				Vec3D awayFromPheremone = toPheremone.getInverted();
				awayFromPheremone.normalizeTo(1).scale(0.5f);
				addForce(awayFromPheremone);
				}
				
				if (distance > maxRepelRadius && distance < dampingRadius){
					damping(0.9f);
				}
				
				
			}
			
			
		///----------------------------------------------------------------------------------------
		//// FUNCTION FOR REPELLING FROM HIGH PHEREMONE VOXELS 
		///----------------------------------------------------------------------------------------		  	
			public void repelFromVoxels(float minDist, float maxDist, float maxForce, String interpolatorType, float maxDampingDistance){
				
				Vec3D VoxelToRepelFrom = getPheremoneWithinRadius(searchRadius, this, pheremone, pheremoneThreshold);
				
				Vec3D to = VoxelToRepelFrom.sub(this);
				float dist = to.magnitude();
				if(dist>=minDist && dist<=maxDist){
				float f = 1-((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
				float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
				addForce(to.normalizeTo(-sf));
			}

				if (dist >= maxDist && dist < maxDampingDistance){
					damping(0.9f);
				}
				
				
				
				
			}
			
			///----------------------------------------------------------------------------------------
			//// FUNCTION FOR SLOWING DOWN AGENTS OUTSIDE OF REPEL RADIUS
			///----------------------------------------------------------------------------------------		
			
public void  damping(float damping) {
	
	this.vel.scaleSelf(damping);
	
}

//				Vec3D slowDown = this.copy();
//				slowDown.scaleSelf(1/dist);
//				slowDown.invert();
//				addForce(slowDown);

			
			
			
		///----------------------------------------------------------------------------------------
		//// FUNCTION TO MANAGE ATTRACTING TO OTHER AGENTS AND THEN RESETTING IF IN SAME POSITION - NOT IN USE
		///----------------------------------------------------------------------------------------				
	
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void bridgeAgents(Vec3D p, float rad, float Attractionforce, Environment environment){
				

				//add potential neighbours to arraylist. I wanted this to be seperate from
				//general flocking behaviour. 
				
				bridgeNeighbours = new ArrayList();
				ArrayList addList = environment.getWithinSphere(p, rad);
					if(addList!=null)bridgeNeighbours.addAll(addList);
				

				//attract to other Agents
				for(Plane3D a:(ArrayList<Plane3D>)bridgeNeighbours){
					cohere(a, 0, rad, Attractionforce, "sigmoid");
				}
				
				//check pos of other agents
				for(Plane3D a:(ArrayList<Plane3D>)bridgeNeighbours){
					
				//shuffle list to avoid getting same one?
				//Collections.shuffle(bridgeNeighbours);
					
				//make sure it isnt including itself in its check for position of other agents 
					if (a != this){
					Vec3D other = a.copy();
					
					
					
				//Check whether other agent occupies same position as this agent within a tollerance of 1  
				//using VEC3D equals function
					
					if (other.equalsWithTolerance(this, 1)){
						reset();
					}
				}	
				}
			}
			
			

		///----------------------------------------------------------------------------------------
		//// RESET FUNCTION FOR OUT OF BOUNDS AGENTS
		///----------------------------------------------------------------------------------------	
			
			public void reset(){
				//set(new Vec3D((float)Math.random()*(volume.getH()*volume.s.x), (float)Math.random()*(volume.getW()*volume.s.y),(float)Math.random()*(volume.getD()*volume.s.z)));
				
				set(new Vec3D((float)Math.random()*(volume.getW()*volume.s.x), (float)(volume.getH()*volume.s.y)-20,(float)Math.random()*(volume.getD()*volume.s.z)));
			}
			
//MAgent a = new MAgent(new Vec3D((float)Math.random()*244, (structureVoxels.getH()*structureVoxels.s.y)-20,(float)Math.random()*244), false, structureVoxels, pheromones,
		
			
		///----------------------------------------------------------------------------------------
		//// FUNCTION FOR PAINTING VOXELS WHICH ARE NOT OCCUPIED BY PHEREMONES
		///----------------------------------------------------------------------------------------		
			
			public void paintNonPheremoneVoxelsWithinRad(VoxelGrid volume, VoxelGrid pheremone, float pheremoneThreshold, int rad){
				//rename this function it does something ocmpletely different
				//following checks for voxels with a pheremone within the radius between 0 and the threshhold
				//if it finds a voxel it returns a vector to that voxel
				//if that vector is not zero, ie it has found a voxel with a value lower than the threshhold,
				//it gets the point its at and paints it.
				//float phVal = pheremone.getValue(this);
				//Vec3D p = pheremone.findValueInRadius(this, 1, 0, pheremoneThreshold); //checks to see if neighbouring cells have a value between 0 and phthreshold
				//if(!p.isZeroVector()){
				
				//Instead of above: get value at position from ph radius. if < threshhold, paint volume grid. otherwise dont paint.
//				volume.get(this).set(255); //gets the voxel under the dla and turns it to 255
				//float phVal = pheremone.getValue(this);
				
				//rad = radius for brush to test voxels and paint in
				int[] v = volume.map(this);
				

				int x = v[0];
				int y = v[1];
				int z = v[2];
//				
				ArrayList<Integer>ii = getShuffled(x-rad/(int)volume.s.x,x+rad/(int)volume.s.x);
				ArrayList<Integer>jj = getShuffled(y-rad/(int)volume.s.y,y+rad/(int)volume.s.y);
				ArrayList<Integer>kk = getShuffled(z-rad/(int)volume.s.z,z+rad/(int)volume.s.z);
				for (int i:ii) {
					for (int j:jj) {
						for (int k:kk) {
//
//							//get value from grid position between -rad pos rad+ in the pheremone grid
							float phVal = pheremone.getValue(i, j, k);

//				
								if(phVal < pheremoneThreshold){
//								//if the pheremone value is below the threshhold then paint the structural grid at i, j, k
									volume.get(i,j,k).set(255);
								}	
						}
					}
				}

//				if (phVal <= 0){
//					volume.get(this).set(255); //gets the voxel under the dla and turns it to 255
//				}
			}
			
		///----------------------------------------------------------------------------------------
		//// Check if agent is in bounds
		///----------------------------------------------------------------------------------------		  			
			
			public boolean inBounds(int boundsMin, int boundsMax) {
				boundsMax = boundsMax + 50;
				if (  x< boundsMin ||  x>boundsMax  ||  y < boundsMin || y>boundsMax ||  z< boundsMin  ||  z>boundsMax )return false;
				return true;
			}
			
			
		///----------------------------------------------------------------------------------------
		//// FUNCTIONS FOR SEARCH RADIUS
		///----------------------------------------------------------------------------------------		  		
			
			
			public int max(int a, int b){
				int r = (a>b)?a:b;
				return r;
			}
			
			public int min(int a, int b){
				int r = (a<b)?a:b;
				return r;
			}
			
			public void setSearchRadiusSize(float _radius) {
				radius = _radius;
				cellRadiusX = (int) (radius / volume.s.x);
				cellRadiusY = (int) (radius / volume.s.y);
				cellRadiusZ = (int) (radius / volume.s.z);
				stretchY = (float) cellRadiusX / cellRadiusY;
				stretchZ = (float) cellRadiusX / cellRadiusZ;
			}
			
			
			
			//-------------------------------------------------------------------------------------
			//Voxel and volume brush functions  
			//-------------------------------------------------------------------------------------
			//GETGRIDPOS only will work with a voxelgrid from 0,0,0. use map otherwise.
						
						 public  int[] getGridPos(Vec3D l, VoxelGrid v){
							  int[] val = new int[3];
						    val[0] = (int) constrain(l.x/v.s.x,0,v.d-1);
						    val[1] = (int) constrain(l.y/v.s.y,0,v.h-1);
						    val[2] = (int) constrain(l.z/v.s.z,0,v.w-1); 
						    return val;
						  }
						  
						  
						  public void setScale(int s){
							  sf = s;
							  
						  }
						

//-------------------------------------------------------------------------------------
//VoxelGrid Functions required inside agent
//-------------------------------------------------------------------------------------						  
						  
						  
						  
							ArrayList<Integer> getShuffled(int min, int max){
								List<Integer> l = new ArrayList<Integer>();
								for(int i = min;i<=max;i++){
									l.add(i);
								}
								Collections.shuffle(l);
								return (ArrayList<Integer>) l;
							}					  

		}
		

//END OF CLASS
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	
//-------------------------------------------------------------------------------------	


