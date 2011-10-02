package org.kcl.jason.env;

import org.kcl.jason.env.action.ExternalAction;

import jason.asSyntax.Structure;
import jason.environment.Environment;

public interface EnvironmentActions<E extends Environment> {
	
	public boolean executeAction(String agName, Structure act);
	
	/**
	 * Adds an already constructed action into the set of modular actions.
	 * @param action
	 */
	public void addExternalAction(ExternalAction<E> act);
}
