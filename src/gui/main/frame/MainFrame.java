package gui.main.frame;

import graph.elements.Graph;
import gui.actions.main.frame.ExitAction;
import gui.actions.main.frame.NewGraphAction;
import gui.actions.palette.AddVertexAction;
import gui.actions.palette.LinkAction;
import gui.actions.palette.SelectAction;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.state.AddState;
import gui.state.LinkState;
import gui.state.SelectState;
import gui.util.GuiUtil;
import gui.util.StatusBar;
import gui.view.GraphView;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar;
	private static MainFrame instance;
	private JTabbedPane pane;
	private JToolBar toolBar;
	private StatusBar statusBar;
	private JToolBar palette;
	private JToggleButton btnVertex;

	public MainFrame(){

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(800, 600);
		setTitle("Graph drawing");
		setLocationRelativeTo(null);
		setLayout(new MigLayout("fill"));

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (GuiUtil.showConfirmDialog("Close application?") == JOptionPane.YES_OPTION)
					System.exit(0);

			}

		});

		initMenu();
		initToolBar();

		initGui();

	}

	private void initGui(){

		pane = new JTabbedPane();
		add(pane, "grow");

		statusBar = new StatusBar();
		add(statusBar, "height 20:20:20, dock south");

		palette = new JToolBar(JToolBar.VERTICAL);
		ButtonGroup group = new ButtonGroup();

		JToggleButton btnVertex = new JToggleButton(new AddVertexAction());
		palette.add(btnVertex);
		group.add(btnVertex);

		JToggleButton btnEdge = new JToggleButton(new LinkAction());
		palette.add(btnEdge);
		group.add(btnEdge);
		
		JToggleButton btnSelect = new JToggleButton(new SelectAction());
		palette.add(btnSelect);
		group.add(btnSelect);


		add(palette, "dock east");

	}

	private void initMenu(){

		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem exitMi = new JMenuItem(new ExitAction());

		JMenu editMenu = new JMenu("Edit");
		JMenuItem newMi = new JMenuItem(new NewGraphAction());
		editMenu.add(newMi);
		menuBar.add(editMenu);

		fileMenu.add(exitMi);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
	}

	private void initToolBar(){
		toolBar = new JToolBar();
		add(toolBar, "dock north");
		toolBar.add(new NewGraphAction());
	}

	public static MainFrame getInstance(){
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}


	public GraphView getCurrentView(){
		if (pane.getComponentCount() > 0)
			return (GraphView) pane.getSelectedComponent();
		return null;
	}
	public void addNewDiagram(){
		Graph<GraphVertex, GraphEdge> graph = new Graph<GraphVertex, GraphEdge>();
		GraphView view = new GraphView(graph);
		pane.add(view);

	}

	public void changeToAdd(ElementsEnum elementType){
		GraphView currentView = getCurrentView();
		currentView.setCurrentState(new AddState(currentView, ElementsEnum.VERTEX));
		statusBar.setLabelText("Add");
	}

	public void changeToLink(){
		GraphView currentView = getCurrentView();
		currentView.setCurrentState(new LinkState(currentView));
		statusBar.setLabelText("Link");


	}

	public void changeToSelect(){
		GraphView currentView = getCurrentView();
		currentView.setCurrentState(new SelectState(currentView));
		statusBar.setLabelText("Select");

	}

}
