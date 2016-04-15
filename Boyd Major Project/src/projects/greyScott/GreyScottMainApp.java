package projects.greyScott;

import core.Canvas;
import core.Environment;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.sim.grayscott.GrayScott;
import toxi.color.*;
import toxi.geom.Vec3D;
import toxi.util.*;
import toxi.util.datatypes.*;
import voxelTools.VoxelGrid;


public class GreyScottMainApp extends PApplet {


	public PatternedGrayScott gs;
	
	public ToneMap toneMap;

	public PImage terrain;

	public VoxelGrid voxels;
	public int timeDepth = 100;
	public int stepsPerLevel = 1;
	public int vWidth = 500;
	public int vHeight = 500;
	public int vDepth = 10;
	public boolean draw3d = false;
	public int zCount = 0;
	
	public Environment environment;
	
	public Canvas canvas;
	
	public void setup() {

		    size(500,500, OPENGL);
		    
		    terrain = loadImage("smhm500b.png");
		    terrain.resize(vWidth, vHeight); //make sure image same size as grid and gs
		    
	    gs= new PatternedGrayScott(vWidth,vWidth,false,terrain, this);
	    
	    //public void setCoefficients(float f, float k, float dU, float dV) 
	  //  gs.setCoefficients(0.023f,0.074f,0.095f,0.03f);
	  // gs.setCoefficients(0.022f,0.079f,0.095f,0.03f); //makes dots
	    //  gs.setCoefficients(0.023f,0.079f,0.095f,0.03f);
	    //   gs.setCoefficients(0.023f,0.079f,0.099f,0.02f); good balance
	    gs.setCoefficients(0.021f,0.074f,0.095f,0.022f);
	    
	    
	    //volume
	    voxels = new VoxelGrid(vWidth,vHeight,vDepth, new Vec3D(1,1,1));
	    voxels.initGrid();
	    
	  //environment
	  	environment = new Environment(this, 2000f);
	  //canvas
	  	canvas = new Canvas(this.g);
	  		
	  		
	//    gs.seedImage(terrain.pixels, terrain.width, terrain.height);
	    
	    
	    
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
		
		
	
		for (int i = 0; i < 1; i++){
			GSAgent a = new GSAgent(new Vec3D(250,250,-10), false, terrain, gs);
			environment.pop.add(a);
		}
	    
	    
	    
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
		canvas.drawPts(environment.pop, 5);
		  
		  
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
	
}

	
}
