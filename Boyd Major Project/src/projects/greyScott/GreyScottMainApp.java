package projects.greyScott;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.sim.grayscott.GrayScott;
import toxi.color.*;

public class GreyScottMainApp extends PApplet {


	PatternedGrayScott gs;
	
	
	ToneMap toneMap;


	PImage terrain;
	
	
	
	public void setup() {

		    size(500,500, JAVA2D);
		    
		    terrain = loadImage("hmDiff.png");
		    
	    gs= new PatternedGrayScott(500,500,false,terrain, this);
	    
	    //public void setCoefficients(float f, float k, float dU, float dV) 
	  //  gs.setCoefficients(0.023f,0.074f,0.095f,0.03f);
	  // gs.setCoefficients(0.022f,0.079f,0.095f,0.03f); //makes dots
	    gs.setCoefficients(0.023f,0.079f,0.095f,0.03f);
	    
	    
	    
	    
	    
	//    gs.seedImage(terrain.pixels, terrain.width, terrain.height);
	    
	    
	    
	 // define a color gradient by adding colors at certain key points
		// a gradient is like a 1D array with target colors at certain points
		// all inbetween values are automatically interpolated (customizable too)
		// this gradient here will contain 256 values
		ColorGradient gradient=new ColorGradient();
		gradient.addColorAt(0, NamedColor.BLACK);
		gradient.addColorAt(128, NamedColor.RED);
		gradient.addColorAt(192, NamedColor.YELLOW);
		gradient.addColorAt(255, NamedColor.ORANGE);

		// now create a ToneMap instance using this gradient
		// this maps the value range 0.0 .. 0.33 across the entire gradient width
		// a 0.0 input value will be black, 0.33 white
		toneMap=new ToneMap(0, 0.33f, gradient);
		
		
	
	    
	    
	    
		}

public	void draw() {
	
	
	
	
	
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
		  

		  
		}

public void keyPressed() {
	if (key == 's') {
		
		//render every 2nd voxel
		gs.seedImage(terrain.pixels, terrain.width, terrain.height);
	}
	
	
	if (key == 'q') {
		
		//render every 2nd voxel
		int scale = 1;
		PGraphics pg = createGraphics(500*2,  500*2, JAVA2D);
		beginRecord(pg);
		
		

		 
		pg.save("hires.tif");
		endRecord();
	}
	
}

	
}
