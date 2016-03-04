package examples.physics;

import peasy.PeasyCam;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;
import controlP5.ControlP5;
import core.Canvas;
import core.Environment;
import core.IO;

public class FlockingBundlingApp extends PApplet {
	// ---------------------------------------------------------------------------EXTERNAL LIBS
			PeasyCam cam;
			ControlP5 controlP5;

			// ---------------------------------------------------------------------------SLOWROBOTICS
			public Environment environment; //store and update agents
			public VoxelGrid voxels; //voxel object
			public VoxelBrush vbrush; //for manipulating voxels
			public Canvas canvas;
			// ----------------------------------------------------------------------------------SETUP Test

			public void setup(){
				size(800,600,OPENGL);
				cam =new PeasyCam(this,400);	
				setupP5();
				
				canvas = new Canvas(this.g); //drawing class 
				
				//create an environment for agents / voxels
				environment = new Environment(this, 2000);

				//add some agents

				for(int i=0;i<50;i++){
					DemoPhysicsAgent a = new DemoPhysicsAgent(new Vec3D(random(20),random(20),0),false);
					a.addForce(new Vec3D(0,0,10));
					environment.addAgent(a);
				}

			}

			public void draw(){
				background(0);
				
				environment.run(); //e.g. run agent pop
				environment.update(true);
				environment.bundle(5,0.1f); //do the bundling
				canvas.drawTrails(environment.pop, 2,255);
				gui(); //draws control p5 sliders as heads up display
			}

			/*------------------------------------

			Global functions 

			------------------------------------*/

			public void keyPressed(){
				if(key=='s'){
					IO.saveTrails(environment.pop, "points.txt");		
				}

			}

			void reset(){
				environment = new Environment(this, 2000);

			}

			/*------------------------------------

			ControlP5

			------------------------------------*/

			public void setupP5(){
				controlP5 = new ControlP5(this);
				controlP5.setAutoDraw(false);
			}


			void gui() {
				hint(DISABLE_DEPTH_TEST);
				cam.beginHUD();
				text("frameRate: "+frameRate, 10, 20);
				controlP5.draw();
				cam.endHUD();
				hint(ENABLE_DEPTH_TEST);
			}
}
