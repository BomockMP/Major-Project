package voxelTools;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jogamp.common.nio.Buffers;

import core.Plane3D;
import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

/*------------------------------------

 Class containing a 3d array of bytes and
 functions for searching, reading and writing 
 to this array

 ------------------------------------*/

public class VoxelGrid {
	public Cell vals[];      // The array of bytes containing the pixels.
	public int w, h, d;
	public Vec3D s;
	public AABB extents;
	FloatBuffer ptBuffer;
	FloatBuffer colBuffer;

	/**
	 * Create a voxel grid from w,d,h dimensions and extents from the origin
	 * 
	 * @param _w Width of grid
	 * @param _h Height of grid
	 * @param _d Depth of grid
	 * @param _s Vector defining maximum extents of the grid
	 */
	public VoxelGrid(int _w, int _h, int _d, Vec3D _s) {
		this(_w,_h,_d, AABB.fromMinMax(new Vec3D(),new Vec3D(_w*_s.x,_h*_s.y,_d*_s.z)));
	}
	
	/**
	 * Create a voxel grid from w,d,h dimensions and AABB extents
	 * 
	 * @param _w Width of grid
	 * @param _h Height of grid
	 * @param _d Depth of grid
	 * @param _extents AABB bounds for the voxels
	 */
	public VoxelGrid (int _w, int _h, int _d, AABB _extents){
		w = _w;
		h = _h;
		d = _d;

		initGrid();
		extents = _extents;
		s = new Vec3D((extents.getMax().x-extents.getMin().x)/w, (extents.getMax().y-extents.getMin().y)/h,(extents.getMax().z-extents.getMin().z)/d);
		ptBuffer = Buffers.newDirectFloatBuffer(w*h*d*3);
		colBuffer = Buffers.newDirectFloatBuffer(w*h*d*4);
		ptBuffer.rewind();
		colBuffer.rewind();
	}
	
	/**
	 * Clone a VoxelGrid
	 * 
	 * @param v VoxelGrid to clone
	 */
	public VoxelGrid(VoxelGrid v){
		this(v.getW(), v.getH(), v.getD(), v.getExtents());
		booleanGrid(v, "union");
	}

	//-------------------------------------------------------------------------------------

	//Functions for initialisation

