package com.akxcv.jsimplex;

class CostFunction {
	
	private double[] coefs;
	private boolean minimize;

	public CostFunction(double[] coefs, boolean minimize) {
		this.coefs = coefs;
		this.minimize = minimize;
	}
	
	public String toString() {
		String string = coefs[0] + "x1" + " ";
		
		for (int i = 1; i < coefs.length; i++) {
			if (coefs[i] != 0) {
				if (Math.signum(coefs[i]) >= 0)
					string += "+ ";
				else
					string += "- ";
				string += Math.abs(coefs[i]) + "x" + (i+1) + " ";
			}
		}
		
		string += "--> " + (minimize ? "min" : "max");
		
		return string;
	}

	public double getCoef(int number) {
		return coefs[number];
	}

	public int getCoefCount() {
		return coefs.length;
	}
	
}
