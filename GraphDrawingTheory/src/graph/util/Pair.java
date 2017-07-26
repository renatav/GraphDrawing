package graph.util;

/**
 * A util class representing a key-value pair
 * @author Renata
 * @param <K> Type of the first element of the pair
 * @param <V> Type of the second element of the pair 
 */
public class Pair<K,V> {

	private K key;
	private V value;
	
	public Pair(K key, V value){
		this.key = key;
		this.value = value;
	}

	/**
	 * @return Key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * @param key Key to set
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * @return Value
	 */
	public V getValue() {
		return value;
	}

	/**
	 * @param value Value to set
	 */
	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Pair [" + key.toString() + ", " +  value.toString() + "]";
	}
	
}
