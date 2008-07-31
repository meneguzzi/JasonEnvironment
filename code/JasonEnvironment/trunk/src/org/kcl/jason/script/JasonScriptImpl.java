package org.kcl.jason.script;

import jason.asSyntax.Literal;
import jason.asSyntax.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class JasonScriptImpl implements JasonScript {
	protected HashMap<Integer, List<Literal>> eventsMap;
	protected Hashtable<Integer, Boolean> wipeEvents;
	
	public JasonScriptImpl() {
		this.eventsMap = new HashMap<Integer, List<Literal>>();
		this.wipeEvents = new Hashtable<Integer, Boolean>();
	}

	public void addEvent(int time, Rule rule) {
		List<Literal> list = null;
		if (eventsMap.containsKey(time) && eventsMap.get(time) != null) {
			list = eventsMap.get(time);
		} else {
			list = new ArrayList<Literal>();
		}
		
		list.add(rule);
	}

	public void addEvents(int time, List<Literal> events) {
		if (eventsMap.containsKey(time) && eventsMap.get(time) != null) {
			eventsMap.get(time).addAll(events);
		} else {
			eventsMap.put(time, events);
		}
	}

	public List<Literal> getEvents(long time) {
		if (eventsMap.containsKey(time) && eventsMap.get(time) != null) {
			return eventsMap.get(time);
		}
		return null;
	}

	public List<Literal> getPercepts(long time) {
		if (eventsMap.containsKey(time) && eventsMap.get(time) != null) {
			List<Literal> list = new ArrayList<Literal>(eventsMap.get(time));
			return list;
		}
		return null;
	}

	public void addWipeEvent(int time, boolean wipe) {
		this.wipeEvents.put(time, wipe);
	}

	public boolean isWipeEvent(long time) {
		if(this.wipeEvents.containsKey(time)) {
			return this.wipeEvents.get(time);
		} else {
			return false;
		}
	}

}
