package de.congrace.exp4j;

public class BultinFunction extends CustomFunction {

	/**
	 * Every standard function in this library.
	 * 
	 * @author Kristian
	 */
	public enum StandardFunctions {
		ABS(1),
		ACOS(1),
		ASIN(1),
		ATAN(1),
		CBRT(1),
		CEIL(1),
		COS(1),
		COSH(1),
		EXP(1),
		EXPM1(1),
		FLOOR(1),
		LOG(1),
		POW(2),
		SIN(1),
		SINH(1),
		SQRT(1),
		TAN(1),
		TANH(1),
		ROUND(1);
		
		final int argCount;
		
		private StandardFunctions(int argCount) {
			this.argCount = argCount;
		}
		
		/**
		 * Retrieves the call name of this function.
		 * @return Function name.
		 */
		public String getFunctionName() {
			return this.name().toLowerCase();
		}

		/**
		 * Retrieves the number of arguments this function uses.
		 * @return Number of arguments.
		 */
		public int getArgCount() {
			return argCount;
		}
	}
	
	// Our standard function type
	final private StandardFunctions standard;
	
	public BultinFunction(String name) throws InvalidCustomFunctionException {
		
		super(name.toLowerCase());
		
		try {
			this.standard = StandardFunctions.valueOf(name.toUpperCase());
			this.argc = standard.getArgCount();
		} catch (IllegalArgumentException e) {
			throw new InvalidCustomFunctionException("No such standard function exists.");
		}
	}

	public BultinFunction(StandardFunctions standard) throws InvalidCustomFunctionException {
		super(standard.getFunctionName());
		this.standard = standard;
		this.argc = standard.getArgCount();
	}
	
	@Override
	public double applyFunction(double... args) {

		// A big old switch statement. Uses less memory/space.
		switch (standard) {
		case ABS:
			return Math.abs(args[0]);
		case ACOS:
			return Math.acos(args[0]);
		case ASIN:
			return Math.asin(args[0]);
		case ATAN:
			return Math.atan(args[0]);
		case CBRT:
			return Math.cbrt(args[0]);
		case CEIL:
			return Math.ceil(args[0]);
		case COS:
			return Math.cos(args[0]);
		case COSH:
			return Math.cosh(args[0]);
		case EXP:
			return Math.exp(args[0]);
		case EXPM1:
			return Math.expm1(args[0]);
		case FLOOR:
			return Math.floor(args[0]);
		case LOG:
			return Math.log(args[0]);
		case POW:
			return Math.pow(args[0], args[1]);
		case ROUND:
			return Math.round(args[0]);
		case SIN:
			return Math.sin(args[0]);
		case SINH:
			return Math.sinh(args[0]);
		case SQRT:
			return Math.sqrt(args[0]);
		case TAN:
			return Math.tan(args[0]);
		case TANH:
			return Math.tanh(args[0]);
		default:
			throw new IllegalStateException("Illegal function name detected.");
		}
	}
	
	public StandardFunctions getStandard() {
		return standard;
	}
}
