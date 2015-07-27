package graph.algorithm;

public class ExecuteResult{
		
		private long duration;
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