package examples.voxels.multipleVoxelGrids;

import toxi.geom.Vec3D;
import voxelTools.VoxelGrid;
import core.Agent;
import core.Environment;

public class VoxParticle extends Agent{
	VoxelGrid voxels;
	
	public VoxParticle(Vec3D _o, boolean _f, VoxelGrid _voxels) {
		super(_o, _f);
		voxels = _voxels;
	}
	
	public void run(Environment environment){
		addForce(getNoiseVector(environment, 0.05f));
		update();
		if(age%5==0)addToTrail(this);
		voxels.setValue(this, 255);
		if(!inBounds(300)){
			reset();
		}
	}
	
	public Vec3D getNoiseVector(Environment environment, float ns){
		return new Vec3D((float)(environment.parent.noise(x*ns,y*ns,z*ns)-0.5)*2, (float)(environment.parent.noise(x*ns,y*ns,z*ns)-0.5)*2,(float)(environment.parent.noise(x*ns,y*ns,z*ns)-0.5)*2);
	}
	
	public void reset(){
		set((float) Math.random()*300, (float) Math.random()*300, (float) Math.random()*300);
		resetTrail();
	}

}
