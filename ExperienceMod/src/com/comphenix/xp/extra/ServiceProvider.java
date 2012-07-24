/*
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.xp.extra;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.NullArgumentException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Represents a string-based service registry.
 * 
 * @author Kristian
 * @param <TService> - the type of each service that will be registered.
 */
public class ServiceProvider<TService extends Service> {

	// Simple reference to the default service
	public static String defaultServiceName = "DEFAULT";
	
	// List of services by name
	protected Map<String, TService> nameLookup = new ConcurrentHashMap<String, TService>();
	
	// List of disabled services
	protected Set<String> disabledLookup = Sets.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	
	// Default service
	private String defaultName;
	
	// Make sure this value is set
	public ServiceProvider(String defaultName) {
		this.defaultName = defaultName;
	}
	
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
		else if (serviceName.equalsIgnoreCase(defaultServiceName))
			return nameLookup.get(getDefaultName());
		
		return nameLookup.get(serviceName);
	}
	
	/**
	 * Registers a service in the system.
	 * @param service - the service to register.
	 * @return The previously registered service with this name, or NULL otherwise.
	 * @throws NullArgumentException If service is null.
	 */
	public TService register(TService service) {
		if (service == null)
			throw new NullArgumentException("service");
		
		String name = service.getServiceName();
		
		// Careful now.
		if (name.equalsIgnoreCase(defaultServiceName))
			throw new IllegalArgumentException(
					"Service cannot have the name DEfAULT. This name is reserved.");

		return setByName(name, service);
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
		else if (serviceName.equalsIgnoreCase(defaultServiceName))
			return nameLookup.remove(getDefaultName());

		return nameLookup.remove(serviceName);
	}
	
	/**
	 * Determines whether or not the given service has been registered.
	 * @param serviceName - name of the service to find.
	 * @return TRUE if it has, FALSE otherwise.
	 */
	public boolean containsService(String serviceName) {
		if (serviceName.equalsIgnoreCase(defaultServiceName))
			return nameLookup.containsKey(getDefaultName());
		
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
	
	/**
	 * Retrieves a collection of every registered services.
	 * @return Collection of every registered service.
	 */
	public Collection<TService> getRegisteredServices() {
		return nameLookup.values();
	}
	
	/**
	 * Retrieves a collection of every enabled service.
	 * @return Every enabled service.
	 */
	public Iterable<TService> getEnabledServices() {
		
		// Do not include disabled services
		return Iterables.filter(nameLookup.values(), new Predicate<TService>() {
			@Override
			public boolean apply(TService service) {
				return isEnabled(service);
			}
		});
	}
	
	/**
	 * Enable all services.
	 */
	public void enableAll() {
		disabledLookup.clear();
	}
	
	/**
	 * Determines if a service is enabled.
	 * @param name - name of service.
	 * @return TRUE if the service is enabled, FALSE otherwise.
	 */
	public boolean isEnabled(String name) {
		if (name == null)
			throw new NullArgumentException("Service name cannot be null.");
		else
			return !disabledLookup.contains(name);
	}
	
	/**
	 * Determines if a given service is enabled.
	 * @param service - service.
	 * @return TRUE if the service is enabled, FALSE otherwise.
	 */
	public boolean isEnabled(TService service) {
		if (service == null)
			throw new NullArgumentException("Service cannot be null.");
		
		return isEnabled(service.getServiceName());
	}
	
	/**
	 * Sets whether or not a service is enabled.
	 * @param name - name of service.
	 * @param value - TRUE if the service should be enabled, FALSE otherwise.
	 */
	public void setEnabled(String name, boolean value) {
		if (name == null)
			throw new NullArgumentException("Service name cannot be null.");
		
		disabledLookup.remove(name);
		
		if (!value) {
			disabledLookup.add(name);
		}
	}
	
	/**
	 * Sets whether or not a service is enabled.
	 * @param service - service.
	 * @param value - TRUE if the service should be enabled, FALSE otherwise.
	 */
	public void setEnabled(TService service, boolean value) {
		if (service == null)
			throw new NullArgumentException("Service cannot be null.");
		
		setEnabled(service.getServiceName(), value);
	}
	
	/**
	 * Retrieves the default service by name.
	 * @return Default service name.
	 */
	public String getDefaultName() {
		return defaultName;
	}

	/**
	 * Sets the default service by name.
	 * @param defaultName default service name.
	 */
	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}
	
	/**
	 * Retrieves the default service, or the next non-disabled service if 
	 * the default service is disabled.
	 * @return The default service, or NULL if not found.
	 */
	public TService getDefaultService() {
		TService service = getByName(getDefaultName());
		
		// Handle disabled services
		if (isEnabled(service))
			return service;
		else
			return Iterables.getFirst(getEnabledServices(), null);
	}
}
