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
		
		Object horizontal = layoutProperties.getProperty(CompactTreeProperties.HORIZONTAL);
		Object invert = layoutProperties.getProperty(CompactTreeProperties.INVERT);
		Object levelDistance = layoutProperties.getProperty(CompactTreeProperties.LEVEL_DISTANCE);
		Object resizeParents = layoutProperties.getProperty(CompactTreeProperties.RESIZE_PARENTS);
		Object nodeDistance = layoutProperties.getProperty(CompactTreeProperties.NODE_DISTANCE);
		
		mxCompactTreeLayout treeLayout = new mxCompactTreeLayout(jGraphXGraph);
		if (horizontal != null)
			treeLayout.setHorizontal((boolean)horizontal);
		if (invert != null)
			treeLayout.setInvert((boolean) invert);
		if (levelDistance != null)
			treeLayout.setLevelDistance((int)levelDistance);
		if (resizeParents != null)
			treeLayout.setResizeParent((boolean)resizeParents);
		if (nodeDistance != null)
			treeLayout.setNodeDistance((int)nodeDistance);
		
		layouter = treeLayout;
		
	}
	

}
