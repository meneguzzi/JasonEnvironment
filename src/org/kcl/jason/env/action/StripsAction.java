/**
 * 
 */
package org.kcl.jason.env.action;

import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meneguzzi
 *
 */
@SuppressWarnings("rawtypes")
public class StripsAction extends Structure implements ExternalAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected List<Literal> preconds;
	protected List<Literal> effects;
	
	/**
	 * 
	 */
	public StripsAction(String functor) {
		super(functor);
		init();
	}
	
	/**
	 * 
	 */
	public StripsAction(Literal l) {
		super(l);
		init();
	}
	
	public StripsAction(Literal l, List<Literal> preconds, List<Literal> effects) {
		super(l);
		this.preconds = new ArrayList<Literal>(preconds);
		this.effects = new ArrayList<Literal>(effects);
	}
	

	private final void init() {
		this.preconds = new ArrayList<Literal>();
		this.effects = new ArrayList<Literal>();
	}
	
	/**
	 * 
	 * @return
	 */
	private final String getNameArity() {
		return this.getFunctor()+"/"+this.getArity();
	}

	/* (non-Javadoc)
	 * @see org.kcl.jason.env.action.ExternalAction#consequences(jason.environment.Environment, java.lang.String, jason.asSyntax.Term[])
	 */
	public List<Literal> consequences(Environment env, String agName, Term... terms) {
		if(terms.length != this.getArity()) {
			throw new RuntimeException("Tried to execute action "+getNameArity()+" with "+terms.length+" parameters");
		}
		
		Unifier un = new Unifier();
		
		for(int i=0; i<this.getArity(); i++) {
			Term t1g = this.getTerm(i);
			Term t2g = terms[i];
			if(!un.unifiesNoUndo(t1g, t2g)) {
				throw new RuntimeException("Error calculating the consequences for "+getNameArity());
			}
		}
		
		List<Literal> res = new ArrayList<Literal>(effects.size());
		for(Literal e:effects) {
			e = e.copy();
			e.apply(un);
			res.add(e);
		}
		
		return res;
	}
	
	private final Unifier getUnifierForParameters(Term... terms) {
		Unifier un = new Unifier();
		if(terms.length != this.getArity()) {
			throw new RuntimeException("Tried to execute action "+getNameArity()+" with "+terms.length+" parameters");
		}
		
		for(int i=0; i<this.getArity(); i++) {
			Term t1g = this.getTerm(i);
			Term t2g = terms[i];
			if(!un.unifiesNoUndo(t1g, t2g)) {
				throw new RuntimeException("Error calculating the consequences for "+getNameArity());
			}
		}
		return un;
	}

	/* (non-Javadoc)
	 * @see org.kcl.jason.env.action.ExternalAction#execute(jason.environment.Environment, java.lang.String, jason.asSyntax.Term[])
	 */
	public boolean execute(Environment env, String agName, Term... terms) {
		Unifier un = getUnifierForParameters(terms);
		if(precondsValid(env, agName, un)) {
			List<Literal> eff = consequences(env, agName, un);
			for(Literal per:eff) {
				env.addPercept(per);
			}
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean precondsValid(Environment env, String agName, Term... terms) {
		Unifier un = getUnifierForParameters(terms);
		return precondsValid(env, agName, un);
	}
	
	public boolean precondsValid(Environment env, String agName, Unifier un) {
		for(Literal l:preconds) {
			l = new LiteralImpl(l);
			l.apply(un);
			boolean foundPrecond = env.getPercepts(agName).contains(l);
			if(!foundPrecond)
				return false;
		}
		return true;
	}
	
	public List<Literal> consequences(Environment env, String agName, Unifier un) {
		List<Literal> res = new ArrayList<Literal>(effects.size());
		for(Literal e:effects) {
			e = e.copy();
			e.apply(un);
			res.add(e);
		}
		
		return res;
	}

	/* (non-Javadoc)
	 * @see org.kcl.jason.env.action.ExternalAction#execute(jason.environment.Environment, java.lang.String, jason.asSyntax.Structure)
	 */
	public boolean execute(Environment env, String agName, Structure invocation) {
		Unifier un = new Unifier();
		if(un.unifies(invocation, this)) {
			if(precondsValid(env, agName, un)) {
				List<Literal> effects = consequences(env, agName, un);
				for(Literal eff: effects) {
					if(eff.negated()) {
						env.removePercept(eff);
					} else {
						env.addPercept(eff);
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
