package projects.sandbox.BeetleGroup;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import voxelTools.Cell;
import voxelTools.VoxelGrid;

public class PlumeVoxelSet extends VoxelGrid{

	public PlumeVoxelSet(int _w, int _h, int _d, Vec3D _s) {
		super(_w, _h, _d, _s);
		// TODO Auto-generated constructor stub
	}


	public void setValue (int x, int y, int z, float val) {
		int index = x + w * (y + h * z);
		vals[index].set(val);
	}
	
	@Override
	public void initGrid() {
		vals = new Cell[w*h*d];
		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				for (int k = 0; k<d; k++) {
					int index = i + w * (j + h * k);
					boolean edge = false;
					if(i==0 || i==w-1 || j==0 || j==h-1 || k==0 || k== d-1)edge = true;
					vals[index] = new Cell(0, edge,i,j,k);
				}
			}
		}
	}
	
	@Override
	public void render(int res, int threshold, float sf, PApplet parent) {
		int index = 0;
		parent.strokeWeight(1);
		for (int z=0; z<d; z+=res) {
			for (int y=0; y<h; y+=res) {
				for (int x=0; x<w; x+=res) {
					index = x + w * (y + h * z);
					float val = vals[index].get()*sf;
					if (val>threshold) {
						parent.stroke(255,0,0);
						parent.point(x*s.x,y*s.y,z*s.z);
						//Vec3D normal = getNormal(x, y, z, 2);
						//parent.line(x*s,y*s,z*s,x*s+normal.x*5,y*s+normal.y*5,z*s+normal.z*5);
					}
				}
			}
		}
	}
	

	
}
