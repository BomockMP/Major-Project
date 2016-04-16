package projects.greyScott;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.nativewindow.OffscreenLayerOption;

import ProGAL.proteins.PDBFile.ParentRecord;
import processing.core.PApplet;
import processing.core.PImage;
import toxi.math.MathUtils;
import toxi.sim.grayscott.GrayScott;

public class PatternedGrayScott extends GrayScott {

	
	public PImage img;
	public ArrayList paintedIndex;
	public int[] vals;
	public PApplet parent;
	
	
	
	  // our constructor just passes things on to the parent class
	  PatternedGrayScott(int w, int h, boolean tiling, PImage _img, PApplet _parent) {
	    super(w,h,tiling);
	    img = _img;
	    parent = _parent;
	    
	    paintedIndex = new ArrayList<>();
	   // int imgHeight = img.height;
	   // int imgWidth = img.width;
	  }

	  
	  
	  //source code for Seedimage
	  @Override
	  public void seedImage(int[] pixels, int imgWidth, int imgHeight) {
	        int xo = MathUtils.clip((width - imgWidth) / 2, 0, width - 1);
	        int yo = MathUtils.clip((height - imgHeight) / 2, 0, height - 1);
	        imgWidth = MathUtils.min(imgWidth, width);
	        imgHeight = MathUtils.min(imgHeight, height);
	        
	       // vals = new int[imgHeight*imgWidth]; //boyd
	        
	        for (int y = 0; y < imgHeight; y++) {
	            int i = y * imgWidth;
	            for (int x = 0; x < imgWidth; x++) {
	                if (0 < (pixels[i + x] & 0xff)) {
	                    int idx = (yo + y) * width + xo + x;
	                    
	                    
	                  //  paintedIndex.add(idx);
	                   // vals[idx];
	                    
	                  
	                    //black
	                    
//	                    uu[idx] = 0.0f;
//	                    vv[idx] = 0.0f;
	                    
	                    uu[idx] = 0.5f;
	                    vv[idx] = 0.25f;
	                    
	                }
	            }
	        }
	    }
	  
	  
	  
	  
	public boolean ChangeDiffusionAtColor(int x, int y){
//		  for (int y = 0; y < img.height; y++) {
//	          for (int x = 0; x < img.width; x++) {
	        	  int c =  img.get(x, y);
	        	  //if color is white
	        	  if (c == -1){
	        		  return true;
	        	  } else {
	        		  return false;
	        	  }
	          }
	
	
	
	public float mapGrey(int x, int y){
		
		int c =  img.get(x, y);
		//get value as brightness
		float b = parent.brightness(c);
			return b;
	}

    
	
//public float changeFCoeffAt(int x, int y, float val){
//	//f = getFCoeffAt(x, y);
//	//float changedval = Fco+val;
//	//f = Fco+val;
//		//System.out.println(f);
//	return f+val;
//}
//	
//
//
//public float changeKCoeffAt(int x, int y, float val){
//	//float kAtPos = getKCoeffAt(x, y);
//	//k = KCo + val;
//	//System.out.println(f);
//	return k+ val;
//}
//	




	  
public float getFCoeffAt(int x, int y) {


//get brightness at point
float b = mapGrey(x, y);
	
	
// if the value is grey
if (b < 255 && b > 2){		
//map greyscale value to a range 1-10
float mappedB = parent.map(b, 1, 254, 1, 30);
return f+0.01f*(mappedB/5);
}


if (b==2){
	return f-0.04f;
}


//if black
if (b < 2){
	return f;
}

//if white make it no go
else{
	return f+0.1f;
}



}
	
	
public float getKCoeffAt(int x, int y) {
	
	//get brightness at point
	float b = mapGrey(x, y);
	
	
	//if agent specific gradient
	
	
	
	
	if (b < 255 && b > 2){		
		//map greyscale value to a range 1-10
		float mappedB = parent.map(b, 1, 254, 1, 20);
		return k+0.01f*(mappedB/7);
		}
	
	
	
	if (b==2){
		return k-0.02f;
	}
	
	
	//if black
	if (b < 2){
		return k;
	}

	//if white make it no go
	else{
		return k+0.1f;
	}
}
	
	
	


//boolean white = ChangeDiffusionAtColor(x, y);
//if (white){
////  x/=1;
////  return 0==x%2 ? f : f-0.005f;
////return (float) (f*0.0001);
//  return f+0.01f;
// // 
//  
//}else{
//return f;
//}
	
// here we only use the y coordinate
// and create a gradient falloff for this param
//	
//	boolean white = ChangeDiffusionAtColor(x, y);
//	 if (white){
//		// return k-y*0.00004f;
//		// return k-y*0.00004f;
//		// return (float) (k*0.0001);
//		 return k+0.01f;
//	  }else{
//return k;
//} 







	  
	  public void getColAtIndex(int x, int y){  
		int c =  img.get(x, y);
	  }
	  
	  
	  
	  
	  
	  
	  
	  
		//function to harrass edges of Pimage...if within radius of white pixel?
		//if x/y position is surrounded by a white pixel, do something
	  

	  
	  
	  

	  
	  
	  
	  

	  
	  

	}








//TOXI EXAMPLE
// this function is called for each cell
// to retrieve its f coefficient
//public float getFCoeffAt(int x, int y) {
//  // here we only take the x coordinate
//  // and choose one of 2 options (even & odd)
//  x/=32;
//  
//  return 0==x%2 ? f : f-0.005f;
//  
//}
//
//// this function is called for each cell
//// to retrieve its K coefficient
//public float getKCoeffAt(int x, int y) {
//  // here we only use the y coordinate
//  // and create a gradient falloff for this param
//  return k-y*0.00004f;
//} 

