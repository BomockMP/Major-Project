package voxelTools;

/*------------------------------------

Class that adds get+set functions and edge
properties to a float value. Used to store
values in the voxelgrid class. Extend to add CA
behaviours.

------------------------------------*/

public class Cell {
	
	float val;
	boolean io;
	public boolean edge;
	public int x,y,z;
	
	public Cell(float v, boolean _e, int _x, int _y, int _z){
		val =v;
		io=true;
		edge = _e;
		x = _x;
		y=_y;
		z=_z;
	}
	
	public Cell(int v, boolean _e, int _x, int _y, int _z){
		this((float)v, _e, _x,_y,_z);
	}
	
	public void set(float v){val=v;}
	public float get(){return val;}
	public boolean on(){return io;}
	public void turnOff(){io=false;}

}
