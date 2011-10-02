/**
 * 
 */
package org.kcl.jason.env.action.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.environment.Environment;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kcl.jason.env.action.StripsAction;

/**
 * @author meneguzzi
 * 
 */
public class StripsParserTest {

	StripsParser parser;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		parser = new StripsParser();
	}

	/**
	 * Test method for
	 * {@link org.kcl.jason.env.action.parser.StripsParser#parseStripsActions(java.io.File)}
	 * .
	 */
	@Test
	public void testParseStripsActionsFile() throws Exception {
		List<StripsAction> acts = parser.parseStripsActions(new File("test/domain.txt"));
		assertNotNull(acts);
		assertEquals(3, acts.size());
		assertEquals("process", acts.get(0).getFunctor());
		assertEquals(2, acts.get(0).getArity());

		StripsAction process = acts.get(0);
		Environment env = new Environment();
		env.addPercept(ASSyntax.parseLiteral("over(b1, pu1)"));
		env.addPercept(ASSyntax.parseLiteral("block(b1)"));
		env.addPercept(ASSyntax.parseLiteral("procUnit(pu1)"));

		Term terms[] = new Term[2];
		terms[0] = ASSyntax.parseTerm("b1");
		terms[1] = ASSyntax.parseTerm("pu1");
		assertFalse(process.precondsValid(env, "testAgent", new Unifier()));
		assertTrue(process.precondsValid(env, "testAgent", terms));
		
		Term invocation = ASSyntax.parseStructure("process(b1,pu1)");
		Unifier un = new Unifier();
		un.unifies(process, invocation);
		assertTrue(process.precondsValid(env, "testAgent", un));
		
		assertTrue(process.execute(env, "testAgent", terms));
		Literal processed = ASSyntax.parseStructure("processed(b1,pu1)");
		assertTrue(env.containsPercept(processed));
	}

}
