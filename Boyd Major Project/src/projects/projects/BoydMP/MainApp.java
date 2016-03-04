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
	public static int dimX = 150;
	public static int dimY = 200;
	public static int dimZ = 15;
	public static Vec3D scale = new Vec3D(1,1,1);
	
	PImage terrain;
	
	public Environment environment;
	
	public Canvas canvas;
	
	//spawn grid array
	public ArrayList<Vec3D> spawnPts;
	public int agentCount;
	
	//trail spring physics
	public VerletPhysics3D physics;
	
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
		
		terrain = loadImage("hm4.png");
		
		//voxels
		
		voxels = new VoxelGrid(dimX, dimY, dimZ, scale);
		
		voxels.createTerrain(terrain);
		
		//spawn pt array
		spawnPts = new ArrayList<Vec3D>();
		
		
		//environment
		environment = new Environment(this, 1000f);
		//canvas
		canvas = new Canvas(this.g);
		//physics for springs
		//optimum settings - 0.1f gravity, 50 iteration, 0 damp, 1 timestep
		physics = new VerletPhysics3D(new Vec3D(0,0,0.1f), 50, 0, 1); 
		//physics = new VerletPhysics3D(); //default
		
		
		
		
		//spawn positions
		for (int i = 0; i < dimX/3; i++){
			for (int k = 0; k < dimY/3; k++){
				Vec3D v = new Vec3D(i*3, k*3, 20);
				spawnPts.add(v);
			}
			
			
			
			agentCount = spawnPts.size();
			//agentCount = 1;
			
		}
		
		// ADD AGENTS
		for (int i = 0; i < agentCount; i++) {
			Agent a = new Agent(spawnPts.get(i), false, voxels, physics);

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
		

		
		
		canvas.drawPts(environment.pop, 5);
		//canvas.drawTrails(environment.pop, 1, 1f);
		canvas.drawAgentSprings(environment.pop, 1, 1f);
		
		
		//physics springs
		physics.update();
		
		
		
	}
	
	
	
	
	// ---------------------------------------------------------------------------------
	// Global Functions
	// ---------------------------------------------------------------------------------
	
	
	
	
}


