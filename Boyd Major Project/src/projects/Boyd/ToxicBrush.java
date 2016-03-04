package projects.Boyd;

import toxi.volume.VolumetricBrush;


import java.util.ArrayList;
import java.util.logging.Level;

import toxi.geom.Vec3D;
import toxi.math.MathUtils;

public class ToxicBrush extends VolumetricBrush {

	protected float radius, radSquared;
	MVoxelSet volume;
	public float toxicThreshold = 1; //above this agent will be affected by toxicity 


	//arraylist for storing toxin values temporarily 
	public ArrayList<Float> toxWithinRad;


	public ToxicBrush(MVoxelSet  _volume, float _radius) {
		super(_volume);

		volume = _volume;
		radius = _radius;
		setSize(radius);



	}

	///----------------------------------------------------------------
	/////NEW FUNCTIONS FOR CHANGING TOXICITY VALUE OF CELLS
	///----------------------------------------------------------------

	public void drawToxAtAbsolutePos(Vec3D pos, float density) {
		float cx = MathUtils.clip((pos.x + volume.halfScale.x) / volume.scale.x
				* volume.resX1, 0, volume.resX1);
		float cy = MathUtils.clip((pos.y + volume.halfScale.y) / volume.scale.y
				* volume.resY1, 0, volume.resY1);
		float cz = MathUtils.clip((pos.z + volume.halfScale.z) / volume.scale.z
				* volume.resZ1, 0, volume.resZ1);
		ToxAtGridPos(cx, cy, cz, density);
	}


	public void ToxAtGridPos(float cx, float cy, float cz, float density) {
		int minX = MathUtils.max(Math.round(cx - cellRadiusX), 0);
		int minY = MathUtils.max(Math.round(cy - cellRadiusY), 0);
		int minZ = MathUtils.max(Math.round(cz - cellRadiusZ), 0);
		int maxX = MathUtils.min(Math.round(cx + cellRadiusX), volume.resX);
		int maxY = MathUtils.min(Math.round(cy + cellRadiusY), volume.resY);
		int maxZ = MathUtils.min(Math.round(cz + cellRadiusZ), volume.resZ);
		for (int z = minZ; z < maxZ; z++) {
			float dz = (z - cz) * stretchZ;
			dz *= dz;
			for (int y = minY; y < maxY; y++) {
				float dyz = (y - cy) * stretchY;
				dyz = dyz * dyz + dz;
				for (int x = minX; x < maxX; x++) {
					float dx = x - cx;
					float d = (float) Math.sqrt(dx * dx + dyz);
					if (d <= cellRadiusX) {
						float cellVal = (1 - d / cellRadiusX) * density;
						updateTox(x, y, z, cellVal);
					}
				}
			}
		}
	}

	//  volume.setToxiAt(idx, brushMode.apply(volume.getVoxelAt(idx), cellVal));

	protected final void updateTox(int x, int y, int z, float cellVal) {
		int idx = volume.getIndexFor(x, y, z);
		volume.setToxiAt(idx, brushMode.apply(volume.getVoxelAt(idx), cellVal));
	}


	///----------------------------------------------------------------
	/*FUNCTION FOR 
	 * 1) FINDING VOXELS WITHIN A RADIUS
	 * 2) TESTING VOXELS FOR TOXICITY
	 * 3) FINDING THEIR POSITION AS A VECTOR
	 *
	 * 
	 */
	///----------------------------------------------------------------

