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

		if (layoutProperties != null){
			Object resizeParent = layoutProperties.getProperty(HierarchicalProperties.RESIZE_PARENT);
			if (resizeParent != null){
				hierarchicalLayouter.setResizeParent((boolean) resizeParent);

				if ((boolean) resizeParent){
					Object moveParent = layoutProperties.getProperty(HierarchicalProperties.MOVE_PARENT);
					Object parentBorder = layoutProperties.getProperty(HierarchicalProperties.PARENT_BORDER);
					if (moveParent != null)
						hierarchicalLayouter.setMoveParent((boolean) moveParent);
					if (parentBorder != null)
						hierarchicalLayouter.setParentBorder((int) parentBorder);
				}
			}

			Object intraCellSpacing =layoutProperties.getProperty(HierarchicalProperties.INTRA_CELL_SPACING);
			if (intraCellSpacing != null)
				hierarchicalLayouter.setIntraCellSpacing((double) intraCellSpacing);

			Object interRankCellSpacing = layoutProperties.getProperty(HierarchicalProperties.INTER_RANK_CELL_SPACING);
			if (interRankCellSpacing != null)
				hierarchicalLayouter.setInterRankCellSpacing((double) interRankCellSpacing);

			Object parentBorder = layoutProperties.getProperty(HierarchicalProperties.INTER_HIERARCHY_SPACING);
			if (parentBorder != null)
				hierarchicalLayouter.setParentBorder((int) parentBorder);

			Object interHierarchySpacing = layoutProperties.getProperty(HierarchicalProperties.PARALLEL_EDGE_SPACING);
			if (interHierarchySpacing != null)
				hierarchicalLayouter.setInterHierarchySpacing((double) interHierarchySpacing);

			Object orientation = layoutProperties.getProperty(HierarchicalProperties.ORIENTATION);
			if (orientation != null)
				hierarchicalLayouter.setOrientation((int) orientation);

			Object fineTuning = layoutProperties.getProperty(HierarchicalProperties.FINE_TUNING);
			if (fineTuning != null)
				hierarchicalLayouter.setFineTuning((boolean)fineTuning);
		}

		layouter = hierarchicalLayouter;

	}

}
