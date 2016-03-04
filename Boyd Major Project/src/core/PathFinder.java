package core;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import meshTools.ColourWETriangleMesh;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WEFace;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh.WEVertex;

/*------------------------------------

Class that handles pathfinding within voxel
environment and drives robotic motion

------------------------------------*/


public class PathFinder {

	//-------------------------------------------------------------------------------------

	//Mesh Search 

	//-------------------------------------------------------------------------------------	

	public static Vec3D getClosestPoint(Vec3D pt, WETriangleMesh mesh){
		WEVertex cPt = (WEVertex) mesh.getClosestVertexToPoint(pt);
		Vec3D out = null;
		if(cPt!=null){
			out = getClosestPointOnSurface(cPt,pt);
		}
		return out;

	}
	
	public static ArrayList<Vec3D> getClosestPoints(Vec3D pt, ColourWETriangleMesh mesh, float radius){
		ArrayList cPts = mesh.getPointsWithinSphere(pt,radius);
		ArrayList<Vec3D> out = new ArrayList<Vec3D>();
		
		if(cPts==null)return out;
		
		ArrayList<WEFace> faces = new ArrayList<WEFace>();
		
		for(Object o:cPts){
			WEVertex v = (WEVertex)o;
			for(WEFace f: v.getRelatedFaces()){
				if(!faces.contains(f))faces.add(f);
			}
		}
		for (WEFace f:faces){
			Vec3D cPt = closestPointOnTriangle(f.toTriangle(), pt);
			if(cPt.distanceTo(pt)<radius/2)out.add(cPt);
		}

		return out;

	}
	


	public static Vec3D getClosestPointOnSurface(WEVertex v, Vec3D pt){
		float d = 1000000;
		Vec3D out = null;
		for (WEFace f: v.getRelatedFaces()){
			Vec3D tPt = closestPointOnTriangle(f.toTriangle(), pt);
			float tD = tPt.distanceTo(pt);
			if(tD<d){
				d = tD;
				out = tPt.copy();
			}
		}
		return out;

	}
	
	public static Vec3D closestPointOnTriangle (Triangle3D t, Vec3D p) {
		
		    final Vec3D ab = t.b.sub(t.a);
		    final Vec3D ac = t.c.sub(t.a);
		    final Vec3D ap = p.sub(t.a);
		    final double d1 = ab.dot(ap);
		    final double d2 = ac.dot(ap);
		    
		    if (d1 <= 0 && d2 <= 0) {
		      return t.a;
		    }

		    final Vec3D bp = p.sub(t.b);
		    final double d3 = ab.dot(bp);
		    final double d4 = ac.dot(bp);
		    
		    if (d3 >= 0 && d4 <= d3) {
		      return t.b;
		    }

		    final double vc = d1 * d4 - d3 * d2;
		    
		    if (vc <= 0 && d1 >= 0 && d3 <= 0) {
		      final double v = d1 / (d1 - d3);
		      return t.a.add(ab.scale((float)v));
		    }

		    final Vec3D cp = p.sub(t.c);
		    final double d5 = ab.dot(cp);
		    final double d6 = ac.dot(cp);
		    
		    if (d6 >= 0 && d5 <= d6) {
		      return t.c;
		    }

		    final double vb = d5 * d2 - d1 * d6;
		    
		    if (vb <= 0 && d2 >= 0 && d6 <= 0) {
		      final double w = d2 / (d2 - d6);
		      return t.a.add(ac.scale((float)w));
		    }

		    final double va = d3 * d6 - d5 * d4;
		    if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
		      final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
		      return t.b.add((t.c.sub(t.b)).scale((float)w));
		    }

