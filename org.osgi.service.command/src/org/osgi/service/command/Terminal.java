/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package org.osgi.service.command;

import java.io.*;

/**
 * The Terminal interface describes a minimal terminal that can easily be mapped
 * to command line editing tools.
 * 
 * A Terminal is associated with an Input Stream and an Output Stream. The Input
 * Stream represents the keyboard and the Output Stream the screen.
 * 
 * A terminal does not block the input, each character is returned as it is
 * typed, no buffering or line editing takes place, characters are also not
 * echoed. However, the Input Stream is not restricted to bytes only, it can
 * also return translated key strokes. Integers from 1000 are used for those.
 * Not all keys have to be supported by an implementation.
 * 
 * A number of functions is provided to move the cursor and erase
 * characters/lines/screens. Any text outputed to the Output Stream is
 * immediately added to the cursor position, which is then moved forwards. The
 * control characters (LF,CR,TAB,BS) perform their normal actions. However lines
 * do not wrap. Text typed that is longer than the window will not be visible,
 * it is the responsibility of the sender to ensure this does not happen.
 * 
 * A screen is considered to be {@link #height()} lines that each have
 * {@link #width()} characters. For cursor positioning, the screen is assumed to
 * be starting at 0,0 and increases its position from left to right and from top
 * to bottom. Positioning outside the screen bounds is undefined.
 */
public interface Terminal {
	/**
	 * Cursor up key
	 */
	int	CURSOR_UP		= 1000;
	/**
	 * Cursor down key.
	 */
	int	CURSOR_DOWN		= 1001;
	/**
	 * Cursors forward key. Usually right.
	 */
	int	CURSOR_FORWARD	= 1002;

	/**
	 * Cursors backward key. Usually left.
	 */
	int	CURSOR_BACKWARD	= 1003;

	/**
	 * Page up key
	 */
	int	PAGE_UP			= 1004;
	/**
	 * Page down key
	 */
	int	PAGE_DOWN		= 1005;
	/**
	 * Home key
	 */
	int	HOME			= 1006;
	/**
	 * End key
	 */
	int	END				= 1007;
	/**
	 * Insert key
	 */
	int	INSERT			= 1008;
	/**
	 * Delete key
	 */
	int	DELETE			= 1009;
	/**
	 * Break key
	 */
	int	BREAK			= 1009;
	/**
	 * Function key 1
	 */
	int	F1				= 1101;
	/**
	 * Function key 2
	 */
	int	F2				= 1102;
	/**
	 * Function key 3
	 */
	int	F3				= 1103;
	/**
	 * Function key 4
	 */
	int	F4				= 1104;
	/**
	 * Function key 5
	 */
	int	F5				= 1105;
	/**
	 * Function key 6
	 */
	int	F6				= 1106;
	/**
	 * Function key 7
	 */
	int	F7				= 1107;
	/**
	 * Function key 8
	 */
	int	F8				= 1108;
	/**
	 * Function key 9
	 */
	int	F9				= 1109;
	/**
	 * Function key 10
	 */
	int	F10				= 1110;
	/**
	 * Function key 11
	 */
	int	F11				= 1111;
	/**
	 * Function key 12
	 */
	int	F12				= 1112;

	/**
	 * An inner class to provide an enum for the attributes
	 */
	public static class Attribute {
		private String	name;

		private Attribute(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

		/**
		 * Blink the text.
		 */
		public Attribute	BLINK			= new Attribute("BLINK");
		/**
		 * Underline the text.
		 */
		public Attribute	UNDERLINE		= new Attribute("UNDERLINE");
		/**
		 * Strike Through the text.
		 */
		public Attribute	STRIKE_THROUGH	= new Attribute("STRIKE THROUGH");
		/**
		 * Bolden the text.
		 */
		public Attribute	BOLD			= new Attribute("BOLD");
		/**
		 * Dim the text.
		 */
		public Attribute	DIM				= new Attribute("DIM");
		/**
		 * Reverse the text.
		 */
		public Attribute	REVERSED		= new Attribute("REVERSED");
	}

	/**
	 * An inner class to be used as enum for the basic colors.
	 * 
	 */
	public static class Color {
		private String	name;

