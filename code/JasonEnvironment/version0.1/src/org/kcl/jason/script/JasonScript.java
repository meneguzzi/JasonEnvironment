package org.kcl.jason.script;

import jason.asSyntax.Literal;
import jason.asSyntax.Rule;

import java.util.List;

/**
 * An interface to a script used to post sensor information to JasonAgents
 * at regular intervals.
 * 
 * @author Felipe Meneguzzi
 *
 */
public interface JasonScript {
	
	/**
	 * Returns a list with all the events for the specified time.
	 * @param time The point in time for which the events are to be returned.
	 * @return A list of <code>jason.asSyntax.Rule</code>.
	 */
	public List<Literal> getEvents(int time);
	
	/**
	 * Conveniency method to allow the list of events to be expressed as literals
	 * @param time The point in time for which the events are to be returned.
	 * @return A list of <code>jason.asSyntax.Literal</code>.
	 */
	public List<Literal> getPercepts(int time);
	
	/**
	 * Tells wether or not the current perceptions should be wiped at the specified
	 * point in time.
	 * @param time
	 * @return
	 */
	public boolean isWipeEvent(int time);
	
	/**
	 * Adds a list of events to be posted at the specified point in time.
	 * @param time The point in time for which the events are to be added.
	 * @param events A list of <code>jason.asSyntax.Rule</code>.
	 */
	public void addEvents(int time, List<Literal> events);
	
	/**
	 * Adds a single event to the specified point in time.
	 * @param time The point in time for which the event is to be added.
	 * @param rule An instance of <code>jason.asSyntax.Rule</code>
	 */
	public void addEvent(int time, Rule rule);
	
	/**
	 * Adds a <em>wipe</em> event, cleaning up any perceptions in a given time.
	 * @param time The time at which a wipe may occur.
	 * @param wipe Wheter or not to wipe the current perceptions.
	 */
	public void addWipeEvent(int time, boolean wipe);
}
