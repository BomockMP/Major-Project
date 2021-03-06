package pathfindingTools;

import java.util.ArrayList;
import java.util.Collections;
import pathfindingTools.Node;
import core.Agent;
import voxelTools.VoxelGrid;
//credit to Kevin Glass of coke and code http://www.cokeandcode.com/main/tutorials/path-finding/

public class AStarPathFinder {
	/** The set of nodes that have been searched through */
	private ArrayList<Node> closed = new ArrayList<Node>();
	/** The set of nodes that we do not yet consider fully searched */
	private SortedList open = new SortedList();
	
	/** The map being searched */
	private VoxelGrid map;
	/** The maximum depth of search we're willing to accept before giving up */
	private int maxSearchDistance;
	
	/** The complete set of nodes across the map */
	private Node[][][] nodes;
	/** True if we allow diaganol movement */
	private boolean allowDiagMovement;
	/** The heuristic we're applying to determine which nodes to search first */
	private AStarHeuristic heuristic;
	
	private float targetValue;
	
	private int res;
	/**
	 * Create a path finder with the default heuristic - closest to target.
	 * 
	 * @param map The map to be searched
	 * @param maxSearchDistance The maximum depth we'll search before giving up
	 * @param allowDiagMovement True if the search should try diaganol movement
	 */
	public AStarPathFinder(VoxelGrid map, int maxSearchDistance, boolean allowDiagMovement, float targetValue, int res) {
		this(map, maxSearchDistance, allowDiagMovement, new ClosestHeuristic(), targetValue, res);
	}

	/**
	 * Create a path finder 
	 * 
	 * @param heuristic The heuristic used to determine the search order of the map
	 * @param map The map to be searched
	 * @param maxSearchDistance The maximum depth we'll search before giving up
	 * @param allowDiagMovement True if the search should try diaganol movement
	 */
	public AStarPathFinder(VoxelGrid map, int maxSearchDistance, 
						   boolean allowDiagMovement, AStarHeuristic heuristic, float targetValue, int res) {
		this.heuristic = heuristic;
		this.map = map;
		this.maxSearchDistance = maxSearchDistance;
		this.allowDiagMovement = allowDiagMovement;
		this.targetValue = targetValue;
		this.res = res;
				
		nodes = new Node[map.getW()/res][map.getH()/res][map.getD()/res];
		for (int x=0;x<map.getW()/res;x++) {
			for (int y=0;y<map.getH()/res;y++) {
				for (int z=0;z<map.getD()/res;z++) {
					nodes[x][y][z] = new Node(x,y,z);
				}
			}
		}
	}
	
	/**
	 * @see PathFinder#findPath(Mover, int, int, int, int)
	 */
	public Path findPath(Agent mover, int sx, int sy, int sz, int tx, int ty, int tz) {
		// easy first check, if the destination is blocked, we can't get there
		
		//TODO BLOCKING CHECK - FIX
		if (map.getValue(tx*res, ty*res, tz*res)<1) {
			System.out.println("empty target");
			return null;
		}
		
		// initial state for A*. The closed group is empty. Only the starting

		// tile is in the open list and it'e're already there
		nodes[sx][sy][sz].cost = 0;
		nodes[sx][sy][sz].depth = 0;
		closed.clear();
		open.clear();
		open.add(nodes[sx][sy][sz]);
		
		nodes[tx][ty][tz].parent = null;
		
		// while we haven'n't exceeded our max search depth
		int maxDepth = 0;
		while ((maxDepth < maxSearchDistance) && (open.size() != 0)) {
			// pull out the first node in our open list, this is determined to 

			// be the most likely to be the next step based on our heuristic

			Node current = getFirstInOpen();
			if (current == nodes[tx][ty][tz]) {
				break; //found the target
			}
			
			removeFromOpen(current);
			addToClosed(current);

			// search through all the neighbours of the current node evaluating

			// them as next steps

			for (int x=-1;x<2;x++) {
				for (int y=-1;y<2;y++) {
					for (int z=-1;z<2;z++) {
						// not a neighbour, its the current tile

						if ((x == 0) && (y == 0) && (z == 0)) {
							continue;
						}

						// if we're not allowing diaganal movement then only 

						// one of x or y can be set

						if (!allowDiagMovement) {
							if ((x != 0) && (y != 0)&& (z != 0)) {
								continue;
							}
						}

						// determine the location of the neighbour and evaluate it

						int xp = x + current.x;
						int yp = y + current.y;
						int zp = z + current.z;

						if (isValidLocation(mover,sx*res,sy*res,sz*res,xp*res,yp*res,zp*res)) {
							// the cost to get to this node is cost the current plus the movement

							// cost to reach this node. Note that the heursitic value is only used

							// in the sorted open list

							float nextStepCost = current.cost + getMovementCost(mover, current.x*res, current.y*res,current.z*res, xp*res, yp*res,zp*res);
							Node neighbour = nodes[xp][yp][zp];
							
							map.setValue(xp*res, yp*res, zp*res, 255); //just tracking what has been visited by the pathfinder

							// if the new cost we've determined for this node is lower than 

							// it has been previously makes sure the node hasn'e've
							// determined that there might have been a better path to get to

							// this node so it needs to be re-evaluated

							if (nextStepCost < neighbour.cost) {
								if (inOpenList(neighbour)) {
									removeFromOpen(neighbour);
								}
								if (inClosedList(neighbour)) {
									removeFromClosed(neighbour);
								}
							}

							// if the node hasn't already been processed and discarded then

							// reset it's cost to our current cost and add it as a next possible

							// step (i.e. to the open list)

							if (!inOpenList(neighbour) && !(inClosedList(neighbour))) {
								neighbour.cost = nextStepCost;
								neighbour.heuristic = getHeuristicCost(mover, xp, yp, zp, tx, ty,tz);
								maxDepth = Math.max(maxDepth, neighbour.setParent(current));
								addToOpen(neighbour);
							}
						}
					}
				}
			}
		}

		// since we'e've run out of search 
		// there was no path. Just return null

		if (nodes[tx][ty][tz].parent == null) {
			System.out.println("no path found");
			return null;
		}
		
		// At this point we've definitely found a path so we can uses the parent

		// references of the nodes to find out way from the target location back

		// to the start recording the nodes on the way.

		Path path = new Path();
		Node target = nodes[tx][ty][tz];
		while (target != nodes[sx][sy][sz]) {
			path.prependStep(target.x*res, target.y*res,target.z*res);
			target = target.parent;
		}
		path.prependStep(sx,sy,sz);
		
		// thats it, we have our path 

		return path;
	}

