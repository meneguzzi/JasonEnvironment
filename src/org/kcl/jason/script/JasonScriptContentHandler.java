/**
 * 
 */
package org.kcl.jason.script;

import jason.asSyntax.Literal;
import jason.asSyntax.parser.ParseException;
import jason.asSyntax.parser.as2j;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * An XML event handler for a JasonScript
 * @author Felipe Meneguzzi
 */
public class JasonScriptContentHandler extends DefaultHandler {
	
	public static final String SCRIPT="script";
	public static final String STEP="step";
	
	public static final String ATTR_TIME="time";
	public static final String ATTR_WIPE="wipe";
	
	protected JasonScript jasonScript;
	protected List<Literal> currentEventList;
	protected int currentTime;
	protected as2j jasonParser;
	protected CharArrayReader charArrayReader;
	protected CharArrayWriter charArrayWriter;

	public JasonScriptContentHandler() {
		this.jasonScript = null;
		currentEventList = null;
		currentTime = 0;
		charArrayReader = null;
		jasonParser = new as2j(charArrayReader);
		charArrayWriter = new CharArrayWriter();
	}

	public JasonScript getJasonScript() {
		return jasonScript;
	}

	@Override
	public void startDocument() throws SAXException {
		this.jasonScript = new JasonScriptImpl();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(qName.equals(STEP)) {
			//System.out.println("Processing "+STEP);
			if(attributes.getValue(ATTR_TIME) == null)
				throw new SAXException("Element "+STEP+" must have a "+ATTR_TIME+" attribute");
			try{
				currentTime = Integer.parseInt(attributes.getValue(ATTR_TIME));
			} catch (Exception e) {
				throw new SAXException("Invalid "+ATTR_TIME+" attribute", e);
			}
			if(attributes.getValue(ATTR_WIPE) != null) {
				boolean wipe = Boolean.parseBoolean(attributes.getValue(ATTR_WIPE));
				jasonScript.addWipeEvent(currentTime, wipe);
			}
			currentEventList = new ArrayList<Literal>();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(currentEventList != null && charArrayWriter != null) {
			charArrayWriter.write(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals(STEP)) {
			if(charArrayWriter != null && charArrayWriter.size() > 0) {
//				System.out.println("Processing events at time "+currentTime);
				charArrayReader = new CharArrayReader(charArrayWriter.toCharArray());
				jasonParser.ReInit(charArrayReader);
				try {
					jasonParser.belief_base(currentEventList);
				} catch (ParseException e) {
					throw new SAXException("Invalid events at time "+currentTime, e);
				}
				this.jasonScript.addEvents(currentTime, currentEventList);
				charArrayWriter.reset();
				currentEventList = null;
			} else {
				throw new SAXException("Problems processing events");
			}
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}
}
