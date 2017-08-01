package gui.dialogs;

import graph.layout.LayoutAlgorithms;
import graph.layout.DefaultGraphLayoutProperties;
import graph.layout.GraphLayoutProperties;
import gui.main.frame.MainFrame;
import gui.panels.layout.LayoutPanelFactory;
import gui.panels.layout.LayoutPropertyPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class LayoutDialog extends JDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<String> cbAlogorithms = new JComboBox<String>(LayoutAlgorithms.names());
	private boolean ok = false;
	private JPanel layoutPanelContainer = new JPanel();
	private LayoutPropertyPanel layoutPanel;

	public LayoutDialog(){
		setTitle("Choose layout algorithm");
		setLayout(new MigLayout("insets 10"));
		setSize(400,300);
		setModal(true);
		setLocationRelativeTo(MainFrame.getInstance());
		
		add(new JLabel("Algorithm:"));
		add(cbAlogorithms, "wrap");
		
		JPanel buttonsPanel = new JPanel();
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = true;
				setVisible(false);
			}
		});
		buttonsPanel.add(btnOk);
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		buttonsPanel.add(btnCancel);
		add(buttonsPanel,"dock south");
		
		add(layoutPanelContainer, "span 2");
		
		cbAlogorithms.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				layoutPanelContainer.removeAll();
				setLayoutPanel();
				layoutPanelContainer.revalidate();
				layoutPanelContainer.repaint();
				pack();
			}
		});
		
		setLayoutPanel();
		pack();
		
	}
	
	
	private void setLayoutPanel(){
		LayoutAlgorithms algorithm = LayoutAlgorithms.getValue((String) cbAlogorithms.getSelectedItem());
		layoutPanel = LayoutPanelFactory.getPanel(algorithm);
		if (layoutPanel != null){
			layoutPanel.setDefaultValue(DefaultGraphLayoutProperties.getDefaultLayoutProperties(algorithm, 
					MainFrame.getInstance().getCurrentView().getModel().getGraph()));
			layoutPanelContainer.add(layoutPanel);
		}
	}
	public LayoutAlgorithms getAlogithm(){
		return (LayoutAlgorithms) cbAlogorithms.getSelectedItem();
	}
	
	public GraphLayoutProperties getLayoutProperties(){
		if (layoutPanel != null)
			return layoutPanel.getEnteredLayoutProperties();
		return null;
	}

	public boolean isOk() {
		return ok;
	}

}