	/**
	 * Get the first element from the open list. This is the next
	 * one to be searched.
	 * 
	 * @return The first element in the open list
	 */
	public Node getFirstInOpen() {
		return (Node) open.first();
	}
	
	/**
	 * Add a node to the open list
	 * 
	 * @param node The node to be added to the open list
	 */
	public void addToOpen(Node node) {
		open.add(node);
	}
	
	/**
	 * Check if a node is in the open list
	 * 
	 * @param node The node to check for
	 * @return True if the node given is in the open list
	 */
	public boolean inOpenList(Node node) {
		return open.contains(node);
	}
	
	/**
	 * Remove a node from the open list
	 * 
	 * @param node The node to remove from the open list
	 */
	protected void removeFromOpen(Node node) {
		open.remove(node);
	}
	
	/**
	 * Add a node to the closed list
	 * 
	 * @param node The node to add to the closed list
	 */
	protected void addToClosed(Node node) {
		closed.add(node);
	}
	
	/**
	 * Check if the node supplied is in the closed list
	 * 
	 * @param node The node to search for
	 * @return True if the node specified is in the closed list
	 */
	protected boolean inClosedList(Node node) {
		return closed.contains(node);
	}
	
	/**
	 * Remove a node from the closed list
	 * 
	 * @param node The node to remove from the closed list
	 */
	protected void removeFromClosed(Node node) {
		closed.remove(node);
	}
	
	/**
	 * Check if a given location is valid for the supplied mover
	 * 
	 * @param mover The mover that would hold a given location
	 * @param sx The starting x coordinate
	 * @param sy The starting y coordinate
	 * @param x The x coordinate of the location to check
	 * @param y The y coordinate of the location to check
	 * @return True if the location is valid for the given mover
	 */
	protected boolean isValidLocation(Agent mover, int sx, int sy, int sz, int x, int y, int z) {
		boolean invalid = (x < 0) || (y < 0) ||(z < 0) || (x >= map.getW()) || (y >= map.getH()) || (z >= map.getD());
		
		if ((!invalid) && ((sx != x) || (sy != y) || (sz != z))) {
			
			//TODO BLOCKING CHECK - FIX
			invalid =  (Math.abs(targetValue-map.getValue(x,y,z))<50)?false:true;
		}
		
		return !invalid;
	}
	
	/**
	 * Get the cost to move through a given location
	 * 
	 * @param mover The entity that is being moved
	 * @param sx The x coordinate of the tile whose cost is being determined
	 * @param sy The y coordiante of the tile whose cost is being determined
	 * @param tx The x coordinate of the target location
	 * @param ty The y coordinate of the target location
	 * @return The cost of movement through the given tile
	 */
	public float getMovementCost(Agent mover, int sx, int sy, int sz, int tx, int ty, int tz) {
		//TODO update cost function
		return Math.abs((targetValue-map.getValue(tx,ty,tz))/255);
		//return 1;
		//return map.getCost(mover, sx, sy, sz, tx, ty, tz);
	}

	/**
	 * Get the heuristic cost for the given location. This determines in which 
	 * order the locations are processed.
	 * 
	 * @param mover The entity that is being moved
	 * @param x The x coordinate of the tile whose cost is being determined
	 * @param y The y coordiante of the tile whose cost is being determined
	 * @param tx The x coordinate of the target location
	 * @param ty The y coordinate of the target location
	 * @return The heuristic cost assigned to the tile
	 */
	public float getHeuristicCost(Agent mover, int x, int y, int z, int tx, int ty, int tz) {
		return heuristic.getCost(map, mover, x, y, z, tx, ty, tz);
	}
	
	/**
	 * A simple sorted list
	 *
	 * @author kevin
	 */
	private class SortedList {
		/** The list of elements */
		private ArrayList<Node> list = new ArrayList<Node>();
		
		/**
		 * Retrieve the first element from the list
		 *  
		 * @return The first element from the list
		 */
		public Node first() {
			return list.get(0);
		}
		
		/**
		 * Empty the list
		 */
		public void clear() {
			list.clear();
		}
		
		/**
		 * Add an element to the list - causes sorting
		 * 
		 * @param o The element to add
		 */
		public void add(Node o) {
			list.add(o);
			Collections.sort(list);
		}
		
		/**
		 * Remove an element from the list
		 * 
		 * @param o The element to remove
		 */
		public void remove(Node o) {
			list.remove(o);
		}
	
		/**
		 * Get the number of elements in the list
		 * 
		 * @return The number of element in the list
 		 */
		public int size() {
			return list.size();
		}
		
		/**
		 * Check if an element is in the list
		 * 
		 * @param o The element to search for
		 * @return True if the element is in the list
		 */
		public boolean contains(Node o) {
			return list.contains(o);
		}
	}
}
