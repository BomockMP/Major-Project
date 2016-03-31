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
import projects.Purnululu.AnchorAgent;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletPhysics3D;

public class MainApp extends PApplet {

	PeasyCam cam;
	public Canvas canvas;
	public int particleCount = 20;
	public VerletPhysics3D springPhysics;
	public SpringManager springManager;
	
	public void setup() {
		
		
		// CAMERA
		size(1280, 720, OPENGL);
		cam = new PeasyCam(this, 200);
		noLights();
		
		
		//canvas
		canvas = new Canvas(this.g);
		
		//trail spring p hysics
		springPhysics = new VerletPhysics3D(new Vec3D(0,0,0f), 100, 0, 1); 
		
		springManager = new SpringManager(springPhysics, 10000);
		
		
		for (int i = 0; i < particleCount; i++) {
				float spawnptX = random(-100,100);
				float spawnptY = random(-100,100);
				float spawnptZ = 0;
				
				
			System.out.println(spawnptX);
			
			ParticleAgent a  = new ParticleAgent(spawnptX, spawnptY, spawnptZ);
			springPhysics.addParticle(a);
			springManager.addAgent(a);

		}
		
		
		
		
		
		
	}
	
	public void draw() {
		
		
	//System.out.println(springPhysics.particles.size());	
		
	background(100);
	lights();
	
	canvas.drawParticles(springPhysics.particles, 1f);
	canvas.drawSpringPhysics(springPhysics.springs, 1f, 250f);
	
	
	
	springManager.update();
	springManager.run();	
	
	springPhysics.update();
	
	
	
	}
	
	
	//GLOBAL FUNCTIONS
	
	
}
