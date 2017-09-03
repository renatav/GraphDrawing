package models.java;

import java.util.List;

import interfaces.IExpression;
import interfaces.ITerm;

public class Expression implements IExpression{
	
	private List<ITerm> terms;
	
	public Expression(List<ITerm> terms) {
		super();
		this.terms = terms;
	}

	@Override
	public List<ITerm> getTerms() {
		return terms;
	}

	@Override
	public String toString() {
		return "Expression [terms=" + terms + "]";
	}
	
	

}
