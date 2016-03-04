package pointCloudTools;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import processing.core.PApplet;
import processing.opengl.PJOGL;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

public class TransformableScanner implements Scanner{
	
	PApplet parent;
	FloatBuffer ptBuffer;
	FloatBuffer colBuffer;
	
	//calibration
	float[] m = new float[16];
	public float tX = 550;
	public float tY = -440;
	public float tZ = 1785;
	public float sx = 1000;
	public float sy = -1000;
	public float sz = -1000;
	public float rA= 0;
	public float rX= 0;
	public float rY= 0;
	public float rZ= 1;


	public TransformableScanner(PApplet _parent){
		parent = _parent;
	}
	
	public void renderDepth(){
		if(ptBuffer!=null){
			int numPts = ptBuffer.capacity()/3;
			PJOGL pgl = (PJOGL)parent.beginPGL();
			GL2 gl2 = pgl.gl.getGL2();
			
			gl2.glEnable( GL2.GL_BLEND );
			gl2.glEnable(GL2.GL_POINT_SMOOTH);      
	
			gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, ptBuffer);
			gl2.glTranslatef(tX, tY, tZ);
			gl2.glRotatef(rA, rX, rY, rZ);
			gl2.glScalef(sx, sy, sz);
			gl2.glDrawArrays(GL2.GL_POINTS, 0, (numPts));
			gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glDisable(GL2.GL_BLEND);
			parent.endPGL();
		}
	}

	public void renderColours(){
		if(ptBuffer!=null && colBuffer!=null){
			int numPts = ptBuffer.capacity()/3;
			PJOGL pgl = (PJOGL)parent.beginPGL();
			GL2 gl2 = pgl.gl.getGL2();

			gl2.glEnable( GL2.GL_BLEND );
			//gl2.glEnable(GL2.GL_POINT_SMOOTH);      

			gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, ptBuffer);
			gl2.glColorPointer(3, GL2.GL_FLOAT, 0, colBuffer);
			gl2.glTranslatef(tX, tY, tZ);
			gl2.glRotatef(rA, rX, rY, rZ);
			gl2.glScalef(sx, sy, sz);
			gl2.glDrawArrays(GL2.GL_POINTS, 0, (numPts)-1);
			gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
			gl2.glDisable(GL2.GL_BLEND);
			parent.endPGL();
		}
	}
	
	public void setOpenGlTransformMatrix(){
		PJOGL pgl = (PJOGL)parent.beginPGL();
		GL2 gl2 = pgl.gl.getGL2();
		gl2.glPushMatrix();
		gl2.glLoadIdentity();
		gl2.glTranslatef(tX, tY, tZ);
		gl2.glRotatef(rA, rX, rY, rZ);
		gl2.glScalef(sx, sy, sz);
		m = new float[16];
		gl2.glGetFloatv(gl2.GL_MODELVIEW_MATRIX, m,0);
		gl2.glPopMatrix();
	}

	float[] transform(float x, float y, float z){
		float[] tp = new float[3];
		tp[0] = x*m[0] + y*m[4] + z*m[8] + m[12];
		tp[1] = x*m[1] + y*m[5] + z*m[9] + m[13];
		tp[2] = x*m[2] + y*m[6] + z*m[10] + m[14];
		return tp;
	}

	public float[] inAABB(AABB box){
		//setup transform matrix
		setOpenGlTransformMatrix();
		int c =0;
		if(ptBuffer!=null){
			float[] pts = new float[ptBuffer.capacity()];
			for(int i = 0;i<ptBuffer.capacity();i+=3){
				float[] p= transform(ptBuffer.get(i), ptBuffer.get(i+1), ptBuffer.get(i+2));
				if(box.containsPoint(new Vec3D(p[0],p[1],p[2]))){
					pts[c]=p[0];
					pts[c+1]=p[1];
					pts[c+2]=p[2];
					c+=3;
				}
			}
			float[] cropPts = new float[c];
			for(int i =0;i<c;i++){
				cropPts[i]=pts[i];
			}
			return cropPts;
		}
		return null;
	}

	public float[][] inAABBColours(AABB box){
		//setup transform matrix
		setOpenGlTransformMatrix();

		int c =0;
		if(ptBuffer!=null && colBuffer!=null){
			float[][] pts = new float[2][ptBuffer.capacity()];
			for(int i = 0;i<ptBuffer.capacity();i+=3){
				float[] p= transform(ptBuffer.get(i), ptBuffer.get(i+1), ptBuffer.get(i+2));
				if(box.containsPoint(new Vec3D(p[0],p[1],p[2]))){
					pts[0][c]=p[0];
					pts[0][c+1]=p[1];
					pts[0][c+2]=p[2];
					pts[1][c]=colBuffer.get(i);
					pts[1][c+1]=colBuffer.get(i+1);
					pts[1][c+2]=colBuffer.get(i+2);
					c+=3;
				}
			}
			float[][] cropPts = new float[2][c];
			for(int i =0;i<c;i++){
				cropPts[0][i]=pts[0][i];
				cropPts[1][i]=pts[1][i];
			}

			return cropPts;
		}
		return null;
	}

	public float[] copyBuffer (FloatBuffer buffer){
		if(ptBuffer!=null){
			//setup transform matrix
			setOpenGlTransformMatrix();
	
			float[] pts = new float[buffer.capacity()];
			for(int i = 0;i<buffer.capacity();i+=3){
				float[] p= transform(buffer.get(i), buffer.get(i+1), buffer.get(i+2));
				//float f = original.get(i);
				pts[i]=p[0];
				pts[i+1]=p[1];
				pts[i+2]=p[2];
			}
	
			return pts;
			}
		return null;

	}
	
	public float[] copyColourBuffer (){
	
		if(colBuffer!=null){
			float[] colours = new float[colBuffer.capacity()];
			for(int i = 0;i<colBuffer.capacity();i+=3){
				//float f = original.get(i);
				colours[i]=colBuffer.get(i);
				colours[i+1]=colBuffer.get(i+1);
				colours[i+2]=colBuffer.get(i+2);
			}
	
			return colours;
		}
		return null;

	}
	
	public ArrayList<Vec3D> getPts(){
		ArrayList<Vec3D>out = new ArrayList<Vec3D>();
		//setup transform matrix
		setOpenGlTransformMatrix();
		int c =0;
		if(ptBuffer!=null){
			float[] pts = new float[ptBuffer.capacity()];
			for(int i = 0;i<ptBuffer.capacity();i+=3){
				float[] p= transform(ptBuffer.get(i), ptBuffer.get(i+1), ptBuffer.get(i+2));
				out.add(new Vec3D(p[0],p[1],p[2]));
			}
		}
		return out;
		
	}

}
