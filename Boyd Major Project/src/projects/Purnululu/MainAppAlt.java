package projects.Purnululu;



//-----------------------------------------------------------------------------------
/// LIBRARY IMPORTS ------------------------------------------------------------------------
//-----------------------------------------------------------------------------------

import java.security.PublicKey;
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
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;





@SuppressWarnings("serial")
public class MainAppAlt extends PApplet {

	// --------------------------------------------------------------------------------
	/// GLOBAL VARIABLES
	// -----------------------------------------------------------------------------------
	
	PeasyCam cam;
	
	VoxelGrid voxels;
	public static int dimX = 150;
	public static int dimY = 150;
	public static int dimZ = 20; //75
	public static Vec3D scale = new Vec3D(1,1,1);
	GradientVoxels gradientVoxels;

	
	public Environment environment;
	public Canvas canvas;
	
	public int anchorAgentCount = 1;
	public int windAgentCount = 3;
	//boundbox 
	public int bounds = dimX*(int)scale.y;
	public int boundScale = (int) (bounds + (0.1f*bounds));
	public  int minBoundsScale = (int)-0.1f*bounds;
	
	PImage terrain;
	public boolean releaseAgents = false;
	

	//springstuffAnchorTest
	//trail spring physics
	public VerletPhysics3D springPhysics;
	
	// --------------------------------------------------------------------------------
	/// SETUP
	// -----------------------------------------------------------------------------------

	public void setup() {
	
		
		//peasey
		// CAMERA
		size(1280, 720, OPENGL);
		cam = new PeasyCam(this, 200);
		noLights();
	
		
		//springstuffAnchorTest
		//trail spring p hysics
		springPhysics = new VerletPhysics3D(new Vec3D(0,0,0f), 50, 0, 1); 
		
		//load image		
		terrain = loadImage("PHM1.png");
		
		//voxels
		voxels = new VoxelGrid(dimX, dimY, dimZ, scale);
		//call create terrain function
		voxels.createTerrain(terrain);
		//gradient terrain from dark at bottom to light at top
		gradientVoxels = new GradientVoxels(voxels, true);
		gradientVoxels.run();
	
		//environment
		environment = new Environment(this, 2000f);
		//canvas
		canvas = new Canvas(this.g);


		
		for (int i = 0; i < anchorAgentCount; i++) {
		//float spawnptX = random((float)-boundScale, 0);
			float spawnptY = random(5,140);
			float spawnptZ = random(10,10);
		//System.out.println(spawnpt);
		AnchorAgent b = new AnchorAgent(new Vec3D(-50, spawnptY, spawnptZ), false, voxels, springPhysics, this);

		environment.pop.add(b);
	}
		
	
		
		
	}
	
	

	
	
	// ---------------------------------------------------------------------------------
	// Draw
	// ---------------------------------------------------------------------------------

	public void draw() {
		
		background(100);
		lights();
		

		
		
		//add wind
		
		for (int i = 0; i < windAgentCount; i++) {
			if (springPhysics.springs.size() > 0){
				if (environment.pop.size() < 1500){
			Agent b = environment.pop.get(0);	
			Vec3D startPosition = b.abs();
			VerletParticle3D p = springPhysics.particles.get(0);
			VerletParticle3D p1 = springPhysics.particles.get(1);
			float heading = (p.x-p1.x);
			if (heading > 0){
//			WindAgent a = new WindAgent(startPosition, false, voxels, this, true);
//			environment.pop.add(a);
//			} else {
//				WindAgent a = new WindAgent(startPosition, false, voxels, this, false);
//				environment.pop.add(a);
			}
			}
			}
		}
			
		for (Agent a : environment.pop){
			if (a.x > 200 || a.x < - 60 || a.y > 200 || a.y < -50 || a.z > 50 || a.z < -50){
				
				if (springPhysics.springs.size() > 0){
					
					//VerletParticle3D p = springPhysics.particles.get(0);
					
					Agent b = environment.pop.get(0);	
					Vec3D startPosition = b.abs();
					VerletParticle3D p = springPhysics.particles.get(0);
					VerletParticle3D p1 = springPhysics.particles.get(1);
					//float heading = (p.x-p1.x);
					
					Vec3D dir = p1.sub(p);
					
			
						a.set(startPosition);
						a.addForce(dir.scaleSelf(10));
					
					}
			}
			
		}
		
		
		
		
		environment.run();
		environment.update(false); //this is needed for neighbours to work
		
		tightenSprings(springPhysics);
		springPhysics.update();
		


		
		canvas.drawPts(environment.pop, 1);
		canvas.drawAgentSprings(environment.pop, 1, 255); //test
	}
	
	
	
	
	// ---------------------------------------------------------------------------------
	// Global Functions
	// ---------------------------------------------------------------------------------
	public void tightenSprings(VerletPhysics3D springPhysics){
		if (springPhysics.springs.size() > 0){
		for (VerletSpring3D s :springPhysics.springs){
			if (frameCount%100==0){
				
				//s.setStrength((s.getStrength()*0.9f)); 
			s.setRestLength(s.getRestLength()*0.95f);
			
			System.out.println(s.getRestLength());
			}
		}
		}
	}
	
	
	public void keyPressed() {
		if (key == 's') {
			
			//render every 2nd voxel
			voxels.render(3, 1, 1, this);
		}
		
		
		if (key == 'v'){
			voxels.save("ErosionVoxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
		}
		
		
if (key == 'r') {
	
	}
	

	}
	
}


