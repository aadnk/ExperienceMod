package com.comphenix.xp.extra;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;

/**
 * Represents a string-based service registry.
 * 
 * @author Kristian
 * @param <TService> - the type of each service that will be registered.
 */
public class ServiceProvider<TService extends Service> {

	protected Map<String, TService> nameLookup = new HashMap<String, TService>();
	
	/**
	 * Returns the currently registered service with this name. The name 
	 * should conform to the Java ENUM convention.
	 * @param serviceName - name to search for.
	 * @return The currently registered service, or NULL if not found.
	 * @throws NullArgumentException If serviceName is null.
	 */
	public TService getByName(String serviceName) {
		if (serviceName == null)
			throw new NullArgumentException("serviceName");

		return nameLookup.get(serviceName);
	}
	
	/**
	 * Registers a service in the system.
	 * @param service - the service to register.
	 * @param override - TRUE to override any previously registered services with the same name or type. 
	 * @return The previously registered service with this name, or NULL otherwise.
	 * @throws NullArgumentException If service is null.
	 */
	public TService register(TService service, boolean override) {
		if (service == null)
			throw new NullArgumentException("service");
		
		String name = service.getServiceName();
		
		// Should we keep the old service?
		if (override) {
			if (nameLookup.containsKey(name)) 
				return getByName(name);
			else
				return null;
		} else {
			return setByName(name, service);
		}
	}
	
	/**
	 * Unregisters a specified service.
	 * @param serviceName - the name of the service to unregister.
	 * @return The previously registered service with this name, or NULL otherwise.
	 * @throws NullArgumentException If serviceName is null.
	 */
	public TService unregister(String serviceName) {
		if (serviceName == null)
			throw new NullArgumentException("serviceName");

		return nameLookup.remove(serviceName);
	}
	
	/**
	 * Determines whether or not the given service has been registered.
	 * @param serviceName - name of the service to find.
	 * @return TRUE if it has, FALSE otherwise.
	 */
	public boolean containsService(String serviceName) {
		return nameLookup.containsKey(serviceName);
	}
	
	/**
	 * Unregisters a particular service.
	 * @param service - the service to unregister.
	 * @return The service that was unregistered, or NULL if this service has 
	 * already been unregistered.
	 * @throws NullArgumentException If service is null.
	 */
	public TService unregister(TService service) {
		if (service == null)
			throw new NullArgumentException("service");

		String name = service.getServiceName();
		
		if (name != null)
			return nameLookup.remove(name);
		else
			return null;
	}
	
	/**
	 * Called by the register function to associate a service with a name.
	 * @param name - name of the service to register.
	 * @param service - service to register.
	 * @throws NullArgumentException Service name cannot be null.
	 */
	protected TService setByName(String name, TService service) {
		if (name == null)
			throw new NullArgumentException("Service name cannot be null.");
		else
			return nameLookup.put(name, service);
	}
}
