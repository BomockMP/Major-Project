package projects.sandbox.BeetleGroup;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.print.attribute.standard.OutputDeviceAssigned;
import javax.rmi.CORBA.Util;
import toxi.sim.dla.*;
import toxi.geom.*;
import processing.opengl.*;
import processing.core.PApplet;
import voxelTools.VoxelBrush;
import voxelTools.VoxelGrid;

import voxelTools.Cell;


public class DLAListener extends DLAEventAdapter {
	
	DLA dla;
	Vec3D p;
	PApplet parent;
	VoxelGrid volume;
	VoxelBrush vBrush;

	public void DLAlistener(DLA _dla, Vec3D _p){
		
		dla = _dla;
		p = _p;
		//volume = _volume;
		vBrush = new VoxelBrush(volume, 100);
		
		
	}
	
	//FUNCTIONS
	
//	public void dlaNewParticleAdded(DLA dla, Vec3D p){
//		
//		System.out.println(p);
//		
//		//vBrush.drawAtGridPos(p.x, p.y, p.z, 100);
//		
//		
//		//put something interesting here. Voxelbrush?
//	}
//	
	
	
}
