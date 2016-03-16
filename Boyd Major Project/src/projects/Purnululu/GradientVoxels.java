package projects.Purnululu;

import java.util.Map;

import processing.core.PApplet;
import voxelTools.VoxelGrid;

//Class to initialize voxel grid with a gradient of values.

public class GradientVoxels {

	//Variables
	public VoxelGrid voxelGrid;
	public PApplet pApplet;
	
	
	//Constructor
	public GradientVoxels(VoxelGrid _voxelGrid){
		voxelGrid = _voxelGrid;
	}
	
	
	
	//Run function
	
public void run(){
	gradientVoxels(voxelGrid);
	}
	
	
//-------------------------------------------------------------------------------------

//Functions for initialisation

//-------------------------------------------------------------------------------------
	
public void gradientVoxels(VoxelGrid voxelGrid){
	

	//if closer to top, lower value. closer to bottom, higher value. around bottom edges, lower value.?!#
	

	int index = 0;

	for (int z = 0; z < voxelGrid.d; z++){
		
		//map z position in grid to possible voxel value range (1-255)
		float m =	pApplet.map(z, 0, (float)voxelGrid.d, 255, 1);
		
		System.out.println(m); //debug
		
		for (int y = 0; y < voxelGrid.h; y++){
			for (int x = 0; x < voxelGrid.w; x++){
				
				//Get index @ current Voxel
				index = x + voxelGrid.w * (y + voxelGrid.h * z);
				
				//set value by determined depth
				voxelGrid.vals[index].set(m);
	
			}	
		}
	}
	}
	

	

	
	

	
	
	
	
}
