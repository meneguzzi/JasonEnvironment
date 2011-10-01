/**
 * 
 */
package org.kcl.jason.env.scripted;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author meneguzzi
 *
 */
public class ScriptedEnvironmentTest {
	private ScriptedEnvironment env;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		env = new ScriptedEnvironment();
		env.init(new String[] {"examples/script.xml", "test/act", "test/domain.txt"});
	}

	/**
	 * Test method for {@link org.kcl.jason.env.scripted.ScriptedEnvironment#addPercept(jason.asSyntax.Literal)}.
	 */
	@Test
	public void testAddPerceptLiteral() throws Exception {
		Literal per = ASSyntax.parseLiteral("over(b1, pu1)");
		env.addPercept(per);
		assertTrue(env.containsPercept(per));
		
		env.addPercept(ASSyntax.parseLiteral("block(b1)"));
		per = ASSyntax.parseLiteral("block(b1)");
		assertTrue(env.containsPercept(per));
		
		env.addPercept(ASSyntax.parseLiteral("procUnit(pu1)"));
		assertEquals(3,env.getPercepts("nobody").size());
	}

	/**
	 * Test method for {@link org.kcl.jason.env.scripted.ScriptedEnvironment#removePercept(jason.asSyntax.Literal)}.
	 */
	@Test
	public void testRemovePerceptLiteral() throws Exception {
		env.addPercept(ASSyntax.parseLiteral("over(b1, pu1)"));
		env.addPercept(ASSyntax.parseLiteral("block(b1)"));
		env.addPercept(ASSyntax.parseLiteral("procUnit(pu1)"));
		
		Literal per = ASSyntax.parseLiteral("over(b1, pu1)");
		env.removePercept(per);
		env.containsPercept(per);
	}

	/**
	 * Test method for {@link org.kcl.jason.env.scripted.ScriptedEnvironment#executeAction(java.lang.String, jason.asSyntax.Structure)}.
	 */
	@Test
	public void testExecuteAction() throws Exception {
		
		env.addPercept(ASSyntax.parseLiteral("over(b1, pu1)"));
		env.addPercept(ASSyntax.parseLiteral("block(b1)"));
		env.addPercept(ASSyntax.parseLiteral("procUnit(pu1)"));

		Structure act = ASSyntax.parseStructure("process(b1,pu1)");
		
		assertTrue(env.executeAction("testAgent", act));
		Literal processed = ASSyntax.parseStructure("processed(b1,pu1)");
		assertTrue(env.containsPercept(processed));
	}

	/**
	 * Test method for {@link org.kcl.jason.env.scripted.ScriptedEnvironment#findPercepts(jason.asSyntax.Literal)}.
	 */
	@Test
	public void testFindPercepts() throws Exception {
		Literal lOver1 = ASSyntax.parseLiteral("over(b1, pu1)");
		Literal lOver2 = ASSyntax.parseLiteral("over(b2, pu1)");
		Literal lOver3 = ASSyntax.parseLiteral("over(b3, pu2)");
		env.addPercept(lOver1);
		env.addPercept(lOver2);
		env.addPercept(lOver3);
		env.addPercept(ASSyntax.parseLiteral("block(b1)"));
		env.addPercept(ASSyntax.parseLiteral("procUnit(pu1)"));
		List<Literal> percepts = env.findPercepts(ASSyntax.parseLiteral("over(B, pu1)"));
		assertNotNull(percepts);
		assertTrue(percepts.contains(lOver1));
		assertTrue(percepts.contains(lOver2));
		assertTrue(!percepts.contains(lOver3));
	}

}
