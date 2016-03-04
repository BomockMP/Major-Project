package pathfindingTools;

import core.Agent;
import voxelTools.VoxelGrid;

//credit to Kevin Glass of coke and code http://www.cokeandcode.com/main/tutorials/path-finding/
public class ClosestHeuristic implements AStarHeuristic {
	/**
	 * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int)
	 */
	public float getCost(VoxelGrid map, Agent mover, int x, int y, int z, int tx, int ty, int tz) {		
		float dx = tx - x;
		float dy = ty - y;
		float dz = tz - z;
		
		float result = (float) (Math.sqrt((dx*dx)+(dy*dy)+(dz*dz)));
		
		return result;
	}
}