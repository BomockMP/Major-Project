package projects.Purnululu;

import java.util.Map;

import org.omg.CORBA.portable.ValueBase;

import processing.core.PApplet;
import toxi.geom.mesh.Terrain;
import voxelTools.VoxelGrid;

//Class to initialize voxel grid with a gradient of values.

public class GradientVoxels {

	//Variables
	public VoxelGrid voxelGrid;
	public PApplet pApplet;
	public boolean terrain;
	
	
	//Constructor
	public GradientVoxels(VoxelGrid _voxelGrid, boolean _terrain){
		voxelGrid = _voxelGrid;
		terrain = _terrain;
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
		//float m =	pApplet.map(0, z, (float)voxelGrid.d, 255, 1);
		
		float m = pApplet.map(z, 0, voxelGrid.d, 1, 255);
		
		System.out.println(m); //debug
		
		for (int y = 0; y < voxelGrid.h; y++){
			for (int x = 0; x < voxelGrid.w; x++){
				
				//Get index @ current Voxel
				index = x + voxelGrid.w * (y + voxelGrid.h * z);
				//get value
				float value = voxelGrid.vals[index].get();
				
				//if its a terrain and the cell is filled with a value, do the following
				if (value > 0 && terrain == true){
					voxelGrid.vals[index].set(m);
					//System.out.println("terrain Gradient"); //debug
				}
				
				//if its not a terrain, do as normal
				if (terrain == false) {
					voxelGrid.vals[index].set(m);
					//System.out.println("Grid Gradient"); //debug
				}
				
				
	
			}	
		}
	}
	}
	

	

	
	

	
	
	
	
}
