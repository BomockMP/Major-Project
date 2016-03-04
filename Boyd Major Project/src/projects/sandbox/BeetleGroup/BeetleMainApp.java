// Boyd, James, Thanh

package projects.sandbox.BeetleGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.print.attribute.standard.OutputDeviceAssigned;

import core.Agent;
import core.Canvas;
import core.Environment;
import peasy.PeasyCam;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;
import toxi.sim.dla.*;

@SuppressWarnings("serial")
public class BeetleMainApp extends PApplet{
	
	public PeasyCam cam;
	public BeetleVoxelSet voxels;
	public PlumeVoxelSet voxelsPlume;
	//public BeetleAgent bAgent;
	public VoxelBrush vBrush;
	public BeetleEnvironment environment;
	public Canvas canvas;
	
	public int voxSize = 30;
	public int numX = voxSize;
	public int numY = voxSize;
	public int numZ = voxSize;
	public int voxScale = 10; //1 for now...
	
	public int releaseTimer; //agent release timer
	public int agentCount = 2; //agent max population
	
	//DLA
	public FungiDLA fungiDLA;
	public ArrayList<Vec3D> trailPoints;
	public boolean runDLA = true;
	public List<Vec3D> nodePts;
	
	public void setup (){
		size(800, 600, OPENGL);
		
		cam =new PeasyCam(this,400);	
		
// GWYLL RIP___________________________________________________________________________________________________
		
		environment = new BeetleEnvironment(this, 10000);
		
		stroke(150, 0, 0);
		voxels = new BeetleVoxelSet(numX,numY,numZ, new Vec3D(voxScale,voxScale,voxScale));
		
		canvas = new Canvas(this.g);
		
		vBrush = new VoxelBrush(voxels, 1000.01f);
		
		
		//DLA
		trailPoints = new ArrayList<Vec3D>();
		
		
	  
		

		
//JAMES WORKING 1___________________________________________________________________________________________

		//		voxels = new BeetleVoxelSet(numX,numY,numZ, new Vec3D(voxScale,voxScale,voxScale));
//		
//		//voxelsPlume = new PlumeVoxelSet(100,numY,numZ, new Vec3D(10,10,10));
//		
//		environment = new Environment(this, 10000);
//		
//		canvas = new Canvas(this.g);
//		
//		vBrush = new VoxelBrush(voxels, .1f);
//		
//		
//		//Beetle Agent Setup
//				for(int i=0; i<5; i++){
//					BeetleAgent a = new BeetleAgent(Vec3D.randomVector().scale(100), false, voxels, vBrush);
//					environment.addAgent(a);
//				}
	//	___________________________________________________________________________________________
	
		
		
	}

	public void draw () {
		background(0);
		//sphere(10);
		
		
		
		
		voxels.render(1, 0, 255, this);
		
		
		//AGENTS ARE NOW ADDED WITHIN DRAW LOOP, BASED ON TIMER. MAKE THE INT LESS IF YOU WANT THEM TO RELEASE FASTER.
		
if (millis() - releaseTimer >= 200 && environment.pop.size() < agentCount)  {
		
			BeetleAgent a = new BeetleAgent(Vec3D.randomVector().scale(500), false, voxels, vBrush, 150);
			
			environment.addBeetleAgent (a);
			
			releaseTimer = millis();
		}


//LIFE COUNTER. KIND OF DUMB AT THE MOMENT.
for (int i=0; i<environment.pop.size(); i++){
	BeetleAgent a = environment.pop.get(i);
	
	

	if (a.age > 1500) { 
		
		//add contents of agents arraylist dlaTrail to arraylist trailPoints, when the agent is killed
		
		
		trailPoints.addAll(a.dlaPoints);
		
		
		//System.out.println(trailPoints);
		
		
		environment.pop.remove(a); 
		}
	}
	

//DLA
//This runs the DLA setup once [because of the runDLA boolean], when there is a long enough list of particles from
//which to form the DLA attractor line

if (trailPoints.size() >= 5 && runDLA){

	//TODO reverse trailpoints arrray
	Collections.reverse(trailPoints);
	
	//set up the DLA based on the trailpoint list of attractors
	fungiDLA = new FungiDLA(trailPoints);
	
	fungiDLA.dlaSetup();
	
	fungiDLA.attractorFromPoints(trailPoints);
	
	
	
	//to make sure this only runs once
	runDLA = false;
	
}


//fungiDLA.dlaRun();

environment.run();

canvas.drawPts(environment.pop, 5);


//DLA. TURNED OFF ATM
if (runDLA != true){
	//System.out.println(fungiDLA.points);
	
	float eR = fungiDLA.dlaConfig.getEscapeRadius();
	
	//System.out.println(eR);
	
	
	//run the simulation
	fungiDLA.drawOctreeNodes(fungiDLA.dla.getParticleOctree());
	fungiDLA.dla.update(10000);
	
	
	
	//draw the simulation
	nodePts = fungiDLA.dla.getParticleOctree().getPoints();
	
	int numP = nodePts.size();
	
	strokeWeight(1);
	beginShape(POINTS);
	for (int i = 0; i < numP; i += 10) {
		
		
       Vec3D p = (Vec3D)nodePts.get(i);
        vertex(p.x, p.y, p.z);
      }
	 endShape();
}







//
//JAMES WORKING 1___________________________________________________________________________________________
//		environment.run();
//		
//		//voxelsPlume.render(2, 0, 255, this);
//		voxels.render(2, 0, 255, this);
//
//		//voxelsReverse.render(2, 0, 255, this);
//		
//		//bAgent.run(environment);
//		canvas.drawPts(environment.pop, 5);
		
		
	}
	
	//////////////////////GLOBAL FUNCTIONS //////////////////////////////////////////
	 
	public void keyPressed(){
		if(key=='e'){
			//environment.saveTrails();
			voxels.save("voxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
		}
		if(key=='s'){
			saveFrame("grab.png");
		}
	}
	
	
	
	
	
	
}
