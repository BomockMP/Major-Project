package core;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import processing.core.PApplet;
import robotTools.Robot;
import toxi.geom.Vec3D;
import voxelTools.Cell;
import voxelTools.VoxelGrid;

public class IO {

	public static void savePts(ArrayList<Vec3D>pts, String name){
		PrintWriter out;
		try {
			out = new PrintWriter(name);
			for (Vec3D l:pts) {
				out.println(""+l.x +"," + l.y + "," + l.z);

			}
			out.close();
			System.out.println("saved to "+name);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public void saveRaw(VoxelGrid v, String fn) {
		  int c=0;
		  int tot = v.vals.length;
		  try {
		    BufferedOutputStream ds = new BufferedOutputStream(new FileOutputStream(fn));
		    // ds.writeInt(volumeData.length);
		    for (Cell e:v.vals) {
		    	if(!e.edge){
		    		ds.write((int) e.get());
		    	}else{
		    		ds.write((int) 0);
		    	}
		    }
		    ds.flush();
		    ds.close();
		  } 
		  catch (IOException e) {
		    e.printStackTrace();
		  }
		}
	
	public static void saveRobotScan(Robot robot, ArrayList<Vec3D>pts, String name){
		PrintWriter out;
		try {
			out = new PrintWriter(name);
			out.println(""+robot.x +"," + robot.y+ "," + robot.z); //robot location
			out.println(""+robot.xx.x +"," + robot.xx.y+ "," + robot.xx.z); //robot xx
			out.println(""+robot.yy.x +"," + robot.yy.y+ "," + robot.yy.z); //robot yy
			for (Vec3D l:pts) {
				out.println(""+l.x +"," + l.y + "," + l.z);

			}
			out.close();
			System.out.println("saved to points.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public static void saveTrails(ArrayList<Agent>agents, String filename){
		PrintWriter out;
		try {
			out = new PrintWriter(filename);
			for(Agent a:agents){
				String s = "";
				for (Link l:a.trail) {
					s+=l.a.x +"," + l.a.y + "," + l.a.z + "/";
				}
				out.println(s);
			}
			out.close();
			System.out.println("saved to points.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void saveTrailList(ArrayList<ArrayList<Link>>trails, String filename){
		PrintWriter out;
		try {
			out = new PrintWriter(filename);
			for(ArrayList<Link> links:trails){
				String s = "";
				for (Link l:links) {
					if(l.getVal()==1){
							s = s+l.a.x +"," + l.a.y + "," + l.a.z +"+" 
						        + l.a.xx.x +"," + (l.a.xx.y) + "," + (l.a.xx.z)+"+"+
									(l.a.yy.x) +"," + (l.a.yy.y) + "," + (l.a.yy.z)+"/";
					//	s+=l.a.x +"," + l.a.y + "," + l.a.z + "/";
					}
				}
				out.println(s);
			}
			out.close();
			System.out.println("saved to points.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static ArrayList<Agent> importPlanes(PApplet parent,String name, boolean fixed){
		ArrayList<Agent> out=new ArrayList<Agent>();
		String[] txtLines = parent.loadStrings(name);
		for (int i = 0; i < txtLines.length; i++) {

			String[] pts = parent.split(txtLines[i], '/');     
			String[] sPt = parent.split(pts[0], ',');
			Vec3D o = new Vec3D(Float.valueOf(sPt[0]), Float.valueOf(sPt[1]), Float.valueOf(sPt[2]));
			String[] ePt = parent.split(pts[1], ',');
			Vec3D b = new Vec3D(Float.valueOf(ePt[0]), Float.valueOf(ePt[1]), Float.valueOf(ePt[2])).normalize(); 
			String[] nPt = parent.split(pts[2], ',');
			Vec3D n = new Vec3D(Float.valueOf(nPt[0]), Float.valueOf(nPt[1]), Float.valueOf(nPt[2])).normalize();
			Agent a = new Agent(new Plane3D(o,b,n),fixed);
			out.add(a);
		}
		return out;
	}
	
	public static ArrayList<Vec3D> importPoints(PApplet parent,String name){
		ArrayList<Vec3D> out=new ArrayList<Vec3D>();
		String[] txtLines = parent.loadStrings(name);
		for (int i = 0; i < txtLines.length; i++) {   
			String[] sPt = parent.split(txtLines[i], ',');
			Vec3D o = new Vec3D(Float.valueOf(sPt[0]), Float.valueOf(sPt[1]), Float.valueOf(sPt[2]));
			out.add(o);
		}
		return out;
	}
	
	public static ArrayList<Plane3D> importPlanes(PApplet parent,String name){
		ArrayList<Plane3D> out=new ArrayList<Plane3D>();
		String[] txtLines = parent.loadStrings(name);
		for (int i = 0; i < txtLines.length; i++) {

			String[] pts = parent.split(txtLines[i], '/');     
			String[] sPt = parent.split(pts[0], ',');
			Vec3D o = new Vec3D(Float.valueOf(sPt[0]), Float.valueOf(sPt[1]), Float.valueOf(sPt[2]));
			String[] ePt = parent.split(pts[1], ',');
			Vec3D b = new Vec3D(Float.valueOf(ePt[0]), Float.valueOf(ePt[1]), Float.valueOf(ePt[2])).normalize(); 
			String[] nPt = parent.split(pts[2], ',');
			Vec3D n = new Vec3D(Float.valueOf(nPt[0]), Float.valueOf(nPt[1]), Float.valueOf(nPt[2])).normalize();
			Plane3D a = new Plane3D(o,b,n);
			out.add(a);
		}
		return out;
	}

	
	public static ArrayList<Agent> importLinkedPlanes(PApplet parent, String name){
		ArrayList<Agent> out=new ArrayList<Agent>();
		String[] txtLines = parent.loadStrings(name);
		
		for (int i = 0; i <txtLines.length; i+=1) {

			
			String[] txtPlanes = parent.split(txtLines[i], '|');  //get list of planes 
			String[] fixedFirst = parent.split(txtPlanes[0], '&');  //get whether first is fixed
			
			boolean fixed = (Float.valueOf(fixedFirst[1])>0)?false:true;
			
			String[] sVecs = parent.split(fixedFirst[0], '/'); //get vectors of first plane
			
			String[] sPt = parent.split(sVecs[0], ','); //get first vector
			Vec3D o = new Vec3D(Float.valueOf(sPt[0]), Float.valueOf(sPt[1]), Float.valueOf(sPt[2]));
			String[] sXX = parent.split(sVecs[1], ',');
			Vec3D x = new Vec3D(Float.valueOf(sXX[0]), Float.valueOf(sXX[1]), Float.valueOf(sXX[2])).normalize(); 
			String[] sYY = parent.split(sVecs[2], ',');
			Vec3D y = new Vec3D(Float.valueOf(sYY[0]), Float.valueOf(sYY[1]), Float.valueOf(sYY[2])).normalize();
			
			Agent newPt = new Agent(o,x,y,fixed);
			
			//newPt.deleteTrail();
			for (int j = 1; j<txtPlanes.length;j++){ 
				String[] fixedTmp = parent.split(txtPlanes[j], '&');  //get whether current
				boolean f = (Float.valueOf(fixedTmp[1])>0)?false:true;
				String[] tVecs = parent.split(fixedTmp[0], '/'); //get vectors of first plane
				String[] tPt = parent.split(tVecs[0], ','); //get first vector
				Vec3D newOrigin = new Vec3D(Float.valueOf(tPt[0]), Float.valueOf(tPt[1]), Float.valueOf(tPt[2]));
				String[] tXX = parent.split(tVecs[1], ',');
				Vec3D newXX = new Vec3D(Float.valueOf(tXX[0]), Float.valueOf(tXX[1]), Float.valueOf(tXX[2])).normalize(); 
				String[] tYY = parent.split(tVecs[2], ',');
				Vec3D newYY = new Vec3D(Float.valueOf(tYY[0]), Float.valueOf(tYY[1]), Float.valueOf(tYY[2])).normalize();
				Plane3D plane = new Plane3D(newOrigin,newXX,newYY);
				if(f)plane.lock();
				newPt.addToTrail(plane);
			}
			
			//set link angles
		//	newPt.initLinkAngles();
			out.add(newPt);

		}
		return out;

	}
}
