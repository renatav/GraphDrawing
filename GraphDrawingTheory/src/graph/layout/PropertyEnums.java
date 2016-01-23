package graph.layout;

public interface PropertyEnums {
	
	
	//TODO ubaciti ovde tip
	//pa onda adjust for gravity za ovaj properties
	//da moze da bude check box
	//
	
	public enum KamadaKawaiProperties implements PropertyEnums{
		DISCONNECTED_DISTANCE_MULTIPLIER("Disconnected distance multiplier", false), LENGTH_FACTOR ("Length factor", false),
		MAXIMUM_ITERATIONS("Maximum itterations", false);
		
		private String name;
		private boolean hidden;
		
		KamadaKawaiProperties(String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
		
	}
	
	public enum SymmetricProperties implements PropertyEnums{
		DISTANCE("Distance", false), PERMUTATION("Permutation", false), CENTER("Center", true);
		
		private String name;
		private boolean hidden;
		
		SymmetricProperties(String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
	}
	
	public enum TutteProperties implements PropertyEnums{
		DISTANCE("Distance", false), FACE("Face", false), PERMUTATION("Permutation", false), CENTER("Center", true);
		
		private String name;
		private boolean hidden;
		
		TutteProperties(String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
	}
	

	
	public enum BoxProperties implements PropertyEnums{
		PROPERY("Test property", false);
		
		private String name;
		private boolean hidden;
		
		BoxProperties (String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
	}
	
	public enum CircleProperties implements PropertyEnums{
		DISTANCE("Distance", false);
		
		private String name;
		private boolean hidden;
		
		CircleProperties (String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
	}
	
	
	public enum FruchtermanReingoldProperties implements PropertyEnums{
		ATTRACTION_MULTIPLIER("Attraction multiplier", false), REPULSION_MULTIPLIER("Repulsion multiplier", false),
		MAXIMUM_ITERATIONS("Maximum iterations", false);
		
		private String name;
		private boolean hidden;
		
		FruchtermanReingoldProperties (String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
	}
	
	public enum SpringProperties implements PropertyEnums{
		STRETCH("Stretch", false), REPULSION_RANGE("Repulsion range", false),
		FORCE_MULTIPLIER("Force multiplier", false);
		
		private String name;
		private boolean hidden;
		
		SpringProperties (String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
	}
	
	public enum BalloonProperties implements PropertyEnums{
		PROPERY("Test property", false);
		
		private String name;
		private boolean hidden;
		
		BalloonProperties (String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
	}
	
	public enum RadialTreeProperties implements PropertyEnums{
		X_DISTANCE("X distance", false), Y_DISTANCE("Y distance", false);
		
		private String name;
		private boolean hidden;
		
		RadialTreeProperties (String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
	}
	
	public enum TreeProperties implements PropertyEnums{
		X_DISTANCE("X distance", false), Y_DISTANCE("Y distance", false);
		
		
		private String name;
		private boolean hidden;
		
		TreeProperties (String name, boolean hidden){
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isHidden(){
			return hidden;
		}
	}
	

}
