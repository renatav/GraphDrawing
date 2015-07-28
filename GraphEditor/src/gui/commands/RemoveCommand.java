package gui.commands;

import gui.model.GraphEdge;
import gui.model.GraphElement;
import gui.model.GraphModel;
import gui.model.GraphVertex;
import gui.view.GraphView;
import gui.view.painters.EdgePainter;
import gui.view.painters.ElementPainter;
import gui.view.painters.VertexPainter;

import java.util.ArrayList;
import java.util.List;

public class RemoveCommand extends Command{

	
	private List<GraphElement> elementsToRemove;
	private List<ElementPainter> removedPainters;
	
	public RemoveCommand(List<GraphElement> elementsToRemove, GraphView view) {
		super(view);
		this.elementsToRemove = elementsToRemove;
	}
	
	@Override
	public void execute() {
		GraphModel model = view.getModel();
		List<GraphEdge> removedEdges = new ArrayList<GraphEdge>();
		for (GraphElement el : elementsToRemove)
			if (el instanceof GraphVertex){
				if (model.getGraph().adjacentEdges((GraphVertex) el) != null)
					removedEdges.addAll(model.getGraph().adjacentEdges((GraphVertex) el));
					
				model.removeVertex((GraphVertex) el);
				view.getSelectionModel().removeVertexFromSelection((GraphVertex) el);
				
			}
			else{
				model.removeEdge((GraphEdge) el);
				view.getSelectionModel().removeEdgeFromSelection((GraphEdge) el);
			}
		
		elementsToRemove.addAll(removedEdges);
		//for undo
		removedPainters = view.removePainters(elementsToRemove);
		view.repaint();
	}

	@Override
	public void undo() {
		GraphModel model = view.getModel();
		
		for (GraphElement el : elementsToRemove)
			if (el instanceof GraphVertex)
				model.addVertex((GraphVertex) el);
		
		for (GraphElement el : elementsToRemove)
			if (el instanceof GraphEdge)
				model.addEdge((GraphEdge) el);
		
		for (ElementPainter painter : removedPainters){
			if (painter instanceof EdgePainter)
				view.addEdgePainter((EdgePainter)painter);
			else
				view.addVertexPainter((VertexPainter)painter);
			
		}
		view.repaint();
		
	}

}
