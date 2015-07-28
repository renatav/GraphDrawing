package gui.commands;

import gui.model.GraphEdge;
import gui.model.GraphElement;
import gui.model.GraphModel;
import gui.model.GraphVertex;
import gui.view.GraphView;
import gui.view.painters.EdgePainter;
import gui.view.painters.ElementPainter;
import gui.view.painters.VertexPainter;

public class AddElementCommand extends Command{

	private GraphElement element;
	private ElementPainter painter;
	
	public AddElementCommand(GraphElement element, GraphView view) {
		super(view);
		this.element = element;
	}

	@Override
	public void execute() {
		GraphModel model = view.getModel();
		
		if (element instanceof GraphVertex){
			model.getGraph().addVertex((GraphVertex)element);
			painter = new VertexPainter((GraphVertex)element);
			view.addVertexPainter((VertexPainter) painter);
		}
		else if (element instanceof GraphEdge){
			model.getGraph().addEdge((GraphEdge)element);
			painter = new EdgePainter((GraphEdge)element);
			view.addEdgePainter((EdgePainter) painter);
		}
		view.repaint();
		
	}

	@Override
	public void undo() {
		GraphModel model = view.getModel();
		
		if (element instanceof GraphVertex){
			model.getGraph().removeVertex((GraphVertex)element);
			view.removeVertexPainter((VertexPainter) painter);
		}
		else if (element instanceof GraphEdge){
			model.getGraph().removeEdge((GraphEdge)element);
			view.removeEdgePainter((EdgePainter) painter);
		}
		
		view.repaint();
		
	}

}