	//-------------------------------------------------------------------------------------
	/**
	 * Initialise the grid with empty cells
	 * 
	 */
	public void initGrid() {
		vals = new Cell[w*h*d];
		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				for (int k = 0; k<d; k++) {
					int index = i + w * (j + h * k);
					boolean edge = false;
					if(i==0 || i==w-1 || j==0 || j==h-1 || k==0 || k== d-1)edge = true;
					vals[index] = new Cell(0, edge,i,j,k);
				}
			}
		}
	}
	/**
	 * Remap image to w and h of the VoxelGrid. Remaps greyscale value of pixel to h of grid and sets cell values beneath to mapped greyscale.
	 * 
	 * @param loadedImage The image to load into the voxel grid
	 */
	public void createTerrain(PImage loadedImage) {
		loadedImage.resize(w, h);

		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				int v = (int) (((loadedImage.pixels[(j*w)+i]>> 16) & 0xFF)*d/255);
				for (int k=0; k<v; k++) {
					int index = i + w * (j + h * k);
					set(index,(1-((float)k/v))*255);
				}
			}
		}
	}
	/**
	 * Remap image to w and d of the VoxelGrid and set cell values at z layer to greyscale of image
	 * 
	 * @param loadedImage The image to load into the voxel grid
	 * @param z The z index of layer to modify
	 */
	public void createLayerFromImage(PImage loadedImage, int z) {
		//loadedImage.resize(w, h);
		//scale up using mapping - doesnt recompress jpg
		float sx = (float)loadedImage.width/w;
		float sy = (float)loadedImage.height/h;
		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				int v = (int) (((loadedImage.pixels[(int)(j*sy)*loadedImage.width+(int)(i*sx)]>> 16) & 0xFF));
				int index = i + w * (j + h * z);
				set(index, v);
			}
		}
	}

	//-------------------------------------------------------------------------------------

	//Functions for writing to the voxel array

	//-------------------------------------------------------------------------------------
	/**
	 * Perform boolean operations between VoxelGrids
	 * 
	 * @param vol The VoxelGrid to use for boolean operations
	 * @param type Type of boolean (can be "subtract" or "union")
	 */
	public void booleanGrid (VoxelGrid vol, String type){
		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				for (int k = 0; k<d; k++) {
					int index = i + w * (j + h * k);
					Cell c = vol.get(i,j,k);
					float cv = c.get();
					if(cv>=0){
						float v = vals[index].get();
						if(type == "subtract"){
							vals[index].set(constrain(v-cv,0,255));	
						}else if(type == "union"){
							vals[index].set(constrain(v+cv,0,255));	
						}
					}
				}
			}
		}
	}
	/**
	 * Set cell values within a cube
	 * @param _x x position of cube corner
	 * @param _y y position of cube corner
	 * @param _z z position of cube corner
	 * @param edgeLength cube edge length
	 * @param fillValue value to set contained cells to
	 */
	public void createCube(int _x, int _y, int _z, int edgeLength, int fillValue) {
		int xmin = (int) constrain(_x, 0, w);
		int ymin = (int) constrain(_y, 0, h);
		int zmin = (int) constrain(_z, 0, d);
		int xmax = (int) constrain(_x+edgeLength, 0, w);
		int ymax = (int) constrain(_y+edgeLength, 0, h);
		int zmax = (int) constrain(_z+edgeLength, 0, d);

		for (int i = xmin; i<xmax; i++) {
			for (int j = ymin; j<ymax; j++) {
				for (int k = zmin; k<zmax; k++) {
					int index = i + w * (j + h * k);
					vals[index].set(fillValue);
				}
			}
		}
	}
	/**
	 * Create a sphere at specified x,y,z coordinates within the VoxelGrid
	 * @param _x
	 * @param _y
	 * @param _z
	 * @param rad
	 * @param fillValue
	 */
	public void createSphere(Vec3D pt, int rad, int val) {
		for (int i = -rad; i<=rad; i++) {
			for (int j = -rad; j<=rad; j++) {
				for (int k = -rad; k<=rad; k++) {
					float d = (float) Math.sqrt(i * i + j * j + k * k);
						if (d <= rad){
							setValue(pt.x+i, pt.y+j, pt.z+k, val);
						}				
					}
			}
		}
	}
	
	/**
	 * Fills cell values with perlin noise
	 * @param ns Noise scale
	 * @param parent PApplet to run noise function
	 */
	public void createNoise(float ns, PApplet parent) {
		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				for (int k =0; k <d; k++){
					int index = i + w * (j + h * k);
					int val = (int) ((parent.noise(i*ns, j*ns, k*ns))*255);
					//int val = (parent.noise(i*ns, j*ns, k*ns/10)>0.5f)?255:0;
					vals[index].set(val);
				}
			}
		}
	}
	/**
	 * constrain a value to a domain
	 * @param v value
	 * @param min min bounds of domain
	 * @param max max bounds of domain
	 * @return constrained value
	 */
	private float constrain(float v, float min, float max){
		if(v<min){
			return min;
		}else if(v>max){
			return max;
		}
		else return v;
	}
	/**
	 * Remaps a vector from world space to voxel space
	 * @param p vector to map
	 * @return integer array containing voxel coordinates
	 */
	public int[] map(Vec3D p){
		return map(p.x, p.y,p.z);
	}
	/**
	 * Remaps a point from world to voxel space
	 * @param x x coordinate of point
	 * @param y y coordinate of point
	 * @param z z coordinate of point
	 * @return integer array containing voxel coordinates
	 */
	private int[] map(float x, float y, float z){
		int[] mapped = new int[3];
		mapped[0] = (int)((x-extents.getMin().x)/(s.x));
		mapped[1] = (int)((y-extents.getMin().y)/(s.y));
		mapped[2] = (int)((z-extents.getMin().z)/(s.z));

			//mapped[0] = (int)constrain(mx,0,w-1);
			//mapped[1] = (int)constrain(my,0,h-1);
			//mapped[2] = (int)constrain(mz,0,d-1);

		return mapped;
	}
	
	/**
	 * Set the value of the Cell at a specified point in world space
	 * @param p location of cell
	 * @param val value to set
	 */
	public void setValue(Vec3D p, float val){
		int[] pt = map(p.x, p.y, p.z);
		setGridValue(pt[0],pt[1],pt[2], val);
	}
	/**
	 * See setValue(Vec3D)
	 * @param x
	 * @param y
	 * @param z
	 * @param val
	 */
	public void setValue(float x, float y, float z, float val){
		int[] pt = map(x,y,z);
		setGridValue(pt[0], pt[1], pt[2],val);
	}
	/**
	 * Constrains cell modification to within grid. See setValue(Vec3D)
	 */
	public void setGridValue (int x, int y, int z, float val) {
		if(x>=0 && x<w && y>=0 && y<h && z>=0 && z<d){
			int index = (x) + w * ((y) + h * (z));	
			set(index,val);
		}
	}
	/**
	 * Sets the value of a cell at a specified index
	 * @param index index of cell in the voxel array
	 * @param val specified value
	 */

	

	private void set(int index, float val){
		vals[index].set(val);
		colBuffer.put(index*4, val/255);
		colBuffer.put((index*4)+1, val/255);
		colBuffer.put((index*4)+2, val/255);
		colBuffer.put((index*4)+3, val/255);
	}

	
	/**
	 * Set the extents of the VoxelGrid
	 * @param _extents AABB defining extents
	 */
	public void setExtents(AABB _extents){
		extents = _extents;
		s = new Vec3D(extents.getExtent().x/w, extents.getExtent().y/h,extents.getExtent().z/d);
	}


	//-------------------------------------------------------------------------------------

	//Functions for modifying the voxel array

	//-------------------------------------------------------------------------------------
	/**
	 * Multiply values of all cells by a factor
	 * @param fadeSpeed factor to multiply by
	 */
	public void fade(float fadeSpeed){
		for(Cell c:vals){
			c.set(c.get()*fadeSpeed);
		}
	}
	/**
	 * Average cell values using neighbours. Corners are weighted.
	 */
	public void blurall() {
		for (int z=0; z<d; z+=1) {
			for (int y=0; y<h; y+=1) {
				for (int x=0; x<w; x+=1) {
					blur3d(x, y, z);
				}
			}
		}
	}
	/**
	 * Average cell values within a specified layer. Corners are weighted.
	 * @param layer layer to blur
	 */
	public void blur2dXY(int layer) {
			for (int y=0; y<h; y+=1) {
				for (int x=0; x<w; x+=1) {
					blur2d(x, y, layer);
				}
			}
	}
	/**
	 * Average the value of a specified cell taking into account only cells on the same z layer
	 * @param x x coordinate of the cell
	 * @param y y coordinate of the cell
	 * @param z z coordinate of the cell
	 */
	public void blur2d (int x, int y, int z) {
		if ((x > 1) && (x < w-2) &&(y > 1) && (y < h-2) &&(z >= 0) && (z < d)) {
			float sum = 0;

			for (int j=-1; j<=1; j++) {
				for (int i=-1; i<=1; i++) {


					int index = (i+x) + w * ((j+y) + h * (z));
					float val=vals[index].get();
					int scalar = 1;
					if (j*i==0) {
						scalar =2;
					}
					if (j==0 && i==0) {
						scalar = 4;
					}
					sum+=(val*scalar);

				}
			}
			int weightedAverage = (int) (sum/16);
			vals[x+w*(y+h*z)].set(weightedAverage);
		}
	}
	
	/**
	 * Average the value of a cell using weighted neighbours
	 * @param x x coordinate of the cell
	 * @param y y coordinate of the cell
	 * @param z z coordinate of the cell
	 */
	public void blur3d (int x, int y, int z) {
		if ((x > 1) && (x < w-2) &&
				(y > 1) && (y < h-2) &&
				(z > 1) && (z < d-2)) {
			float sum = 0;
			for (int k=-1; k<=1; k++) {
				for (int j=-1; j<=1; j++) {
					for (int i=-1; i<=1; i++) {
						int index = (i+x) + w * ((j+y) + h * (k+z));
						float val=vals[index].get();
						int scalar = 1;
						if (k==0) {
							if (j*i==0) {
								scalar =2;
							}
							if (j==0 && i==0) {
								scalar = 4;
							}
						} else if (j==0 && i==0) {
							scalar=2;
						}

						sum+=(val*scalar);
					}
				}
			}
			int weightedAverage = (int) (sum/36);
			vals[x+w*(y+h*z)].set(weightedAverage);
		}
	}

	//-------------------------------------------------------------------------------------

	//Functions for search
	//TODO move to pathfinder class

	//-------------------------------------------------------------------------------------

	public  Vec3D findVal(Vec3D p, int rad, float angle, Vec3D dir, float target){
		int[] pt = map(p);
		return findVal(pt[0],pt[1],pt[2],rad,angle, dir, target);
	}
	
	public  Vec3D findVal(int x, int y, int z, int rad, float angle, Vec3D dir, float target){
		Vec3D toBest = new Vec3D();
		float best = 1000;
		for(Cell c:getNeighbours(x, y, z, rad)){
			Vec3D toVoxel = new Vec3D(c.x-x,c.y-y,c.z-z);
			float a = toVoxel.angleBetween(dir,true);
			if(a<angle){
				float val=c.get();
				if(Math.abs(target-val)<best){
					best = Math.abs(target-val);
					toBest = toVoxel.copy();
				}
			}
		}
		return toBest;
	}
	
	//BOYD TESTING
	
	
	public  Vec3D findClosest(Vec3D p, int rad, float target){
		int[] pt = map(p);
		return findClosest(pt[0],pt[1],pt[2],rad, target);
	}
	

	public  Vec3D findClosest(int x, int y, int z, int rad, float target){
		//Vec3D toBest = new Vec3D();
		float smallestDistance = 100;
		Vec3D closestVoxel = new Vec3D();
		
		
		for(Cell c:getNeighbours(x, y, z, rad)){
			
			float val=c.get();
			if(val>target){
			
			Vec3D toVoxel = new Vec3D(c.x-x,c.y-y,c.z-z);
			if (toVoxel.magnitude() < smallestDistance){smallestDistance = toVoxel.magnitude(); closestVoxel = toVoxel;
			};		
			}
			}
		return closestVoxel;
		}
		
	////
	public  Vec3D findValPosition(Vec3D p, int rad, float angle, Vec3D dir, float target){
		//int[] pt = map(p);
		return findValPosition((int)p.x,(int)p.y,(int)p.z,rad,angle, dir, target);
	}
	
	public  Vec3D findValPosition(int x, int y, int z, int rad, float angle, Vec3D dir, float target){
		Vec3D toBest = new Vec3D();
		float best = 1000;
		
		
		
		
		for(Cell c:getNeighbours(x, y, z, rad)){
			
			
			
			Vec3D toVoxel = new Vec3D(c.x,c.y,c.z);
			//Vec3D toVoxel = new Vec3D(c.x-x,c.y-y,c.z-z);
			//float a = toVoxel.angleBetween(dir,true);
			//if(a<angle){
				float val=c.get();
				//if (val > 1){
				if(Math.abs(target-val)<best && c.x > 0 && c.y > 0 && c.z > 0 ){
					best = Math.abs(target-val);
					//toBest = new Vec3D(c.x,c.y, c.z);
					toBest = toVoxel;
				}
				//}
			}
		//}
		return toBest;
	}
	
	
	
	
	
	
	
	
	
	
	
	

