/*package examples.robotics;

import core.Canvas;
import core.IO;
import core.Plane3D;
import peasy.PeasyCam;
import pointCloudTools.KinectScanner;
import pointCloudTools.PointCloud;
import pointCloudTools.StructureScanner;
//import pointCloudTools.StructureScanner;
import processing.core.PApplet;
import robotTools.GoToPlane;
import robotTools.RobotWorkspace;
import toxi.geom.Vec3D;

public class RobotVisionApp extends PApplet{
	static { 
		try { 
			System.load("C:\\Program Files\\OpenNI2\\Samples\\Bin\\OpenNI2.dll"); 
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
		} 
	} 
	
	PeasyCam cam;
	Canvas canvas;
	RobotWorkspace rs;
	StructureScanner structure;
	KinectScanner kinect;
	public int ctr = 0;
	
	public void setup(){
		size(500,500,OPENGL);
		cam =new PeasyCam(this,400);
		canvas = new Canvas(this.g);
		//-------------------------------------VISION
		
		kinect = new KinectScanner(this);
		structure = new StructureScanner(this);

		//-------------------------------------ROBOT CONTROL
		
		rs = new RobotWorkspace(this);
		rs.addTask(new GoToPlane(rs.robot.homePos.add(new Vec3D(100,0,0)), rs.robot));
		//Plane3D pose = new Plane3D(rs.robot.homePos.add(new Vec3D(-100,0,0)), new Vec3D(1,0,0), new Vec3D(0,1.2f,0.1f));
		//rs.addTask(new GoToPlane(pose, rs.robot));
		rs.addTask(new GoToPlane(rs.robot.homePos.add(new Vec3D(-100,0,0)), rs.robot));
		rs.addTask(new GoToPlane(rs.robot.homePos.add(new Vec3D(-100,-100,0)), rs.robot));
		rs.addTask(new GoToPlane(rs.robot.homePos.add(new Vec3D(-100,100,0)), rs.robot));
	}
	
	
	public void draw(){
		background(0);
		box(40);
		structure.move(rs.robot);
		//structure.rotate(rs.robot.abc);
		structure.renderDepth();
		//kinect.renderColours();
		rs.run("10.220.244.191");
		canvas.drawPlane(rs.robot, 20,2);
		canvas.drawPlane(rs.robot.targetPos, 10,2);
	//	rs.additiveScan(rs.robot, 300, structure);
		//rs.pcl.renderHeightField();
	}
	

	public void keyPressed(){
		if(key=='s'){
			if(rs.robot.getDelay()<15){
				IO.savePts(structure.getPts(), "pts"+ctr+".txt");
				ctr++;
			}
			
		}
		
	}
}
*/