package gui.panels.layout;

import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.NodeLinkTreeProperties;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class NodeLinkTreeLayoutPanel extends LayoutPropertyPanel{

	private static final long serialVersionUID = 1L;
	
	private JComboBox<String> cbOrientation;
	
	public NodeLinkTreeLayoutPanel(Class<?> enumClass) {
		super(enumClass);
		
		cbOrientation = new JComboBox<String>(new String[]{"Left to right", "Right to left", "Top to bottom", "Bottom to top"});
		add(new JLabel("Orientation"));
		add(cbOrientation);
	}
	
	
	@Override
	public GraphLayoutProperties getEnteredLayoutProperties(){
		GraphLayoutProperties layoutProperties = super.getEnteredLayoutProperties();
		String selected = (String) cbOrientation.getSelectedItem();
		Integer intValue = 0;
		if (selected.equals("Left to right"))
			intValue = 0;
		else if (selected.equals("Right to left"))
			intValue = 1;
		else if (selected.equalsIgnoreCase("Top to bottom"))
			intValue = 2;
		else if (selected.equals("Bottom to top"))
			intValue = 3;
		layoutProperties.setProperty(NodeLinkTreeProperties.ORIENTATION, intValue);
		return layoutProperties;
		
	}
}