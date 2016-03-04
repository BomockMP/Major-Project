package examples.voxels.voxelNoise;


import java.util.ArrayList;

import core.Agent;
import core.Canvas;
import core.Environment;
import core.IO;
import peasy.PeasyCam;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.TriangleMesh;
import voxelTools.MeshVoxeliser;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;

public class VoxelNoiseApp extends PApplet{

	PeasyCam cam;
	VoxelGrid voxels;
	VoxelBrush brush;
	int numX= 100;
	int numY = 100;
	int numZ = 100;
	Environment environment;
	Canvas canvas;
	
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);	
		canvas = new Canvas(this.g);
		
		//imports
		environment = new Environment(this, 1000);
		environment.addAgents(IO.importLinkedPlanes(this,"links11.txt"));
		environment.update(false);
		
		
		voxels = new VoxelGrid(numX,numY,numZ, new Vec3D(2,2,2)); // make some voxels
		voxels.createNoise(0.04f, this);
		sinWaveThroughVoxels();

		//test merge comment
	}
	
	public void draw(){
		background(0);
		//voxels.blurall();

		voxels.render(2, 50, 1, this);
	}
	
	public void keyPressed(){
		if(key=='s'){
			//NOTE - save this and open in amira- use data type byte and enter the size of the voxels (100,100,100)
			voxels.save("grid.raw");
		}
	}
	
	public void sinWaveThroughVoxels(){
		for(int x=0; x<voxels.w;x++){
			for(int y=0; y<voxels.h;y++){
				for(int z=0; z<voxels.d;z++){
					double scaledVoxelVal = (double)voxels.getValue(x,y,z)/255;
					double numWaves = Math.PI*5;
					
					voxels.setValue(x, y, z, (float)Math.sin(scaledVoxelVal*numWaves)*255);
				}
			}
		}
	}
}
