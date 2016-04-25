package projects.projects.BoydMP;

//-----------------------------------------------------------------------------------
/// LIBRARY IMPORTS ------------------------------------------------------------------------
//-----------------------------------------------------------------------------------

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import projects.Boyd.MAgent;
import projects.Boyd.MEnvironment;
import core.Agent;
import core.Canvas;
import core.Environment;
import core.IO;
import peasy.PeasyCam;
import toxi.geom.Vec3D;
import toxi.geom.mesh.subdiv.NewSubdivStrategy;
import toxi.physics3d.VerletPhysics3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;





@SuppressWarnings("serial")
public class MainApp extends PApplet {

	// --------------------------------------------------------------------------------
	/// GLOBAL VARIABLES
	// -----------------------------------------------------------------------------------
	
	PeasyCam cam;
	
	VoxelGrid voxels;
	public static int dimX = 200;
	public static int dimY = 200;
	public static int dimZ = 5;
	public static Vec3D scale = new Vec3D(1,1,1);
	
	PImage terrain;
	
	public Environment environment;
	
	public Canvas canvas;
	
	//spawn grid array
	public ArrayList<Vec3D> spawnPts;
	public int agentCount;
	
	//trail spring physics
	public VerletPhysics3D physics;
	
	public int zthreshhold = 10;
	
	public ArrayList<Vec3D> spawnPoints;
	// --------------------------------------------------------------------------------
	/// SETUP
	// -----------------------------------------------------------------------------------

	public void setup() {
	
		
		//peasey
		// CAMERA
		size(1280, 720, OPENGL);
		cam = new PeasyCam(this, 200);
		noLights();
	
		
		//load image		
		terrain = loadImage("pland.png");
		
		//voxels
		voxels = new VoxelGrid(dimX, dimY, dimZ, scale);
		//call create terrain function
		voxels.createTerrain(terrain);
		
		//spawn pt array for agents
		spawnPts = new ArrayList<Vec3D>();
		spawnPoints = new ArrayList<Vec3D>();
		
		//environment
		environment = new Environment(this, 2000f);
		//canvas
		canvas = new Canvas(this.g);
		//physics for springs
		//optimum settings - 0.1f gravity, 50 iteration, 0 damp, 1 timestep
		physics = new VerletPhysics3D(new Vec3D(0,0,0.1f), 50, 0, 1); 
		
		
		
		
		
		//spawn positions = spawn as grid
//		for (int i = 0; i < dimX/10; i++){
//			for (int k = 0; k < dimY/10; k++){
//				Vec3D v = new Vec3D(i*10, k*10, 20);
//				spawnPts.add(v);
//			}
			
			//spawn positions - spawn from textfile
		
			ArrayList<Vec3D> points = IO.importPoints(this, "C:\\Users\\Boyd\\git\\Major  Project\\Boyd Major Project\\bin\\Bpts.txt");
			System.out.println("file Read");
			System.out.println(points.size());
			
			//remove Vec3Ds below z
		for (int i = 0; i < points.size(); i++){
			Vec3D v = points.get(i);
			
			//System.out.println(v);
			v.scaleSelf(2);
			//System.out.println(v);
//			if (v.z < zthreshhold){
//				points.remove(i);
//			}
			spawnPoints.add(v);
		}
		
		
		System.out.println(spawnPoints.size());
			//you want agent count to match this size 
			agentCount = spawnPoints.size();
			
			
			
			//agentCount = spawnPts.size();
			//agentCount = 1; - debug
			
	
		
		// ADD AGENTS
		for (int i = 0; i < agentCount; i++) {
			Agent a = new Agent(spawnPoints.get(i), false, voxels, physics);
			
			environment.pop.add(a);
		}
		
		
		
	}
	
	

	
	
	// ---------------------------------------------------------------------------------
	// Draw
	// ---------------------------------------------------------------------------------

	public void draw() {
		
		background(100);
		lights();
		voxels.render(1, 50, 1, this);
		environment.run();
		environment.update(false); //this is needed for neighbours to work
		

		
		//draw points
		canvas.drawPts(environment.pop, 5);
		//canvas.drawTrails(environment.pop, 1, 1f);
		canvas.drawAgentSprings(environment.pop, 1, 1f);
		
		
		//physics springs
		physics.update();
		
		
		
	}
	
	
	
	
	// ---------------------------------------------------------------------------------
	// Global Functions
	// ---------------------------------------------------------------------------------
	public void keyPressed() {
		if (key == 's') {
			
			environment.saveSpringParticles(frameCount);
		}
		
		
		if (key == 'v'){
			voxels.save("PheremoneVoxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
		}
		
		
		
		if (key == 'i'){
			//ArrayList<Vec3D> points = IO.importPoints(this, "C:\\Users\\Boyd\\git\\Major Project\\Boyd Major Project\\bin\\BptList.txt");
		
			
			ArrayList<Vec3D> points = IO.importPoints(this, "C:\\Users\\Boyd\\git\\Major  Project\\Boyd Major Project\\bin\\Bpts.txt");
			System.out.println("file Read");
			System.out.println(points.size());
			
		}
		
	}
	
	
	
}


