package com.malinowski.models;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class EmailTest {
	Email email, emptyFieldsEmail;
	JSONObject json;

	@Before
	public void setUp() throws Exception {
		json = new JSONObject();
		json.put("to", "hello@you.com");
		json.put("to_name", "Francois Whatever");
		json.put("from", "helloFrom@you.com");
		json.put("from_name", "Hey bro");
		json.put("subject", "Subject of the email");
		json.put("body", "<html><body>Content of the email</body></html>");
		
		email = new Email(json);
		emptyFieldsEmail = new Email(new JSONObject());
	}
	
	/**
	 * Test the isValid method and make sure all validations are implemented
	 */
	@Test
	public void testIsValid() {
		assertTrue(email.isValid());
		
		json.put("to", "");
		email = new Email(json);
		assertFalse(email.isValid());
		json.put("to", "hello@you.com");
		email = new Email(json);
		assertTrue(email.isValid());
		
		json.put("to_name", "");
		email = new Email(json);
		assertFalse(email.isValid());
		json.put("to_name", "Francois Whatever");
		email = new Email(json);
		assertTrue(email.isValid());
		
		json.put("from", "");
		email = new Email(json);
		assertFalse(email.isValid());
		json.put("from", "helloFrom@you.com");
		email = new Email(json);
		assertTrue(email.isValid());
		
		json.put("from_name", "");
		email = new Email(json);
		assertFalse(email.isValid());
		json.put("from_name", "Hey bro");
		email = new Email(json);
		assertTrue(email.isValid());
		
		json.put("subject", "");
		email = new Email(json);
		assertFalse(email.isValid());
		json.put("subject", "Subject of the email");
		email = new Email(json);
		assertTrue(email.isValid());
	}

	/**
	 * Test the format validation of the to field
	 */
	@Test
	public void testEmailToFormat() {
		assertEmailFormat("to");
	}
	
	/**
	 * Test the format validation of the from field
	 */
	@Test
	public void testEmailFromFormat() {
		assertEmailFormat("from");
	}

	/**
	 * Test the getter of the to field
	 */
	@Test
	public void testGetTo() {
		assertEquals("hello@you.com", email.getTo());
		assertEquals("", emptyFieldsEmail.getTo());
	}

	/**
	 * Test the getter of the toName field
	 */
	@Test
	public void testGetToName() {
		assertEquals("Francois Whatever", email.getToName());
		assertEquals("", emptyFieldsEmail.getToName());
	}

	/**
	 * Test the getter of the from field
	 */
	@Test
	public void testGetFrom() {
		assertEquals("helloFrom@you.com", email.getFrom());
		assertEquals("", emptyFieldsEmail.getFrom());
	}

	/**
	 * Test the getter of the fromName field
	 */
	@Test
	public void testGetFromName() {
		assertEquals("Hey bro", email.getFromName());
		assertEquals("", emptyFieldsEmail.getFromName());
	}

	/**
	 * Test the getter of the subject field
	 */
	@Test
	public void testGetSubject() {
		assertEquals("Subject of the email", email.getSubject());
		assertEquals("", emptyFieldsEmail.getSubject());
	}

	/**
	 * Test the getter of the body field
	 */
	@Test
	public void testGetBody() {
		assertEquals("<html><body>Content of the email</body></html>", email.getBody());
		assertEquals("", emptyFieldsEmail.getBody());
	}

	/**
	 * Test the getter of the body field rendered as text (without html tags)
	 * We make sure that the HTML tags do not appear
	 */
	@Test
	public void testGetTextBody() {
		json.put("body", "<html>something<head>something else</head>Whatelse<br /><body>Done and <strong>done</strong></body></html>");
		Email email2 = new Email(json);
		assertEquals("somethingsomething elseWhatelse Done and done", email2.getTextBody());
		assertEquals("", emptyFieldsEmail.getTextBody());
	}

	private void assertEmailFormat(String jsonKey) {
		json.put(jsonKey, "hello@asd.com");
		email = new Email(json);
		assertTrue(email.isValid());
		
		json.put(jsonKey, "Ah9.Ez%0-Z@y0Auz9.dfd.Za.aAx");
		email = new Email(json);
		assertTrue(email.isValid());
		
		json.put(jsonKey, "hello@asd");
		email = new Email(json);
		assertFalse(email.isValid());
		
		json.put(jsonKey, "hello@.com");
		email = new Email(json);
		assertFalse(email.isValid());
		
		json.put(jsonKey, "hello@asd.");
		email = new Email(json);
		assertFalse(email.isValid());
		
		json.put(jsonKey, "helloasdcom");
		email = new Email(json);
		assertFalse(email.isValid());
		
		json.put(jsonKey, "hello@.com");
		email = new Email(json);
		assertFalse(email.isValid());
		
		json.put(jsonKey, "@asd.com");
		email = new Email(json);
		assertFalse(email.isValid());
	}

}
