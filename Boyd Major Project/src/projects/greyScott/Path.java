package projects.greyScott;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import toxi.geom.*;


public class Path {

	
	public ArrayList<Vec2D> controlPoints;
	public Spline2D splinePath;
	public List<Vec2D> pointsOnCurve;
	public LineStrip2D strip;
	public List<Line2D> stripSegments;
	 
	Path(ArrayList<Vec2D> _controlPoints) {
		controlPoints = _controlPoints;
	  }
	
	
	
	
	public void initiatePath(){
		
		//initiate spline container
		splinePath = new Spline2D();
		//add control points
		for (Vec2D v : controlPoints){
			splinePath.add(v.x, v.y);
		}
		decimateCurve();
	}
	
	
	public void decimateCurve(){
		//clear strip
		strip = new LineStrip2D();
		//add points to strip
		strip = splinePath.toLineStrip2D(5);
		//get line segments
		stripSegments = strip.getSegments();
	}
	
	
	public void getClosestStripPoints(){
		List<Vec2D> stripPoints = strip.getVertices();
	}
	
	
	public Vec2D getClosestNormal(Vec2D pos){
		//get line segments
		List<Line2D> stripSegments = strip.getSegments();
		//go through line segments and find the normal from supplied point to line segment .closestPointTo()
		Vec2D closestNormal = new Vec2D();
		float closestDistance = 999;
		Line2D closestLineSegment;
		
		
		//for each line segment
		for (Line2D l : stripSegments){
			//get the normal
		Vec2D normal = l.closestPointTo(pos);
		//if the normals less than the closest distance (starting at 999) update
		if (normal.magnitude()<closestDistance){
			closestDistance = normal.magnitude();
			closestNormal = normal;
			//save closest line segment
			closestLineSegment = l;
		}
		}
		return closestNormal;
	}
	
	
	
	public Line2D getClosestLineSegment(Vec2D pos){
		
		
		
		
		//go through line segments and find the normal from supplied point to line segment .closestPointTo()
		Vec2D closestNormal = new Vec2D();
		
		float closestDistance = 999;
		Line2D closestLineSegment = null;
		
		//for each line segment
		for (Line2D l : stripSegments){
			//get the normal
		Vec2D closestPointOnLine = l.closestPointTo(pos);
		
		
		Vec2D VectorToClosetPoint = closestPointOnLine.subSelf(pos);
		//if closest point on line is the least
		
		if (VectorToClosetPoint.magnitude()<closestDistance){
			closestDistance = VectorToClosetPoint.magnitude();
			
			
			
			closestNormal = closestPointOnLine;
			//save closest line segment
			closestLineSegment = l;
			
		}
		}
		//System.out.println(closestLineSegment);
		//
		
		
		return closestLineSegment;
		
	}
	
}
