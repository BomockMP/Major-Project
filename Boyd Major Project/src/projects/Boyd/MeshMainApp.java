package projects.Boyd;

//ctr-shift f - for formatting



/// LIBRARY IMPORTS ------------------------------------------------------------------------
import processing.core.PApplet;



import core.Canvas;
import peasy.PeasyCam;
import toxi.geom.Vec3D;


import voxelTools.VoxelBrush;


import voxelTools.VoxelGrid;




@SuppressWarnings("serial")
public class MeshMainApp extends PApplet {

	// / GLOBAL VARIABLES
	// ------------------------------------------------------------------------

	public PeasyCam cam;

	//ENVIRONMENT CLASSES
	//CONSOLIDATE INTO ONE ENVIRONMENT CLASS AT SOME POINT
	public MEnvironment environment;
	public ToxicEnvironment toxicEnvironment;

	public Canvas canvas;

	
	// Voxel Grid----------------------
	// Two voxel Grids. StructureVoxels to handle form. Pheremones to handle toxicity
	
	public VoxelGrid structureVoxels;
	public VoxelGrid pheromones;
	
	//Voxel Grid comparer class. compares voxel grids and fades where overlapping if desired
	
	public CompareMultipleVoxelGrids compareMultipleVoxelGrids;

	public static int dimX = 100;
	public static int dimY = 100;
	public static int dimZ = 100;
	Vec3D scale = new Vec3D(2, 2, 2);

	// Voxel brush----------------------
	
	public VoxelBrush brush;

	
	//VARIABLES TO ADJUST
	
	public float pheremoneFadeRate = 0.95f; //fade rate for pheremone Voxel grid. 0-1. lower val, greater Fadespeed
	public float structureFadeRate = 1f; //fade rate for structural voxel grid. 1 = wont fade.
	
	
	
	public float pheremoneThreshold = 30f; //threshold for agents to be affected by pheremones. higher = more resistant.1-255.
	
	public float structuralFadeRate = 0.9f; //fade rate for structural voxel grid. will only fade if corresponding cell has ph val > threshhold
	public float structuralFadeThreshold = 1f; //higher threshhold, less suceptibility to fade
	
	public int toxicAgentDeployRate = 3; //toxic(pheremonereleasing)agents released per frame. 2

	public float mAgentCount = 5;
	
	/// SETUP
	// -----------------------------------------------------------------------------------

	public void setup() {

		// CAMERA
		size(1280, 720, OPENGL);
		cam = new PeasyCam(this, 200);
		noLights();

		// VOXELGRIDS

		structureVoxels = new VoxelGrid(dimX, dimY, dimZ, scale);
		pheromones = new VoxelGrid(dimX, dimY, dimZ, scale);
		VoxelBrush brush;
		compareMultipleVoxelGrids = new CompareMultipleVoxelGrids(structureVoxels, pheromones, structuralFadeThreshold, structuralFadeRate);


		// ENVIRONMENT
		environment = new MEnvironment(this, 10000);
		toxicEnvironment = new ToxicEnvironment(this, 10000);

		// CANVAS
		canvas = new Canvas(this.g);

		// VOXELBRUSH
		
		brush = new VoxelBrush(structureVoxels, 5);
		
		

		// ADD AGENTS
		for (int i = 0; i < mAgentCount; i++) {
			MAgent a = new MAgent(new Vec3D((float)Math.random()*244, (structureVoxels.getH()*structureVoxels.s.y)-20,(float)Math.random()*244), false, structureVoxels, pheromones,
					brush, 5f, pheremoneThreshold );

			environment.pop.add(a);
		}

	}

	// ---------------------------------------------------------------------------------
	// Draw
	// ---------------------------------------------------------------------------------

	public void draw() {
		background(100);
		lights();



		// ADD TOXIC WIND AGENTS
		for (int i = 0; i < toxicAgentDeployRate; i++) {
			ToxicAgent a = new ToxicAgent(new Vec3D(dimX * 2, random(
					dimY), random(dimZ)), false, structureVoxels, pheromones,
					this);
			toxicEnvironment.pop.add(a);
		}

		// REMOVE AGENTS - DO THIS WITHIN THE TOXICAGENT CLASS
//		for (int i = 0; i < toxicEnvironment.pop.size(); i++) {
//			ToxicAgent a = toxicEnvironment.pop.get(i);
//			if (a.x < -dimX * 2) {
//				toxicEnvironment.pop.remove(a);
//			}
//		}

		// RUN ENVIRONMENT
		environment.run();
		toxicEnvironment.run();

		
		
		
		// RENDER MAGENTS
		canvas.drawPts(environment.pop, 5);
		

		//RENDER VOXELS
		//Render Pheremone active voxels
		pheromones.render(2, 50, 1, this);
		structureVoxels.render(1, 50, 1, this); //is sf scale factor meant to be the same as what you scale the vox grid?
		
		//render pheremone voxels which are acting on agents
		renderPheremoneVoxelsFound();
		
		//render empty voxels which are acting on agents (wip)
		renderEmptyVoxelsFound();
	
		//FADE TOXICITY 
		pheromones.fade(pheremoneFadeRate);
		//FADE STRUCTUREE
		structureVoxels.fade(structureFadeRate);
		
		//FADE STRUCTURAL GRID BASED ON TOXICITY
		compareMultipleVoxelGrids.run();

		
		

		
		
		

		
		
		
		
		
	}

	// ---------------------------------------------------------------------------------
	// Global Functions
	// ---------------------------------------------------------------------------------

	public void renderPheremoneVoxelsFound(){
		Vec3D phPos = new Vec3D();
		
		
for (int i = 0; i < environment.pop.size(); i++){
			
			MAgent a = environment.pop.get(i);
			
			//run branching function
			a.growUpwards(200f, environment);
			
			for (int f = 0; f < a.pheremoneVoxelsFound.size(); f++)
		    phPos = a.pheremoneVoxelsFound.get(f);
			
			strokeWeight(5);
			stroke(255,0,0);
			point(phPos.x, phPos.y, phPos.z);
}
		}
		

//TEST FUNCTION - RENDERING EMPTY VOXELS TO SEE IF ATTRACTION IS WORKING
public void renderEmptyVoxelsFound(){
	Vec3D emPos = new Vec3D();
	
	
for (int i = 0; i < environment.pop.size(); i++){
		
		MAgent a = environment.pop.get(i);
		
		for (int f = 0; f < a.emptyStructureVoxelsFound.size(); f++)
	    emPos = a.emptyStructureVoxelsFound.get(f);
		
		if (!a.isZeroVector()){
		
		strokeWeight(5);
		stroke(0,0,255);
		point(emPos.x, emPos.y, emPos.z);
		}
	}
//TEST FUNCTION - RENDERING EMPTY VOXELS TO SEE IF ATTRACTION IS WORKING

		
		
		
		
	}
	
	
	// ---------------------------------------------------------------------------------
	// Key Functions
	// ---------------------------------------------------------------------------------

	public void keyPressed() {
		if (key == 'e') {
			toxicEnvironment.saveTrails(); //save noisewave trails
			structureVoxels.save("StructureVoxels_"+frameCount+"_"+structureVoxels.w+"_"+structureVoxels.h+"_"+structureVoxels.d+".raw");
			pheromones.save("PheremoneVoxels_"+frameCount+"_"+pheromones.w+"_"+pheromones.h+"_"+pheromones.d+".raw");
			
		}
		
		
		if (key == 't'){
			
			//toxic Agent Render
			canvas.drawPts(toxicEnvironment.pop, 1);
		}
		
		
		
	}

}
