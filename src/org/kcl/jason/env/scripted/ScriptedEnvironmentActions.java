package org.kcl.jason.env.scripted;

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.kcl.jason.env.ModularEnvironment;

public class ScriptedEnvironmentActions extends
		ModularEnvironment<ScriptedEnvironment> {
	protected Logger logger = Logger.getLogger(ScriptedEnvironment.class
			.getName());

	public ScriptedEnvironmentActions(ScriptedEnvironment env, String path) {
		super(env);
		//addActions();
		addActions(path);
	}

	/*private void addActions(String pckgname) {
		try {
			Class classes[] = getClasses(pckgname);
			for (int i = 0; i < classes.length; i++) {
				try {
					this.addExternalAction(classes[i]);
				} catch (Exception e) {
					//e.printStackTrace();
					logger.warning("Class "+classes[i]+" is not an ExternalAction");
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	public static Class[] getClasses(String pckgname)
			throws ClassNotFoundException {
		ArrayList<Class> classes = new ArrayList<Class>();
		// Get a File object for the package
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = "/" + pckgname.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " (" + directory
					+ ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(pckgname + '.'
							+ files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be a valid package");
		}
		Class[] classesA = new Class[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}

	/*private void addActions(String pckgname) {
		try {
			this.addExternalAction("act.charge");
			this.addExternalAction("act.drop");
			this.addExternalAction("act.move");
			this.addExternalAction("act.pickup");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	private void addActions(String path) {
		File file = new File(path);
		if(file.isDirectory()) {
			String files[] = file.list();
			for (int i = 0; i < files.length; i++) {
				if(files[i].endsWith(".class") || files[i].endsWith(".java")) {
					String className = files[i];
					className = className.substring(0,className.lastIndexOf('.'));
					String pkg = path.replace('/', '.');
					className = pkg+"."+className;
					try {
						this.addExternalAction(className);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			// TODO implement JAR reading
		}
		
		/*try {
			this.addExternalAction("rover.act.move");
			this.addExternalAction("rover.act.perfectMove");
			this.addExternalAction("rover.act.charge");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/*
	 * public boolean executeAction(String agName, Structure act) { boolean
	 * success = true;
	 * 
	 * ExternalAction<ScriptedEnvironment> action =
	 * actions.get(act.getFunctor()); if(action != null) { success =
	 * action.execute(env, agName, act.getTermsArray()); } else { success =
	 * false; }
	 * 
	 * if(!success) { logger.info("Action "+act.toString()+" failed"); } return
	 * success; }
	 */

	public Literal updateBattery(Literal battLiteral) {
		Term t0 = battLiteral.getTerm(0);
		Literal retLiteral = battLiteral;
		if (t0.isNumeric()) {
			NumberTerm term = (NumberTerm) t0;
			int battCondition = (int) term.solve();
			battCondition--;
			retLiteral = Literal.parseLiteral("batt(" + battCondition + ")");
			// logger.info("Batt is "+battCondition);
		}

		return retLiteral;
	}
}
