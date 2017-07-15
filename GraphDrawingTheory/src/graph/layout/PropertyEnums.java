package graph.layout;

/**
 * List of all properties for each implemented layout algorithm
 * Each enum contains values used for generis implementation
 * of the user interface
 * @author Renata
 */
public interface PropertyEnums {


	public enum KamadaKawaiProperties implements PropertyEnums{
		DISCONNECTED_DISTANCE_MULTIPLIER("Disconnected distance multiplier", false, Double.class), LENGTH_FACTOR ("Length factor", false, Double.class),
		MAXIMUM_ITERATIONS("Maximum iterations", false, Integer.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		KamadaKawaiProperties(String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}

	}

	public enum SymmetricProperties implements PropertyEnums{
		DISTANCE("Distance", false, Double.class), PERMUTATION("Permutation", false, String.class), CENTER("Center", true, String.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		SymmetricProperties(String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum TutteProperties implements PropertyEnums{
		DISTANCE("Distance", false, Double.class),
		CENTER("Center", true, String.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		TutteProperties(String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}



	public enum BoxProperties implements PropertyEnums{
		COLUMNS("Number of columns", false, Integer.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		BoxProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum CircleProperties implements PropertyEnums{
		DISTANCE("Distance", false, Integer.class), OPTIMIZE_CROSSINGS("Optimize crossings", false,Boolean.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		CircleProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}


	public enum FruchtermanReingoldProperties implements PropertyEnums{
		ATTRACTION_MULTIPLIER("Attraction multiplier", false, Double.class), REPULSION_MULTIPLIER("Repulsion multiplier", false, Double.class),
		MAXIMUM_ITERATIONS("Maximum iterations", false, Integer.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		FruchtermanReingoldProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum SpringProperties implements PropertyEnums{
		STRETCH("Stretch", false, Double.class), REPULSION_RANGE("Repulsion range", false, Integer.class),
		FORCE_MULTIPLIER("Force multiplier", false, Double.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		SpringProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}


	public enum RadialTreeProperties implements PropertyEnums{
		X_DISTANCE("X distance", false, Integer.class), Y_DISTANCE("Y distance", false, Integer.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		RadialTreeProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum TreeProperties implements PropertyEnums{
		X_DISTANCE("X distance", false, Integer.class), Y_DISTANCE("Y distance", false, Integer.class);


		private String name;
		private boolean hidden;
		private Class<?> type;

		TreeProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum CompactTreeProperties implements PropertyEnums{
		HORIZONTAL("Horizontal", false, Boolean.class), INVERT("Invert", false, Boolean.class),
		RESIZE_PARENTS("Resize parents", false, Boolean.class), LEVEL_DISTANCE("Level distance", false, Integer.class),
		NODE_DISTANCE("Node distance", false, Integer.class);


		private String name;
		private boolean hidden;
		private Class<?> type;

		CompactTreeProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum  FastOrganicProperties implements PropertyEnums{
		FORCE_CONSTANT("Force constant", false, Double.class), MINIMAL_DISTANCE_LIMIT("Minimal distance limit", false, Double.class),
		INITIAL_TEMP("Initial temperature", false, Double.class), MAX_ITERATIONS("Iterations ", false, Double.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		FastOrganicProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum  OrganicProperties implements PropertyEnums{

		IS_OPTIMIZE_EDGE_CROSSING("Should optimize edge crossing", false, Boolean.class),
		EDGE_CROSSING_FACTOR("Edge crossing factor", false, Double.class),
		IS_OPTIMIZE_EDGE_DISTANCE("Should optimize edge distance", false, Boolean.class),
		EDGE_DISTANCE_FACTOR("Edge distance factor", false, Double.class),
		IS_OPTIMIZE_BORDER_LINE("Should optimize border line", false, Boolean.class),
		BORDER_LINE_FACTOR("Border line factor", false, Double.class),
		IS_OPTIMIZE_NODE_DISTRIBUTION("Should optimize node distribution", false, Boolean.class),
		NODE_DISTRIBUTION_FACTOR("Node distribution factor", false, Double.class),
		IS_FINE_TUNING("Should fine tune", false, Boolean.class),
		FINE_TUNING_RADIUS("Fine tuning radius", false, Double.class),
		AVERAGE_NODE_AREA("Average node area", false, Double.class),
		AVERAGE_SCALE_FACTOR("Average scale factor (between 0 and 1)", false, Double.class),
		MAX_ITERATIONS("Iterations", false, Integer.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		OrganicProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum HierarchicalProperties implements PropertyEnums{
		RESIZE_PARENT("Resize parent", false, Boolean.class),
		MOVE_PARENT("Move parent", false, Boolean.class),
		PARENT_BORDER("Parent border", false, Integer.class),
		INTRA_CELL_SPACING("Spacing between cells on the same layer", false, Double.class),
		INTER_RANK_CELL_SPACING("Space between cells on adjacent layers", false, Double.class),
		INTER_HIERARCHY_SPACING("Soace between unconnected hierarchies", false, Double.class),
		PARALLEL_EDGE_SPACING("Distance between parallel edges on each rank", false, Double.class),
		ORIENTATION("Orientation", true, Integer.class),
		FINE_TUNING("Perform fine tuning", false, Boolean.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		HierarchicalProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum StackProperties implements PropertyEnums{

		HORIZONTAL("Horizontal", false, Boolean.class),
		SPACING("Spacing between cells", false, Integer.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		StackProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}

	public enum PartitionProperties implements PropertyEnums{

		HORIZONTAL("Horizontal", false, Boolean.class),
		SPACING("Spacing between cells", false, Integer.class),
		BORDER("Border", false, Integer.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		PartitionProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}
	
	public enum BalloonProperties implements PropertyEnums{

		MIN_RADIUS("Minimal radius", false, Integer.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		BalloonProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}
	
	public enum NodeLinkTreeProperties implements PropertyEnums{

		ORIENTATION("Orientation", true, Integer.class),
		SPACING_SIBLINGS("Spacing between sibling nodes", false, Double.class),
		SPACING_SUBTREES("Spacing between subtreess", false, Double.class),
		SPACING_DEPTH_LEVELS("Spacing between depth levels", false, Double.class),
		SPACING_ROOT_NODE("Offset for root node position", false, Double.class);

		private String name;
		private boolean hidden;
		private Class<?> type;

		NodeLinkTreeProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}
	
	public enum RadialTree2Properties implements PropertyEnums{

		RADIUS_INCREMENT("Radius increment", false, Double.class),
		AUSTO_SCALE("Auto scale", false, Boolean.class);
		
		private String name;
		private boolean hidden;
		private Class<?> type;

		RadialTree2Properties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}
	
	public enum SquarifiedTreemapProperties implements PropertyEnums{

		FRAME("Framing space", false, Double.class);
		
		private String name;
		private boolean hidden;
		private Class<?> type;

		SquarifiedTreemapProperties (String name, boolean hidden, Class<?> type){
			this.name = name;
			this.hidden = hidden;
			this.type = type;
		}

		public String getName(){
			return name;
		}

		public boolean isHidden(){
			return hidden;
		}

		public Class<?> getType(){
			return type;
		}
	}
	
	


}
