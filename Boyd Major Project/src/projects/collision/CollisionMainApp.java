package projects.collision;

/// LIBRARY IMPORTS ------------------------------------------------------------------------
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import ProGAL.geom3d.volumes.Volume;
import bRigid.*;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import core.Agent;
import core.Canvas;
import core.Environment;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import peasy.PeasyCam;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import voxelTools.VoxelBrush;
import nervoussystem.obj.OBJExport;



public class CollisionMainApp extends PApplet{

	
	/// GLOBAL VARIABLES ------------------------------------------------------------------------
	
		public PeasyCam cam;
		
		//define physics engine:
		public BPhysics physics;
	
		public float gravity = 75f; //70 //around 28 lands pretty straight
		
		public Environment environment;
		
		public Canvas canvas;
		
		//OBJ EXPORT
		public boolean record = false;
	
		//trail spring physics
		public VerletPhysics3D springPhysics;
	
		//collision
		public BSphere sphere;
		//List for wanted collision objects
		public ArrayList<BObject> sphereColliders;
		
		public ArrayList<BObject> program;
		
		public BPlane ground;
		
		public BConvexHull core;
		
		
		
		
		//program array lists//better way to do this
		public ArrayList<Vector3f> programPos;
		public ArrayList<Vector3f>  programScale;
		
		
		 public float height = 100;
		 public float weight = 10f;
		 public float startWeight = 1;
		 public float spacing = 1.3f;
		 public boolean inertia = true;
		
