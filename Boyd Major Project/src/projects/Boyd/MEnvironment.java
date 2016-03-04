package projects.Boyd;

import java.util.ArrayList;

import pointCloudTools.Plane3DOctree;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import core.Agent;
import core.Environment;
import core.Link;
import core.Plane3D;


public class MEnvironment extends Environment {
	
	
	public ArrayList<MAgent> pop;
	public ArrayList<MAgent> removeMAgents;
	public ArrayList<MAgent> addMAgents;
	public ArrayList<Agent> trailList;
	public PApplet parent;
	public float bounds;

	public MEnvironment(PApplet _parent, float _bounds) {
		super(_parent, _bounds);
		parent = _parent;
		bounds = _bounds;
		
		pop = new ArrayList<MAgent>();
		removeMAgents = new ArrayList<MAgent>();
		addMAgents = new ArrayList<MAgent>();
		trailList =new ArrayList<Agent>();
		
	}
	
	
	
	
	public void run() {
		for (MAgent a: pop)a.run(this);
		updateEnvironment();
	}

	public void updateEnvironment(){
		//delete any dead MAgents
		for (MAgent a:addMAgents){
			pop.add(a);
		}
		for (MAgent a: removeMAgents){
			pop.remove(a); 
		}
		addMAgents = new ArrayList<MAgent>();
		removeMAgents = new ArrayList<MAgent>();
		pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2);
		for(MAgent a:pop){
			pts.addPoint(a);
			/*
			for (Link l:a.trail){
				pts.addPoint(l.a);
			}
			*/
		}
	}
	
	public void copyTrail(Agent a){
		Agent tmp = new Agent(a, true);
		for(Link l:a.trail){
			tmp.addToTrail(new Plane3D(l.a));
		}
		trailList.add(tmp);
	}
	
	public void addMAgent(MAgent a){
		addMAgents.add(a);
	}
	
	
	
	public void remove(MAgent a){
		removeMAgents.add(a);
	}
	
	public void removeAll(){
		removeMAgents.addAll(pop);
	}
	
	public void saveTrails(){
		ArrayList<String>lineList = new ArrayList<String>();
		for (MAgent a: pop) {
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
