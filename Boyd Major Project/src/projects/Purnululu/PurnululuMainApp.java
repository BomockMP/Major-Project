package projects.Purnululu;



//-----------------------------------------------------------------------------------
/// LIBRARY IMPORTS ------------------------------------------------------------------------
//-----------------------------------------------------------------------------------

import java.security.PublicKey;
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
public class PurnululuMainApp extends PApplet {

	// --------------------------------------------------------------------------------
	/// GLOBAL VARIABLES
	// -----------------------------------------------------------------------------------
	
	PeasyCam cam;
	
	VoxelGrid voxels;
	public static int dimX = 100;
	public static int dimY = 100;
	public static int dimZ = 50;
	public static Vec3D scale = new Vec3D(1,1,1);
	GradientVoxels gradientVoxels;

	
	public Environment environment;
	
	public Canvas canvas;
	
	//spawn grid array
	public int windAgentCount = 2500;
	public int rainAgentCount = 10;

	//boundbox 
	public int bounds = dimX*(int)scale.y;
	public int boundScale = (int) (bounds + (0.1f*bounds));
	public  int minBoundsScale = (int)-0.1f*bounds;
	
	PImage terrain;
	
	public boolean releaseAgents = true;
	
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
				terrain = loadImage("PHM9.png");
		
		//voxels
		voxels = new VoxelGrid(dimX, dimY, dimZ, scale);
		//call create terrain function
		voxels.createTerrain(terrain);
		//gradient terrain from dark at bottom to light at top
		gradientVoxels = new GradientVoxels(voxels, true);
		gradientVoxels.run();
	
		//environment
		environment = new Environment(this, 2000f);
		//canvas
		canvas = new Canvas(this.g);

		
		for (int i = 0; i < windAgentCount; i++) {
			
			//float spawnptX = -45;
			float spawnptY = random(5,90);
			float spawnptZ = random(40,40);
			
			
		if(i <= windAgentCount*0.6){
			
			
			
			WindAgent a = new WindAgent(new Vec3D(-45, spawnptY, 40), false, voxels, this, true);
			environment.pop.add(a);
		 }else{
			 
		WindAgent a = new WindAgent(new Vec3D(145, spawnptY, 33), false, voxels, this, false);		
		environment.pop.add(a);
		 }
	}
		

		
		
		// ADD AGENTS - WIND

		 
//		for (int i = 0; i < windAgentCount; i++) {
//			//float spawnptX = random((float)-boundScale, 0);
//			float spawnptY = random((float)-boundScale, (boundScale*1.2f));
//			float spawnptZ = random((float)-boundScale, (boundScale*1.2f));
//			//System.out.println(spawnpt);
//			WindAgent a = new WindAgent(new Vec3D(-bounds, spawnptY, spawnptZ), false, voxels, this);
//			
//			environment.pop.add(a);
//		}
		
		
		
		
		
		
	}
	
	

	
	
	// ---------------------------------------------------------------------------------
	// Draw
	// ---------------------------------------------------------------------------------

	public void draw() {
		
		background(100);
		lights();
		
		
		//ADD AGENTS - RAIN
//		for (int i = 0; i < rainAgentCount; i++) {
//			float spawnptX = random((float)0, 100);
//			float spawnptY = random((float)0, 100);
//			float spawnptZ = 100;
//			
//			RainAgent a = new RainAgent(new Vec3D(spawnptX, spawnptY, spawnptZ), false, voxels, this);
//			
//			environment.pop.add(a);
//		}
//		
		
		
//		for (int i = 0; i < windAgentCount; i++) {
//		float spawnptX = -45;
//		float spawnptY = random(0,120);
//		float spawnptZ = random(10,15);
//		//System.out.println(spawnpt);
//		WindAgent a = new WindAgent(new Vec3D(spawnptX, spawnptY, spawnptZ), false, voxels, this);
//		
//		environment.pop.add(a);
//	}
//		
		
		
		
		
		environment.run();
		environment.update(false); //this is needed for neighbours to work
	
		


		
		canvas.drawPts(environment.pop, 1);
		
	}
	
	
	
	
	// ---------------------------------------------------------------------------------
	// Global Functions
	// ---------------------------------------------------------------------------------
	public void keyPressed() {
		if (key == 's') {
			
			//render every 2nd voxel
			voxels.render(3, 1, 1, this);
		}
		
		
		if (key == 'v'){
			voxels.save("ErosionVoxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
		}
		
		
if (key == 'r') {
	//releaseAgents = false;
	}
	
	}
	
}


