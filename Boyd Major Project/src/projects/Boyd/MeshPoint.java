package projects.Boyd;

import toxi.geom.Vec3D;
import toxi.geom.mesh.WEFace;

public class MeshPoint extends Vec3D {
	  
	  WEFace p;
	  
	  MeshPoint(Vec3D _v, WEFace _p){
	    super(_v);
	    p = _p;
	  }
	  
	  void setFace(WEFace _p){
	    p=_p;
	  }
	  
	  WEFace getFace(){
	    return p;
	  }
	}