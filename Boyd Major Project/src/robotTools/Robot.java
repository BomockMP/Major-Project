package robotTools;


import core.Plane3D;
import examples.flocking.FlockingApp;
import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.Vec3D;

public class Robot extends Plane3D{
	public Plane3D targetPos, homePos;
	public Vec3D abc;
	public int io =1;
	public float delay;
	
	//-------------------------------------------------------------------------------------

	//Constructors

	//-------------------------------------------------------------------------------------
	
	public Robot(Vec3D _endEffector){
		super(_endEffector);
		abc = new Vec3D();
		targetPos = new Plane3D(_endEffector);
		homePos = new Plane3D(targetPos);
	}
	
	public void run(){
		

	}
	
	//-------------------------------------------------------------------------------------

	//get+set+initialisation

	//-------------------------------------------------------------------------------------
	
	public void setIO(int _io){
		io = _io;
	}
	
	public void toggleIO(){
		if(io==0){
			io=1;
		}else if(io==1){
			io=0;
		}
	}
	
	public void setDelay(float time){
		delay = time;
	}
	public float getDelay(){
		return delay;
	}
	public void updatePosition(Vec3D newPosition){
		set(newPosition);
	}
	public void updatePosition(String[] newPosition){
		float x = Float.valueOf(newPosition[0]);
	    float y = Float.valueOf(newPosition[1]);
	    float z = Float.valueOf(newPosition[2]);
	    updatePosition(new Vec3D(x,y,z));
	}
	
	public void updatePose(String[] newPose){
		float xx = Float.valueOf(newPose[0]);
	    float xy = Float.valueOf(newPose[1]);
	    float xz = Float.valueOf(newPose[2]);
	    float zx = Float.valueOf(newPose[0]);
	    float zy = Float.valueOf(newPose[1]);
	    float zz = Float.valueOf(newPose[2]);
	    this.xx.set(xx,xy,xz).normalize();
	    this.zz.set(zx,zy,zz).normalize();
	}
	
	public void updatePose(Vec3D newPose){
		abc = newPose.copy();
		Vec3D nz = new Vec3D(0,0,1);
		nz.rotateZ((float)(newPose.z*(Math.PI)/180));
		nz.rotateY((float)(newPose.y*(Math.PI)/180));
		nz.rotateX((float)(newPose.x*(Math.PI)/180));
		Vec3D nx = new Vec3D(1,0,0);
		nx.rotateZ((float)(newPose.z*(Math.PI)/180));
		nx.rotateY((float)(newPose.y*(Math.PI)/180));
		nx.rotateX((float)(newPose.x*(Math.PI)/180));
		interpolateToZZ(nz, 1);
		interpolateToXX(nx, 1);
	}
	
	public void setTarget(Vec3D target){
		targetPos.set(target);
	}
	public void setTarget(Plane3D target){
		targetPos = new Plane3D (target);
	}
	
	public void moveTarget(Vec3D vector){
		targetPos.addSelf(vector);
	}
	
	public float getDistToTarget(){
		return distanceTo(targetPos);
	}
	

}
