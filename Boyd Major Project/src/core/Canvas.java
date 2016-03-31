package core;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2;

import peasy.PeasyCam;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PJOGL;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.Mesh3D;
import toxi.physics3d.VerletConstrainedSpring3D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletSpring3D;


/*------------------------------------

Class that handles common drawing tasks

//TODO multithreading

------------------------------------*/
public class Canvas{
	PGraphics graphics;
	
	public Canvas(PGraphics _graphics){
		graphics = _graphics;
	}
	
	public void drawPts(ArrayList pts, float rad){
		graphics.strokeWeight(rad);
		graphics.stroke(255);
		for(Vec3D p:(ArrayList<Vec3D>) pts){
			graphics.point(p.x,p.y,p.z);
		}
	}
	
	//Boyd
	public void drawParticles(List pts, float rad){
		graphics.strokeWeight(rad);
		graphics.stroke(255);
		for(VerletParticle3D p:(List<VerletParticle3D>) pts){
			graphics.point(p.x,p.y,p.z);
		}
	}
	
	
	public void drawPlane(Plane3D p, float s, float w){
		graphics.strokeWeight(w);
		graphics.stroke(255,100,100);
		graphics.line(p.x, p.y, p.z, p.x+(p.xx.x*s), p.y+(p.xx.y*s), p.z+(p.xx.z*s)); 
		graphics.stroke(100,100,255);
		graphics.line(p.x, p.y, p.z, p.x+(p.yy.x*s), p.y+(p.yy.y*s), p.z+(p.yy.z*s)); 
		graphics.stroke(100,255,255);
		graphics.line(p.x, p.y, p.z, p.x+(p.zz.x*s), p.y+(p.zz.y*s), p.z+(p.zz.z*s)); 
	}
	
	public void drawPlanes(Collection collection, float s){
		graphics.strokeWeight(2);
		graphics.stroke(255);
		for(Object o:collection){
			
			Plane3D p = (Plane3D)o;
			graphics.stroke(255, 100, 100);
			graphics.line(p.x, p.y, p.z, p.x+(p.xx.x*s), p.y+(p.xx.y*s), p.z+(p.xx.z*s)); 
			graphics.stroke(100,100,255);
			graphics.line(p.x, p.y, p.z, p.x+(p.yy.x*s), p.y+(p.yy.y*s), p.z+(p.yy.z*s)); 
			graphics.stroke(100,255,255);
			graphics.line(p.x, p.y, p.z, p.x+(p.zz.x*s), p.y+(p.zz.y*s), p.z+(p.zz.z*s)); 
		}
	}
	
	public void drawTrails(ArrayList<Agent> agents, float s, float greyscale){
		for(Agent a:agents){
			drawLinks(a.trail, s,greyscale);
		}
	}
	
	public void drawLinks(ArrayList<Link>links, float size, float greyscale ){
		graphics.strokeWeight(size);
		graphics.stroke(greyscale);
		for(Link l:links){
			graphics.strokeWeight(size);
			graphics.line(l.a.x, l.a.y, l.a.z, l.b.x, l.b.y, l.b.z); 
			graphics.strokeWeight(size*2);
			graphics.stroke(255);
			if(l.a.locked())graphics.stroke(255,0,0);
			graphics.point(l.a.x, l.a.y,l.a.z);

		}
	}
	
	//BOYD-----------
	
	public void drawAgentSprings(ArrayList<Agent> agents, float s, float greyscale){
		for(Agent a:agents){
			drawSprings(a.springList, s,greyscale);
		}
	}
	
	
	
	public void drawSprings(ArrayList<VerletSpring3D>springs, float size, float greyscale ){
		graphics.strokeWeight(size);
		graphics.stroke(greyscale);
		for(VerletSpring3D s:springs){
			graphics.strokeWeight(size);
			graphics.line(s.a.x, s.a.y, s.a.z, s.b.x, s.b.y, s.b.z); 
			graphics.strokeWeight(size*2);
			graphics.stroke(255);
			//if(s.a.locked())graphics.stroke(255,0,0);
			graphics.point(s.a.x, s.a.y,s.a.z);

		}
	}
	
	public void drawSpringPhysics(List<VerletSpring3D>springs, float size, float greyscale ){
		graphics.strokeWeight(size);
		graphics.stroke(greyscale);
		for(VerletSpring3D s:springs){
			graphics.strokeWeight(size);
			graphics.line(s.a.x, s.a.y, s.a.z, s.b.x, s.b.y, s.b.z); 
			graphics.strokeWeight(size*2);
			graphics.stroke(255);
			//if(s.a.locked())graphics.stroke(255,0,0);
			graphics.point(s.a.x, s.a.y,s.a.z);

		}
	}
	
	//BOYD-----------
	
	public void drawTrailsDOF(ArrayList<Agent>agents, float size, float greyscale, Vec3D cam ){
		graphics.strokeWeight(size);
		float minD = 200;
		float maxD = 800;
		for(Agent a:agents){

			for(Link l:a.trail){
				float gs = map(cam.distanceTo(l.a),minD,maxD,greyscale,0);
				graphics.stroke(gs);
				graphics.line(l.a.x, l.a.y, l.a.z, l.b.x, l.b.y, l.b.z); 
	
			}
		}
	}
	public float map(float v, float min, float max, float min2, float max2){
		return min2+((v/(max-min))*(max2-min2));
	}
	
	public void drawPtBuffer(FloatBuffer pts){
		int numPts = pts.capacity()/3;
		PJOGL pgl = (PJOGL)graphics.beginPGL();
		GL2 gl2 = pgl.gl.getGL2();
		gl2.glEnable( GL2.GL_BLEND );
		gl2.glEnable(GL2.GL_POINT_SMOOTH);      
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, pts);
		gl2.glDrawArrays(GL2.GL_POINTS, 0, (numPts));
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisable(GL2.GL_BLEND);
		graphics.endPGL();
	}
	
	public void drawPtBuffer(FloatBuffer pts, FloatBuffer cols){
		int numPts = pts.capacity()/3;
		PJOGL pgl = (PJOGL)graphics.beginPGL();
		GL2 gl2 = pgl.gl.getGL2();

		gl2.glEnable( GL2.GL_BLEND );
		gl2.glEnable(GL2.GL_POINT_SMOOTH);  
		gl2.glAlphaFunc(gl2.GL_GREATER, 0.5f); //alpha culling
		gl2.glEnable(gl2.GL_ALPHA_TEST);

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, pts);
		gl2.glColorPointer(4, GL2.GL_FLOAT, 0, cols);
		gl2.glDrawArrays(GL2.GL_POINTS, 0, (numPts));
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
		gl2.glDisable(GL2.GL_BLEND);
		graphics.endPGL();
	}
	
	 public final void mesh(Mesh3D mesh, float normalLength) {
	        graphics.beginShape(PConstants.TRIANGLES);
	        graphics.noFill();
	        graphics.stroke(200);
	        graphics.strokeWeight(1);
	            for (Face f : mesh.getFaces()) {
	            	graphics.normal(f.a.normal.x, f.a.normal.y, f.a.normal.z);
	            	graphics.vertex(f.a.x, f.a.y, f.a.z);
	            	graphics.normal(f.b.normal.x, f.b.normal.y, f.b.normal.z);
	            	graphics.vertex(f.b.x, f.b.y, f.b.z);
	            	graphics.normal(f.c.normal.x, f.c.normal.y, f.c.normal.z);
	            	graphics.vertex(f.c.x, f.c.y, f.c.z);
	            }
	            graphics.endShape();
	 }
}
