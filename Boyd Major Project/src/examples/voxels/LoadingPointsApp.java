package examples.voxels;

import java.util.ArrayList;

import peasy.PeasyCam;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;
import core.Canvas;
import core.Environment;
import core.IO;

public class LoadingPointsApp extends PApplet {
	PeasyCam cam;
	VoxelGrid voxels;
	VoxelBrush brush;
	int numX= 100;
	int numY = 100;
	int numZ = 100;
	Environment environment;
	Canvas canvas;
	int ctr = 0;
	
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);	
		canvas = new Canvas(this.g);
		
		//imports
		environment = new Environment(this, 1000);
		environment.addAgents(IO.importLinkedPlanes(this,"links11.txt"));
		environment.update(false);
		
		
		voxels = new VoxelGrid(numX,numY,numZ, new Vec3D(4,4,4)); // make some voxels
		//voxels.createCube(20, 20, 20, 20, 255);
		//TriangleMesh triMesh=(TriangleMesh)new STLReader().loadBinary(sketchPath("cliff.stl"),STLReader.TRIANGLEMESH);
		//MeshVoxeliser mv = new MeshVoxeliser(numX, numY, numZ, new Vec3D(1,1,1), triMesh, voxels);
		//mv.voxelizeMesh(mv.getMesh(),255,this);
		//voxels.createNoise(0.04f, this);
		brush = new VoxelBrush(voxels, 5);

	}
	
	public void draw(){
		background(0);
		//voxels.blurall();
		voxels.render(1, 150, 1, this);
		
		//HEY - why dont you try making a function that loops through the voxels
		//and draws them as boxes (rather than points).
	}
	
	public void keyPressed(){
		if(key=='s'){
			voxels.save("grid.raw");
		}
		if (key == 'i'){
			ArrayList<Vec3D> points = IO.importPoints(this, "C:\\Users\\E83684\\Desktop\\scans\\Third scan\\pts"+ctr+".txt");
			
			for(Vec3D p: points){
				//loop through the imported points
				voxels.setValue(p, 255);
			}
			
			ctr++;
		}
	}
}
