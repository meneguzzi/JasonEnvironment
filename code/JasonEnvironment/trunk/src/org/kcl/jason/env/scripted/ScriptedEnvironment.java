/**
 * 
 */
package org.kcl.jason.env.scripted;

import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.runtime.MASConsoleGUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kcl.jason.env.EnvironmentActions;
import org.kcl.jason.script.JasonScript;
import org.kcl.jason.script.JasonScriptContentHandler;
import org.kcl.jason.script.JasonScriptImpl;
import org.xml.sax.SAXException;


/**
 * @author Felipe Rech Meneguzzi
 *
 */
public class ScriptedEnvironment extends Environment implements Runnable {
	protected Logger logger = Logger.getLogger(ScriptedEnvironment.class.getName());
	
	protected JasonScript script = null;
	
	protected boolean running;
	
	protected boolean paused;
	
	protected Thread environmentThread;
	
	protected int cycleSize;
	
	protected long currentCycle;
	
	protected EnvironmentActions actions;
	
	protected List<ScriptedEnvironmentListener> listeners;
	
	/**
	 * A list of percepts that may be seen from external actions
	 */
	protected List<Literal> accessiblePercepts;
	
	public ScriptedEnvironment() {
		this.running = false;
		this.environmentThread = new Thread(this, "ScriptedEnvironment");
		this.cycleSize = 1000;
		this.currentCycle = 0;
		this.accessiblePercepts = Collections.synchronizedList(new ArrayList<Literal>());
		this.listeners = new ArrayList<ScriptedEnvironmentListener>();
		//this.actions = new ScriptedEnvironmentActions(this);
	}
	@Override
	public void init(String[] args) {
		super.init(args);
		clearPercepts();
		this.resume();
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			JasonScriptContentHandler contentHandler = new JasonScriptContentHandler();
			//First parse the script file
			if(args.length > 0) {
				File scriptFile = new File(args[0]);
				if(scriptFile.exists()) {
					logger.info("Reading script file: "+args[0]);
					parser.parse(scriptFile, contentHandler);
					this.script = contentHandler.getJasonScript();
				} else {
					this.script = new JasonScriptImpl();
				}
				//Then instantiate the proper external actions
				if(args.length > 1) {
					this.actions = new ScriptedEnvironmentActions(this, args[1]);
				} else {
					this.actions = new ScriptedEnvironmentActions(this, ScriptedEnvironment.class.getPackage().getName());
				}
			} else {
				this.script = new JasonScriptImpl();
			}
			this.running = true;
			environmentThread.start();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
		this.running = false;
	}
	
	/**
	 * Adds a new scripted environment listener
	 * @param listener
	 */
	public void addScriptedEnvironmentListener(ScriptedEnvironmentListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Notifies all listeners that time has changed.
	 * @param newTime
	 */
	private void timeChangedEvent(long newTime) {
		for (ScriptedEnvironmentListener listener : listeners) {
			listener.timeChanged(newTime);
		}
	}
	
	/**
	 * Return whether or not the environment thread is paused.
	 * @return
	 */
	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * Pauses the environment thread
	 */
	public void pause() {
		paused = true;
	}
	
	/**
	 * Resumes the environment thread after being paused
	 */
	public void resume() {
		paused = false;
	}
	
	public void addPercepts(List<Literal> list) {
		for (Literal literal : list) {
			logger.info("Adding percept: "+literal);
			addPercept(literal);
		}
	}
	
	/* Since we cannot access the percepts in the regular Jason environment, 
	 * we create a parallel list to maintain them accessible.
	 * */
	@Override
	public void addPercept(Literal per) {
		if (per != null) {
			if(!this.accessiblePercepts.contains(per)) {
				this.accessiblePercepts.add(per);
			}
			super.addPercept(per);
		}
	}
	
	@Override
	public boolean removePercept(Literal per) {
		if(per != null) {
			this.accessiblePercepts.remove(per);
			return super.removePercept(per);
		} else {
			return false;
		}
	}
	
	@Override
	public void clearPercepts() {
		if(!accessiblePercepts.isEmpty()) {
			accessiblePercepts.clear();
		}
		super.clearPercepts();
	}
	
	/**
	 * Finds the percepts matching the supplied query.
	 * @param queryLiteral
	 * @return
	 */
	public List<Literal> findPercepts(Literal queryLiteral) {
		List<Literal> matchingPercepts = new ArrayList<Literal>();
		
		for(Literal percept : accessiblePercepts) {
			//if the query matches the percept perfectly
			//there is no need to search further
			if(queryLiteral.equals(percept)) {
				matchingPercepts.add(percept);
				return matchingPercepts;
			} else {
				Unifier unifier = new Unifier();
				if(unifier.unifies(queryLiteral, percept)) {
					matchingPercepts.add(percept);
				}
			}
			
		}
		
		return matchingPercepts;
	}
	
	/**
	 * Returns the current time in the simulated environment
	 * @return
	 */
	public synchronized long getCurrentCycle() {
		return currentCycle;
	}
	
	/* ************************************************************* */
	
	@Override
	public boolean executeAction(String agName, Structure act) {
		//return super.executeAction(agName, act);
		return this.actions.executeAction(agName, act);
	}
	
	public Literal findMatchingLiteral(Literal prototype, List<Literal> literals) {
		if(literals == null) {
			return null;
		}
		Unifier unifier = new Unifier();
		for (Literal literal : literals) {
			if(unifier.unifies(prototype, literal))
				return literal;
			unifier.clear();
		}
		return null;
	}
	
	public Literal findLiteralByFunctor(String key, List<Literal> literals) {
		if(literals == null)
			return null;
		for (Literal literal : literals) {
			if(literal.getFunctor().equals(key)) {
				return literal;
			}
		}
		return null;
	}
	
	
	
	public List<Literal> findLiteralsByFunctor(String key, List<Literal> literals) {
		List <Literal> ret = new ArrayList<Literal>();
		for (Literal literal : literals) {
			if(literal.getFunctor().equals(key)) {
				ret.add(literal);
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public synchronized void run() {
		Literal time = Literal.parseLiteral("time(0)");
		while (running) {
			try {
				wait(cycleSize);
				//If we have the console GUI
				//Then this could have been paused
				if( MASConsoleGUI.hasConsole()) {
					//In which case, we simply jump
					//cycles till it is no longer paused
					if(MASConsoleGUI.get().isPause() || paused) {
						continue;
					} else {
						if(script.isWipeEvent(currentCycle)) {
							logger.info("Clearing Percepts");
							this.clearPercepts();
						}
							
						if(script.getEvents(currentCycle) != null) {
							this.addPercepts(script.getPercepts(currentCycle));
						}
						
						this.removePercept(time);
						time = Literal.parseLiteral("time("+currentCycle+")");
						this.addPercept(time);
						
						currentCycle++;
						//then notify all listeners
						this.timeChangedEvent(currentCycle);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
