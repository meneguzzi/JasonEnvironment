/**
 * 
 */
package org.kcl.jason.env.action;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meneguzzi
 *
 */
public class StripsAction implements ExternalAction {
	
	protected Structure actionName;
	protected List<Literal> preconds;
	protected List<Literal> effects;
	
	/**
	 * 
	 */
	public StripsAction() {
		this.preconds = new ArrayList<Literal>();
		this.effects = new ArrayList<Literal>();
	}

	/* (non-Javadoc)
	 * @see org.kcl.jason.env.action.ExternalAction#getFunctor()
	 */
	public String getFunctor() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.kcl.jason.env.action.ExternalAction#consequences(jason.environment.Environment, java.lang.String, jason.asSyntax.Term[])
	 */
	public List<Literal> consequences(Environment env, String agName, Term... terms) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.kcl.jason.env.action.ExternalAction#execute(jason.environment.Environment, java.lang.String, jason.asSyntax.Term[])
	 */
	public boolean execute(Environment env, String agName, Term... terms) {
		// TODO Auto-generated method stub
		return false;
	}

}
