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
	public static int dimX = 250;
	public static int dimY = 250;
	public static int dimZ = 18; //75
	public static Vec3D scale = new Vec3D(1,1,1);
	GradientVoxels gradientVoxels;

	
	public Environment environment;
	
	public Canvas canvas;
	
	//spawn grid array
	public int windAgentCountXPos = 200;
	public int windAgentCountYPos = 200;
	public int windAgentCountXNeg = 200;
	public int windAgentCountYNeg = 200;
	
	//boundbox 
//	public int bounds = dimX*(int)scale.y;
//	public int boundScale = (int) (bounds + (0.1f*bounds));
//	public  int minBoundsScale = (int)-0.1f*bounds;
	
	PImage terrain;
	
	public boolean releaseAgents = true;
	
	
	
	//springstuffAnchorTest
	//trail spring p hysics
	//public VerletPhysics3D springPhysics;
	
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
				terrain = loadImage("gradHM.png");
		
		//voxels
		voxels = new VoxelGrid(dimX, dimY, dimZ, scale);
		
		voxels.initGrid();
		
		//call create terrain function
		//voxels.createTerrain(terrain);
		
		//gradient terrain from dark at bottom to light at top
		gradientVoxels = new GradientVoxels(voxels, false);
		//gradientVoxels.run();
		gradientVoxels.splitGrid(voxels, 12);
	
		//environment
		environment = new Environment(this, 2000f);
		//canvas
		canvas = new Canvas(this.g);

		
		for (int i = 0; i < windAgentCountXPos; i++) {
			float spawnptX = -35;
			float spawnptY = random(10,240);
			float spawnptZ = random(4,7);
			WindAgent a = new WindAgent(new Vec3D(spawnptX, spawnptY, spawnptZ), false, voxels, this, "posX");
			environment.pop.add(a);
	}
		
		for (int i = 0; i < windAgentCountYPos; i++) {
			float spawnptX = random(10,240);
			float spawnptY = -35;
			float spawnptZ = random(4,7);
			WindAgent a = new WindAgent(new Vec3D(spawnptX, spawnptY, spawnptZ), false, voxels, this, "posY");
			environment.pop.add(a);
	}

		for (int i = 0; i < windAgentCountXNeg; i++) {
			float spawnptX = dimX+35;
			float spawnptY = random(10,240);
			float spawnptZ = random(4,7);
			WindAgent a = new WindAgent(new Vec3D(spawnptX, spawnptY, spawnptZ), false, voxels, this, "negX");
			environment.pop.add(a);
	}
		
		for (int i = 0; i < windAgentCountYNeg; i++) {
			float spawnptX = random(10,240);
			float spawnptY = dimY+35;
			float spawnptZ = random(4,7);
			WindAgent a = new WindAgent(new Vec3D(spawnptX, spawnptY, spawnptZ), false, voxels, this, "negY");
			environment.pop.add(a);
	}	
		
		
		
	}
	
	

	
	
	// ---------------------------------------------------------------------------------
	// Draw
	// ---------------------------------------------------------------------------------

	public void draw() {
		
		background(100);
		lights();

		environment.run();
		environment.update(false); //this is needed for neighbours to work
	

		//canvas.drawPts(environment.pop, 2);
		canvas.drawSandBank(environment.pop, 2);
		
		//canvas.drawVector(environment.pop, 1);
		
		if (frameCount%100==0){
		gradientVoxels.collapseVoxels(voxels);
		//System.out.println("collapsed!");
		}
		
		voxels.render(2, 1, 1, this);
//		
//		if (frameCount%700==0){
//			//voxels.blurall();
//			
//			
//			for (int z=0; z<voxels.d; z+=1) {
//				for (int y=0; y<voxels.h; y+=1) {
//					for (int x=0; x<voxels.w; x+=1) {
//						voxels.blur2d(x, y, z);
//					}
//				}
//			}
//			
//			
//			
//			System.out.println("c blurred!");
//			}
	}
	
	
	
	
	// ---------------------------------------------------------------------------------
	// Global Functions
	// ---------------------------------------------------------------------------------
	public void keyPressed() {
		if (key == 's') {
			
			//render every 2nd voxel
			voxels.render(10, 50, 1, this);
		}
		
		
		if (key == 'v'){
			voxels.save("ErosionVoxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
		}
		
		
if (key == 'b') {
	
//	for (int z=0; z<voxels.d; z+=1) {
//	for (int y=0; y<voxels.h; y+=1) {
//		for (int x=0; x<voxels.w; x+=1) {
//			voxels.blur2d(x, y, z);
//		}
//	}
//}
	
	voxels.blurall();



System.out.println("c blurred!");
	}
	
	}
	
}


