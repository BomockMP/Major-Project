package projects.distribution;

/*
 * MainApp for managing evenly spaced distribution of particles.
 *
 * 
 * 
 * 
 */




import core.Canvas;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PImage;
import projects.Purnululu.AnchorAgent;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import voxelTools.VoxelGrid;

public class MainApp extends PApplet {

	PeasyCam cam;
	public Canvas canvas;
	public int particleCount = 60;
	public VerletPhysics3D springPhysics;
	public SpringManager springManager;
	
	VoxelGrid voxels;
	public static int dimX = 200;
	public static int dimY = 200;
	public static int dimZ = 1;
	public static Vec3D scale = new Vec3D(1,1,1);
	PImage terrain;
	
	
	public void setup() {
		
		
		// CAMERA
		size(1280, 720, OPENGL);
		cam = new PeasyCam(this, 200);
		noLights();
		
		
		//canvas
		canvas = new Canvas(this.g);
		
		//load image		
		terrain = loadImage("hm2.png");
		
		//voxels
		voxels = new VoxelGrid(dimX, dimY, dimZ, scale);
		//call create terrain function
		voxels.createTerrain(terrain);
		//voxels.initGrid();
		
		//trail spring p hysics
		springPhysics = new VerletPhysics3D(new Vec3D(0,0,0.004f), 100, 0, 1); 
		
		springManager = new SpringManager(springPhysics, 10000, this);
		
		
		for (int i = 0; i < particleCount; i++) {
				float spawnptX = random(0,200);
				float spawnptY = random(0,200);
				float spawnptZ = random(0,200);
				
				
			System.out.println(spawnptX);
			
			ParticleAgent a  = new ParticleAgent(spawnptX, spawnptY, 0, voxels);
			springPhysics.addParticle(a);
			springManager.addAgent(a);

		}
		
		
		
		
		
		
	}
	
	public void draw() {
		
		
	//System.out.println(springPhysics.particles.size());	
		
	background(100);
	lights();
	voxels.render(3, 50, 1, this);
	
	canvas.drawParticles(springPhysics.particles, 1f);
	canvas.drawSpringPhysics(springPhysics.springs, 1f, 250f);
	
	
	
	springManager.update();
	springManager.run();	
	
	springPhysics.update();
	
	System.out.println(springPhysics.springs.size());
	
	}
	
	
	//GLOBAL FUNCTIONS
	
	// ---------------------------------------------------------------------------------
	// Global Functions
	// ---------------------------------------------------------------------------------
	public void keyPressed() {
		if (key == 's') {
			
			springManager.saveSpringParticles(frameCount);
		}
		
		
		if (key == 'v'){
			voxels.save("PheremoneVoxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
		}
		
		if (key == 'l'){
			for (int i = 0; i < springPhysics.particles.size(); i++){
			VerletParticle3D p =  springPhysics.particles.get(i);
			p.lock();
			}
		}
			
			if (key == 'u'){
				for (int i = 0; i < springPhysics.particles.size(); i++){
				VerletParticle3D p =  springPhysics.particles.get(i);
				p.unlock();
				}
		}
		
	}
}
