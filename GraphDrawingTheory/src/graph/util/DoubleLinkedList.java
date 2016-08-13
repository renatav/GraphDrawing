package graph.util;

import java.util.ArrayList;

/**
 * Double linked circular link
 * @param <T>
 */
public class DoubleLinkedList<T> extends ArrayList<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public T getPrevious(T current){
		int index = indexOf(current);
		if (index == 0)
			index = size() - 1;
		else 
			index--;
		return get(index);
	}
	
	public T getNext(T current){
		int index = indexOf(current);
		if (index == size() - 1)
			index = 0;
		else
			index++;
		return get(index);
	}
}
