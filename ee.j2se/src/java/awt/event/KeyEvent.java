/*
 * $Revision$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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
 */

package java.awt.event;
public class KeyEvent extends java.awt.event.InputEvent {
	/** @deprecated */ public KeyEvent(java.awt.Component var0, int var1, long var2, int var3, int var4) { }
	public KeyEvent(java.awt.Component var0, int var1, long var2, int var3, int var4, char var5) { }
	public KeyEvent(java.awt.Component var0, int var1, long var2, int var3, int var4, char var5, int var6) { }
	public char getKeyChar() { return '\0'; }
	public int getKeyCode() { return 0; }
	public int getKeyLocation() { return 0; }
	public static java.lang.String getKeyModifiersText(int var0) { return null; }
	public static java.lang.String getKeyText(int var0) { return null; }
	public boolean isActionKey() { return false; }
	public void setKeyChar(char var0) { }
	public void setKeyCode(int var0) { }
	/** @deprecated */ public void setModifiers(int var0) { }
	public final static char CHAR_UNDEFINED = 65535;
	public final static int KEY_FIRST = 400;
	public final static int KEY_LAST = 402;
	public final static int KEY_LOCATION_LEFT = 2;
	public final static int KEY_LOCATION_NUMPAD = 4;
	public final static int KEY_LOCATION_RIGHT = 3;
	public final static int KEY_LOCATION_STANDARD = 1;
	public final static int KEY_LOCATION_UNKNOWN = 0;
	public final static int KEY_PRESSED = 401;
	public final static int KEY_RELEASED = 402;
	public final static int KEY_TYPED = 400;
	public final static int VK_0 = 48;
	public final static int VK_1 = 49;
	public final static int VK_2 = 50;
	public final static int VK_3 = 51;
	public final static int VK_4 = 52;
	public final static int VK_5 = 53;
	public final static int VK_6 = 54;
	public final static int VK_7 = 55;
	public final static int VK_8 = 56;
	public final static int VK_9 = 57;
	public final static int VK_A = 65;
	public final static int VK_ACCEPT = 30;
	public final static int VK_ADD = 107;
	public final static int VK_AGAIN = 65481;
	public final static int VK_ALL_CANDIDATES = 256;
	public final static int VK_ALPHANUMERIC = 240;
	public final static int VK_ALT = 18;
	public final static int VK_ALT_GRAPH = 65406;
	public final static int VK_AMPERSAND = 150;
	public final static int VK_ASTERISK = 151;
	public final static int VK_AT = 512;
	public final static int VK_B = 66;
	public final static int VK_BACK_QUOTE = 192;
	public final static int VK_BACK_SLASH = 92;
	public final static int VK_BACK_SPACE = 8;
	public final static int VK_BRACELEFT = 161;
	public final static int VK_BRACERIGHT = 162;
	public final static int VK_C = 67;
	public final static int VK_CANCEL = 3;
	public final static int VK_CAPS_LOCK = 20;
	public final static int VK_CIRCUMFLEX = 514;
	public final static int VK_CLEAR = 12;
	public final static int VK_CLOSE_BRACKET = 93;
	public final static int VK_CODE_INPUT = 258;
	public final static int VK_COLON = 513;
	public final static int VK_COMMA = 44;
	public final static int VK_COMPOSE = 65312;
	public final static int VK_CONTROL = 17;
	public final static int VK_CONVERT = 28;
	public final static int VK_COPY = 65485;
	public final static int VK_CUT = 65489;
	public final static int VK_D = 68;
	public final static int VK_DEAD_ABOVEDOT = 134;
	public final static int VK_DEAD_ABOVERING = 136;
	public final static int VK_DEAD_ACUTE = 129;
	public final static int VK_DEAD_BREVE = 133;
	public final static int VK_DEAD_CARON = 138;
	public final static int VK_DEAD_CEDILLA = 139;
	public final static int VK_DEAD_CIRCUMFLEX = 130;
	public final static int VK_DEAD_DIAERESIS = 135;
	public final static int VK_DEAD_DOUBLEACUTE = 137;
	public final static int VK_DEAD_GRAVE = 128;
	public final static int VK_DEAD_IOTA = 141;
	public final static int VK_DEAD_MACRON = 132;
	public final static int VK_DEAD_OGONEK = 140;
	public final static int VK_DEAD_SEMIVOICED_SOUND = 143;
	public final static int VK_DEAD_TILDE = 131;
	public final static int VK_DEAD_VOICED_SOUND = 142;
	public final static int VK_DECIMAL = 110;
	public final static int VK_DELETE = 127;
	public final static int VK_DIVIDE = 111;
	public final static int VK_DOLLAR = 515;
	public final static int VK_DOWN = 40;
	public final static int VK_E = 69;
	public final static int VK_END = 35;
	public final static int VK_ENTER = 10;
	public final static int VK_EQUALS = 61;
	public final static int VK_ESCAPE = 27;
	public final static int VK_EURO_SIGN = 516;
	public final static int VK_EXCLAMATION_MARK = 517;
	public final static int VK_F = 70;
	public final static int VK_F1 = 112;
	public final static int VK_F10 = 121;
	public final static int VK_F11 = 122;
	public final static int VK_F12 = 123;
	public final static int VK_F13 = 61440;
	public final static int VK_F14 = 61441;
	public final static int VK_F15 = 61442;
	public final static int VK_F16 = 61443;
	public final static int VK_F17 = 61444;
	public final static int VK_F18 = 61445;
	public final static int VK_F19 = 61446;
	public final static int VK_F2 = 113;
	public final static int VK_F20 = 61447;
	public final static int VK_F21 = 61448;
	public final static int VK_F22 = 61449;
	public final static int VK_F23 = 61450;
	public final static int VK_F24 = 61451;
	public final static int VK_F3 = 114;
	public final static int VK_F4 = 115;
	public final static int VK_F5 = 116;
	public final static int VK_F6 = 117;
	public final static int VK_F7 = 118;
	public final static int VK_F8 = 119;
	public final static int VK_F9 = 120;
	public final static int VK_FINAL = 24;
	public final static int VK_FIND = 65488;
	public final static int VK_FULL_WIDTH = 243;
	public final static int VK_G = 71;
	public final static int VK_GREATER = 160;
	public final static int VK_H = 72;
	public final static int VK_HALF_WIDTH = 244;
	public final static int VK_HELP = 156;
	public final static int VK_HIRAGANA = 242;
	public final static int VK_HOME = 36;
	public final static int VK_I = 73;
	public final static int VK_INPUT_METHOD_ON_OFF = 263;
	public final static int VK_INSERT = 155;
	public final static int VK_INVERTED_EXCLAMATION_MARK = 518;
	public final static int VK_J = 74;
	public final static int VK_JAPANESE_HIRAGANA = 260;
	public final static int VK_JAPANESE_KATAKANA = 259;
	public final static int VK_JAPANESE_ROMAN = 261;
	public final static int VK_K = 75;
	public final static int VK_KANA = 21;
	public final static int VK_KANA_LOCK = 262;
	public final static int VK_KANJI = 25;
	public final static int VK_KATAKANA = 241;
	public final static int VK_KP_DOWN = 225;
	public final static int VK_KP_LEFT = 226;
	public final static int VK_KP_RIGHT = 227;
	public final static int VK_KP_UP = 224;
	public final static int VK_L = 76;
	public final static int VK_LEFT = 37;
	public final static int VK_LEFT_PARENTHESIS = 519;
	public final static int VK_LESS = 153;
	public final static int VK_M = 77;
	public final static int VK_META = 157;
	public final static int VK_MINUS = 45;
	public final static int VK_MODECHANGE = 31;
	public final static int VK_MULTIPLY = 106;
	public final static int VK_N = 78;
	public final static int VK_NONCONVERT = 29;
	public final static int VK_NUMBER_SIGN = 520;
	public final static int VK_NUMPAD0 = 96;
	public final static int VK_NUMPAD1 = 97;
	public final static int VK_NUMPAD2 = 98;
	public final static int VK_NUMPAD3 = 99;
	public final static int VK_NUMPAD4 = 100;
	public final static int VK_NUMPAD5 = 101;
	public final static int VK_NUMPAD6 = 102;
	public final static int VK_NUMPAD7 = 103;
	public final static int VK_NUMPAD8 = 104;
	public final static int VK_NUMPAD9 = 105;
	public final static int VK_NUM_LOCK = 144;
	public final static int VK_O = 79;
	public final static int VK_OPEN_BRACKET = 91;
	public final static int VK_P = 80;
	public final static int VK_PAGE_DOWN = 34;
	public final static int VK_PAGE_UP = 33;
	public final static int VK_PASTE = 65487;
	public final static int VK_PAUSE = 19;
	public final static int VK_PERIOD = 46;
	public final static int VK_PLUS = 521;
	public final static int VK_PREVIOUS_CANDIDATE = 257;
	public final static int VK_PRINTSCREEN = 154;
	public final static int VK_PROPS = 65482;
	public final static int VK_Q = 81;
	public final static int VK_QUOTE = 222;
	public final static int VK_QUOTEDBL = 152;
	public final static int VK_R = 82;
	public final static int VK_RIGHT = 39;
	public final static int VK_RIGHT_PARENTHESIS = 522;
	public final static int VK_ROMAN_CHARACTERS = 245;
	public final static int VK_S = 83;
	public final static int VK_SCROLL_LOCK = 145;
	public final static int VK_SEMICOLON = 59;
	public final static int VK_SEPARATER = 108;
	public final static int VK_SEPARATOR = 108;
	public final static int VK_SHIFT = 16;
	public final static int VK_SLASH = 47;
	public final static int VK_SPACE = 32;
	public final static int VK_STOP = 65480;
	public final static int VK_SUBTRACT = 109;
	public final static int VK_T = 84;
	public final static int VK_TAB = 9;
	public final static int VK_U = 85;
	public final static int VK_UNDEFINED = 0;
	public final static int VK_UNDERSCORE = 523;
	public final static int VK_UNDO = 65483;
	public final static int VK_UP = 38;
	public final static int VK_V = 86;
	public final static int VK_W = 87;
	public final static int VK_X = 88;
	public final static int VK_Y = 89;
	public final static int VK_Z = 90;
}

