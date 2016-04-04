package com.akxcv.jsimplex.problem;

public class CostFunction {
	
	private double[] coefs;
	private Variable[] variables;
	private boolean minimize;

	public CostFunction(double[] coefs, Variable[] variables, boolean minimize) {
		this.coefs = coefs;
        this.variables = variables;
		this.minimize = minimize;
	}
	
	public String toString() {
		String string = coefs[0]  + " ";
		
		for (int i = 1; i < coefs.length; i++) {
			if (coefs[i] != 0) {
				if (Math.signum(coefs[i]) >= 0)
					string += "+ ";
				else
					string += "- ";
				string += Math.abs(coefs[i]) + variables[i].toString() + " ";
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

    public Variable[] getVariables() {
        return variables;
    }

	public boolean shouldBeMinimized() {return minimize;}
	
}
