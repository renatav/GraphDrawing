package models.java;

import java.util.Map;

import interfaces.IExpression;
import interfaces.IFactor;

public class Factor implements IFactor{
	
	private boolean negative;
	private Map<String, Object> aestheticCriterion;
	private IExpression expression;
	
	public Factor(boolean negative, Map<String, Object> aestheticCriterion, IExpression expression) {
		super();
		this.negative = negative;
		this.aestheticCriterion = aestheticCriterion;
		this.expression = expression;
	}

	@Override
	public boolean isNegative() {
		return negative;
	}

	@Override
	public Map<String, Object> getAestheticCriterion(){
		return aestheticCriterion;
	}

	@Override
	public IExpression getExpression() {
		return expression;
	}
	
	@Override
	public String toString() {
		return "Factor [negative=" + negative + ", aestheticCriterion=" + aestheticCriterion + ", expression="
				+ expression + "]";
	}


}
