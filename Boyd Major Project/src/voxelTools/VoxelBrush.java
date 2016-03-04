package voxelTools;

import toxi.geom.Vec3D;

/*------------------------------------

Class for modifying values in a voxelgrid
within a given sphere. Contains functions
for scaling the sphere of the brush and
interpolating values through the sphere.

------------------------------------*/

public class VoxelBrush {

	public float radius;
	public VoxelGrid volume;
	public float cellRadiusX,cellRadiusY,cellRadiusZ;
	public float stretchX,stretchY,stretchZ;

	public VoxelBrush(VoxelGrid v, float radius) {
		volume = v;
		setSize(radius);
	}
	
	public float interpExp(float a, float b, float f){
		return(a+(b-a)*(f*f));
	}

	public void drawAt(Vec3D p, int density) {
		int minX = (int) (p.x - cellRadiusX);
		int minY = (int) (p.y - cellRadiusY);
		int minZ = (int) (p.z - cellRadiusZ);
		int maxX = (int) (p.x + cellRadiusX);
		int maxY = (int) (p.y + cellRadiusY);
		int maxZ = (int) (p.z + cellRadiusZ);
		for (int z = minZ; z < maxZ; z++) {
			for (int y = minY; y < maxY; y++) {
				for (int x = minX; x < maxX; x++) {
					float dx = x - p.x;
					float dy = y - p.y;
					float dz = z - p.z;
					
					float d = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
					if (d <= cellRadiusX) {
						//Cell idx = volume.get(x, y, z);
						float v = interpExp(density, density/2, ((cellRadiusX-d)/cellRadiusX));
						volume.setValue(x, y,z,v);
					}
				}
			}
		}
	}
	
	public int max(int a, int b){
		int r = (a>b)?a:b;
		return r;
	}
	
	public int min(int a, int b){
		int r = (a<b)?a:b;
		return r;
	}

	public void setSize(float _radius) {
		radius = _radius;
		cellRadiusX = (int) (radius / volume.s.x);
		cellRadiusY = (int) (radius / volume.s.y);
		cellRadiusZ = (int) (radius / volume.s.z);
	}

}