		 public boolean displayImportMesh = false;
		 public String fileName = "reactor_core.obj";
		
		
		public void setup (){
			
			//CAMERA
			size(1280, 720, OPENGL);
			cam =new PeasyCam(this,200);
			frameRate(60);
			
			//spring physics
			
			springPhysics = new VerletPhysics3D(new Vec3D(0,0,0.000f), 50, 0, 1); 
			
			environment = new Environment(this, 2000f);
			
			canvas = new Canvas(this.g);
			
			//create a rigid physics engine with a bounding box
			  physics = new BPhysics();
			  //set gravity
			  physics.world.setGravity(new Vector3f(0, gravity, 0));
			
			//ground plane
			  BPlane ground = new BPlane(new Vector3f(0, 105, 0), new Vector3f(0, -1, 0));
			  physics.addPlane(ground);
			  
			  //arraylist of programatic boxes
			  program = new ArrayList<BObject>();
			  
			  //create the first rigidBody as Box or Sphere
			  //BSphere(PApplet p, float mass, float x, float y, float z, float radius)
			  sphere = new BSphere(this, 0, 0, 0, 0, 1);
			  
			  
			  //generic Boxes
			  for (int i = -10; i <= 10; i++) {
				    for (int j = -10; j <= 10; j++) {
				      BBox box = new BBox(this, 1, new Vector3f(i * 8, 100, j * 8), new Vector3f(6, 10, 6), true);
				     // physics.addBody(box);
				      program.add(box);
				    }
				  }
			  
			  
			  
			//specific boxes //vector position //vector scale
			// programPos = new ArrayList<Vector3f>(); 
			// programScale = new ArrayList<Vector3f>();  
			 
//			  float height = 100;
//			  float weight = 10f;
//			  float startWeight = 0;
//			  float spacing = 1.3f;
//			  boolean inertia = true;
			  
			  BBox box1 = new BBox(this, startWeight, new Vector3f(12.5f*spacing, height, 48.5f*spacing), new Vector3f(7, 10, 7.5f), inertia);
			  BBox box2 = new BBox(this, startWeight, new Vector3f(21f*spacing, height, 48.5f*spacing), new Vector3f(9, 10, 5f), inertia);
			  BBox box3 = new BBox(this, startWeight, new Vector3f(31f*spacing, height, 48.5f*spacing), new Vector3f(7, 10, 7f), inertia);
			  BBox box4 = new BBox(this, startWeight, new Vector3f(38f*spacing, height, 48.5f*spacing), new Vector3f(4.5f, 10, 7f), inertia);
			  BBox box5 = new BBox(this, startWeight, new Vector3f(44f*spacing, height, 48.5f*spacing), new Vector3f(6f, 10, 7f), inertia);
			  BBox box6 = new BBox(this, startWeight, new Vector3f(53f*spacing, height, 48.5f*spacing), new Vector3f(18f, 10, 7f), inertia);
			  BBox box7 = new BBox(this, startWeight, new Vector3f(66f*spacing, height, 48.5f*spacing), new Vector3f(5.5f,10, 11.5f), inertia);
			  BBox box8 = new BBox(this, startWeight, new Vector3f(74f*spacing, height, 48.5f*spacing), new Vector3f(7f, 10, 8.5f), inertia);
			  BBox box9= new BBox(this, startWeight, new Vector3f(12.5f*spacing, height, 58.5f*spacing), new Vector3f(7f, 10, 10.5f), inertia);
			  BBox box10 = new BBox(this, startWeight, new Vector3f(21.5f*spacing, height, 58.5f*spacing), new Vector3f(7f, 10, 8.5f), inertia);
			  BBox box11 = new BBox(this, startWeight, new Vector3f(35f*spacing, height, 58.5f*spacing), new Vector3f(17f, 10, 8.5f), inertia);
			  BBox box12 = new BBox(this, startWeight, new Vector3f(47f*spacing, height, 58.5f*spacing), new Vector3f(10f, 10,18f ), inertia);
			  BBox box13 = new BBox(this, startWeight, new Vector3f(60f*spacing, height, 59f*spacing), new Vector3f(14f, 10, 4f), inertia);
			  
			  BBox box14 = new BBox(this, startWeight, new Vector3f(60f*spacing, height, 66f*spacing), new Vector3f(23f, 10, 10.5f), inertia);
			  
			  BBox box15 = new BBox(this, startWeight, new Vector3f(12.5f*spacing, height, 65f*spacing), new Vector3f(7f, 10, 4f), inertia);
			  BBox box16 = new BBox(this, startWeight, new Vector3f(12.5f*spacing, height, 70f*spacing), new Vector3f(5f, 10, 5f), inertia); 
			  
			  BBox box17 = new BBox(this, startWeight, new Vector3f(21f*spacing, height, 65f*spacing), new Vector3f(7f, 10, 3f), inertia);
			  BBox box18 = new BBox(this, startWeight, new Vector3f(29f*spacing, height, 65f*spacing), new Vector3f(15f, 10, 4f), inertia);
			  
			  BBox box19 = new BBox(this, startWeight, new Vector3f(12.5f*spacing, height, 76f*spacing), new Vector3f(5f, 10, 3f), inertia);
			  BBox box20 = new BBox(this, startWeight, new Vector3f(12.5f*spacing, height, 80f*spacing), new Vector3f(5f, 10, 2.5f), inertia);
			  BBox box21 = new BBox(this, startWeight, new Vector3f(12.5f*spacing, height, 84f*spacing), new Vector3f(5f, 10, 4f), inertia);
			  
			  
			  
//			  program.add(box1);
//			  program.add(box2);
//			  program.add(box3);
//			  program.add(box4);
//			  program.add(box5);
//			  program.add(box6);
//			  program.add(box7);
//			  program.add(box8);
//			  program.add(box9);
//			  program.add(box10);
//			  program.add(box11);
//			  program.add(box12);
//			 program.add(box13);
//			 program.add(box14);
//			 program.add(box15);
//			 program.add(box16);
//			 program.add(box17);
//			 program.add(box18);
//			 program.add(box19);
//			 program.add(box20);
//			 program.add(box21);
			 

//			  core = new BConvexHull(this, 1, fileName, new Vector3f(45,100,85), true, displayImportMesh);
//			 physics.addBody(core);
			 
			 
			  for (BObject b : program){
				  //b.setRotation(b.getPosition(), 0f);
				 // b.setMass(weight);
				  physics.addBody(b);
			  }
			  
//			 for (BObject r : physics.rigidBodies) {
//				 r.setMass(weight);
//			 }
//			  
			  
	
			  
			  //--------------------------------------------------------------------------------
			  //Add snake agent
			  //---------------------------------------------------------------------------------
			
			  for (int i = 0; i < 1; i++ ){
				  SnakeAgent a = new SnakeAgent(new Vec3D(300f,105,200f), false, this, springPhysics);
				  environment.addAgent(a);
				  
			  }
			  
			  
			  
			  sphereColliders = new ArrayList<BObject>(); 
			  
			  
			  
			  
			  
			  
		}
	
	
	
