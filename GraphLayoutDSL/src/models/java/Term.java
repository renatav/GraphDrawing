package models.java;

import java.util.List;

import interfaces.IFactor;
import interfaces.ITerm;

public class Term implements ITerm{
	
	private List<IFactor> factors;
		

	public Term(List<IFactor> factors) {
		this.factors = factors;
	}

	@Override
	public String toString() {
		return "Term [factors=" + factors + "]";
	}

	@Override
	public List<IFactor> getFactors() {
		return factors;
	}
	
	
}
