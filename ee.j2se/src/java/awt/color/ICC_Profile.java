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

package java.awt.color;
public class ICC_Profile implements java.io.Serializable {
	protected void finalize() { }
	public int getColorSpaceType() { return 0; }
	public byte[] getData() { return null; }
	public byte[] getData(int var0) { return null; }
	public static java.awt.color.ICC_Profile getInstance(int var0) { return null; }
	public static java.awt.color.ICC_Profile getInstance(java.io.InputStream var0) throws java.io.IOException { return null; }
	public static java.awt.color.ICC_Profile getInstance(java.lang.String var0) throws java.io.IOException { return null; }
	public static java.awt.color.ICC_Profile getInstance(byte[] var0) { return null; }
	public int getMajorVersion() { return 0; }
	public int getMinorVersion() { return 0; }
	public int getNumComponents() { return 0; }
	public int getPCSType() { return 0; }
	public int getProfileClass() { return 0; }
	protected java.lang.Object readResolve() throws java.io.ObjectStreamException { return null; }
	public void setData(int var0, byte[] var1) { }
	public void write(java.io.OutputStream var0) throws java.io.IOException { }
	public void write(java.lang.String var0) throws java.io.IOException { }
	public final static int CLASS_ABSTRACT = 5;
	public final static int CLASS_COLORSPACECONVERSION = 4;
	public final static int CLASS_DEVICELINK = 3;
	public final static int CLASS_DISPLAY = 1;
	public final static int CLASS_INPUT = 0;
	public final static int CLASS_NAMEDCOLOR = 6;
	public final static int CLASS_OUTPUT = 2;
	public final static int icAbsoluteColorimetric = 3;
	public final static int icCurveCount = 8;
	public final static int icCurveData = 12;
	public final static int icHdrAttributes = 56;
	public final static int icHdrCmmId = 4;
	public final static int icHdrColorSpace = 16;
	public final static int icHdrCreator = 80;
	public final static int icHdrDate = 24;
	public final static int icHdrDeviceClass = 12;
	public final static int icHdrFlags = 44;
	public final static int icHdrIlluminant = 68;
	public final static int icHdrMagic = 36;
	public final static int icHdrManufacturer = 48;
	public final static int icHdrModel = 52;
	public final static int icHdrPcs = 20;
	public final static int icHdrPlatform = 40;
	public final static int icHdrRenderingIntent = 64;
	public final static int icHdrSize = 0;
	public final static int icHdrVersion = 8;
	public final static int icPerceptual = 0;
	public final static int icRelativeColorimetric = 1;
	public final static int icSaturation = 2;
	public final static int icSigAToB0Tag = 1093812784;
	public final static int icSigAToB1Tag = 1093812785;
	public final static int icSigAToB2Tag = 1093812786;
	public final static int icSigAbstractClass = 1633842036;
	public final static int icSigBToA0Tag = 1110589744;
	public final static int icSigBToA1Tag = 1110589745;
	public final static int icSigBToA2Tag = 1110589746;
	public final static int icSigBlueColorantTag = 1649957210;
	public final static int icSigBlueTRCTag = 1649693251;
	public final static int icSigCalibrationDateTimeTag = 1667329140;
	public final static int icSigCharTargetTag = 1952543335;
	public final static int icSigChromaticityTag = 1667789421;
	public final static int icSigCmyData = 1129142560;
	public final static int icSigCmykData = 1129142603;
	public final static int icSigColorSpaceClass = 1936744803;
	public final static int icSigCopyrightTag = 1668313716;
	public final static int icSigCrdInfoTag = 1668441193;
	public final static int icSigDeviceMfgDescTag = 1684893284;
	public final static int icSigDeviceModelDescTag = 1684890724;
	public final static int icSigDeviceSettingsTag = 1684371059;
	public final static int icSigDisplayClass = 1835955314;
	public final static int icSigGamutTag = 1734438260;
	public final static int icSigGrayData = 1196573017;
	public final static int icSigGrayTRCTag = 1800688195;
	public final static int icSigGreenColorantTag = 1733843290;
	public final static int icSigGreenTRCTag = 1733579331;
	public final static int icSigHead = 1751474532;
	public final static int icSigHlsData = 1212961568;
	public final static int icSigHsvData = 1213421088;
	public final static int icSigInputClass = 1935896178;
	public final static int icSigLabData = 1281450528;
	public final static int icSigLinkClass = 1818848875;
	public final static int icSigLuminanceTag = 1819635049;
	public final static int icSigLuvData = 1282766368;
	public final static int icSigMeasurementTag = 1835360627;
	public final static int icSigMediaBlackPointTag = 1651208308;
	public final static int icSigMediaWhitePointTag = 2004119668;
	public final static int icSigNamedColor2Tag = 1852009522;
	public final static int icSigNamedColorClass = 1852662636;
	public final static int icSigOutputClass = 1886549106;
	public final static int icSigOutputResponseTag = 1919251312;
	public final static int icSigPreview0Tag = 1886545200;
	public final static int icSigPreview1Tag = 1886545201;
	public final static int icSigPreview2Tag = 1886545202;
	public final static int icSigProfileDescriptionTag = 1684370275;
	public final static int icSigProfileSequenceDescTag = 1886610801;
	public final static int icSigPs2CRD0Tag = 1886610480;
	public final static int icSigPs2CRD1Tag = 1886610481;
	public final static int icSigPs2CRD2Tag = 1886610482;
	public final static int icSigPs2CRD3Tag = 1886610483;
	public final static int icSigPs2CSATag = 1886597747;
	public final static int icSigPs2RenderingIntentTag = 1886597737;
	public final static int icSigRedColorantTag = 1918392666;
	public final static int icSigRedTRCTag = 1918128707;
	public final static int icSigRgbData = 1380401696;
	public final static int icSigScreeningDescTag = 1935897188;
	public final static int icSigScreeningTag = 1935897198;
	public final static int icSigSpace2CLR = 843271250;
	public final static int icSigSpace3CLR = 860048466;
	public final static int icSigSpace4CLR = 876825682;
	public final static int icSigSpace5CLR = 893602898;
	public final static int icSigSpace6CLR = 910380114;
	public final static int icSigSpace7CLR = 927157330;
	public final static int icSigSpace8CLR = 943934546;
	public final static int icSigSpace9CLR = 960711762;
	public final static int icSigSpaceACLR = 1094929490;
	public final static int icSigSpaceBCLR = 1111706706;
	public final static int icSigSpaceCCLR = 1128483922;
	public final static int icSigSpaceDCLR = 1145261138;
	public final static int icSigSpaceECLR = 1162038354;
	public final static int icSigSpaceFCLR = 1178815570;
	public final static int icSigTechnologyTag = 1952801640;
	public final static int icSigUcrBgTag = 1650877472;
	public final static int icSigViewingCondDescTag = 1987405156;
	public final static int icSigViewingConditionsTag = 1986618743;
	public final static int icSigXYZData = 1482250784;
	public final static int icSigYCbCrData = 1497588338;
	public final static int icSigYxyData = 1501067552;
	public final static int icTagReserved = 4;
	public final static int icTagType = 0;
	public final static int icXYZNumberX = 8;
	ICC_Profile() { } /* generated constructor to prevent compiler adding default public constructor */
}

