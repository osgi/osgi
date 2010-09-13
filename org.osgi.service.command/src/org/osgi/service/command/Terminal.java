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
 * A screen is considered to be {@link #getHeight()} lines that each have
 * {@link #getWidth()} characters. For cursor positioning, the screen is assumed
 * to be starting at 0,0 and increases its position from left to right and from
 * top to bottom. Positioning outside the screen bounds is undefined.
 */
public interface Terminal {
	/**
	 * Start point of control characters.
	 */
	int	CONTROL_START	= 0x10000;

	/**
	 * Cursor up key
	 */
	int	CURSOR_UP		= CONTROL_START + 0;
	/**
	 * Cursor down key.
	 */
	int	CURSOR_DOWN		= CONTROL_START + 1;
	/**
	 * Cursors forward key. Usually right.
	 */
	int	CURSOR_FORWARD	= CONTROL_START + 2;

	/**
	 * Cursors backward key. Usually left.
	 */
	int	CURSOR_BACKWARD	= CONTROL_START + 3;

	/**
	 * Page up key
	 */
	int	PAGE_UP			= CONTROL_START + 4;
	/**
	 * Page down key
	 */
	int	PAGE_DOWN		= CONTROL_START + 5;
	/**
	 * Home key
	 */
	int	HOME			= CONTROL_START + 6;
	/**
	 * End key
	 */
	int	END				= CONTROL_START + 7;
	/**
	 * Insert key
	 */
	int	INSERT			= CONTROL_START + 8;
	/**
	 * Delete key
	 */
	int	DELETE			= CONTROL_START + 9;
	/**
	 * Break key
	 */
	int	BREAK			= CONTROL_START + 10;

	/**
	 * The window size has changed.
	 */
	int	SIZE_CHANGE		= CONTROL_START + 11;

	/**
	 * Helper
	 */
	int	FUNCTION_START	= CONTROL_START + 0x100;
	/**
	 * Function key 1
	 */
	int	F1				= FUNCTION_START + 1;
	/**
	 * Function key 2
	 */
	int	F2				= FUNCTION_START + 2;
	/**
	 * Function key 3
	 */
	int	F3				= FUNCTION_START + 3;
	/**
	 * Function key 4
	 */
	int	F4				= FUNCTION_START + 4;
	/**
	 * Function key 5
	 */
	int	F5				= FUNCTION_START + 5;
	/**
	 * Function key 6
	 */
	int	F6				= FUNCTION_START + 6;
	/**
	 * Function key 7
	 */
	int	F7				= FUNCTION_START + 7;
	/**
	 * Function key 8
	 */
	int	F8				= FUNCTION_START + 8;
	/**
	 * Function key 9
	 */
	int	F9				= FUNCTION_START + 9;
	/**
	 * Function key 10
	 */
	int	F10				= FUNCTION_START + 10;
	/**
	 * Function key 11
	 */
	int	F11				= FUNCTION_START + 11;
	/**
	 * Function key 12
	 */
	int	F12				= FUNCTION_START + 12;

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
		 * Underline the text.
		 */
		public Attribute	UNDERLINE		= new Attribute("UNDERLINE");
		/**
		 * Bolden the text.
		 */
		public Attribute	BOLD			= new Attribute("BOLD");
		/**
		 * Reverse the text.
		 */
		public Attribute	REVERSED		= new Attribute("REVERSED");
		
		/**
		 * Default foreground color.
		 */
		public Attribute	FORE_DEFAULT		= new Attribute("FORE_DEFAULT");
		
		/**
		 * No Color, transparent.
		 */
		public Attribute	FORE_NONE		= new Attribute("FORE_NONE");
		/**
		 * Black
		 */
		public Attribute	FORE_BLACK		= new Attribute("FORE_BLACK");
		/**
		 * Green
		 */
		public Attribute	FORE_GREEN		= new Attribute("FORE_GREEN");
		/**
		 * Yellow
		 */
		public Attribute	FORE_YELLOW		= new Attribute("FORE_YELLOW");

		/**
		 * Magenta
		 */
		public Attribute	FORE_MAGENTA	= new Attribute("FORE_MAGENTA");

		/**
		 * Cyan
		 */
		public Attribute	FORE_CYAN		= new Attribute("FORE_CYAN");
		/**
		 * Blue
		 */
		public Attribute	FORE_BLUE		= new Attribute("FORE_BLUE");
		/**
		 * Red
		 */
		public Attribute	FORE_RED		= new Attribute("FORE_RED");
		/**
		 * White
		 */
		public Attribute	FORE_WHITE		= new Attribute("FORE_WHITE");
		/**
		 * Default background color.
		 */
		public Attribute	BACK_DEFAULT		= new Attribute("BACK_DEFAULT");
		/**
		 * No Color, transparent.
		 */
		public Attribute	BACK_NONE		= new Attribute("BACK_NONE");
		/**
		 * Black
		 */
		public Attribute	BACK_BLACK		= new Attribute("BACK_BLACK");
		/**
		 * Green
		 */
		public Attribute	BACK_GREEN		= new Attribute("BACK_GREEN");
		/**
		 * Yellow
		 */
		public Attribute	BACK_YELLOW		= new Attribute("BACK_YELLOW");

		/**
		 * Magenta
		 */
		public Attribute	BACK_MAGENTA	= new Attribute("BACK_MAGENTA");

		/**
		 * Cyan
		 */
		public Attribute	BACK_CYAN		= new Attribute("BACK_CYAN");
		/**
		 * Blue
		 */
		public Attribute	BACK_BLUE		= new Attribute("BACK_BLUE");
		/**
		 * Red
		 */
		public Attribute	BACK_RED		= new Attribute("BACK_RED");
		/**
		 * White
		 */
		public Attribute	BACK_WHITE		= new Attribute("BACK_WHITE");
	}

	/**
	 * Get a character from the input. Characters less than 0x10000 are Unicode
	 * characters, if more it is a control code defined by the constants in this
	 * class.
	 * 
	 * @return the current Input Stream.
	 * @throws Exception When character cannot be read
	 */
	int getIn() throws Exception;

	/**
	 * Read a complete line from the input. The result will not contain any
	 * command codes, just text. Implementers can allow line editing and history
	 * handling. The string must not contain the LF or CR at the end.
	 * 
	 * @return a new line
	 * @throws Exception
	 */
	String readLine() throws Exception;

	/**
	 * Return the associated standard output stream.
	 * 
	 * @return the associated standard output stream
	 */
	PrintWriter getOut();

	/**
	 * Return the associated standard error stream.
	 * 
	 * @return the associated standard error stream
	 */
	PrintWriter getErr();

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
	 * cannot be established a -1 must be returned. If the size changes and the
	 * terminal supports reporting these events a {@link #SIZE_CHANGE} key must
	 * be returned.
	 * 
	 * @return the width of the screen or -1.
	 * 
	 * @throws Exception when the method fails
	 */
	int getWidth() throws Exception;

	/**
	 * Return the actual height of the screen. Some screens can change their
	 * size and this method must return the actual height if possible. If the
	 * width cannot be established a -1 must be returned. If the size changes
	 * and the terminal supports reporting these events a {@link #SIZE_CHANGE}
	 * key must be returned.
	 * 
	 * @return the height of the screen or -1.
	 * 
	 * @throws Exception when the method fails
	 */
	int getHeight() throws Exception;

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
	 * maximum value is given by {@link #getWidth()}, {@link #getHeight()}. The
	 * visible cursor is moved to this position and text insertion will continue
	 * from that position.
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
	 * Position the cursor x position on the screen. Is the same as position(x,y),
	 * where y represents the current line on the screen.
	 * 
	 * @param x The x position, must be from 0-width
	 * @return {@code true} if the position could be set, otherwise {@code
	 *         false}
	 * @throws IllegalArgumentException when x or y is not in range
	 * @throws Exception when this method fails
	 */
	boolean position(int x) throws IllegalArgumentException, Exception;

	/**
	 * Set the attributes of the text to outputed next. The method returns the
	 * current settings, which can be used to restore the display to the
	 * previous state. These current settings are stream based and not
	 * associated with the position of the cursor.
	 * 
	 * Attributes must be completely specified, they do not inherit from the
	 * current display. If attributes are specified multiple times, the last one
	 * wins.
	 * 
	 * @param attr A number of attributes describing the output
	 * @return The previous state of attributes or null if attributes are not
	 *         supported.
	 * @throws Exception when this method fails
	 */
	Attribute[] attributes(Attribute... attr) throws Exception;

	/**
	 * Value for {@link #getCapabilities()}, if set this Terminal can report the
	 * cursor position. See {@link #position(int, int)}
	 */
	long	REPORTS_CURSOR_POS				= 0x00000000001;
	/**
	 * Value for {@link #getCapabilities()}, if set this Terminal sends a
	 * control code when the terminal changes size. See {@link #SIZE_CHANGE}.
	 */
	long	REPORTS_SIZE_CHANGES			= 0x00000000002;

	/**
	 * Value for {@link #getCapabilities()}, if set this Terminal can report the
	 * current size, see {@link #getHeight()} and {@link #getWidth()}.
	 */
	long	REPORTS_SIZE					= 0x00000000004;

	/**
	 * Value for {@link #getCapabilities()}, if set this Terminal will handle
	 * bidirectional scripts. If supported, text and cursor movements must be
	 * automatically reordered to match the visualization. This will allow users
	 * to just send Unicode strings where text is in increasing memory order.
	 * Any reordering is only done on the display.
	 */
	long	SUPPORTS_BIDIRECTIONAL_SCRIPTS	= 0x00000000100;

	/**
	 * Value for {@link #getCapabilities()}, if set this Terminal supports
	 * attributes. If not set, {@link #attributes(Attribute...)} will always
	 * return {@code null}.
	 */
	long	SUPPORTS_ATTRIBUTES				= 0x00000000400;

	/**
	 * Value for {@link #getCapabilities()}, if set this Terminal supports
	 * setting the cursor position. If not set, {@link #position(int, int)} will
	 * always return false and not set the cursor.
	 */
	long	SUPPORTS_CURSOR_POS				= 0x00000004000;
	/**
	 * Value for {@link #getCapabilities()}, if set this Terminal supports the
	 * LATIN_1 supplement. These are the UNICODE 80-FF codes. A Terminal must
	 * minimally support US-ASCII.
	 */
	long	SUPPORTS_LATIN_1_SUPPLEMENT		= 0x00000010000;

	/**
	 * Value for {@link #getCapabilities()}, if set this Terminal supports the
	 * full UNICODE set. This does not imply Bidirectional script handling. A
	 * Terminal must minimally support US-ASCII.
	 */
	long	SUPPORTS_UNICODE				= 0x00000020000;

	/**
	 * Answer the capabilities of this terminal. The following capabilities can
	 * be returned:
	 * 
	 * 
	 * @return the bitmap of capabilities
	 */
	long getCapabilities();

}
