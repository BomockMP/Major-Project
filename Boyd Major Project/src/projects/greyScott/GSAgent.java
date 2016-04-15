package projects.greyScott;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PImage;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import voxelTools.Cell;
import core.Agent;
import core.Environment;
import core.Plane3D;


public class GSAgent extends Agent{

	//pimage
	public PImage img;
	public PatternedGrayScott gs;

	
	public GSAgent (Vec3D _o, boolean _f, PImage _img, PatternedGrayScott _gs){
		super (_o, _f);
		f=_f;
		img = _img;
		gs = _gs;
		resetTrail();
		
	}
	
	//@Override
		public void run(Environment environment) {
			
		//ReadGS();
			avoidGSspots(5, 200, 255, 1);
			AvoidBrightness(5, 200, 255, 1);
			paintToPImage(1, 30, false);
		update();
		}
	
	
	
		//function for reading GS & setting to it
		public void ReadGS(){
			
			//get current position, translate to Vec2D, turn to int values
			Vec2D position = this.to2DXY().getFloored();;
			
			
			 float GSval = gs.getCurrentUAt((int)position.x, (int)position.y);
			 float mappedVal = map(GSval, 0, 1, 255, 0); //or it could just avoid closer to 1 if map too slow
			 
			 System.out.println(GSval);
		}
		
		
		
		//function for finding GS within range of values & radius
		public  Vec3D findValInRange(int x, int y, int rad, float min, float max){
			
			//create an array list for storing of value positions
			ArrayList<Vec2D>GSneighbours = new ArrayList<Vec2D>();
			//shuffle radius
			ArrayList<Integer>ii = getShuffled(-rad,rad);
			ArrayList<Integer>jj = getShuffled(-rad,rad);
			
			//go through the array and add the surrounding x y coordinates
			for (int i:ii) {
				for (int j:jj) {
						int ix = i+x;
						int jy = j+y;
						if(ix>=0 && jy>=0 && ix<img.width && jy<img.height ){
							GSneighbours.add(new Vec2D(ix, jy));
						}
				}
			}
			//go through the list of x y coordinates and get the values. if the value is in the range, return the vec3d position of it
			for (Vec2D gsPos :GSneighbours){
				 float GSval = gs.getCurrentUAt((int)gsPos.x, (int)gsPos.y);
				 float mappedVal = map(GSval, 0, 1, 255, 0); //or it could just avoid closer to 1 if map too slow
					if(mappedVal>=min&&mappedVal<=max){
						return new Vec3D(gsPos.x-x,gsPos.y-y,0);
					}
			}
			return new Vec3D();
		}

			
			
			public void avoidGSspots(int SearchRad, int minVal, int maxVal, float force){
				
				//get current position, translate to Vec2D, turn to int values
				Vec2D position = this.to2DXY().getFloored();
				//get the vector to structural voxels with a val  between those defined within the search radius. Note: not the position of the voxel.
				Vec3D toGS = findValInRange((int)position.x, (int)position.y, SearchRad, minVal, maxVal);
				
				Vec3D awayFromGS = toGS.getInverted();
				awayFromGS.scaleSelf(force);
				addForce(awayFromGS);
			}
			
		
		//function for writing to the Pimage with white
		public void paintToPImage(int rad, int val, boolean size){
			if (size){
			for (int i= -rad; i<=rad; i++){
				img.set((int)this.x+i, (int)this.y+i, val);
			}
			}
			else{
			img.set((int)this.x, (int)this.y, val);
		}
		}
		
		
		
		//function for avoiding brightness in range
		
		public void AvoidBrightness(int SearchRad, int minVal, int maxVal, float force){
			Vec2D position = this.to2DXY().getFloored();
			Vec3D toBs = findBrightnessInRange((int)position.x, (int)position.y,SearchRad, minVal,maxVal);
			Vec3D awayFromBs = toBs.getInverted();
			awayFromBs.scaleSelf(force);
			addForce(awayFromBs);
		}
		
		
		
		
		
		//function for finding brightness on the Pimage
		public Vec3D findBrightnessInRange(int x, int y, int rad, float min, float max){
			
		//	float bVal = img.get((int)this.x, (int)this.y);
			
			//create an array list for storing of value positions
			ArrayList<Vec2D>imgNeighbours = new ArrayList<Vec2D>();
			//shuffle radius
			ArrayList<Integer>ii = getShuffled(-rad,rad);
			ArrayList<Integer>jj = getShuffled(-rad,rad);
			
			//go through the array and add the surrounding x y coordinates
			for (int i:ii) {
				for (int j:jj) {
						int ix = i+x;
						int jy = j+y;
						if(ix>=0 && jy>=0 && ix<img.width && jy<img.height ){
							imgNeighbours.add(new Vec2D(ix, jy));
						}
				}
			}
			
			for (Vec2D bsPos :imgNeighbours){
				float bVal = img.get((int)bsPos.x, (int)bsPos.y);
					if(bVal>=min&&bVal<=max){
						return new Vec3D(bsPos.x-x,bsPos.y-y,0);
					}
			}
			return new Vec3D();
		}
		
		
		
		//ADDITIONAL FUNCTIONS
		private ArrayList<Integer> getShuffled(int min, int max){
			List<Integer> l = new ArrayList<Integer>();
			for(int i = min;i<=max;i++){
				l.add(i);
			}
			Collections.shuffle(l);
			return (ArrayList<Integer>) l;
		}
		
		
}
