package com.akxcv.jsimplex;

class Limitation {

	public enum LimitationSign { LE, EQ, GE }
	
	private double[] coefs;
	private LimitationSign sign;
	private double freeTerm;

	public Limitation(double[] coefs, LimitationSign sign, double freeTerm) {
		this.coefs = coefs;
		this.sign = sign;
		this.freeTerm = freeTerm;
	}
	
	public String toString() {
		String string = coefs[0] + "x" + 1 + " ";
		
		for (int i = 1; i < coefs.length; i++) {
			if (coefs[i] != 0) {
				if (Math.signum(coefs[i]) >= 0)
					string += "+ ";
				else
					string += "- ";
				string += Math.abs(coefs[i]) + "x" + (i+1) + " ";
			}
		}
		
		switch(sign) {
			
			case LE:
			string += "<= ";
			break;
			
			case EQ:
			string += "= ";
			break;
			
			case GE:
			string += ">= ";
			break;
			
			default:
			break;
		}
		
		string += freeTerm;
		
		return string;
	}

	public double getCoef(int number) {
		return coefs[number];
	}
	
	public int getCoefCount() {
		return coefs.length;
	}
	
	public LimitationSign getSign() {
		return sign;
	}

	public double getFreeTerm() {
		return freeTerm;
	}
	
}
