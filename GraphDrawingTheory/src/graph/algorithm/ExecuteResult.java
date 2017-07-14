package graph.algorithm;

/**
 * A class which groups result of the execution of an algorithm and duration of
 * that execution
 * @author Renata
 */
public class ExecuteResult{
		
		/**
		 * Time in millisecond measuring how long did it take to execute an algorithm
		 */
		private long duration;
		/**
		 * Result of the execution of an algorithm
		 */
		private Object value;
		
		
		public ExecuteResult(long duration, Object value) {
			super();
			this.duration = duration;
			this.value = value;
		}
		
		public long getDuration() {
			return duration;
		}
		
		public void setDuration(long duration) {
			this.duration = duration;
		}
		
		public Object getValue() {
			return value;
		}
		
		public void setValue(Object value) {
			this.value = value;
		}
		
	}