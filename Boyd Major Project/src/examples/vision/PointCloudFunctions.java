package examples.vision;

import peasy.PeasyCam;
import pointCloudTools.KinectScanner;
import pointCloudTools.PointCloud;
import processing.core.PApplet;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

public class PointCloudFunctions extends PApplet{
	PeasyCam cam;
	KinectScanner kinect;
	PointCloud points;
	
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);	
		kinect = new KinectScanner(this);
		points = new PointCloud(this);
	}
	
	public void draw(){
		background(0);
		box(40);
		kinect.renderColours();
		//points.renderColours(); this will draw the point cloud - only will show up after pushing l
	}
	
	public void keyPressed(){
		if (key == 'l'){
			AABB box = new AABB(new Vec3D(),500); //box to contain points - could centre on an agent or whatever
			float[][] data = kinect.inAABBColours(box); //gets the points in the box as floats
			points.load(data); //loads these into the point cloud
			points.extractRGBRange(255,255,255,10); //deletes all points that are more than 10 brightness from white
			points.buildTree(); //updates the octree of the point cloud so you can use these points for search
			points.createBuffers(false); //creates the vertex buffer objects to render the pointcloud in openGL if you want to
		}
	}
	
}
