package org.kcl.jason.env;

import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.util.HashMap;

import org.kcl.jason.env.action.ExternalAction;

/**
 * A modular environment class for Jason, allowing easy development of new 
 * environments for Jason, including the concept of an <em>external</em> action,
 * similar in development concept to Jason's internal actions, but this one 
 * used in the environment, thus avoiding concentrating the action 
 * implementation on the {@link #executeAction(String, Structure)} method from
 * {@linkplain Environment}.
 * 
 * @author Felipe Meneguzzi
 *
 * @param <E> The specific type of Modular environment being used, in case of 
 *            subclassing
 */
public class ModularEnvironment<E extends Environment> implements EnvironmentActions {
	protected HashMap<String, ExternalAction<E>> actions;
	protected E env;
	
	protected ModularEnvironment(E env) {
		actions = new HashMap<String, ExternalAction<E>>();
		this.env = env;
	}
	
	@SuppressWarnings("unchecked")
	public void addExternalAction(String classname) throws Exception {
		ExternalAction<E> action;
		action = instantiateAction(ExternalAction.class, classname);
		this.actions.put(action.getFunctor(), action);
	}
	
	@SuppressWarnings("unchecked")
	public void addExternalAction(Class c) throws Exception {
		ExternalAction<E> action;
		action = instantiateAction(ExternalAction.class, c);
		this.actions.put(action.getFunctor(), action);
	}
	
	@SuppressWarnings("unchecked")
	protected <K> K instantiateAction(Class<K> classType, Class c) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		K newObject = null;
		
		if(classType.isAssignableFrom(c)) {
			newObject = (K) c.newInstance();
		} else {
			throw new InstantiationException(classType.getCanonicalName());
		}
		return newObject;
	}
	
	protected <K> K instantiateAction(Class<K> classType, String classname) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> c = Class.forName(classname);
		
		return instantiateAction(classType, c);
	}
	
	public boolean executeAction(String agName, Structure act) {
		if(!actions.containsKey(act.getFunctor())) {
			return false;
		} else {
			ExternalAction<E> action = actions.get(act.getFunctor());
			return action.execute(env, agName, act.getTermsArray());
		}
	}
}
