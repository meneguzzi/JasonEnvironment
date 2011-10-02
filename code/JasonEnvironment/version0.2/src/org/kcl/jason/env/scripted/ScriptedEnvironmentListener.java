package org.kcl.jason.env.scripted;

public interface ScriptedEnvironmentListener {
	/**
	 * This method is invoked whenever the simulated time changes in a
	 * <code>ScriptedEnvironment</code>.
	 * 
	 * @param time The new simulated time.
	 */
	public void timeChanged(long time);
}
