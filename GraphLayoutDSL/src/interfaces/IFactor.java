package interfaces;

import java.util.Map;

public interface IFactor {
	
	public boolean isNegative();
	public Map<String, Object> getAestheticCriterion();
	public IExpression getExpression();
	

}
