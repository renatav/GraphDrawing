package graph.layout.tree;

import prefuse.action.layout.graph.NodeLinkTreeLayout;
import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractPrefuseLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.NodeLinkTreeProperties;

public class PrefuseNodeLinkTreeLayouter<V extends Vertex, E extends Edge<V>> extends AbstractPrefuseLayouter<V, E>{

	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		NodeLinkTreeLayout nodeLinkTreeLayouter = new NodeLinkTreeLayout("graph");
		
		Integer orientation = (Integer) layoutProperties.getProperty(NodeLinkTreeProperties.ORIENTATION);
		nodeLinkTreeLayouter.setOrientation(orientation);
		
		Double bspace = (Double) layoutProperties.getProperty(NodeLinkTreeProperties.SPACING_SIBLINGS);
		if (bspace != null)
			nodeLinkTreeLayouter.setBreadthSpacing(bspace);
		
		Double dspace = (Double) layoutProperties.getProperty(NodeLinkTreeProperties.SPACING_DEPTH_LEVELS);
		if (dspace != null)
			nodeLinkTreeLayouter.setDepthSpacing(dspace);
		
		Double tspace = (Double) layoutProperties.getProperty(NodeLinkTreeProperties.SPACING_SUBTREES);
		if (tspace != null)
			nodeLinkTreeLayouter.setSubtreeSpacing(tspace);
		
		Double offset = (Double) layoutProperties.getProperty(NodeLinkTreeProperties.SPACING_ROOT_NODE);
		if (offset != null)
			nodeLinkTreeLayouter.setRootNodeOffset(offset);
		
		layouter = nodeLinkTreeLayouter;
		
	}

}
