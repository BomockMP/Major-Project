package projects.Boyd;

import java.util.ArrayList;

import pointCloudTools.Plane3DOctree;
import processing.core.PApplet;

import toxi.geom.Vec3D;

import core.Environment;
import core.Link;


public class ToxicEnvironment extends Environment {
	
	
	public ArrayList<ToxicAgent> pop;
	public ArrayList<ToxicAgent> removeToxicAgents;
	public ArrayList<ToxicAgent> addToxicAgents;
	public PApplet parent;
	public float bounds;

	public ToxicEnvironment(PApplet _parent, float _bounds) {
		super(_parent, _bounds);
		parent = _parent;
		bounds = _bounds;
		
		pop = new ArrayList<ToxicAgent>();
		removeToxicAgents = new ArrayList<ToxicAgent>();
		addToxicAgents = new ArrayList<ToxicAgent>();
		
	}
	
	
	
	
	public void run() {
		for (ToxicAgent a: pop)a.run(this);
		updateEnvironment();
	}

	public void updateEnvironment(){
		//delete any dead ToxicAgents
		for (ToxicAgent a:addToxicAgents){
			pop.add(a);
		}
		for (ToxicAgent a: removeToxicAgents){
			pop.remove(a); 
		}
		addToxicAgents = new ArrayList<ToxicAgent>();
		removeToxicAgents = new ArrayList<ToxicAgent>();
		pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2);
		for(ToxicAgent a:pop){
			pts.addPoint(a);
			/*
			for (Link l:a.trail){
				pts.addPoint(l.a);
			}
			*/
		}
	}
	
	public void addToxicAgent(ToxicAgent a){
		addToxicAgents.add(a);
	}
	
	
	
	public void remove(ToxicAgent a){
		removeToxicAgents.add(a);
	}
	
	public void removeAll(){
		removeToxicAgents.addAll(pop);
	}
	
	public void saveTrails(){
		ArrayList<String>lineList = new ArrayList<String>();
		for (ToxicAgent a: pop) {
			if(a.trail.size()>4){
				String c = "";
				for(int i = 0; i<a.trail.size();i++){
					Link l = a.trail.get(i);
					c = c+l.a.x +"," + l.a.y + "," + l.a.z +"/";
					if(i == a.trail.size()-1){
						c = c+l.b.x +"," + l.b.y + "," + l.b.z +"/";
					}
				}
				lineList.add(c);
			}
		}
		String[] skin = new String[lineList.size()];
		for (int i =0;i<lineList.size()-1;i++) {
			skin[i]=lineList.get(i);
		}
		parent.saveStrings("trails.txt", skin);
	}
}
	
	
	
	
	