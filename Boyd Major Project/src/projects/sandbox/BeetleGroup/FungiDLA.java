package projects.sandbox.BeetleGroup;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.rmi.CORBA.Util;

import toxi.sim.dla.*;
import toxi.geom.*;
import processing.opengl.*;
import processing.core.PApplet;



public class FungiDLA {
	
	DLA dla;
	public DLAConfiguration dlaConfig;
	public DLAListener listener;
	public ArrayList points;
	DLAGuideLines guides;
	PApplet parent;
	
	//I want to bring in a list of trail points from an agent to use as a trail for a DLA simulation
	
	public FungiDLA(ArrayList _points) {
	
		points = _points;
		ArrayList points = new ArrayList();
		 dlaConfig = new DLAConfiguration();
		 listener = new DLAListener();
		
	}
	
	
	//function to set up the DLA system in setup loop
	
	public void dlaSetup(){
	
		
		// create DLA 3D simulation space 128 units wide (cubic)
		  dla = new DLA(12800);
		  
		  //add listener for events, like making points do cool shit
		  dla.addListener(listener);
		  
		// if u want to use default configuration - > dla.setConfig(new DLAConfiguration());
		  
		  
		//add DLA configuration behaviours
		  
		// threshold distance below which a particle attaches itself to an older one
			 dlaConfig.setSnapDistance(0.1f);
		// threshold distance to attach to a curve/guideline particle
			 dlaConfig.setCurveAttachDistance(5f);
			 
			 
		// max. radius of the sphere when the particle's random walk is interrupted/restarted

			 dlaConfig.setEscapeRadius(36f);
			 
			// percentage amount to progress along segments after each new particle
			 dlaConfig.setCurveSpeed(1);
			 
			// actual size of the particles (currently assumed to be uniform for entire system)
			 dlaConfig.setParticleRadius(10f);

			 
			// stickiness factor, only ?% of all attachments will be successful
			 dlaConfig.setStickiness(0.5f);
			 
			// velocity of the random particle wal
			 dlaConfig.setSearchSpeed(70f);
			 
			// turn speed when particle changes direction
			 dlaConfig.setParticleSpeed(2f);
			 
			// max. radius of the sphere in which a new particle is spawned around a previous one
			 dlaConfig.setSpawnRadius(1);
			 
			// progress speed when scanning guidelines to build octree
			dlaConfig.setGuideLineDensity(0.1);
		  
		  
		//CUSTOM CONFIGURATION
		  dla.setConfig(new DLAConfiguration());
		  
		  
		
		  
		  
		
		// set leaf size of octree
		  dla.getParticleOctree().setMinNodeSize(1);
		  
		  //set up DLA configuration class
		  
		
		 
		 
		 
		// dlaConfig.setGrowthScale(new Vec3D(500,500,500));
		 
		 
		 
	}
	
	//DLA RUN FUNCTION
	
	
	//function to turn arraylist of points from an agent into a spline DLA attractor
	
	
	public void attractorFromPoints(ArrayList points){
		
		 // use points to simulate a growth path  as DLA guidelines
		
		DLAGuideLines guides = new DLAGuideLines();
		
		
		 guides.addPointList(points);
		
		
		  // add guide lines
		  dla.setGuidelines(guides);
		  
	}
	
	public void dlaRun(){
		
		//add guides
		//attractorFromPoints(points);
		//update octree
		//dla.update(10000);
		//draw octree
		
	}
	
	
	
	
	
	//render octree of points
	public void drawOctreeNodes(PointOctree node){
		
	
		
		if (points != null) {
			
			
			if (node.getNumChildren() > 0){
				//System.out.println(points);
				PointOctree[] children = node.getChildren();
				for (int i = 0; i < 8; i++) {
				      if (children[i] != null) {
				    	  drawOctreeNodes(children[i]);
				      }
				    }
				//draw points 
				//java.util.List nodePts = node.getPoints();
				
			// parent.strokeWeight(10);
			// parent.stroke(255,0,0);
//				 parent.beginShape(parent.POINTS);
//				 int numP = points.size();
//				 for (int i = 0; i < numP; i += 10) {
//				        Vec3D p = (Vec3D)points.get(i);
//				        parent.vertex(p.x, p.y, p.z);
//				        parent.endShape();
//			      }
				//System.out.println(dla.getNumParticles());
			}
			
			
//			parent.strokeWeight(10);
//		      parent.stroke(255,0,0);
//		     //parent.beginShape(parent.POINTS);
//		      int numP = points.size();
//		      for (int i = 0; i < numP; i += 10) {
//		        Vec3D p = (Vec3D)points.get(i);
//		        parent.vertex(p.x, p.y, p.z);
//		      }
//		       // parent.endShape();
   }
		
		
		  }
		

	
}
