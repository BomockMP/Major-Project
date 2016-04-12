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
import toxi.geom.AABB;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.physics2d.behaviors.AttractionBehavior2D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.behaviors.AttractionBehavior3D;
import voxelTools.VoxelGrid;

public class MainApp extends PApplet {

	PeasyCam cam;
	public Canvas canvas;
	public int particleCount = 110;
	public VerletPhysics3D springPhysics;
	public SpringManager springManager;
	
	VoxelGrid voxels;
	public static int dimX = 400;
	public static int dimY = 400;
	public static int dimZ = 1;
	public static Vec3D scale = new Vec3D(1,1,1);
	PImage terrain;
	
	public int drawRad;
	
	
	public void setup() {
		
		
		// CAMERA
		size(1280, 720, OPENGL);
		cam = new PeasyCam(this, 500);
		noLights();
		
		
		
		
		//canvas
		canvas = new Canvas(this.g);
		
		//load image		
		terrain = loadImage("hm7.png");
		
		//voxels
		voxels = new VoxelGrid(dimX, dimY, dimZ, scale);
		//call create terrain function
		voxels.createTerrain(terrain);
		//voxels.initGrid();
		
		//trail spring p hysics
		springPhysics = new VerletPhysics3D(new Vec3D(0,0,0.0f), 200, 0.1f, 1); //150 
		
		//bounds from centre point to extents. 0 in Z direction.
		springPhysics.setWorldBounds(new AABB(new Vec3D(dimX/2, dimY/2, 0), new Vec3D((dimX/2f)*0.9f,(dimY/2f)*0.9f,0)));
		
		springManager = new SpringManager(springPhysics, 10000, this);
		
		
		
		
		
		
		
		
		
	}
	
	public void draw() {
		
		background(255);	
		//fill(255, 100);
		//rect(-width, -height, width*2, height*2);
	//System.out.println(springPhysics.springs.size());	
		
	
	lights();
	
	if (frameCount%1==0){
	if (springPhysics.particles.size() < particleCount){
		
	//	for (int i = 0; i < particleCount; i++) {
			float spawnptX = random(50,dimX*0.77f);
			float spawnptY = random(180,dimY*0.80f);
			float spawnptZ = random(0,200);
			
			
		
		
		ParticleAgent a  = new ParticleAgent(spawnptX, spawnptY, 0, voxels);
		springPhysics.addParticle(a);
		springManager.addAgent(a);
		//AttractionBehavior(toxi.geom.Vec3D attractor, float radius, float strength, float jitter) 
		//springPhysics.addBehavior(new AttractionBehavior3D(a, 30, -0.03f)); //0.005
	//springPhysics.addBehavior(new AttractionBehavior3D(a, 20, -5f, 0.001f));
//	}	
		}
		}
	
	
	
	
	
	
	
	
	voxels.render(1, 50, 1, this);
	//
	canvas.drawParticles(springPhysics.particles, 1f);
	canvas.drawSpringPhysics(springPhysics.springs, 1f, 250f);
	canvas.drawParticleElipse(springPhysics.particles, drawRad);
	
	
	springManager.update();
	springManager.run();	
	
	springPhysics.update();
	

	
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
			
			if (key == 'k'){
				for (int i = 0; i < springPhysics.particles.size(); i++){
				ParticleAgent p =  (ParticleAgent) springPhysics.particles.get(i);
				p.removeIfOverEmptyVoxels(springManager);
				}
		}
			
			if (key == 'q'){
				for (int i = 0; i < springPhysics.particles.size(); i++){
				ParticleAgent p =  (ParticleAgent) springPhysics.particles.get(i);
				p.LockIfOverPaintedVoxels(springManager);
				springPhysics.addBehavior(new AttractionBehavior3D(p, 20, -0.1f)); //0.005
				}
		}
		
	}
}