		public void draw () {
			background(255);
			lights();
			
			environment.run();
			environment.update(false);
			

			System.out.println(physics.rigidBodies.size());
			
			
			//this function needs to run more
			for (int k = 0; k<2; k++){
			if (sphereColliders.size() > 0){
				for (int i = 0; i < springPhysics.particles.size(); i++ ) {
					
					BObject r = sphereColliders.get(i);
					VerletParticle3D a = springPhysics.particles.get(i);
					r.setPosition(a.x, a.y, a.z);
					}
			}
			}
			
			
			
			
			
			
			
			  
			  
//rendering boxes
			  if (physics.rigidBodies.size()>1){
				  for (int i = 1;i<physics.rigidBodies.size();i++) {
					  //BConvexHull b = (BConvexHull) physics.rigidBodies.get(i);
					  BObject b = (BObject) physics.rigidBodies.get(i);
					  b.display(50, 50, 50);
					  }
				  }
			  
			  canvas.drawPts(environment.pop, 5);
		//canvas.drawTrails(environment.pop, 5, 255);
			  canvas.drawAgentSprings(environment.pop, 1, 255);
				
			  
			 physics.display(50,50,50); //test 
			 physics.update();
			 springPhysics.update();
			
		}
		
	
		/// GLOBAL FUNCTIONS ------------------------------------------------------------------------
		//export OBJS  
		
		
		void drawBodiesAsPshapes(){
			
			 if (record) {
				    beginRaw("nervoussystem.obj.OBJExport", "exportedRockMesh_"+frameCount+".obj"); 
				  }  
			
			for (int i =1;i<physics.rigidBodies.size();i++) {
				BObject b = (BObject) physics.rigidBodies.get(i);
			    
			
			 //  Vector3f position = b.getPosition();
			   
			   
			 
			Transform transform = new Transform();
			transform = b.rigidBody.getMotionState().getWorldTransform(transform);
			
			Matrix4f out = new Matrix4f();
			out = transform.getMatrix(out);
			
			transform.set(transform);
			   
			b.displayShape.applyMatrix(out.m00, out.m01, out.m02, out.m03, out.m10, out.m11, out.m12, out.m13, out.m20, out.m21, out.m22,
					out.m23, out.m30, out.m31, out.m32, out.m33);
			
			pushMatrix();
			shape(b.displayShape);
			popMatrix();
			
			b.displayShape.resetMatrix();
			
			
		
			}
			
			 if (record) {
				    endRaw();
				    record = false;
				  }
			
		}
	
	
		//---------------------------------------------------------------------------------
		//Key Functions
		//---------------------------------------------------------------------------------
			  
			  
			  public void keyPressed(){

					
					if(key=='o'){
						
						record = true;
						drawBodiesAsPshapes();
						
					}
					
					
					if(key=='p'){
						
						
						
						if (springPhysics.particles.size() > 0){

							for (VerletParticle3D a : springPhysics.particles){
								 Vector3f pos = new Vector3f(a.x, a.y, a.z);
								 BObject r = new BObject(this, 1f, sphere, pos, true);
								
								// r.setPosition(pos);
								 physics.addBody(r);
								 sphereColliders.add(r);
							}
							}
						
						
					}
					
			
					
					if(key=='h'){
						saveFrame("grab.png");
					}
					
					
			  }
			  
	
	
}
