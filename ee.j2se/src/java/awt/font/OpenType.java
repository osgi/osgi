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

package java.awt.font;
public abstract interface OpenType {
	public abstract byte[] getFontTable(int var0);
	public abstract byte[] getFontTable(int var0, int var1, int var2);
	public abstract byte[] getFontTable(java.lang.String var0);
	public abstract byte[] getFontTable(java.lang.String var0, int var1, int var2);
	public abstract int getFontTableSize(int var0);
	public abstract int getFontTableSize(java.lang.String var0);
	public abstract int getVersion();
	public final static int TAG_ACNT = 1633906292;
	public final static int TAG_AVAR = 1635148146;
	public final static int TAG_BASE = 1111577413;
	public final static int TAG_BDAT = 1650745716;
	public final static int TAG_BLOC = 1651273571;
	public final static int TAG_BSLN = 1651731566;
	public final static int TAG_CFF = 1128678944;
	public final static int TAG_CMAP = 1668112752;
	public final static int TAG_CVAR = 1668702578;
	public final static int TAG_CVT = 1668707360;
	public final static int TAG_DSIG = 1146308935;
	public final static int TAG_EBDT = 1161970772;
	public final static int TAG_EBLC = 1161972803;
	public final static int TAG_EBSC = 1161974595;
	public final static int TAG_FDSC = 1717859171;
	public final static int TAG_FEAT = 1717920116;
	public final static int TAG_FMTX = 1718449272;
	public final static int TAG_FPGM = 1718642541;
	public final static int TAG_FVAR = 1719034226;
	public final static int TAG_GASP = 1734439792;
	public final static int TAG_GDEF = 1195656518;
	public final static int TAG_GLYF = 1735162214;
	public final static int TAG_GPOS = 1196445523;
	public final static int TAG_GSUB = 1196643650;
	public final static int TAG_GVAR = 1735811442;
	public final static int TAG_HDMX = 1751412088;
	public final static int TAG_HEAD = 1751474532;
	public final static int TAG_HHEA = 1751672161;
	public final static int TAG_HMTX = 1752003704;
	public final static int TAG_JSTF = 1246975046;
	public final static int TAG_JUST = 1786082164;
	public final static int TAG_KERN = 1801810542;
	public final static int TAG_LCAR = 1818452338;
	public final static int TAG_LOCA = 1819239265;
	public final static int TAG_LTSH = 1280594760;
	public final static int TAG_MAXP = 1835104368;
	public final static int TAG_MMFX = 1296909912;
	public final static int TAG_MMSD = 1296913220;
	public final static int TAG_MORT = 1836020340;
	public final static int TAG_NAME = 1851878757;
	public final static int TAG_OPBD = 1836020340;
	public final static int TAG_OS2 = 1330851634;
	public final static int TAG_PCLT = 1346587732;
	public final static int TAG_POST = 1886352244;
	public final static int TAG_PREP = 1886545264;
	public final static int TAG_PROP = 1886547824;
	public final static int TAG_TRAK = 1953653099;
	public final static int TAG_TYP1 = 1954115633;
	public final static int TAG_VDMX = 1447316824;
	public final static int TAG_VHEA = 1986553185;
	public final static int TAG_VMTX = 1986884728;
}

