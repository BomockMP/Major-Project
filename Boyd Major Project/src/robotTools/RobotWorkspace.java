package robotTools;

import java.net.UnknownHostException;

import core.Plane3D;
import examples.flocking.FlockingApp;
import pointCloudTools.TransformableScanner;
import pointCloudTools.PointCloud;
import processing.core.PApplet;
import projects.archive.DabAgent;
import projects.archive.TracerAgent;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class RobotWorkspace {
	
	public PApplet parent;
	WETriangleMesh mesh;
	public PointCloud pcl;
	public Robot robot;
	public RobotClient rc;
	public TaskHandler tasks; 
	ServerSimulator sim;
	public float lastTime;

	
	public RobotWorkspace(PApplet _parent, boolean _loop){
		//default home position
		this(_parent, new Vec3D(540.5f,-400.1f,600f),_loop);
	}
	
	public RobotWorkspace(PApplet _parent, Vec3D robotHomePos, boolean _loop){
		parent = _parent;
		pcl = new PointCloud(parent);
		Plane3D initialPlane = new Plane3D(robotHomePos);
	
		try {
			rc = new RobotClient(5000, 5001, parent);
			rc.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		robot = new Robot(initialPlane);
		tasks = new TaskHandler(_loop); 
		//db = new DabAgent(pcl, this, robot);
		initTasks();
		
		sim = new ServerSimulator(robot);
	}
	
	public void initTasks(){
	}
	
	public void addTask(Task task){
		tasks.addTask(task);
	}
	
	public void run(String ip){
		
		//update robot with data from client
		
		if (rc.available()) {
			String[] pos = rc.getPoseData("Position");
			String[] orientation = rc.getPoseData("Orientation");
		    robot.updatePosition(pos);
		    robot.updatePose(orientation);
		    robot.setDelay(parent.millis()-lastTime);
		    lastTime = parent.millis();
		}
	    
	    // TODO task handler should also be a seperate thread. 
	    //hack
		Vec3D tP = robot.targetPos.copy();
		
		tasks.run(); 
		
		if(tP.distanceTo(robot.targetPos)>0){
			System.out.println("moving to: "+ robot.targetPos);
			send(ip); //update rsi
		}
	}
	
	public void simulate(){
		listenSim();
		tasks.run(); 
	}
	
	public void listenSim(){
		sim.step();
	}
	
	public void send(String ip){
		//rc.sendVector(robot.targetPos, "Position", ip);
		//rc.sendPlane3D(robot.targetPos, ip);
		
		/*OLD COMMANDS
		 * 
		String attributes = "X=\""+robot.targetPos.x+"\" Y=\""+robot.targetPos.y+"\" Z=\""+robot.targetPos.z+"\"";
		String data = "<Robot><" + "Position" + " "+attributes + "/>";
		String plane = "XX=\""+robot.targetPos.xx.x+"\" XY=\""+robot.targetPos.xx.y+"\" XZ=\""+robot.targetPos.xx.z+"\" "
				+ "ZX=\""+robot.targetPos.zz.x+"\" ZY=\""+robot.targetPos.zz.y+"\" ZZ=\""+robot.targetPos.zz.z+"\"";
	    data = data+ "<" +  "PoseXZ" + " "+plane + "/></Robot>";
	    
	    */
	    
		String attributes = "<Pose N=\"-1\" " + 
				"Position=\""+robot.targetPos.x +","+ robot.targetPos.y+","+ robot.targetPos.z+
				"\" Orientation=\""+robot.targetPos.xx.x+","+robot.targetPos.xx.y+","+robot.targetPos.xx.z+","+robot.targetPos.zz.x+","+robot.targetPos.zz.y+","+robot.targetPos.zz.z+"\" Velocity=\"0.3\" FinalVelocity=\"0.1\" />";
		String data = "<Robot>" +attributes + "</Robot>";
		rc.sendString(data, ip);
		//rc.sendIO(robot.io, "Gripper", ip);
	}
	
	public PointCloud updatePcl(){
		//float r = 800;
		//AABB box = AABB.fromMinMax(robot.add(-r,-r,-1000), robot.add(r,r,-50));
		//pcl.load(parent.kinect.copyAABB(box));
		//pcl.extractColourRange(253, 253, 253, 10);
		pcl.loadSinWavePts();
		return pcl;
		//mesh = pcl.createDelaunayMesh();
	}
	
	public PointCloud maskScan(int r, int g, int b, int fuzz, Vec3D loc, float rad, TransformableScanner scanner){
		PointCloud tmp = new PointCloud(parent);
		//AABB box = AABB.fromMinMax(robot.add(-rad,-rad,-1000), robot.add(rad,rad,-50));
		AABB box = AABB.fromMinMax(loc.add(new Vec3D(-rad,-rad,-800)), loc.add(new Vec3D(rad,rad,0)));
		parent.noFill();
		parent.stroke(255,0,0);
		tmp.load(scanner.inAABBColours(box));
		tmp.extractRGBRange(r, g, b, fuzz);
		//pcl.loadSinWavePts();
		return tmp;
	}
	
	public PointCloud additiveScan(Vec3D loc, float rad, TransformableScanner scanner){
		pcl.appendHeightField(maskScan(253, 253, 253, 10, loc,rad, scanner));
		return pcl;
	}

	public void loadMesh(String path){
		mesh=(WETriangleMesh)new STLReader().loadBinary(parent.sketchPath(path),STLReader.TRIANGLEMESH);
	}
	
	public void renderMesh(ToxiclibsSupport gfx){
		parent.strokeWeight(1);
		if(mesh!=null)gfx.mesh(mesh);
	}
	
}
