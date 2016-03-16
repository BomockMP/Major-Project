package projects.Purnululu;



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
public class PurnululuMainApp extends PApplet {

	// --------------------------------------------------------------------------------
	/// GLOBAL VARIABLES
	// -----------------------------------------------------------------------------------
	
	PeasyCam cam;
	
	VoxelGrid voxels;
	public static int dimX = 50;
	public static int dimY = 50;
	public static int dimZ = 50;
	public static Vec3D scale = new Vec3D(1,1,1);
	GradientVoxels gradientVoxels;

	
	public Environment environment;
	
	public Canvas canvas;
	
	//spawn grid array
	public int windAgentCount = 400;

	
	// --------------------------------------------------------------------------------
	/// SETUP
	// -----------------------------------------------------------------------------------

	public void setup() {
	
		
		//peasey
		// CAMERA
		size(1280, 720, OPENGL);
		cam = new PeasyCam(this, 200);
		noLights();
	
		

		
		//voxels
		voxels = new VoxelGrid(dimX, dimY, dimZ, scale);
		//call create terrain function
		gradientVoxels = new GradientVoxels(voxels);

	
		//environment
		environment = new Environment(this, 2000f);
		//canvas
		canvas = new Canvas(this.g);


		gradientVoxels.run();

		
		
		// ADD AGENTS
		 int bounds = voxels.getH()*(int)voxels.s.y;
		 int boundScale = (int) (bounds + (0.9f*bounds));
		 
		 
		for (int i = 0; i < windAgentCount; i++) {
			//float spawnptX = random((float)-boundScale, 0);
			float spawnptY = random((float)-boundScale, boundScale);
			float spawnptZ = random((float)-boundScale, boundScale);
			//System.out.println(spawnpt);
			WindAgent a = new WindAgent(new Vec3D(-bounds, spawnptY, spawnptZ), false, voxels, this);
			
			environment.pop.add(a);
		}
		
	}
	
	

	
	
	// ---------------------------------------------------------------------------------
	// Draw
	// ---------------------------------------------------------------------------------

	public void draw() {
		
		background(100);
		lights();
		//render every 2nd voxel
		voxels.render(2, 1, 1, this);
		environment.run();
		environment.update(false); //this is needed for neighbours to work
	
		


		
		canvas.drawPts(environment.pop, 1);
		
	}
	
	
	
	
	// ---------------------------------------------------------------------------------
	// Global Functions
	// ---------------------------------------------------------------------------------
	public void keyPressed() {
		if (key == 's') {
			
			
		}
		
		
		if (key == 'v'){
			voxels.save("PheremoneVoxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
		}
		
		
		
	}
	
	
	
}


