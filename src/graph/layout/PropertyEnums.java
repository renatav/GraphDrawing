package graph.layout;

public interface PropertyEnums {
	
	
	public enum KamadaKawaiProperties implements PropertyEnums{
		DISCONNECTED_DISTANCE_MULTIPLIER("Disconnected distance multiplier"), LENGTH_FACTOR ("Length factor");
		
		private String name;
		
		KamadaKawaiProperties(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
	}
	
	public enum BoxProperties implements PropertyEnums{
		PROPERY("Test property");
		
		private String name;
		
		BoxProperties (String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	}
	
	public enum CircleProperties implements PropertyEnums{
		PROPERY("Test property");
		
		private String name;
		
		CircleProperties (String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	}
	
	
	public enum FruchtermanReingoldProperties implements PropertyEnums{
		PROPERY("Test property");
		
		private String name;
		
		FruchtermanReingoldProperties (String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	}
	
	public enum SpringProperties implements PropertyEnums{
		PROPERY("Test property");
		
		private String name;
		
		SpringProperties (String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	}
	
	

}
