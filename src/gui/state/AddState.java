package gui.state;

import gui.main.frame.ElementsEnum;
import gui.model.GraphVertex;
import gui.view.GraphView;
import gui.view.painters.VertexPainter;

import java.awt.event.MouseEvent;

public class AddState extends State{

	private ElementsEnum currentElementType;

	public AddState(GraphView view, ElementsEnum currentElementType){
		this.currentElementType = currentElementType;
		this.view = view;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (currentElementType == ElementsEnum.VERTEX){
			int count = view.getGraph().getVertices().size();
			String content = "v" + count;
			GraphVertex vertex = new GraphVertex(e.getPoint(), content);
			view.getGraph().addVertex(vertex);
			VertexPainter painter = new VertexPainter(vertex);
			view.addVertexPainter(painter);
			view.repaint();
		}

	}

	public ElementsEnum getCurrentElementType() {
		return currentElementType;
	}

	public void setCurrentElementType(ElementsEnum currentElementType) {
		this.currentElementType = currentElementType;
	}


}