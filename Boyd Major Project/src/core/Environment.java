package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import pointCloudTools.Plane3DOctree;
import processing.core.PApplet;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;

/*------------------------------------

Class containing agents, data structures and maps.
Contains functions for adding to, initialising and
saving the environment

------------------------------------*/

public class Environment {


	public HashMap<String, DataMap> maps = new HashMap<String, DataMap>();
	public HashMap<String, Phe>phe = new HashMap<String, Phe>();
	public HashMap<Vec3D,Agent>trailMap = new HashMap<Vec3D,Agent>();
	
	// TODO sort out including pins in a map
	//MultiValueMap<Plane3D, Link> links = new MultiValueMap<Plane3D, Link>();
	public Plane3DOctree pts;
	public ArrayList<Agent> pop;
	public ArrayList<Agent> removeAgents;
	public ArrayList<Agent> addAgents;
	public ArrayList<ArrayList<Link>> trailLog;
	public PApplet parent;
	public float bounds;
	public int gridRes =10;

	public Environment(PApplet _parent, float _bounds) {
		parent = _parent;
		bounds = _bounds;
		pop = new ArrayList<Agent>();
		removeAgents = new ArrayList<Agent>();
		addAgents = new ArrayList<Agent>();
		trailLog = new ArrayList<ArrayList<Link>>();
		pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2);
	}

	//-------------------------------------------------------------------------------------

	//Functions for updating the environment

	//-------------------------------------------------------------------------------------

	public void run() {
		for (Agent a: pop)a.run(this);
	}

	public void update(boolean addTrailToTree){
		//delete any dead Agents
		for (Agent a:addAgents){
			pop.add(a);
		}
		for (Agent a: removeAgents){
			pop.remove(a); 
		}
		trailMap = new HashMap<Vec3D,Agent>();
		addAgents = new ArrayList<Agent>();
		removeAgents = new ArrayList<Agent>();
		pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2);
		for(Agent a:pop){
			pts.addPoint(a);
			if(addTrailToTree){
				for (Link l:a.trail){
					pts.addPoint(l.a);
					trailMap.put(l.a, a);
				}
			}
		}
	}
	
	public void addMesh(TriangleMesh m){
		//mesh.addMesh(m); 
	}

	public void addPlane(Plane3D j){
		pts.addPoint(j);
	}

	//-------------------------------------------------------------------------------------

	//Pheromones

	//-------------------------------------------------------------------------------------
	
	public void setGridRes(int _gridRes){
		gridRes = _gridRes;
	}
	public String getCoord(Vec3D p) {
	    String s = ""+Math.floor(p.x/gridRes)+Math.floor(p.y/gridRes)+Math.floor(p.z/gridRes);
	    return s;
	}
	
	public void addPhe(Plane3D pos){
		//check empty
		String key = getCoord(pos);
	//	if(!phe.containsKey(key)){
			phe.put(key, new Phe(pos, 1));
		//}
	}
	
	public void addPhe(ArrayList<Plane3D>positions){
		for(Plane3D p:positions){
			addPhe(p);
		}
	}
	
	public void updatePhe(float fadeSpeed){
		for(Iterator<Entry<String, Phe>> it = phe.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, Phe> entry = it.next();
			Phe p = entry.getValue();
			p.update(fadeSpeed);
			if(p.v<0.05)it.remove();
		}
	}
	
	public ArrayList<Phe> getPheInAABB(AABB box){
		ArrayList<Phe>out = new ArrayList<Phe>();
		Vec3D min = box.getMin();
		Vec3D max = box.getMax();
		for(int x = (int) min.x; x<max.x;x+=gridRes){
			for(int y = (int) min.y; y<max.y;y+=gridRes){
				for(int z = (int) min.z; z<max.z;z+=gridRes){
					String key = getCoord(new Vec3D(x,y,z));
					if(phe.containsKey(key))out.add(phe.get(key));
				}
			}
		}
		return out;
		
	}
	
	//-------------------------------------------------------------------------------------

	//Trails

	//-------------------------------------------------------------------------------------
	public void bundle(int numPts, float strength){
		//iterate over particles
		for(Agent a:pop){
			for(Link l:a.trail){
				ArrayList<Vec3D> forces= new ArrayList<Vec3D>();
				Particle p = l.a;
				float minDist = 99999;
				if(!p.locked()){
					//get neighbours
					//ArrayList<Vec3D>neighbours = (ArrayList<Vec3D>) pts.getLeafForPoint(p).getParent().getPoints();
					ArrayList<Vec3D>neighbours = pts.getPointsWithinSphere(p,10);
					if(neighbours!=null){
						Vec3D nearest = PathFinder.getClosestOtherPoint(trailMap,p, neighbours);
						if(!nearest.isZeroVector()){
							Vec3D toP = nearest.sub(p);
							float d = toP.magnitude();
							if(d>1 && d<minDist){
								
								// TODO fix this - not finding just the closest pts.
								toP.scaleSelf(strength/toP.magnitude());
								forces.add(toP);
								if(forces.size()>numPts)forces.remove(0);
							}
						}
					}	
				}
				for(Vec3D f:forces){
					p.addForce(f);
				}
				if(p.age>300)p.lock();
			}
		}
	}
	
	public void alignTrails(){
		// TODO trail alignment
	}
	
	
	//-------------------------------------------------------------------------------------

	//Functions for reading the environment

	//-------------------------------------------------------------------------------------

	public DataMap getMap (String mapName) {
		return maps.get(mapName);
	}

	public boolean containsPts (Vec3D p, float e){
		Vec3D boxPos = p.sub(new Vec3D(e,e,e).scaleSelf((float) 0.5));
		AABB b = new AABB(boxPos,e);
		ArrayList inBox = pts.getPointsWithinBox(b);
		if (inBox ==null) return false;
		return true;
	}
	
	public ArrayList getWithinSphere(Vec3D p, float rad){
		return pts.getPointsWithinSphere(p,rad);
	}

	//-------------------------------------------------------------------------------------

	//Functions for creating DataMaps

	//-------------------------------------------------------------------------------------

	public void addDataMap(String name, String loc) {
		maps.put(name, new DataMap(loc,parent));
	}

	public void addNewDataMap(String name, int w, int h) {
		maps.put(name, new DataMap(w, h, parent.color(255, 255,255),name,parent));
	}

	//-------------------------------------------------------------------------------------

	//Functions for creating and removing Agents

	//-------------------------------------------------------------------------------------
	public void addAgents(ArrayList<Agent>agents){
		for(Agent a:agents){
			addAgent(a);
		}
	}
	
	public void addAgent(Agent a){
		addAgents.add(a);
	}
	
	public void addAgent(Plane3D loc){
		Agent a = new Agent(loc,false);
		addAgent(a);
	}
	
	public void remove(Agent a){
		removeAgents.add(a);
	}
	
	public void removeAll(){
		removeAgents.addAll(pop);
	}
	//-------------------------------------------------------------------------------------

	//Functions for importing and exporting

	//-------------------------------------------------------------------------------------
	
	
	void saveComponents(){
		ArrayList<String>lineList = new ArrayList<String>();
		for (Agent l: pop) {
			lineList.add(l.x +"," + l.y + "," + l.z +"/" + (l.xx.x) +"," + (l.xx.y) + "," + (l.xx.z)+"+"+l.x +"," + l.y + "," + l.z +"/" + (l.yy.x) +"," + (l.yy.y) + "," + (l.yy.z));
		}
		String[] skin = new String[lineList.size()];
		for (int i =0;i<lineList.size()-1;i++) {
			skin[i]=lineList.get(i);
		}
		parent.saveStrings("comps.txt", skin);
	}

	public void saveTrails(){
		ArrayList<String>lineList = new ArrayList<String>();
		for (Agent a: pop) {
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
	
	void saveMeshes(){
		//mesh.saveAsSTL(sketchPath("allMeshes.stl"));
	}
	
	public void saveTrailPlanes(){
		ArrayList<String>lineList = new ArrayList<String>();
		for (Agent a: pop) {
			if(a.trail.size()>4){
				String c = "";
				for(Link l:a.trail){
					c = c+l.a.x +"," + l.a.y + "," + l.a.z +"+" 
				        + l.a.xx.x +"," + (l.a.xx.y) + "," + (l.a.xx.z)+"+"+
							(l.a.yy.x) +"," + (l.a.yy.y) + "," + (l.a.yy.z)+"/";
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

	void saveMaps(){
		DataMap m = maps.get("ground");
		m.saveImage();
	}
	
	public void saveAgent(ArrayList<Link> a){
		trailLog.add(a);
	}

	//-------------------------------------------------------------------------------------

	//Functions for rendering and saving the environment

	//-------------------------------------------------------------------------------------
	
	public void render(){

	}

	
}