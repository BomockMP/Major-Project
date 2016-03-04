package examples.voxels;


import java.util.ArrayList;

import core.Agent;
import core.Canvas;
import core.Environment;
import core.IO;
import peasy.PeasyCam;
import processing.core.PApplet;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.TriangleMesh;
import voxelTools.MeshVoxeliser;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;

public class VoxelApp extends PApplet{

	PeasyCam cam;
	VoxelGrid voxels;
	VoxelBrush brush;
	int numX= 100;
	int numY = 100;
	int numZ = 100;
	VoxelAgent vAgent;
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
		
		
		voxels = new VoxelGrid(numX,numY,numZ, AABB.fromMinMax(new Vec3D(-100,-100,-100), new Vec3D(100,100,100))); // make some voxels
		//voxels.createCube(20, 20, 20, 20, 255);
		//TriangleMesh triMesh=(TriangleMesh)new STLReader().loadBinary(sketchPath("cliff.stl"),STLReader.TRIANGLEMESH);
		//MeshVoxeliser mv = new MeshVoxeliser(numX, numY, numZ, new Vec3D(1,1,1), triMesh, voxels);
		//mv.voxelizeMesh(mv.getMesh(),255,this);
		//voxels.createNoise(0.04f, this);
		brush = new VoxelBrush(voxels, 5);
		vAgent = new VoxelAgent(new Vec3D(10,10,0), false, voxels, brush);
		
		voxels.createLayerFromImage(loadImage("dumb.png"), 0);
	}
	
	public void draw(){
		background(0);
		//voxels.blurall();
		vAgent.run(environment);
		vAgent.stayOn(voxels);
		vAgent.render(this);
		canvas.drawTrails(environment.pop, 2, 255);
		voxels.render(2, 150, 1, this);
	}
	
	public void keyPressed(){
		if(key=='s'){
			
			voxels.save("grid.raw");
		}
	}
}
