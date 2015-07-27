package gui.commands;

import gui.model.GraphEdge;
import gui.model.GraphElement;
import gui.model.GraphModel;
import gui.model.GraphVertex;
import gui.view.GraphView;
import gui.view.painters.ElementPainter;

import java.util.List;

public class RemoveCommand extends Command{

	
	private List<GraphElement> elementsToRemove;
	private List<ElementPainter> removedPainters;
	
	public RemoveCommand(List<GraphElement> elementsToRemove, GraphView view) {
		this.elementsToRemove = elementsToRemove;
		this.view = view;
	}
	
	@Override
	public void execute() {
		GraphModel model = view.getModel();
		for (GraphElement el : elementsToRemove)
			if (el instanceof GraphVertex){
				model.removeVertex((GraphVertex) el);
				view.getSelectionModel().removeVertexFromSelection((GraphVertex) el);
			}
			else{
				model.removeEdge((GraphEdge) el);
				view.getSelectionModel().removeEdgeFromSelection((GraphEdge) el);
			}
		//for undo
		removedPainters = view.removePainters(elementsToRemove);
		view.repaint();
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}

}
