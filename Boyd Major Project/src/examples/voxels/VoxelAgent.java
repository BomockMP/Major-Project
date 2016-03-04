package examples.voxels;

import java.util.ArrayList;

import processing.core.PApplet;
import projects.archive.VolumeAgent;
import core.Agent;
import core.Environment;
import core.Link;
import toxi.geom.Vec3D;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;

public class VoxelAgent extends VolumeAgent{

	public VoxelAgent(Vec3D _o, boolean _f, VoxelGrid _v, VoxelBrush _br) {
		super(_o, _f, _v, _br);
	}
	
	public void run(Environment environment){
		//addForce(Vec3D.randomVector().scale(5));
		orbitPaths(environment.pop);
		update();
		if(age%3==0)addToTrail(this);
		//stiffenTrail(0.05f);
		//updateTrail();
		if(trail.size()>2)volBrushLocation(trail.get(trail.size()-3).a, 255, 10);
		//volume.setValue(this, 255);
	}
	
	public void repel(VoxelGrid volume) {

		//get the value of the current voxel
		//float v = volume.getValue(this);

		//check neighbouring voxels for difference
		Vec3D from = volume.getNormal(this, 200, 10, 0).limit(0.3f);
	
		addForce(from);

	}
	
	public void stayOn(VoxelGrid volume){
		
		Vec3D from = volume.findVal(this, 5, 1.5f, vel, 200).limit(0.4f);;
		
		addForce(from);
	}
	
	public void orbitPaths(ArrayList<Agent>agents ){
		Vec3D dir = new Vec3D();
		Vec3D cPt = new Vec3D();
		float minD = 99999;
		for(Agent a: agents){
			for(Link l:a.trail){
				//get closest pt on links
				Vec3D pt = l.closestPt(this);
				float d = distanceTo(pt);
				if(d<minD){
					minD = d;
					dir = l.getDir();
					cPt=pt.sub(this).normalize();
				}
			}
		}
		
		Vec3D crossProduct = dir.cross(cPt);
		addForce(crossProduct);
		addForce(dir.scale(0.05f));
		if(minD<(50-(age/100))){
			cPt.invert();
		}
		addForce(cPt.scale(0.2f));
		//apply force
	}
	
	public void render(PApplet parent){
		parent.stroke(255);
		for(Link l:trail){
			parent.line(l.a.x, l.a.y, l.a.z, l.b.x, l.b.y, l.b.z); 

		}
		
	}
	

}
