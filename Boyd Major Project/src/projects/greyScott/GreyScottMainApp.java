package projects.greyScott;

import processing.core.PApplet;
import processing.core.PImage;
import toxi.sim.grayscott.GrayScott;
import toxi.color.*;

public class GreyScottMainApp extends PApplet {


	PatternedGrayScott gs;
	
	
	ToneMap toneMap;


	PImage terrain;
	
	
	
	public void setup() {

		    size(500,500);
		    
		    terrain = loadImage("hmDiff.png");
		    
	    gs= new PatternedGrayScott(500,500,false,terrain);
	  //  gs.setCoefficients(0.023f,0.074f,0.095f,0.03f);
	  // gs.setCoefficients(0.022f,0.079f,0.095f,0.03f); //makes dots
	    gs.setCoefficients(0.022f,0.079f,0.095f,0.03f);
	    
	    
	    
	    
	    
	    
	    
	    
	    
	 // define a color gradient by adding colors at certain key points
		// a gradient is like a 1D array with target colors at certain points
		// all inbetween values are automatically interpolated (customizable too)
		// this gradient here will contain 256 values
		ColorGradient gradient=new ColorGradient();
		gradient.addColorAt(0, NamedColor.BLACK);
		gradient.addColorAt(128, NamedColor.RED);
		gradient.addColorAt(192, NamedColor.YELLOW);
		gradient.addColorAt(255, NamedColor.WHITE);

		// now create a ToneMap instance using this gradient
		// this maps the value range 0.0 .. 0.33 across the entire gradient width
		// a 0.0 input value will be black, 0.33 white
		toneMap=new ToneMap(0, 0.33f, gradient);
		
		
		
	    
	    
	    
		}

public	void draw() {
	
	
	gs.seedImage(terrain.pixels, terrain.width, terrain.height);
	
	
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
//		  
//		  for(int i=0; i<gs.v.length; i++) {
//			    // take a GS v value and turn it into a packed integer ARGB color value
//			    pixels[i]=toneMap.getARGBToneFor(gs.v[i]);
//			}
//
//		  
//		  
//		  // update simulation by 10 time steps per frame
//		  for(int i=0; i<10; i++) gs.update(1);
//		  
		 gs.getKAtIndex();

		  updatePixels();
		  
		  
		 
		  
	
		  
		  
		  
		  
		}

	

	
}
