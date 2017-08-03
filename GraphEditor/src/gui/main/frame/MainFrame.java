package gui.main.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import graph.algorithm.AlgorithmExecutor;
import graph.algorithm.ExecuteResult;
import graph.algorithm.cycles.SimpleCyclesFinder;
import graph.algorithms.planarity.FraysseixMendezPlanarity;
import graph.algorithms.planarity.PlanarityTestingAlgorithm;
import graph.elements.Graph;
import graph.properties.components.SplitPair;
import graph.properties.splitting.AlgorithmErrorException;
import graph.properties.splitting.HopcroftTarjanSplitting;
import graph.properties.splitting.SeparationPairSplitting;
import graph.symmetry.Permutation;
import graph.symmetry.nauty.McKayGraphLabelingAlgorithm;
import gui.actions.main.frame.ExitAction;
import gui.actions.main.frame.LoadAction;
import gui.actions.main.frame.NewGraphAction;
import gui.actions.main.frame.SaveAction;
import gui.actions.palette.AddVertexAction;
import gui.actions.palette.LayoutAction;
import gui.actions.palette.LinkAction;
import gui.actions.palette.SelectAction;
import gui.actions.toolbar.RedoAction;
import gui.actions.toolbar.RemoveAction;
import gui.actions.toolbar.UndoAction;
import gui.command.panel.CommandPanel;
import gui.components.CloseableTabComponent;
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
	private NewGraphAction newGraphAction = new NewGraphAction();
	private LoadAction loadAction = new LoadAction();
	private SaveAction saveAction = new SaveAction();
	private RemoveAction removeAction = new RemoveAction();
	private RedoAction redoAction = new RedoAction();
	private UndoAction undoAction = new UndoAction();
	private JPopupMenu popup;
	private PopupClickListener popupListener;
	private PlanarityTestingAlgorithm<GraphVertex, GraphEdge> planarityTest =
			new FraysseixMendezPlanarity<GraphVertex, GraphEdge>();
	//TODO da se moze ovo pozvati vise puta
	private SeparationPairSplitting<GraphVertex, GraphEdge> separationPairsSplitting =
			new SeparationPairSplitting<GraphVertex, GraphEdge>();

	private static int graphCount = 1;

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

		try {
			// Setup the look and feel properties
			Properties props = new Properties();
			props.put("logoString", "");

			String foregroundColor = "228 228 255";

			props.put("backgroundPattern", "off");

			props.put("menuForegroundColor", foregroundColor);
			props.put("menuBackgroundColor", "24 26 28");
			props.put("menuSelectionForegroundColor", "0 0 0");
			props.put("menuSelectionBackgroundColor", "91 151 32");

			props.put("toolbarColorLight", "0 22 90");
			props.put("toolbarColorDark", "2 52 7");

			String btnColor = "91 151 32";
			props.put("buttonForegroundColor", foregroundColor);
			props.put("buttonBackgroundColor", "70 103 40");
			props.put("buttonColorLight", btnColor);
			props.put("buttonColorDark", btnColor);

			props.put("foregroundColor", foregroundColor);
			props.put("backgroundColor", "44 47 44");
			props.put("backgroundColorLight", "16 16 96");
			props.put("backgroundColorDark", "8 8 64");
			props.put("alterBackgroundColor", "255 0 0");

			props.put("disabledForegroundColor", foregroundColor);
			props.put("disabledBackgroundColor", "24 26 28");

			props.put("selectionForegroundColor", foregroundColor);
			props.put("selectionBackgroundColor", "70 103 40");

			props.put("inputForegroundColor", "228 228 255");
			props.put("inputBackgroundColor", "71 75 71");

			props.put("systemTextFont", "Sans PLAIN 15");
			props.put("controlTextFont", "Sans PLAIN 15");
			props.put("menuTextFont", "Sans PLAIN 15");
			props.put("userTextFont", "Sans PLAIN 15");
			props.put("subTextFont", "Sans PLAIN 15");
			props.put("windowTitleFont", "Sans BOLD 15");

			// Set your theme
			com.jtattoo.plaf.noire.NoireLookAndFeel.setCurrentTheme(props);

			UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		initMenu();
		initToolBar();
		initGui();
		initPopup();

	}

	private void initGui(){

		JPanel centralPanel = new JPanel(new MigLayout("fill"));
		add(centralPanel, "grow");

		pane = new JTabbedPane();
		pane.addTab("+", new JPanel());
		pane.setTabComponentAt(pane.getTabCount() - 1, new AddTabComponent());
		// this tab must not be enabled because we don't want to select this tab
		pane.setEnabledAt(pane.getTabCount() - 1, false);

		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JPanel palettePanel = new JPanel(new MigLayout());
		palettePanel.setBorder(BorderFactory.createEtchedBorder());
		ButtonGroup group = new ButtonGroup();

		Dimension buttonsDim = new Dimension(100,20);
		btnVertex = new JToggleButton(new AddVertexAction());
		btnVertex.setPreferredSize(buttonsDim);
		palettePanel.add(btnVertex, "gapy 10px, wrap");
		group.add(btnVertex);

		btnEdge = new JToggleButton(new LinkAction());
		btnEdge.setPreferredSize(buttonsDim);
		palettePanel.add(btnEdge, "wrap");
		group.add(btnEdge);

		btnSelect = new JToggleButton(new SelectAction());
		btnSelect.setPreferredSize(buttonsDim);		
		palettePanel.add(btnSelect, "wrap");
		group.add(btnSelect);

		JButton btnLayout = new JButton(new LayoutAction());
		btnLayout.setPreferredSize(buttonsDim);		
		palettePanel.add(btnLayout);


		propertiesPanel = new JPanel(new MigLayout("fill"));
		propertiesPanel.add(new JLabel("Properties"), "dock north");
		propertiesPanel.setBorder(BorderFactory.createEtchedBorder());

		rightSplitPane.setLeftComponent(palettePanel);
		rightSplitPane.setRightComponent(propertiesPanel);


		commandPanel = new CommandPanel();
		commandPanel.setPreferredSize(new Dimension(150,150));
		commandPanel.setBorder(BorderFactory.createEtchedBorder());

		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftSplitPane.setLeftComponent(pane);
		leftSplitPane.setRightComponent(commandPanel);
		leftSplitPane.setResizeWeight(0.9);
		leftSplitPane.setBorder(BorderFactory.createEtchedBorder());

		JSplitPane centralSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centralSplitPane.setRightComponent(rightSplitPane);
		centralSplitPane.setResizeWeight(0.9);
		centralSplitPane.setLeftComponent(leftSplitPane);
		centralPanel.add(centralSplitPane, "grow");

		statusBar = new StatusBar();
		add(statusBar, "height 20:20:20, dock south");

	}

	private void initMenu(){

		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem exitMi = new JMenuItem(new ExitAction());
		JMenuItem saveMi = new JMenuItem(saveAction);
		JMenuItem loadMi = new JMenuItem(loadAction);

		JMenu editMenu = new JMenu("Edit");
		JMenuItem newMi = new JMenuItem(newGraphAction);
		JMenuItem undoMi = new JMenuItem(undoAction);
		JMenuItem redoMi = new JMenuItem(redoAction);
		JMenuItem removeMi = new JMenuItem(removeAction);
		editMenu.add(newMi);
		editMenu.addSeparator();
		editMenu.add(undoMi);
		editMenu.add(redoMi);
		editMenu.addSeparator();
		editMenu.add(removeMi);

		fileMenu.add(saveMi);
		fileMenu.add(loadMi);
		fileMenu.addSeparator();
		fileMenu.add(exitMi);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		setJMenuBar(menuBar);
	}

	private void initToolBar(){
		toolBar = new JToolBar();
		//toolBar.setBackground(Color.RED);
		add(toolBar, "dock north");
		toolBar.add(newGraphAction);
		toolBar.add(saveAction);
		toolBar.add(loadAction);
		toolBar.addSeparator();
		toolBar.add(removeAction);
		toolBar.addSeparator();
		toolBar.add(undoAction);
		toolBar.add(redoAction);
	}

	private void initPopup(){
		popup = new JPopupMenu("Analyze");
		JMenuItem connectedMI = new JMenuItem("Check connectivity");

		connectedMI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String answer = getGraph().isConnected() ? "yes" : "no";
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Graph is connected: " +  answer);
			}
		});

		JMenuItem biconnectedMI = new JMenuItem("Check biconnectivity");
		biconnectedMI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String answer = getGraph().isBiconnected() ? "yes" : "no";
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Graph is biconnected: " +  answer);	
			}
		});

		JMenuItem cycleMI = new JMenuItem("Check if cyclic");
		cycleMI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String answer = getGraph().isCyclic() ? "yes" : "no";
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Graph is cyclic: " +  answer);	
			}
		});

		JMenuItem planarMI = new JMenuItem("Check planarity");
		planarMI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String answer = planarityTest.isPlannar(getGraph()) ? "yes" : "no";
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Graph is planar: " +  answer);	
			}
		});

		JMenuItem cycleBasisMI = new JMenuItem("Cycles basis");
		cycleBasisMI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SimpleCyclesFinder<GraphVertex, GraphEdge> cycles = new SimpleCyclesFinder<GraphVertex,GraphEdge>();
				String cyclesStr = cycles.findCycles(getGraph()).toString();
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Cycles basis: " +  cyclesStr);
			}
		});

		JMenuItem cutVerticesMI = new JMenuItem("Cut vertices");
		cutVerticesMI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cutVertices = getGraph().listCutVertices().toString();
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Cut vertices: " +  cutVertices);	
			}
		});

		JMenuItem blocksMI = new JMenuItem("Biconnected components");
		blocksMI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String ret;
				if (getGraph().isBiconnected())
					ret = "Graph is biconnected";
				else{
					List<Graph<GraphVertex, GraphEdge>> blocks = getGraph().listBiconnectedComponents();
					StringBuilder builder = new StringBuilder("Biconnected components: \n");
					for (int i = 0; i < blocks.size(); i++){
						Graph<GraphVertex, GraphEdge> block  = blocks.get(i);
						builder.append("Component " + (i+1) + " " + block.printFormat() + "\n");
					}
					ret = builder.toString();
				}
				JOptionPane.showMessageDialog(MainFrame.getInstance(), ret);	
			}
		});
		
		JMenuItem triconnectedMI = new JMenuItem("Check triconnectivity");
		triconnectedMI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String answer = "no";
				try {
					answer = separationPairsSplitting.findSeaparationPairs(getGraph()).size() == 0 ? "yes" : "no";
				} catch (AlgorithmErrorException e) {
					e.printStackTrace();
				}
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Graph is triconnected: " +  answer);	
			}
		});
		
		JMenuItem triconnectedComponentsMI = new JMenuItem("Split components");
		triconnectedComponentsMI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				HopcroftTarjanSplitting<GraphVertex, GraphEdge> hopcroftTarjan = new HopcroftTarjanSplitting<GraphVertex, GraphEdge>(getGraph());
				try {
					hopcroftTarjan.execute();
				} catch (AlgorithmErrorException e) {
				}
				
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "Graph is triconnected: " +  hopcroftTarjan.getSplitComponents());	
			}
		});
		
		JMenuItem separationPairsMI = new JMenuItem("Separation pairs");
		separationPairsMI.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String ret;
				try {
					List<SplitPair<GraphVertex, GraphEdge>> separationPairs = separationPairsSplitting.findSeaparationPairs(getGraph());
					ret = separationPairs.toString();
				} catch (AlgorithmErrorException e) {
					e.printStackTrace();
					ret = e.getMessage();
				}
				JOptionPane.showMessageDialog(MainFrame.getInstance(), ret);	
			}
		});
		
		JMenuItem automorphismsMI = new JMenuItem("Automorphisms");
		automorphismsMI.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String ret = "";
				McKayGraphLabelingAlgorithm<GraphVertex, GraphEdge> nauty = new McKayGraphLabelingAlgorithm<GraphVertex,GraphEdge>(getGraph());
				List<Permutation> automorphisms = nauty.findAutomorphisms();
				for (Permutation p : automorphisms){
					ret += p.cyclicRepresenatation() + "\n";
				}
				JOptionPane.showMessageDialog(MainFrame.getInstance(), ret);	
			}
		});

		popup.add(connectedMI);
		popup.add(biconnectedMI);
		popup.add(triconnectedMI);
		popup.add(cycleMI);
		popup.add(planarMI);
		popup.add(cycleBasisMI);
		popup.add(cutVerticesMI);
		popup.add(blocksMI);
		popup.add(separationPairsMI);
		popup.add(triconnectedComponentsMI);
		popup.add(automorphismsMI);
		//TODO triconnected components posle joinova
		//TODO find path from to
		//is tree
		//is ring
		popupListener = new PopupClickListener();
	}

	private Graph<GraphVertex, GraphEdge> getGraph(){
		return MainFrame.getInstance().getCurrentView().getModel().getGraph();
	}

	public static MainFrame getInstance(){
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}


	public GraphView getCurrentView(){
		if (pane.getSelectedComponent() != null && pane.getSelectedComponent() instanceof GraphView)
			return (GraphView) pane.getSelectedComponent();
		return null;
	}

	public void renameCurrentView(String name){
		((CloseableTabComponent) pane.getTabComponentAt(pane.getSelectedIndex())).rename(name);
	}

	public void addNewDiagram(){
		Graph<GraphVertex, GraphEdge> graph = new Graph<GraphVertex, GraphEdge>();
		GraphView view = new GraphView(graph);

		int tabIndex = pane.getTabCount() - 1;
		String tabTitle = "Graph " + graphCount++ + " ";
		pane.insertTab(tabTitle, null, view, null, tabIndex);
		pane.setTabComponentAt(tabIndex, new CloseableTabComponent(pane, tabTitle));
		pane.setSelectedIndex(tabIndex);
		view.addMouseListener(popupListener);

	}

	public void addDiagram(GraphView view, String name){
		int tabIndex = pane.getTabCount() - 1;
		String tabTitle = name;
		pane.insertTab(tabTitle, null, view, null, tabIndex);
		pane.setTabComponentAt(tabIndex, new CloseableTabComponent(pane, tabTitle));
		pane.setSelectedIndex(tabIndex);
		view.addMouseListener(popupListener);
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


	// A component for the last tab with an add button
	private static class AddTabComponent extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JButton addButton = null; 

		public AddTabComponent() {
			super(new BorderLayout());

			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));

			addButton = new AddButton();

			addButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					MainFrame.getInstance().addNewDiagram();
				}

			});

			add(addButton, BorderLayout.EAST);
		}
	}

	// A add button for the last tab in the tabbed pane
	private static class AddButton extends JButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static final Dimension PREF_SIZE = new Dimension(16, 15);
		private final ImageIcon CLOSER_ICON =  new ImageIcon(getClass().getResource("/gui/resources/plus.png"));
		private final ImageIcon CLOSER_ROLLOVER_ICON =  new ImageIcon(getClass().getResource("/gui/resources/plus_rollover.png"));
		private final ImageIcon CLOSER_PRESSED_ICON =  new ImageIcon(getClass().getResource("/gui/resources/plus.png"));

		public AddButton() {
			super();
			// setup the button
			// setup the button
			setIcon(CLOSER_ICON);
			setRolloverIcon(CLOSER_ROLLOVER_ICON);
			setPressedIcon(CLOSER_PRESSED_ICON);
			setFocusable(false);
			setContentAreaFilled(false);
			setBorder(BorderFactory.createEmptyBorder());
		}

		@Override
		public Dimension getPreferredSize() {
			return PREF_SIZE;
		}

	}

	class PopupClickListener extends MouseAdapter{
		public void mousePressed(MouseEvent e){
			if (e.isPopupTrigger())
				doPop(e);
		}

		public void mouseReleased(MouseEvent e){
			if (e.isPopupTrigger())
				doPop(e);
		}

		private void doPop(MouseEvent e){
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
