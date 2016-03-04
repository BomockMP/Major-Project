package pathfindingTools;

public class Step{
	/** The x coordinate at the given step */
	private int x;
	/** The y coordinate at the given step */
	private int y;
	private int z;
	/**
	 * Create a new step
	 * 
	 * @param x The x coordinate of the new step
	 * @param y The y coordinate of the new step
	 */
	public Step(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Get the x coordinate of the new step
	 * 
	 * @return The x coodindate of the new step
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the y coordinate of the new step
	 * 
	 * @return The y coodindate of the new step
	 */
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return x*y*z;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object other) {
		if (other instanceof Step) {
			Step o = (Step) other;
			
			return (o.x == x) && (o.y == y) && (o.z==z);
		}
		
		return false;
	}
}
