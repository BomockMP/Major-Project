package projects.Boyd;





import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.volume.VolumetricSpaceArray;


public class MVoxelSet extends VolumetricSpaceArray {
	
	
	//TYPICAL VOXEL CELL ARRAY IS DATA
	
	protected float[] data;
	
	//ADD ANOTHER VALUE TO EACH VOXEL - TOXICITY

	protected float[] toxicity;
	
	public float fadeTimer;
	PApplet parent;

	public MVoxelSet(Vec3D scale, int resX, int resY, int resZ, PApplet _parent) {
		super(scale, resX, resY, resZ);
		// TODO Auto-generated constructor stub
		toxicity = new float[resX * resY * resZ];
		parent = _parent;
	}

	 //--------------------------------------------------------------------------------------------
	//FUNCTION FOR SETTING TOXICITY AT A SPECIFIC POINT
	 //--------------------------------------------------------------------------------------------
	 public final void setToxiAt(int index, float value) {
	        if (index >= 0 && index < toxicity.length) {
	        	toxicity[index] = value;
	        }
	    }
	
	 
	 //--------------------------------------------------------------------------------------------
		//FUNCTION FOR GETTING TOXICITY AT A SPECIFIC POINT
		 //--------------------------------------------------------------------------------------------
	 
	 public final float getToxiAt(int index) {
	        return toxicity[index];
	    }
	 
	 //--------------------------------------------------------------------------------------------
	 //FUNCTION FOR FADING TOXICITY ACROSS THE WHOLE GRID
	 //--------------------------------------------------------------------------------------------
	 public void fadeToxicity(float fadeSpeed){
		 
		 if (toxicity.length > 0 ){
		 for (int i = 0; i < toxicity.length; i++){
			 float t = getToxiAt(i);
			 
			// if (t > 0 && parent.millis() - fadeTimer >= 300){
				 //DIVIDE TOXICTY TO SIMULATE FADING
			float f = t*fadeSpeed;
			setToxiAt(i, f);
			//fadeTimer = parent.millis();
			//System.out.println("timer");
			// }
		 }
		 }

	 }
	 
	 //--------------------------------------------------------------------------------------------
	 //FUNCTION FOR TESTING TOXICITY value OF OVERALL VOXEL GRID
	 //--------------------------------------------------------------------------------------------
	 
	 public void getToxiOverall(){
		 if (toxicity.length > 0){
			 for (int i = 0; i < toxicity.length; i++){
				 float t = getToxiAt(i);
				 if (t > 0 ){
				 System.out.println(t);
				 }
			 }
			 }
	 }
	 
	 //--------------------------------------------------------------------------------------------
	 ///FUNCTIONING FOR RENDERING TOXICITY OF VOXEL GRID
	 //--------------------------------------------------------------------------------------------
	 
	 
	 
	 
	 
	
}