		private Color(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

		/**
		 * No Color, transparent.
		 */
		public Color	NONE	= new Color("NONE");
		/**
		 * Black
		 */
		public Color	BLACK	= new Color("BLACK");
		/**
		 * Green
		 */
		public Color	GREEN	= new Color("GREEN");
		/**
		 * Yellow
		 */
		public Color	YELLOW	= new Color("YELLOW");

		/**
		 * Magenta
		 */
		public Color	MAGENTA	= new Color("MAGENTA");

		/**
		 * Cyan
		 */
		public Color	CYAN	= new Color("CYAN");
		/**
		 * Blue
		 */
		public Color	BLUE	= new Color("BLUE");
		/**
		 * Red
		 */
		public Color	RED		= new Color("RED");
		/**
		 * White
		 */
		public Color	WHITE	= new Color("WHITE");
	}

	/**
	 * Return the associated Input Stream that represents the keyboard. Note
	 * that this InputStream can return values > 256, these characters are
	 * defined in this interface as special keys.
	 * 
	 * @return the current Input Stream.
	 */
	InputStream getInputStream();

	/**
	 * Return the associated standard output stream.
	 * 
	 * @return the associated standard output stream
	 */
	OutputStream getOutputStream();

	/**
	 * Return the associated standard error stream.
	 * 
	 * @return the associated standard error stream
	 */
	OutputStream getErrorStream();

	/**
	 * Clear the complete screen and position the cursor at 0,0.
	 * 
	 * @throws Exception when the method fails
	 */
	void clear() throws Exception;

	/**
	 * Leave the cursor where it is but clear the remainder of the line.
	 * 
	 * @throws Exception when the method fails
	 */
	void eraseEndOfLine() throws Exception;

	/**
	 * Move the cursor up one line, this must not cause a scroll if the cursor
	 * moves off the screen.
	 * 
	 * @throws Exception when the method fails
	 */
	void up() throws Exception;

	/**
	 * Move the cursor down one line, this must not cause a scroll if the
	 * cursors moves off the screen.
	 * 
	 * @throws Exception when the method fails
	 */
	void down() throws Exception;

	/**
	 * Move the cursor backward. Must not wrap to previous line.
	 * 
	 * @throws Exception when the method fails
	 */
	void backward() throws Exception;

	/**
	 * Move the cursor forward. Must not wrap to next line if the cursor becomes
	 * higher than the width.
	 * 
	 * @throws Exception when the method fails
	 */
	void forward() throws Exception;

	/**
	 * Return the actual width of the screen. Some screens can change their size
	 * and this method must return the actual width if possible. If the width
	 * cannot be established a -1 must be returned.
	 * 
	 * @return the width of the screen or -1.
	 * 
	 * @throws Exception when the method fails
	 */
	int width() throws Exception;

	/**
	 * Return the actual height of the screen. Some screens can change their
	 * size and this method must return the actual height if possible. If the width
	 * cannot be established a -1 must be returned.
	 * 
	 * @return the height of the screen or -1.
	 * 
	 * @throws Exception when the method fails
	 */
	int height() throws Exception;

	/**
	 * Return the current cursor position.
	 * 
	 * The position is returned as an array of 2 elements. The first element is
	 * the x position and the second elements is the y position. Both are zero
	 * based.
	 * 
	 * @return the current position or {@code null} if it is not possible to
	 *         establish the cursor position.
	 * 
	 * @throws Exception when the method fails
	 */
	int[] getPosition() throws Exception;

	/**
	 * Position the cursor on the screen. Positioning starts at 0,0 and the
	 * maximum value is given by {@link #width()}, {@link #height()}. The visible
	 * cursor is moved to this position and text insertion will continue from
	 * that position.
	 * 
	 * @param x The x position, must be from 0-width
	 * @param y The y position, must be from 0-height
	 * @return {@code true} if the position could be set, otherwise {@code
	 *         false}
	 * @throws IllegalArgumentException when x or y is not in range
	 * @throws Exception when this method fails
	 */
	boolean position(int x, int y) throws IllegalArgumentException, Exception;

	/**
	 * Set the attributes of the text to outputed. This method must reset all
	 * current attributes. That is, attributes are not inherited from the
	 * current position.
	 * 
	 * @param foreground The foreground color
	 * @param background The background color (around the character)
	 * @param attr A number of attributes.
	 * @return {@code true} if the attributes could be set, otherwise {@code
	 *         false}
	 * @throws Exception when this method fails
	 */
	boolean attributes(Color foreground, Color background, Attribute... attr)
			throws Exception;

}
