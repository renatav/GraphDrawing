package graph.layout;

/**
 * Lists all implemented layout algorithms
 * @author Renata
 */
public enum LayoutAlgorithms {

	AUTOMATIC("Automatic"), BALLOON("Balloon tree"), ORTHOGONAL("Orthogonal"),  RADIAL_TREE("Radial tree"),
	TREE("Level-based tree"), COMPACT_TREE("Compact tree"), HIERARCHICAL("Hierarchical"),
	 NODE_LINK_TREE("Node-link tree"), CONCENTRIC("Concentric symmetric"), CONVEX("Convex"), TUTTE("Tutte embedding"),
	KAMADA_KAWAI("Kamada-Kawai"), FRUCHTERMAN_REINGOLD("Fruchterman-Reingold"), SPRING("Spring"),
	CIRCLE("Circular"), CIRCLE_CENTER("Circular around a vertex"), BOX("Box"),
	ISOM("Isom"), FAST_ORGANIC("Fast organic"),
	ORGANIC("Organic");

	private String name;
	
	private LayoutAlgorithms(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static String[] names() {
		LayoutAlgorithms[] algorithms = values();
	    String[] names = new String[algorithms.length];

	    for (int i = 0; i < names.length; i++) {
	        names[i] = algorithms[i].name;
	    }
	    return names;
	}
	
	public static LayoutAlgorithms getValue(String name){
		for (LayoutAlgorithms al : values())
			if (al.getName().equals(name))
				return al;
		return null;
	}
	
}
