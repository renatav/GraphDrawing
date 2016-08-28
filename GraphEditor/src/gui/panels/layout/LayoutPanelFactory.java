package gui.panels.layout;

import graph.layout.LayoutAlgorithms;
import graph.layout.PropertyEnums.BalloonProperties;
import graph.layout.PropertyEnums.BoxProperties;
import graph.layout.PropertyEnums.CircleProperties;
import graph.layout.PropertyEnums.CompactTreeProperties;
import graph.layout.PropertyEnums.FastOrganicProperties;
import graph.layout.PropertyEnums.FruchtermanReingoldProperties;
import graph.layout.PropertyEnums.HierarchicalProperties;
import graph.layout.PropertyEnums.KamadaKawaiProperties;
import graph.layout.PropertyEnums.NodeLinkTreeProperties;
import graph.layout.PropertyEnums.OrganicProperties;
import graph.layout.PropertyEnums.PartitionProperties;
import graph.layout.PropertyEnums.RadialTree2Properties;
import graph.layout.PropertyEnums.RadialTreeProperties;
import graph.layout.PropertyEnums.SpringProperties;
import graph.layout.PropertyEnums.SymmetricProperties;
import graph.layout.PropertyEnums.TreeProperties;
import graph.layout.PropertyEnums.TutteProperties;

import java.util.HashMap;
import java.util.Map;

public class LayoutPanelFactory {
	
	private static Map<LayoutAlgorithms, LayoutPropertyPanel> panelsMap= new HashMap<LayoutAlgorithms, LayoutPropertyPanel>();
	
	public static LayoutPropertyPanel getPanel(LayoutAlgorithms al){
		if (panelsMap.containsKey(al))
			return panelsMap.get(al);
		
		LayoutPropertyPanel panel = null;
		if (al == LayoutAlgorithms.KAMADA_KAWAI)
			panel = new LayoutPropertyPanel(KamadaKawaiProperties.class);
		else if (al == LayoutAlgorithms.CONCENTRIC)
			panel = new SymmetricLayoutPanel(SymmetricProperties.class);
		else if (al == LayoutAlgorithms.TUTTE)
			panel = new LayoutPropertyPanel(TutteProperties.class);
		else if (al == LayoutAlgorithms.BOX)
			panel = new LayoutPropertyPanel(BoxProperties.class);
		else if (al == LayoutAlgorithms.CIRCLE || al == LayoutAlgorithms.CIRCLE_CENTER)
			panel = new LayoutPropertyPanel(CircleProperties.class);
		else if (al == LayoutAlgorithms.FRUCHTERMAN_REINGOLD)
			panel = new LayoutPropertyPanel(FruchtermanReingoldProperties.class);
		else if (al == LayoutAlgorithms.SPRING)
			panel = new LayoutPropertyPanel(SpringProperties.class);
		else if (al == LayoutAlgorithms.DAG)
			panel = new LayoutPropertyPanel(SpringProperties.class);
		else if (al == LayoutAlgorithms.TREE)
			panel = new LayoutPropertyPanel(TreeProperties.class);
		else if (al == LayoutAlgorithms.RADIAL_TREE)
			panel = new LayoutPropertyPanel(RadialTreeProperties.class);
		else if (al == LayoutAlgorithms.COMPACT_TREE)
			panel = new LayoutPropertyPanel(CompactTreeProperties.class);
		else if (al == LayoutAlgorithms.FAST_ORGANIC)
			panel = new LayoutPropertyPanel(FastOrganicProperties.class);
		else if (al == LayoutAlgorithms.ORGANIC)
			panel = new LayoutPropertyPanel(OrganicProperties.class);
		else if (al == LayoutAlgorithms.HIERARCHICAL)
			panel = new HierarchicalLayoutPanel(HierarchicalProperties.class);
		else if (al == LayoutAlgorithms.PARTITION)
			panel = new LayoutPropertyPanel(PartitionProperties.class);
		else if (al == LayoutAlgorithms.BALLOON)
			panel = new LayoutPropertyPanel(BalloonProperties.class);
		else if (al == LayoutAlgorithms.NODE_LINK_TREE)
			panel = new NodeLinkTreeLayoutPanel(NodeLinkTreeProperties.class);
		else if (al == LayoutAlgorithms.RADIAL_TREE2)
			panel = new LayoutPropertyPanel(RadialTree2Properties.class);
		panelsMap.put(al, panel);
		return panel;
	}
	
}
