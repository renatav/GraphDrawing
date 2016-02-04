package graph.layout.util;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import prefuse.action.Action;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

	/**
	 * Class needed to get the positions of graph elements after a layout algorithm is performed
	 * Prefuse uses schedulers, so this class is supposed to be instantiated in order to be scheduled
	 * after the layout action
	 * @author xxx
	 */
	public class PositionAction extends Action{

		private Map<Integer, Point2D> positionsMap = new HashMap<Integer, Point2D>();
		private boolean finished = false;
		
		public PositionAction(long duration) {
			super(duration);
		}
		
		public void run(){
			exec();
		}

		@Override
		protected void run(long elapsedTime) {
			exec();
			
		}

		@Override
		public void run(double frac) {
			exec();
		}
		
		private void exec(){
			
			TupleSet visGroup = m_vis.getVisualGroup("graph.nodes");
			@SuppressWarnings("unchecked")
			Iterator<Tuple> iter = visGroup.tuples();
			positionsMap.clear();
			
			while (iter.hasNext()){
				
				Tuple t = iter.next();
				VisualItem item = m_vis.getVisualItem("graph.nodes", t);
				positionsMap.put(item.getRow(), new Point2D.Double(item.getX(), item.getY()));
			}
			finished = true;
		}

		public Map<Integer, Point2D> getPositionsMap() {
			return positionsMap;
		}

		public boolean isFinished() {
			return finished;
		}


	}