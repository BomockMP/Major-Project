package projects.greyScott;

import java.util.ArrayList;

import core.Canvas;
import core.Environment;
import core.IO;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.sim.grayscott.GrayScott;
import toxi.color.*;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.util.*;
import toxi.util.datatypes.*;
import voxelTools.VoxelGrid;


public class GreyScottMainApp extends PApplet {

	//920 size for details
	//400 size for mound size as larger

	public PatternedGrayScott gs;
	
	public ToneMap toneMap;

	public PImage terrain;

	public VoxelGrid voxels;
	public int timeDepth = 100;
	public int stepsPerLevel = 1;
	public int vWidth = 400; //920 max //920 for features, 950 for waterways, 400 for humps
	public int vHeight =400;//920 max
	public int vDepth = 7; //8 makes them nippley //7 is ideal
	public boolean draw3d = false;
	public int zCount = 0;
	
	public Environment environment;
	
	public Canvas canvas;
	
	public  ArrayList<Vec2D> pathPts;
	public Path path;
	
	public ArrayList<Vec3D> spawnPts; //array list of vectors for agent spawn locations
	public int agentCount;
	
	
	public void setup() {

		    size(400,400, OPENGL); //920 max
		    
		    terrain = loadImage("sitehm8_mirror.png"); // terrain = loadImage("W10HMA.png");
		    terrain.resize(vWidth, vHeight); //make sure image same size as grid and gs
		    
	    gs= new PatternedGrayScott(vWidth,vWidth,false,terrain, this);
	    
	    //SITE MESHES
	    
	    //public void setCoefficients(float f, float k, float dU, float dV) 
	  //  gs.setCoefficients(0.023f,0.074f,0.095f,0.03f);
	 //  gs.setCoefficients(0.022f,0.079f,0.095f,0.03f); //makes dots still does
	    //  gs.setCoefficients(0.023f,0.079f,0.095f,0.03f);
	   //    gs.setCoefficients(0.023f,0.079f,0.099f,0.02f); //good balance no longer
	    // gs.setCoefficients(0.021f,0.074f,0.095f,0.022f); //required
	   //  gs.setCoefficients(0.021f,0.074f,0.096f,0.028f); //required!
	    
	 // gs.setCoefficients(0.016f,0.074f,0.096f,0.028f); //test1
	    
	    //KEY ONES
	//   gs.setCoefficients(0.0155f,0.074f,0.096f,0.028f); //test2 good for road edges
	//gs.setCoefficients(0.0155f,0.076f,0.090f,0.02f); //test3 - slow but good bunts THIS ONE IS GOOD for overall landscape details
	   gs.setCoefficients(0.022f,0.079f,0.095f,0.03f); //mounds    

	    //---------------
	    
	    
		 //   gs.setCoefficients(0.0215f,0.065f,0.095f,0.02f); //test4 - honeycomb
		    
		//    gs.setCoefficients(0.0215f,0.065f,0.095f,0.02f); //test5
		    
		   // gs.setCoefficients(0.021f,0.074f,0.097f,0.037f); //very dynamic
		   // gs.setCoefficients(0.021f,0.074f,0.098f,0.059f); //very dynamic2
		   // gs.setCoefficients(0.022f,0.079f,0.098f,0.045f); //very dynamic2	    
		    
	    
	    //-----------------------------
	    //SPIRITUAL SITE 
	    //-----------------------------
	  // gs.setCoefficients(0.0155f,0.074f,0.096f,0.028f); //test2 good for road edges
	 //  gs.setCoefficients(0.0155f,0.076f,0.090f,0.02f);
	//   gs.setCoefficients(0.0215f,0.065f,0.095f,0.02f); //test4 - honeycomb
	  //  gs.setCoefficients(0.021f,0.074f,0.098f,0.059f);
	 //   gs.setCoefficients(0.0215f,0.065f,0.095f,0.02f); //test5
	//    gs.setCoefficients(0.0155f,0.074f,0.096f,0.028f); //test2 good for road edges
	    
	    
	    
	    
	    //-----------------------------
	    //volume
	    voxels = new VoxelGrid(vWidth,vHeight,vDepth, new Vec3D(1,1,1));
	    voxels.initGrid();
	    
	  //environment
	  	environment = new Environment(this, 2000f);
	  //canvas
	  	canvas = new Canvas(this.g);
	  	
	  //spawn pt array for agents
//	  	spawnPts = new ArrayList<Vec3D>();
//		ArrayList<Vec3D> points = IO.importPoints(this, "C:\\Users\\Boyd\\git\\Major  Project\\Boyd Major Project\\bin\\Bpts.txt");
//		System.out.println("file Read");
//		System.out.println(points.size());
	  	
	  	
		 //Path Pts to create path
	  	pathPts = new ArrayList<Vec2D>();
	  	
	  	//import the points for path
//		ArrayList<Vec3D> points = IO.importPoints(this, "C:\\Users\\Boyd\\git\\Major-Project\\Boyd Major Project\\bin\\PathPts.txt");
//		System.out.println("file Read");
//		System.out.println(points.size());
		
		
		
	  	//import the points for AGENT SPAWN
//		ArrayList<Vec3D> spawn = IO.importPoints(this, "C:\\Users\\Boyd\\git\\Major-Project\\Boyd Major Project\\bin\\SpawnPts.txt");
//		System.out.println("file Read");
//		System.out.println(spawn.size());
		
		//C:\Users\Boyd\git\Major-Project\Boyd Major Project\bin
		
	  	//AGENT PATH TESTING
	  	
	  	//for each vec3d point in point list, turn to ve2d and add to path
//	  	for (int i = 0; i < points.size(); i++){
//	  	
//	  		Vec3D v = points.get(i);
//	  		Vec2D v2d = v.to2DXY();
//	  		pathPts.add(v2d);
//
//	  	}
	  		

//	  	path = new Path(pathPts, 2);
//	  	path.initiatePath();
	  	
	  
	  		

	  	
	  	
	  	
	  	
	  	
	 // define a color gradient by adding colors at certain key points
		// a gradient is like a 1D array with target colors at certain points
		// all inbetween values are automatically interpolated (customizable too)
		// this gradient here will contain 256 values
		ColorGradient gradient=new ColorGradient();
		gradient.addColorAt(0, NamedColor.BLACK);
		gradient.addColorAt(128, NamedColor.RED);
		gradient.addColorAt(192, NamedColor.GREY);
		gradient.addColorAt(255, NamedColor.WHITE);

	
		
		//backup
//		gradient.addColorAt(0, NamedColor.BLACK);
//		gradient.addColorAt(128, NamedColor.RED);
//		gradient.addColorAt(192, NamedColor.YELLOW);
//		gradient.addColorAt(255, NamedColor.ORANGE);
		
		
		
		// now create a ToneMap instance using this gradient
		// this maps the value range 0.0 .. 0.33 across the entire gradient width
		// a 0.0 input value will be black, 0.33 white
		toneMap=new ToneMap(0, 0.33f, gradient);
		
		
	
//		for (int i = 0; i < spawn.size(); i++){
//			GSAgent a = new GSAgent(spawn.get(i), false, terrain, gs, path);
//			environment.pop.add(a);
//		}
	    
	    
	    
		}

public	void draw() {
	
	
	background(100);
	lights();
	environment.run();
	environment.update(false); //this is needed for neighbours to work

	
		  if (mousePressed) {
		    // set cells around mouse pos to max saturation
		    gs.setRect(mouseX, mouseY,20,20);
		  }
		  
		  
	
		  
		  
		  
		  
		  
		  loadPixels();
		  for(int i=0; i<10; i++) gs.update(1);
		  // read out the V result array
		  // and use tone map to render colours
		  for(int i=0; i<gs.v.length; i++) {
		    pixels[i]=toneMap.getARGBToneFor(gs.v[i]);
		  }
		  
		  updatePixels();
		  

		  
		  
		  
		  if (draw3d){
  //VOXEL DRAWING OF GS
			  System.out.println("drawing");
			  
			  //for one Z layer
			  for (int z = 0; z<=1; z++){
				  
			//for the width & height of the GS	  
		    for(int i=0;i<vWidth;++i) {
		      for(int j=0;j<vHeight;++j) {
		    	  
	
		    	 //get the GS value and map it to a brightness range
		    	 float val = gs.getCurrentUAt(i, j);
		    	 float mappedVal = map(val, 0, 1, 255, 0);
		    	 
		    	 //get the value and map it across the desired depth of voxe grid vdepth
		    	 float v = mappedVal*((float)vDepth/255f);
		    	 System.out.println(v);
		    	 
		    	 //set values based on brightness across voxel depth (heightmap)
		    	 for (int k=0; k<v; k++) {
		    	voxels.setValue(i, j, k, mappedVal);
		    	 }
		      }
		    zCount++;
		  }
		  draw3d = false;
		  }
		  }
		  
		  
		  
		 //draw agent over the top
//		canvas.drawPts(environment.pop, 5);
//		canvas.drawSplinePoints(path);
//		  
		  
		}

public void keyPressed() {
	if (key == 's') {
		
		//render every 2nd voxel
		gs.seedImage(terrain.pixels, terrain.width, terrain.height);
	}
	
	
	if (key == 'q') {
		
		//render every 2nd voxel
		//int scale = 2;
//		PGraphics pg = createGraphics(width, height, JAVA2D);
//		beginRecord(pg);
//		//g.save("hi");
//
		saveFrame();
//		  pg.loadPixels();
//		//  for(int i=0; i<10; i++) gs.update(1);
//		  for(int i=0; i<gs.v.length; i++) {
//			    pg.pixels[i]=toneMap.getARGBToneFor(gs.v[i]);
//			  }
//		  pg.updatePixels();
//		// updatePixels();
//		 
//		pg.save("hires.tif");
//		endRecord();
	}
	
	
	if (key == 'v'){
		voxels.save("ErosionVoxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
	}
	
	
	if (key == 'd'){
		draw3d = true;
	}
	
	
//	if (key == 'i'){
//		//import
//		ArrayList<Vec3D> points = IO.importPoints(this, "C:\\Users\\Boyd\\git\\Major  Project\\Boyd Major Project\\bin\\Bpts.txt");
//		System.out.println("file Read");
//		System.out.println(points.size());
//	}
	
	
	if (key == 't'){
		
		
		IO.saveTrails(environment.pop, "GStrails_"+frameCount+".txt");
	}
	
	
}

	
}
