package projects.sandbox.BeetleGroup;

import java.util.ArrayList;

import javax.print.attribute.standard.OutputDeviceAssigned;

import org.omg.CORBA.portable.OutputStream;

import processing.core.PApplet;
import projects.archive.VolumeAgent;
import core.Environment;
import core.Link;
import core.Plane3D;
import toxi.geom.Vec3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;

public class BeetleAgent extends VolumeAgent{
	
////////////////////////CLASS GLOBAL VARIABLES
	
	VoxelGrid volume;
	PApplet parent;
	
	
	//Vec for Agents to be attracted to, placed within the voxel grid
	Vec3D dest = new Vec3D();
	float randomZ;
	
	//point list for DLA
	ArrayList<Vec3D> dlaPoints;

	public BeetleAgent(Vec3D _o, boolean _f, VoxelGrid _v, VoxelBrush _br, float _randomZ) {
		super(_o, _f, _v, _br);
		
		//BOYDEDIT
		_v = volume;
		_randomZ = randomZ;
		
	
		//trail of points for DLA
		dlaPoints = new ArrayList<Vec3D>();
		
	}

	@Override
	public void run(Environment environment){
		//GENERAL RANDOM WALKER FORCE
		addForce(Vec3D.randomVector().scale(25));
		//ATTRACTION FORCE TO A POINT 
		beetleAttraction(randomZ, parent, dest);
		
		update();
		//TRAIL FOR VOXEL BRUSH
		if(age%1==0)addToTrail(this);
		volBrushTrail(0,0,25);
		
		
		//POINT TRAIL FOR DLA SPAWN PATH
		Vec3D startDLAtrail = new Vec3D(150,150,150);
		Vec3D distance = startDLAtrail.subSelf(this);
		float d = distance.magnitude();
		//System.out.println(d);
		
		
		//FOR LOOP TO ADD A POINT TO TRAIL EVERY FEW SECONDS
		for (int i = 0; i < 120; i++){	
			if (i  == 0){
		if(d > 100 && d < 400 )addToDLAtrail(this.copy());
			}
		}
	}
	

	
	public void render(PApplet parent){
		parent.stroke(255);
		
		}
	
	
	
	public void beetleAttraction(Float randomZ, PApplet parent, Vec3D dest) {
		
		//function to attract beetle agents to point within voxel grid. ignore randomZ for now. 
		//establish point for destination. Might need to ammend this to account for voxel scale.
		//150 is based off the middle of the voxel grid when its 30,30,30 with scale 10
	
		dest = new Vec3D(150,150,150);
		
		//
		
		//establish a vector (distance) between the position of our agent and the attractor/
		//This calculates the difference between the two positions.
		
		dest.subSelf(this);
		
		//normalise the vector. this keeps the direction of the vector the same but makes the magnitude a float of your choice
		dest.normalizeTo(25);
		//System.out.println(dest);
		addForce(dest);
		//get vector between agent pos and destination
		age++;
	}
	
	public void addToDLAtrail(Vec3D pt){
		dlaPoints.add(pt);
	}
	
	

}

