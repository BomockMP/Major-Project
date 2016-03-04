package examples.voxels.multipleVoxelGrids;

import core.Agent;
import core.Canvas;
import core.Environment;
import core.IO;
import peasy.PeasyCam;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;

public class MultiVoxelApp extends PApplet {

	PeasyCam cam;
	VoxelGrid structureVoxels;
	VoxelGrid pheromones;
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
		structureVoxels = new VoxelGrid(numX,numY,numZ, new Vec3D(2,2,2)); // make some voxels
		pheromones = new VoxelGrid(numX,numY,numZ, new Vec3D(2,2,2)); // make some voxels
		//test merge comment
	}
	
	public void draw(){
		background(0);
		environment.run();
		environment.update(false);
		pheromones.fade(0.99f);
		pheromones.render(2, 50, 1, this);
		//canvas.drawTrails(environment.pop, 1, 255);
	}
	
	public void keyPressed(){
		if(key=='s'){
			//NOTE - save this and open in amira- use data type byte and enter the size of the voxels (100,100,100)
			pheromones.save("grid.raw");
		}
		if(key=='a'){
			addParticles();
		}
	}
	public void addParticles(){
		for(int i=0;i<100;i++){
			environment.addAgent(new VoxParticle(new Vec3D(random(numX*2),random(numY*2),random(numZ*2)),false,pheromones));
		}
	}

}
