package gui.properties;

import gui.model.GraphEdge;
import gui.model.GraphElement;
import gui.model.GraphVertex;

import java.util.HashMap;
import java.util.Map;

public class PropertiesFactory {
	
	private static Map<Class<?>, PropertiesPanel> map = new HashMap<Class<?>, PropertiesPanel>();
	
	public static PropertiesPanel getPropertiesPanel(GraphElement element){
		PropertiesPanel panel = map.get(element.getClass());
		if (panel == null){
			if (element instanceof GraphVertex)
				panel = new VertexPropertiesPanel();
			else if (element instanceof GraphEdge)
				panel = new EdgePropertiesPanel();
		}
		panel.setElement(element);
		return panel;
		
	}

}
