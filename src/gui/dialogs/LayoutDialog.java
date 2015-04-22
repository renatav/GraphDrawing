package gui.dialogs;

import graph.layout.Algorithms;
import gui.main.frame.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JComboBox<?> cbAlogorithms = new JComboBox<>(Algorithms.values());
	private boolean ok = false;

	public LayoutDialog(){
		setTitle("Choose layout algorithm");
		setLayout(new MigLayout());
		setSize(400,300);
		setModal(true);
		setLocationRelativeTo(MainFrame.getInstance());
		
		add(new JLabel("Algorithm:"));
		add(cbAlogorithms);
		
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
		pack();
		
	}
	
	public Algorithms getAlogithm(){
		return (Algorithms) cbAlogorithms.getSelectedItem();
	}

	public boolean isOk() {
		return ok;
	}

}
