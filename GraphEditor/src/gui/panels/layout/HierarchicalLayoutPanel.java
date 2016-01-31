package gui.panels.layout;

import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.HierarchicalProperties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class HierarchicalLayoutPanel extends LayoutPropertyPanel{

	private static final long serialVersionUID = 1L;
	
	private JComboBox<String> cbOrientation;
	
	public HierarchicalLayoutPanel(Class<?> enumClass) {
		super(enumClass);
		
		cbOrientation = new JComboBox<String>(new String[]{"North", "South", "West", "East"});
		add(new JLabel("Orientation"));
		add(cbOrientation);
	}
	
	
	@Override
	public GraphLayoutProperties getEnteredLayoutProperties(){
		GraphLayoutProperties layoutProperties = super.getEnteredLayoutProperties();
		String selected = (String) cbOrientation.getSelectedItem();
		Integer intValue = 0;
		if (selected.equals("North"))
			intValue = SwingConstants.NORTH;
		else if (selected.equals("South"))
			intValue = SwingConstants.SOUTH;
		else if (selected.equalsIgnoreCase("West"))
			intValue = SwingConstants.WEST;
		else if (selected.equals("East"))
			intValue = SwingConstants.EAST;
		layoutProperties.setProperty(HierarchicalProperties.ORIENTATION, intValue);
		return layoutProperties;
		
	}


}
