package org.kcl.jason.env.action;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.environment.Environment;

import java.util.List;
import java.util.logging.Logger;

/**
 * An interface for an action executable from within a certain environment.
 * @author meneguzzi
 *
 */
public interface ExternalAction<E extends Environment> {
	public static final Logger logger = Logger.getLogger(ExternalAction.class.getName());
	/**
	 * Returns the functor string for this action, effectively the name of the action.
	 * @return The action's functor.
	 */
	public String getFunctor();
	
	/**
	 * Returns a list with the consequences of this action in terms of positive and negative 
	 * literals, under the assumption of a completely observable world. However, implementors
	 * are free to implement actions whose expected consequences do not match with the actual
	 * results of the <code>executeAction</code> method. If the action is expected not to
	 * succeed in the provided environment, the resulting list should contain a single 
	 * <i>false</i> literal element.
	 * 
	 * @param env 	 The environment in which this action is executed.
	 * @param agName The name of the agent executing the action.
	 * @param terms	 The parameters used in this action.
	 * @return		 A list with the consequences of this action, in case the action succeeds, 
	 * 				 or a list contain a single <i>false</i> literal element.
	 */
	public List<Literal> consequences(E env, String agName, Term ... terms);
	
	/**
	 * Executes the external action called by the specified agent from the 
	 * specified environment. In theory the this agent should add the consequences
	 * specified by the <code>consequences</code> method to the list of perceptions
	 * of the agent, however, if the world is not completely observable, this method
	 * can result in a different outcome.
	 * 
	 * @param env	 The environment in which this action is executed.
	 * @param agName The name of the agent executing the action.
	 * @param terms	 The parameters to this action.
	 * @return		 Whether or not the action has been successful
	 */
	public boolean execute(E env, String agName, Term ... terms);
}
