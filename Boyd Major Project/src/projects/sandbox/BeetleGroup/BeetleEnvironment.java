package projects.sandbox.BeetleGroup;

import java.util.ArrayList;

import pointCloudTools.Plane3DOctree;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import core.Agent;
import core.Environment;
import core.Link;
import core.Plane3D;



public class BeetleEnvironment extends Environment {
	
	public ArrayList<BeetleAgent> pop;
	public ArrayList<BeetleAgent> removeBeetleAgents;
	public ArrayList<BeetleAgent> addBeetleAgents;
	public PApplet parent;
	public float bounds;

	public BeetleEnvironment(PApplet _parent, float _bounds) {
		super(_parent, _bounds);
		parent = _parent;
		bounds = _bounds;
		
		pop = new ArrayList<BeetleAgent>();
		removeBeetleAgents = new ArrayList<BeetleAgent>();
		addBeetleAgents = new ArrayList<BeetleAgent>();
		
	}
	
	
	
	
	public void run() {
		for (BeetleAgent a: pop)a.run(this);
		updateEnvironment();
	}

	public void updateEnvironment(){
		//delete any dead BeetleAgents
		for (BeetleAgent a:addBeetleAgents){
			pop.add(a);
		}
		for (BeetleAgent a: removeBeetleAgents){
			pop.remove(a); 
		}
		addBeetleAgents = new ArrayList<BeetleAgent>();
		removeBeetleAgents = new ArrayList<BeetleAgent>();
		pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2);
		for(BeetleAgent a:pop){
			pts.addPoint(a);
			/*
			for (Link l:a.trail){
				pts.addPoint(l.a);
			}
			*/
		}
	}
	
	public void addBeetleAgent(BeetleAgent a){
		addBeetleAgents.add(a);
	}
	
	
	
	public void remove(BeetleAgent a){
		removeBeetleAgents.add(a);
	}
	
	public void removeAll(){
		removeBeetleAgents.addAll(pop);
	}
	
	public void saveTrails(){
		ArrayList<String>lineList = new ArrayList<String>();
		for (BeetleAgent a: pop) {
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
