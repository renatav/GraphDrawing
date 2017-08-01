package graph.layout;

/**
 * Lists all implemented layout algorithms
 * @author Renata
 */
public enum LayoutAlgorithms {

	AUTOMATIC("Automatic"), ORTHOGONAL("Orthogonal"), CONCENTRIC("Concentric symmetrix"), CONVEX("Convex"), TUTTE("Tutte embedding"), KAMADA_KAWAI("Kamada-Kawai"),
	FRUCHTERMAN_REINGOLD("Fruchterman-Reingold"), SPRING("Spring"), DAG("DAG"), CIRCLE("Circular"), CIRCLE_CENTER("Circular around vertex"), BOX("Box"),
	BALLOON("Balloon tree"), RADIAL_TREE("Radial tree"), TREE("Level-based tree"), COMPACT_TREE("Compact tree"), ISOM("Isom"), FAST_ORGANIC("Fast organic"),
	ORGANIC("Organic"), HIERARCHICAL("Hierarchical"),SPTING2("Spring 2"), NODE_LINK_TREE("Node-link tree"), RADIAL_TREE2("Radial tree 2");

	private String title;
	
	private LayoutAlgorithms(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
}
