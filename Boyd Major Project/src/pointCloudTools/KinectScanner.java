package pointCloudTools;


import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import examples.flocking.FlockingApp;
import KinectPV2.KinectPV2;
import processing.core.PApplet;
import processing.opengl.PJOGL;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

public class KinectScanner extends TransformableScanner{

	KinectPV2 kinect;

	public KinectScanner(PApplet _parent){
		super(_parent);
		kinect = new KinectPV2(parent);
		kinect.enableColorChannel(true);
		kinect.enablePointCloudColor(true);
		kinect.init();
	
	}
	@Override
	public void renderColours(){
	    ptBuffer = kinect.getPointCloudColorPos();
		colBuffer = kinect.getColorChannelBuffer();
		super.renderColours();
	}
	
	@Override
	public void renderDepth(){
		ptBuffer = kinect.getPointCloudDepthPos();
		super.renderDepth();
	}
	
	@Override
	public  float[] inAABB(AABB box){
		ptBuffer = kinect.getPointCloudColorPos();
		return super.inAABB(box);
	}

	@Override
	public  float[][] inAABBColours(AABB box){
		ptBuffer = kinect.getPointCloudColorPos();
		colBuffer  = kinect.getColorChannelBuffer();
		return super.inAABBColours(box);
	}

}
