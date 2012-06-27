package com.comphenix.xp.extra;

public interface Service {

	/**
	 * Retrieves a unique string identifying this service. May also be used during parsing. 
	 * <p>
	 * Note that this identifier must conform to an ENUM convention: upper case only, 
	 * underscore for space. 
	 * <p>
	 * A service MUST not alter its identifier once it has been registered.
	 * 
	 * @return A unique reward ID.
	 */
	public String getServiceName();
}
