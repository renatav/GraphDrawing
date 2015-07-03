package gui.main.frame;

import graph.elements.Graph;
import gui.actions.main.frame.ExitAction;
import gui.actions.main.frame.LoadAction;
import gui.actions.main.frame.NewGraphAction;
import gui.actions.main.frame.SaveAction;
import gui.actions.palette.AddVertexAction;
import gui.actions.palette.LayoutAction;
import gui.actions.palette.LinkAction;
import gui.actions.palette.SelectAction;
import gui.command.panel.CommandPanel;
import gui.model.GraphEdge;
import gui.model.GraphVertex;
import gui.properties.PropertiesPanel;
import gui.state.AddState;
import gui.state.LassoSelectState;
import gui.state.LinkState;
import gui.state.MoveState;
import gui.state.SelectState;
import gui.util.GuiUtil;
import gui.util.StatusBar;
import gui.view.GraphView;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
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
	private JToggleButton btnVertex = new JToggleButton(new AddVertexAction());
	private JToggleButton btnEdge = new JToggleButton(new LinkAction());
	private JToggleButton btnSelect = new JToggleButton(new SelectAction());
	private JPanel propertiesPanel;
	private CommandPanel commandPanel;

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
		
		JPanel centralPanel = new JPanel(new MigLayout("fill"));
		add(centralPanel, "grow");
		
		JPanel leftPanel = new JPanel(new MigLayout("fill"));
		pane = new JTabbedPane();
		leftPanel.add(pane, "grow");
		
		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JPanel palettePanel = new JPanel(new MigLayout());
		ButtonGroup group = new ButtonGroup();

		btnVertex = new JToggleButton(new AddVertexAction());
		palettePanel.add(btnVertex, "gapy 10px, wrap");
		group.add(btnVertex);

		btnEdge = new JToggleButton(new LinkAction());
		palettePanel.add(btnEdge, "wrap");
		group.add(btnEdge);
		
		btnSelect = new JToggleButton(new SelectAction());
		palettePanel.add(btnSelect, "wrap");
		group.add(btnSelect);
		
		palettePanel.add(new JButton(new LayoutAction()));
		
		
		
		propertiesPanel = new JPanel(new MigLayout("fill"));
		propertiesPanel.add(new JLabel("Properties"), "dock north");
		
		rightSplitPane.setLeftComponent(palettePanel);
		rightSplitPane.setRightComponent(propertiesPanel);
	
		commandPanel = new CommandPanel();
		leftPanel.add(commandPanel, "dock south");
		
		JSplitPane centralSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centralSplitPane.setRightComponent(rightSplitPane);
		centralSplitPane.setResizeWeight(0.9);
		centralSplitPane.setLeftComponent(leftPanel);
		centralPanel.add(centralSplitPane, "grow");
		
		statusBar = new StatusBar();
		add(statusBar, "height 20:20:20, dock south");

	}
	
	private void initMenu(){

		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem exitMi = new JMenuItem(new ExitAction());
		JMenuItem saveMi = new JMenuItem(new SaveAction());
		JMenuItem loadMi = new JMenuItem(new LoadAction());

		JMenu editMenu = new JMenu("Edit");
		JMenuItem newMi = new JMenuItem(new NewGraphAction());
		editMenu.add(newMi);
	

		fileMenu.add(saveMi);
		fileMenu.addSeparator();
		fileMenu.add(exitMi);
		fileMenu.add(loadMi);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
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
	public void addDiagram(GraphView view){
		pane.add(view);
	}
	
	public void setPropertiesPanel(PropertiesPanel panel){
		if (propertiesPanel.getComponentCount() > 1)
			propertiesPanel.remove(1);
		if (panel != null)
			propertiesPanel.add(panel, "grow");
		propertiesPanel.revalidate();
		propertiesPanel.repaint();
	}
	
	public void updateStatusBarPosition(Point2D position){
		statusBar.setPositionText((int)position.getX() + ", " + (int)position.getY());
	}

	public void changeToAdd(ElementsEnum elementType){
		btnVertex.setSelected(true);
		GraphView currentView = getCurrentView();
		currentView.setCurrentState(new AddState(currentView, ElementsEnum.VERTEX));
		statusBar.setLabelText("Add");
	}

	public void changeToLink(){
		btnEdge.setSelected(true);
		GraphView currentView = getCurrentView();
		currentView.setCurrentState(new LinkState(currentView));
		statusBar.setLabelText("Link");


	}

	public void changeToSelect(){
		btnSelect.setSelected(true);
		GraphView currentView = getCurrentView();
		currentView.setCurrentState(new SelectState(currentView));
		statusBar.setLabelText("Select");

	}
	
	public void changeToLassoSelect(){
		GraphView currentView = getCurrentView();
		currentView.setCurrentState(new LassoSelectState(currentView));
		statusBar.setLabelText("Lasso selection");
		
	}

	public void changeToMoveState(Point2D mousePosition){
		GraphView currentView = getCurrentView();
		currentView.setCurrentState(new MoveState(currentView.getSelectionModel().getSelectedVertices(), currentView, mousePosition));
		statusBar.setLabelText("Move state");
	}
}
