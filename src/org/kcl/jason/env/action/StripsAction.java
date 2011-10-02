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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.kcl.jason.env.action.parser.ParseException;
import org.kcl.jason.env.action.parser.StripsParser;

/**
 * An external action that represents a STRIPS operator (and can be parsed 
 * from a STRIPS specification).
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
	 * Creates a new StripsAction with no preconditions or effects (used for parsing).
	 */
	public StripsAction(String functor) {
		super(functor);
		init();
	}
	
	/**
	 * Creates a new StripsAction with no preconditions or effects (used for parsing).
	 */
	public StripsAction(Literal l) {
		super(l);
		init();
	}
	
	/**
	 * Creates a new StripsAction with an invocation condition <code>l</code> with
	 * preconditions <code>preconds</code> and effects <code>effects</code>.
	 * 
	 * @param l
	 * @param preconds
	 * @param effects
	 */
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
	 * Parses a string containing a strips operator.
	 * @param src
	 * @return
	 * @throws ParseException 
	 */
	public static StripsAction parseAction(String src) throws ParseException {
		StripsParser parser = new StripsParser(new StringReader(src));
		return parser.action();
	}
	
	/**
	 * Returns the name of the action \"/\" its arity.
	 * @return
	 */
	private final String getNameArity() {
		return this.getPredicateIndicator().toString();
	}

	/* (non-Javadoc)
	 * @see org.kcl.jason.env.action.ExternalAction#consequences(jason.environment.Environment, java.lang.String, jason.asSyntax.Term[])
	 */
	public List<Literal> consequences(Environment env, String agName, Term... terms) {
		if(terms.length != this.getArity()) {
			throw new RuntimeException("Tried to execute action "+getPredicateIndicator()+" with "+terms.length+" parameters");
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
	
	/**
	 * Returns a unifier representing the substitution of the parameters of this action with
	 * the <code>terms</code> specified. 
	 * @param terms
	 * @return
	 */
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
	
	/**
	 * Returns whether or not the preconditions for this action are valid in the 
	 * specified environment <code>env</code> for the specified agent <code>agName</code>,
	 * with the specified parameters <code>terms</code>, this method eventually delegates
	 * to {@link #precondsValid(Environment, String, Unifier)}.
	 * 
	 * @param env
	 * @param agName
	 * @param terms
	 * @return
	 */
	public boolean precondsValid(Environment env, String agName, Term... terms) {
		Unifier un = getUnifierForParameters(terms);
		return precondsValid(env, agName, un);
	}
	
	/**
	 * Returns whether or not the preconditions for this action are valid in the 
	 * specified {@link Environment} <code>env</code> for the specified agent 
	 * <code>agName</code>, under {@link Unifier} <code>un</code>.
	 * 
	 * @param env
	 * @param agName
	 * @param un
	 * @return
	 */
	public boolean precondsValid(Environment env, String agName, Unifier un) {
		for(Literal l:preconds) {
			l = new LiteralImpl(l);
			l.apply(un);
			boolean foundPrecond = env.containsPercept(l) || env.containsPercept(agName, l);
			if(!foundPrecond)
				return false;
		}
		return true;
	}
	
	/**
	 * Returns the consequences of applying this action to the specified 
	 * {@link Environment} <code>env</code> for the specified agent 
	 * <code>agName</code>, under {@link Unifier} <code>un</code>.
	 * @param env
	 * @param agName
	 * @param un
	 * @return
	 */
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
				synchronized(env) {
					List<Literal> effects = consequences(env, agName, un);
					for(Literal eff: effects) {
						if(eff.negated()) {
							eff = new LiteralImpl(true, eff);
							if(!env.removePercept(eff)) {
								return false;
							}
						} else {
							env.addPercept(eff);
						}
					}
				}
				Thread.yield();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
