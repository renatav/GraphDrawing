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
		
		
		/**
		 * @param duration Duration of the execution
		 * @param value Result
		 */
		public ExecuteResult(long duration, Object value) {
			super();
			this.duration = duration;
			this.value = value;
		}

		/**
		 * @return Duration of the execution
		 */
		public long getDuration() {
			return duration;
		}
		
		/**
		 * @param duration Duration to set
		 */
		public void setDuration(long duration) {
			this.duration = duration;
		}
		
		/**
		 * @return Result of the execution
		 */
		public Object getValue() {
			return value;
		}
		
		/**
		 * @param value The value to set
		 */
		public void setValue(Object value) {
			this.value = value;
		}
		
	}