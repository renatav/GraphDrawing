package gui.panels.layout;

import graph.layout.LayoutAlgorithms;
import graph.layout.PropertyEnums.BoxProperties;
import graph.layout.PropertyEnums.CircleProperties;
import graph.layout.PropertyEnums.FruchtermanReingoldProperties;
import graph.layout.PropertyEnums.KamadaKawaiProperties;
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
			panel = new SymmetricLayoutPanel(TutteProperties.class);
		else if (al == LayoutAlgorithms.BOX)
			panel = new LayoutPropertyPanel(BoxProperties.class);
		else if (al == LayoutAlgorithms.CIRCLE)
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
		
		panelsMap.put(al, panel);
		return panel;
	}
	
}
