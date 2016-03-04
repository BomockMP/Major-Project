package pointCloudTools;

import toxi.geom.AABB;

public interface Scanner {

	public float[] inAABB(AABB box);
	
	public float[][] inAABBColours(AABB box);

	
}
