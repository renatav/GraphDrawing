package graph.layout.organic;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractJGraphXLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.HierarchicalProperties;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

public class JGraphHierarchicalLayouter<V extends Vertex, E extends Edge<V>> extends AbstractJGraphXLayouter<V, E> {

	public JGraphHierarchicalLayouter(){
		positionsEdges = true;
	}
	
	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		mxHierarchicalLayout hierarchicalLayouter = new mxHierarchicalLayout(jGraphXGraph);
		
		Boolean resizeParent = (Boolean)layoutProperties.getProperty(HierarchicalProperties.RESIZE_PARENT);
		hierarchicalLayouter.setResizeParent(resizeParent);
		
		if (resizeParent){
			Boolean moveParent = (Boolean)layoutProperties.getProperty(HierarchicalProperties.MOVE_PARENT);
			Integer parentBorder = (Integer)layoutProperties.getProperty(HierarchicalProperties.PARENT_BORDER);
			hierarchicalLayouter.setMoveParent(moveParent);
			if (parentBorder != null)
				hierarchicalLayouter.setParentBorder(parentBorder);
		}
		
		Integer intraCellSpacing = (Integer)layoutProperties.getProperty(HierarchicalProperties.INTRA_CELL_SPACING);
		if (intraCellSpacing != null)
			hierarchicalLayouter.setIntraCellSpacing(intraCellSpacing);
		
		Integer interRankCellSpacing = (Integer)layoutProperties.getProperty(HierarchicalProperties.INTER_RANK_CELL_SPACING);
		if (interRankCellSpacing != null)
			hierarchicalLayouter.setInterRankCellSpacing(interRankCellSpacing);
		
		Integer parentBorder = (Integer)layoutProperties.getProperty(HierarchicalProperties.INTER_HIERARCHY_SPACING);
		if (parentBorder != null)
			hierarchicalLayouter.setParentBorder(parentBorder);
		
		Integer interHierarchySpacing = (Integer)layoutProperties.getProperty(HierarchicalProperties.PARALLEL_EDGE_SPACING);
		if (interHierarchySpacing != null)
			hierarchicalLayouter.setInterHierarchySpacing(interHierarchySpacing);
		
		Integer orientation = (Integer)layoutProperties.getProperty(HierarchicalProperties.ORIENTATION);
		if (orientation != null)
			hierarchicalLayouter.setOrientation(orientation);
		
		Boolean fineTuning = (Boolean)layoutProperties.getProperty(HierarchicalProperties.FINE_TUNING);
		hierarchicalLayouter.setFineTuning(fineTuning);

		layouter = hierarchicalLayouter;

	}

}
