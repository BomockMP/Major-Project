package examples.voxels.pathfinding;

import pathfindingTools.AStarPathFinder;
import pathfindingTools.Path;
import pathfindingTools.Step;
import peasy.PeasyCam;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;
import core.Agent;
import core.Canvas;
import core.Environment;
import core.IO;

public class VoxelPathfinding extends PApplet{
	PeasyCam cam;
	VoxelGrid voxels;
	VoxelBrush brush;
	int numX= 100;
	int numY = 100;
	int numZ = 100;
	Environment environment;
	Canvas canvas;
	Path path;
	Vec3D target = new Vec3D(65,65,0);
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);	
		canvas = new Canvas(this.g);
		
		//imports
		environment = new Environment(this, 1000);
		environment.addAgents(IO.importLinkedPlanes(this,"links11.txt"));
		environment.update(false);
		
		
		voxels = new VoxelGrid(numX,numY,numZ, new Vec3D(1,1,1)); // make some voxels
		voxels.createLayerFromImage(loadImage("path.png"),0);
		//voxels.createNoise(0.08f, this);

	}
	
	public void draw(){
		background(0);
		//voxels.blurall();

		voxels.render(2, 150, 1, this);
		if(path!=null){
			stroke(255,0,0);
			strokeWeight(2);
			for(Step s: path.getSteps()){
				point(s.getX(),s.getY(),s.getZ());
			}
		}
		stroke(0,255,0);
		strokeWeight(5);
		point(target.x, target.y,target.z);
	}
	
	public void keyPressed(){
		if(key=='s'){
			//NOTE - save this and open in amira- use data type byte and enter the size of the voxels (100,100,100)
			voxels.save("grid.raw");
		}
		
		if (key == 'p'){
			int res = 2;
			AStarPathFinder pathfinder = new AStarPathFinder(voxels,70,true,150,res);
			path = pathfinder.findPath(new Agent(new Vec3D(), true), 0, 0, 0, (int) target.x/res,(int)target.y/res,(int)target.z/res);
		}
		
		if(key =='r'){
			voxels = new VoxelGrid(numX,numY,numZ, new Vec3D(1,1,1)); // make some voxels
			voxels.createLayerFromImage(loadImage("path.png"),0);
		}
		if(key =='n'){
			noiseSeed((long)random(100));
			voxels.createNoise(0.08f, this);
		}
	}
	
}
