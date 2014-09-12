
package org.osgi.impl.service.resourcemonitoring.persistency.json;

/**
 */
public class JsonTokenizer {

	private final String	stringToBeParsed;
	private String			currentToken;
	private int				currentPosition	= 0;

	/**
	 * @param pStringToBeParsed
	 */
	public JsonTokenizer(String pStringToBeParsed) {
		stringToBeParsed = pStringToBeParsed;
		nextToken();
	}

	/**
	 * Go further into the string to parse the next token. This new token is
	 * retrieved throught a call to current token;
	 */
	public void nextToken() {
		// String token = null;
		int initialPosition = currentPosition;
		while ((currentPosition < stringToBeParsed.length())
				&& (!isDelimeter())) {
			currentPosition++;
		}

		if (currentPosition == stringToBeParsed.length()) {
			currentToken = null;
			return;
		}

		int endPosition = currentPosition;
		if (endPosition - initialPosition == 0) {
			endPosition++;
			currentPosition++;
		}
		currentToken = stringToBeParsed.substring(initialPosition, endPosition);

	}

	/**
	 * Get current token.
	 * 
	 * @return current token.
	 */
	public String getCurrentToken() {
		return currentToken;
	}

	/**
	 * Is the current character a JSON delimiter. A JSON delimiter is one of the
	 * following characters:
	 * <ul>
	 * <li>[</li>
	 * <li>]</li>
	 * <li>{</li>
	 * <li></li>
	 * <li>,</li>
	 * <li>:</li>
	 * <li>"</li>
	 * </ul>
	 * 
	 * @return true if the current character is a json delimiter.
	 */
	private boolean isDelimeter() {
		char currentCharacter = stringToBeParsed.charAt(currentPosition);
		return (currentCharacter == '[') || (currentCharacter == ']')
				|| (currentCharacter == '{') || (currentCharacter == '}')
				|| (currentCharacter == ',') || (currentCharacter == ':')
				|| (currentCharacter == '"');
	}
}
