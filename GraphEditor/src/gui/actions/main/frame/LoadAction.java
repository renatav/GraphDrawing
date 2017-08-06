package gui.actions.main.frame;

import gui.main.frame.MainFrame;
import gui.model.GraphModel;
import gui.view.GraphView;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class LoadAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private XStream xstream;

	public LoadAction(){
		putValue(NAME, "Load");
		putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/gui/resources/load.png")));
		putValue(SHORT_DESCRIPTION, "Load graph");
		KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		putValue(ACCELERATOR_KEY,ctrlO);
		xstream = new XStream(new StaxDriver());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser fileChooser = new JFileChooser();
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			    "Graph files", "graph");
		fileChooser.setFileFilter(filter);
		
		File f;
		if (fileChooser.showOpenDialog(MainFrame.getInstance()) == JFileChooser.APPROVE_OPTION){
			f = fileChooser.getSelectedFile();
		}
		else
			return;
		
		GraphModel model = (GraphModel) xstream.fromXML(f);
		GraphView view = new GraphView(model);
		MainFrame.getInstance().addDiagram(view, f.getName().substring(0, f.getName().length()-6));
		view.repaint();
		
	}

}