//ORIGINAL

	public Vec3D findValInRange(Vec3D p, int rad, float min, float max){
		int[] pt = map(p);
		return findValInRange(pt[0],pt[1],pt[2],rad, min,max);
	}

	public  Vec3D findValInRange(int x, int y, int z, int rad, float min, float max){
		for(Cell c:getNeighbours(x, y, z, rad)){
			float v = c.get();
			if(v>=min&&v<=max){
				return new Vec3D(c.x-x,c.y-y,c.z-z);
			}
		}
		return new Vec3D();
	}
	
	public  Vec3D getNormal(int x, int y, int z, float v, int rad, float jitter) {
		Vec3D from = new Vec3D();
		for(Cell c:getNeighbours(x, y, z, rad)){
			float val=c.get();
			float diff = v-val;
			if(diff!=0)from.addSelf(new Vec3D(c.x-x,c.y-y,c.z-z).add(Vec3D.randomVector().scale(jitter)).scale(diff));
		}
		return from;
	}
	
	public Vec3D getNormal(Vec3D p, float v, int rad,float jitter){
		int[] pt = map(p);
		return getNormal(pt[0],pt[1],pt[2],v,rad,jitter);
	}
	public ArrayList<Cell>getNeighbours(int x, int y, int z, int rad){
		ArrayList<Cell>neighbours = new ArrayList<Cell>();
		ArrayList<Integer>ii = getShuffled(-rad,rad);
		ArrayList<Integer>jj = getShuffled(-rad,rad);
		ArrayList<Integer>kk = getShuffled(-rad,rad);
		for (int i:ii) {
			for (int j:jj) {
				for (int k:kk) {
					int ix = i+x;
					int jy = j+y;
					int kz = k+z;
					if(ix>=0 && jy>=0 && kz>=0 && ix<w && jy<h && kz<d){
						neighbours.add(get(ix, jy, kz));
					}
				}
			}
		}
		return neighbours;
	}
	
	public ArrayList<Cell>getNeighboursXY(int x, int y, int z, int rad){
		ArrayList<Cell>neighbours = new ArrayList<Cell>();
		ArrayList<Integer>ii = getShuffled(-rad,rad);
		ArrayList<Integer>jj = getShuffled(-rad,rad);
		for (int i:ii) {
			for (int j:jj) {
					int ix = i+x;
					int jy = j+y;
					int kz = z;
					if(ix>=0 && jy>=0 && kz>=0 && ix<w && jy<h && kz<d){
						neighbours.add(get(ix, jy, kz));
						
					}
			}
		}
		return neighbours;
	}
	
	public ArrayList<Cell>getNeighboursPlane3D(Plane3D p, int rad, int depth){
		ArrayList<Cell>neighbours = new ArrayList<Cell>();
		Vec3D xx = new Vec3D(p.xx.x*s.x, p.xx.y*s.y,p.xx.z*s.z);//scale to grid
		Vec3D yy = new Vec3D(p.yy.x*s.x, p.yy.y*s.y,p.yy.z*s.z);
		Vec3D zz = new Vec3D(p.zz.x*s.x, p.zz.y*s.y,p.zz.z*s.z);
		for (int i=-rad;i<=rad;i++) {
			for (int j=-rad;j<=rad;j++) {
				for (int k=-depth;k<=depth;k++) {
				//i+j scale factors for x + y axes of plane
				Vec3D worldPt = p.add(xx.scale(i).add(yy.scale(j)).add(zz.scale(k)));
				int[] gridPt = map(worldPt);
					if(gridPt[0]>=0 && gridPt[1]>=0 && gridPt[2]>=0 && gridPt[0]<w && gridPt[1]<h && gridPt[2]<d){
						neighbours.add(get(gridPt[0], gridPt[1], gridPt[2]));
					}
				}
			}
		}
		Collections.shuffle(neighbours);
		return neighbours;
	}
	
	//-------------------------------------------------------------------------------------

	// Utilities

	//-------------------------------------------------------------------------------------
	
	/**
	 * Creates a list of random numbers within a specified domain
	 * @param min
	 * @param max
	 * @return shuffled list
	 */
	private ArrayList<Integer> getShuffled(int min, int max){
		List<Integer> l = new ArrayList<Integer>();
		for(int i = min;i<=max;i++){
			l.add(i);
		}
		Collections.shuffle(l);
		return (ArrayList<Integer>) l;
	}
	
	/**
	 * Gets the cell at the specified position in the voxel grid
	 * @param x x coordinate of cell in voxel space
	 * @param y y coordinate of cell in voxel space
	 * @param z z coordinate of cell in voxel space
	 * @return Cell at the point
	 * @throws IndexOutOfBoundsException
	 */
	public Cell get(int x, int y, int z) throws IndexOutOfBoundsException{
		try {
			if(x>w || x<0 || y<0 || y>h || z<0 || z>d){
				throw new IndexOutOfBoundsException();
			}
			int index = x + w * (y + h * z);
			Cell val = vals[index];
			return val;
		} catch (Exception e) {
			return new Cell(-1,true, x,y,z);
		}


	}
	
	/**
	 * Gets the value of the cell at the specified position in the voxel grid
	 * @param x
	 * @param y
	 * @param z
	 * @return value of cell
	 */
	public float getValue(int x, int y, int z){
		return get(x,y,z).val;
	}
	/**
	 * Gets the value of the cell at the specified position in world space
	 * @param p position of cell
	 * @return value of cell
	 */
	public float getValue(Vec3D p){
		int[] pt = map(p);
		return getValue(pt[0],pt[1],pt[2]);
	}
	
	/**
	 * Gets the cell at the specified position in world space
	 * @param p position of cell
	 * @return cell at position
	 */
	public  Cell get(Vec3D p){
		int[] pt = map(p);
		return get(pt[0],pt[1],pt[2]);
	}
	
	/**
	 * Get the number of cells along the width of the VoxelGrid
	 * @return width
	 */
	public  int getW(){
		return w;
	}
	/**
	 * Get the number of cells along the height of the VoxelGrid
	 * @return height
	 */
	public int getH(){
		return h;
	}
	/**
	 * Get the number of cells along the depth of the VoxelGrid
	 * @return depth
	 */
	public int getD(){
		return d;
	}
	/**
	 * Get the extents of the VoxelGrid in world space
	 * @return extents
	 */
	public AABB getExtents(){
		return extents;
	}
	/**
	 * Get voxels above a threshold as a floatbuffer
	 * @param threshold
	 * @return pts
	 */	
	public void resetPtBuffer(){
		ptBuffer = Buffers.newDirectFloatBuffer(w*h*d*3);
		for(Cell c: vals){
			ptBuffer.put((c.x*s.x)+extents.getMin().x);
			ptBuffer.put((c.y*s.y)+extents.getMin().y);
			ptBuffer.put((c.z*s.z)+extents.getMin().z);
		}
		ptBuffer.rewind();
	}
	
	public void resetColBuffer(float threshold){
		colBuffer = Buffers.newDirectFloatBuffer(w*h*d*4);
		for(Cell c: vals){
			float gs = c.get()/255;
			colBuffer.put(gs); //r=g=b for greyscale
			colBuffer.put(gs);
			colBuffer.put(gs);
			if(c.get()<threshold){
				colBuffer.put(0);
			}else colBuffer.put(1); //alpha
		}
		colBuffer.rewind();
	}

	public FloatBuffer getPtBuffer(){
		return ptBuffer;
	}
	
	public FloatBuffer getColBuffer(){
		return colBuffer;
	}

	//-------------------------------------------------------------------------------------

	//Functions for drawing and saving

	//-------------------------------------------------------------------------------------
	
	public void render(int res, int threshold, float sf, PApplet parent) {
		parent.strokeWeight(10);
		parent.stroke(255,0,0);
		parent.point(extents.getMin().x,extents.getMin().y,extents.getMin().z);
		parent.point(extents.getMax().x,extents.getMax().y,extents.getMax().z);
		
		int index = 0;
		parent.strokeWeight(1);
		for (int z=0; z<d; z+=res) {
			for (int y=0; y<h; y+=res) {
				for (int x=0; x<w; x+=res) {
					index = x + w * (y + h * z);
					float val = vals[index].get()*sf;
					if (val>threshold) {
						float valInverted = parent.map(val, 0, 255, 255, 0); //invert colours
						parent.stroke(valInverted);
						parent.point((x*s.x)+extents.getMin().x,(y*s.y)+extents.getMin().y,(z*s.z)+extents.getMin().z);
						//Vec3D normal = getNormal(x, y, z, 2);
						//parent.line(x*s,y*s,z*s,x*s+normal.x*5,y*s+normal.y*5,z*s+normal.z*5);
					}
				}
			}
		}
	}
	
	public void save(String fn) {
		  int c=0;
		  int tot = vals.length;
		  try {
		    BufferedOutputStream ds = new BufferedOutputStream(new FileOutputStream(fn));
		    // ds.writeInt(volumeData.length);
		    for (Cell e:vals) {
		    	if(!e.edge){
		    		ds.write((int) e.get());
		    	}else{
		    		ds.write((int) 0);
		    	}
		    }
		    ds.flush();
		    ds.close();
		  } 
		  catch (IOException e) {
		    e.printStackTrace();
		  }
		}

}
