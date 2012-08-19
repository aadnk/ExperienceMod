package de.congrace.exp4j;

/**
 * this classed is used to create custom functions for exp4j<br/>
 * <br/>
 * <b>Example</b><br/>
 * <code><pre>{@code}	
 * CustomFunction fooFunc = new CustomFunction("foo") {
 * 		public double applyFunction(double value) {
 * 			return value*Math.E;
 * 		}
 * };
 * double varX=12d;
 * Calculable calc = new ExpressionBuilder("foo(x)").withCustomFunction(fooFunc).withVariable("x",varX).build();
 * assertTrue(calc.calculate() == Math.E * varX);
 * }</pre></code>
 * 
 * @author frank asseg
 * 
 */
public abstract class CustomFunction {
	// Had to remove the final flag
	protected int argc;

	final String name;

	/**
	 * create a new single value input CustomFunction with a set name
	 * 
	 * @param value
	 *            the name of the function (e.g. foo)
	 */
	protected CustomFunction(String name) throws InvalidCustomFunctionException {
		if (name == null)
			throw new IllegalArgumentException("Function name is null.");
		
		this.argc = 1;
		this.name = name;
		int firstChar = (int) name.charAt(0);
		if ((firstChar < 65 || firstChar > 90) && (firstChar < 97 || firstChar > 122)) {
			throw new InvalidCustomFunctionException("functions have to start with a lowercase or uppercase character");
		}
	}

	/**
	 * create a new single value input CustomFunction with a set name
	 * 
	 * @param value
	 *            the name of the function (e.g. foo)
	 */
	protected CustomFunction(String name, int argumentCount) throws InvalidCustomFunctionException {
		this.argc = argumentCount;
		this.name = name;
	}

	public abstract double applyFunction(double... args);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + argc;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomFunction other = (CustomFunction) obj;
		if (argc != other.argc)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
