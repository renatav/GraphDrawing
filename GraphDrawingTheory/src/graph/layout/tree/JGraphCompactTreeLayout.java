package graph.layout.tree;

import com.mxgraph.layout.mxCompactTreeLayout;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractJGraphXLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.CompactTreeProperties;

public class JGraphCompactTreeLayout<V extends Vertex, E extends Edge<V>> extends AbstractJGraphXLayouter<V, E> {

	public JGraphCompactTreeLayout(){
		oneGraph = true;
	}
	
	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		boolean horizontal = (boolean) layoutProperties.getProperty(CompactTreeProperties.HORIZONTAL);
		boolean invert = (boolean) layoutProperties.getProperty(CompactTreeProperties.INVERT);
		Integer levelDistance = (Integer) layoutProperties.getProperty(CompactTreeProperties.LEVEL_DISTANCE);
		boolean resizeParents = (boolean) layoutProperties.getProperty(CompactTreeProperties.RESIZE_PARENTS);
		Integer nodeDistance = (Integer) layoutProperties.getProperty(CompactTreeProperties.NODE_DISTANCE);
		
		mxCompactTreeLayout treeLayout = new mxCompactTreeLayout(jGraphXGraph);
		treeLayout.setHorizontal(horizontal);
		treeLayout.setInvert(invert);
		if (levelDistance != null)
			treeLayout.setLevelDistance(levelDistance);
		treeLayout.setResizeParent(resizeParents);
		if (nodeDistance != null)
			treeLayout.setNodeDistance(nodeDistance);
		
		layouter = treeLayout;
		
	}
	

}