	public Vec3D getPosOfToxicVoxels(Vec3D pos, float searchRadius){

		//Vec3d for storing position of highest toxicity value within search rad
		Vec3D toxPos = new Vec3D();
		//boolean for making sure array is always populated..must be a better way of doing this
		float maxToxicity = 0;

		//GET VOXELS WITHIN SEARCH RADIUS

		float cx = MathUtils.clip((pos.x + volume.halfScale.x) / volume.scale.x
				* volume.resX1, 0, volume.resX1);
		float cy = MathUtils.clip((pos.y + volume.halfScale.y) / volume.scale.y
				* volume.resY1, 0, volume.resY1);
		float cz = MathUtils.clip((pos.z + volume.halfScale.z) / volume.scale.z
				* volume.resZ1, 0, volume.resZ1);

		int minX = MathUtils.max(Math.round(cx - searchRadius), 0);
		int minY = MathUtils.max(Math.round(cy - searchRadius), 0);
		int minZ = MathUtils.max(Math.round(cz - searchRadius), 0);
		int maxX = MathUtils.min(Math.round(cx + searchRadius), volume.resX);
		int maxY = MathUtils.min(Math.round(cy + searchRadius), volume.resY);
		int maxZ = MathUtils.min(Math.round(cz + searchRadius), volume.resZ);

		for (int z = minZ; z < maxZ; z++) {
			float dz = (z - cz) * stretchZ;
			dz *= dz;
			for (int y = minY; y < maxY; y++) {
				float dyz = (y - cy) * stretchY;
				dyz = dyz * dyz + dz;
				for (int x = minX; x < maxX; x++) {
					float dx = x - cx;
					float d = (float) Math.sqrt(dx * dx + dyz);
					if (d <= searchRadius) {
						
						//CHECK VOXELS FOR TOXICITY VALUE
						int idx = volume.getIndexFor((int)x, (int)y, (int)z);
						float currentToxicityAtPoint = volume.getToxiAt(idx);
						if(currentToxicityAtPoint>maxToxicity){
							//found a point thats more toxic than anything before
							maxToxicity = currentToxicityAtPoint;
							toxPos = new Vec3D(x,y,z);
						}

					}
				}
			}
		}
		// System.out.println(toxPos);
		return toxPos;

	}

















	////OVERRIDED FUNCTIONS FROM VOLUMEBRUSH
	public void drawAtAbsolutePos(Vec3D pos, float density) {
		float cx = MathUtils.clip((pos.x + volume.halfScale.x) / volume.scale.x
				* volume.resX1, 0, volume.resX1);
		float cy = MathUtils.clip((pos.y + volume.halfScale.y) / volume.scale.y
				* volume.resY1, 0, volume.resY1);
		float cz = MathUtils.clip((pos.z + volume.halfScale.z) / volume.scale.z
				* volume.resZ1, 0, volume.resZ1);
		drawAtGridPos(cx, cy, cz, density);
	}


	@Override
	public void drawAtGridPos(float cx, float cy, float cz, float density) {
		int minX = MathUtils.max(Math.round(cx - cellRadiusX), 0);
		int minY = MathUtils.max(Math.round(cy - cellRadiusY), 0);
		int minZ = MathUtils.max(Math.round(cz - cellRadiusZ), 0);
		int maxX = MathUtils.min(Math.round(cx + cellRadiusX), volume.resX);
		int maxY = MathUtils.min(Math.round(cy + cellRadiusY), volume.resY);
		int maxZ = MathUtils.min(Math.round(cz + cellRadiusZ), volume.resZ);
		for (int z = minZ; z < maxZ; z++) {
			float dz = (z - cz) * stretchZ;
			dz *= dz;
			for (int y = minY; y < maxY; y++) {
				float dyz = (y - cy) * stretchY;
				dyz = dyz * dyz + dz;
				for (int x = minX; x < maxX; x++) {
					float dx = x - cx;
					float d = (float) Math.sqrt(dx * dx + dyz);
					if (d <= cellRadiusX) {
						float cellVal = (1 - d / cellRadiusX) * density;
						updateVoxel(x, y, z, cellVal);
					}
				}
			}
		}
	}

	@Override
	public void setSize(float radius) {
		this.radius = radius;
		this.cellRadiusX = (int) (radius / volume.scale.x * volume.resX + 1);
		this.cellRadiusY = (int) (radius / volume.scale.y * volume.resY + 1);
		this.cellRadiusZ = (int) (radius / volume.scale.z * volume.resZ + 1);
		stretchY = (float) cellRadiusX / cellRadiusY;
		stretchZ = (float) cellRadiusX / cellRadiusZ;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("new brush size: " + radius);
		}
	}

}
