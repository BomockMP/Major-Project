package examples.flocking;

import processing.core.*;
import controlP5.*;
import core.Canvas;
import core.Environment;
import core.IO;
import toxi.geom.Vec3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;
import peasy.*; 


/*----------------------------------------------------------------------------------NOTES

This is an example setup for working with the classes in the library. 

/----------------------------------------------------------------------------------NOTES*/

@SuppressWarnings("serial")
public class FlockingApp extends PApplet {
	
	// ---------------------------------------------------------------------------EXTERNAL LIBS
	PeasyCam cam;
	ControlP5 controlP5;

	// ---------------------------------------------------------------------------SLOWROBOTICS
	public Environment environment; //store and update agents
	public VoxelGrid voxels; //voxel object
	public VoxelBrush vbrush; //for manipulating voxels
	public Canvas canvas;
	// ----------------------------------------------------------------------------------SETUP

	public void setup(){
		size(800,600,OPENGL);
		cam =new PeasyCam(this,400);	
		setupP5();
		
		canvas = new Canvas(this.g); //drawing class 
		
		//create an environment for agents / voxels
		environment = new Environment(this, 2000);
		voxels = new VoxelGrid(10,10,10,new Vec3D(1,1,1));
		
		//add some agents
		for(int i=0;i<200;i++){
			DemoAgent a = new DemoAgent(Vec3D.randomVector().scale(200),false);
			environment.addAgent(a);
		}

	}

	public void draw(){
		background(0);
		environment.run(); //e.g. run agent pop
		environment.update(false);
		canvas.drawTrails(environment.pop, 1,255);
		gui(); //draws control p5 sliders as heads up display
	}

	/*------------------------------------

	Global functions 

	------------------------------------*/

	public void keyPressed(){
		if(key=='e'){
			environment.saveTrails();
			voxels.save("voxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
		}
		if(key=='s'){
			IO.saveTrails(environment.pop, "points.txt");
		}
	}

	void reset(){
		environment = new Environment(this, 2000);
		voxels = new VoxelGrid(10,10,10,new Vec3D(1,1,1));
	}

	/*------------------------------------

	ControlP5

	------------------------------------*/

	public void setupP5(){
		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(false);
	}


	void gui() {
		hint(DISABLE_DEPTH_TEST);
		cam.beginHUD();
		controlP5.draw();
		cam.endHUD();
		hint(ENABLE_DEPTH_TEST);
	}
}