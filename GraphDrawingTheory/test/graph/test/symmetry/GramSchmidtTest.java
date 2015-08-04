package graph.test.symmetry;

import graph.symmetry.GramSchmidt;
import junit.framework.TestCase;

import org.junit.Test;

import Jama.Matrix;

public class GramSchmidtTest extends TestCase{

	@Test
	public void test(){
		
		double[][] testGS = new double[2][2];
		testGS[0] = new double[]{4,-2};
		testGS[1] = new double[]{3,1};
		Matrix res = GramSchmidt.execute(new Matrix(testGS));
		double[][] values = res.getArray();
		assertEquals(0.8, values[0][0]);
		assertEquals(-0.6, values[0][1]);
		assertEquals(0.6,values[1][0]);
		assertEquals(0.8, values[1][1]);
	}
}
