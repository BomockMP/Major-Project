package core;

import toxi.geom.Vec3D;

public class Phe extends Plane3D {
	
	float v;

	public Phe(Vec3D _origin, float val) {
		this(_origin, new Vec3D(1,0,0), val);
	}
	
	public Phe(Vec3D _origin, Vec3D _dir, float val){
		super(_origin, _dir);
		v = val;
	}
	
	public Phe(Plane3D _origin, float val){
		super(_origin);
		v = val;
	}
	
	@Override
	public void update(float fadeSpeed){
		v*=fadeSpeed;
	}
	
	public Vec3D getDir(){
		return xx.copy();
	}
	
	public Vec3D getScaledDir(){
		return xx.scale(v);
	}
	
	public Vec3D getNormal(){
		return zz.copy();
	}
	
	public float get(){
		return v;
	}

}