		    final double denom = 1.0 / (va + vb + vc);
		    final double v = vb * denom;
		    final double w = vc * denom;
		    return t.a.add(ab.scale((float)v).add(ac.scale((float)w)));
		    
		  }
	

	//-------------------------------------------------------------------------------------

	//Point cloud search

	//-------------------------------------------------------------------------------------	

	public static Vec3D getHighestPoint(Vec3D out, ArrayList<Vec3D> pts){
		for(Vec3D v:pts){
			if(v.z>out.z)out.set(v);
		}
		return out.copy();
	}

	public static Vec3D getLowestPoint(Vec3D out, ArrayList<Vec3D> pts){

		for(Vec3D v:pts){
			if(v.z<out.z)out.set(v);
		}
		return out.copy();
	}

	public static Vec3D getLowPoint(Vec3D out, ArrayList<Vec3D> pts){
		Collections.shuffle(pts);
		for(Vec3D v:pts){
			if(v.z<out.z)return v.copy();
		}
		return out.copy();
	}

	public static Vec3D getClosestPoint(Vec3D pt, ArrayList<Vec3D> pts){

		float d = 10000;
		Vec3D cPt = new Vec3D();
		for(Vec3D v:pts){
			if(v!=pt){
				if(v.distanceTo(pt)<d){
					d = v.distanceTo(pt);
					cPt.set(v);
				}
			}
		}
		return cPt;
	}

	public static Vec3D getClosestPointFOV(Vec3D pt, Vec3D dir, float fov, ArrayList<Vec3D> pts){
		//	Collections.shuffle(pts);
		float d = 10000;
		Vec3D cPt = null;
		for(Vec3D v:pts){
			Vec3D ab = v.sub(pt);
			float angle = dir.angleBetween(ab,true);
			if(angle<fov){
				if(v.distanceTo(pt)>1){
					float dist = v.distanceTo(pt);
					if(dist<d){
						d = dist;
						cPt = v.copy();
					}
				}
			}
		}
		return cPt;
	}

	public static Vec3D getHighestPointFOV(Vec3D pt, Vec3D dir, float fov, float maxd, ArrayList<Vec3D> pts){
		//Collections.shuffle(pts);
		float d = -10000;
		Vec3D cPt = null;
		for(Vec3D v:pts){
			Vec3D ab = v.sub(pt);
			float angle = dir.angleBetween(ab,true);
			if(angle<fov){
				if(pt.distanceTo(v)<maxd){
					if(v.z>d){
						d = v.z;
						cPt = v.copy();
					}
				}
			}
		}
		return cPt;
	}

	public static Vec3D getNearZ(Vec3D pt, Vec3D dir, float fov, float targetZ, ArrayList<Vec3D> pts){
		//Collections.shuffle(pts);
		float d = 0;
		Vec3D cPt = null;
		for(Vec3D v:pts){
			Vec3D ab = v.sub(pt);
			float angle = dir.angleBetween(ab,true);
			float dist = pt.distanceTo(v);
			if(angle<fov){
				float diff = v.z*((fov-angle));
				if(diff>d){
					d = diff;
					cPt = v.copy();
				}
			}
		}
		return cPt;
	}

	public static Vec3D getLowestPointFOV(Vec3D pt, Vec3D dir, float fov,ArrayList<Vec3D> pts){
		//Collections.shuffle(pts);
		float d = 10000;
		Vec3D cPt = null;
		for(Vec3D v:pts){
			Vec3D ab = v.sub(pt);
			float angle = dir.angleBetween(ab,true);
			if(angle<fov){

				if(v.z<d){
					d = v.z;
					cPt = v.copy();
				}
			}
		}
		return cPt;
	}

	//-------------------------------------------------------------------------------------

	//Image Search 

	//-------------------------------------------------------------------------------------	
	public Vec3D getBestInFOV(DataMap map, Vec3D p, Vec3D dir, int rad){
		Vec3D toBest = new Vec3D(); //create a temp vector to keep track of the direction of the best condition
		float maxV = 0;

		//loop through pixels
		for (int i = -rad; i<=rad;i++){
			for (int j = -rad; j<=rad;j++){
				if(!(i==0 && j==0)){
					//checks for edges
					int x = (int)p.x+i;
					int y = (int)p.y+j;


					//check to see if this is the smallest current value
					//scale by the distance to the value
					float v = map.getVal(x,y);
					Vec3D toV = new Vec3D(i,j,0);

					//limit the angle of vision
					if(toV.angleBetween(dir,true)<Math.PI/2){

						//check to see if the current value is larger than 
						//the current best
						if((v)>maxV){

							//reset all our variables that keep track of the best option
							float d = toV.magnitude();
							toV.scaleSelf(1/d);

							toBest = toV;
							maxV = v;
						}
					}
				}
			}
		}
		return toBest;
	}

	//-------------------------------------------------------------------------------------

	//Agent Search

	//-------------------------------------------------------------------------------------	
	public static Vec3D getClosestOtherPoint(HashMap<Vec3D,Agent> trailMap, Vec3D pt, ArrayList<Vec3D> pts){
		Agent parentAgent = trailMap.get(pt);
		float d = 10000;
		Vec3D cPt = new Vec3D();
		for(Vec3D v:pts){
			if(trailMap.get(v)!=parentAgent){
				float td = v.distanceTo(pt);
				if(td<d && td>0){
					d = v.distanceTo(pt);
					cPt.set(v);
				}
			}
		}
		return cPt;
	}
}
