package projects.Boyd;



import voxelTools.VoxelGrid;


/* A class to run a check across two Voxel Grids to compare the values in each
 * and fade voxels depending on the value in the other grid
 */

public class CompareMultipleVoxelGrids {
	
	public VoxelGrid v1;
	public VoxelGrid v2;
	public float v2Threshold;
	public float fadeRate;

	
	
	public CompareMultipleVoxelGrids(VoxelGrid _v1, VoxelGrid _v2, float _v2Threshold, float _fadeRate) {
		
		//add some kind of fade threshhold or float comparrison thing
	
		v1 = _v1;
		v2 = _v2;
		v2Threshold = _v2Threshold;
		fadeRate = _fadeRate;
		
	}
	
	
	
	public void run(){
		
		compareVoxelGridsAndFade(v1, v2, v2Threshold, fadeRate);
		
	}
	
	//-------------------------------------------------------------------------------------

	//Functions for reading both voxel rays, comparing the values. If the value of voxelgrid v2
	//is above the threshhold, then fade the voxel value in grid 1.
	//Note; The purpose of this is so that the voxels in the structuralVoxelGrid will fade 
	//over time if occupying the space of voxels with a high pheremone value

	//-------------------------------------------------------------------------------------
	
	public void compareVoxelGridsAndFade(VoxelGrid v1, VoxelGrid v2, float v2Threshold, float fadeRate){
		
		int index = 0;

		//Iterate through grid 1
		
		
		for (int z=0; z<v1.d; z++) {
			for (int y=0; y<v1.h; y++) {
				for (int x=0; x<v1.w; x++) {
					index = x + v1.w * (y + v1.h * z);
					float val = v1.vals[index].get(); //sf?
					
					//find all voxels within grid v1 with a Val > 0
					
					if (val>0) {
						//for the index of these voxels, check grid v2 at same index. 
						float val2 = v2.vals[index].get();
						//if the value of the corresponding cell is > specified threshhold, fade these cells
						if (val2 > v2Threshold){
							
							//System.out.println("fading");
							v1.vals[index].set(v1.vals[index].get()*fadeRate);
						}
					}
				}
			}
		}
	
	}
	

	

}
