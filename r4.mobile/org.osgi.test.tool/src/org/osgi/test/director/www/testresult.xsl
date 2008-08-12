<?xml  version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method='html' encoding='ISO-8859-1'/>
<xsl:template match="/">
<html>
	<head>
		<title>OSGi Test Result <xsl:value-of select="/testcase/@name"/></title>
		<style>
			A,OL,LI,BODY		{ background-color:#FFFFFF; font-family: Helvetica, Arial, sans-serif; font-size: 10pt; font-weight: plain; }
			H1  		{ font-size: 14pt; font-weight: bold; font-family: Helvetica, Arial, sans-serif;}
			H2  		{ font-size: 12pt; font-weight: bold; font-family: Helvetica, Arial, sans-serif;}
			H3  		{ font-size: 10pt; font-weight: bold; font-family: Helvetica, Arial, sans-serif;}
			H4  		{ font-size: 10pt; font-weight: bold; font-family: Helvetica, Arial, sans-serif;}
			TD  		{ vertical-align:top; text-align:left; font-family: Helvetica, Arial, sans-serif; font-size: 10pt; font-weight: plain;}
			TH  		{ background-color: #FFFFDD; color:blue; vertical-align:top; text-align:left; font-family: Helvetica, Arial, sans-serif; font-size: 10pt; font-weight: plain; }
			EM  		{ font-weight: bold }
			DIV.body	{ width:600;margin-left:50; }
			P.footer	{ font-size:9pt; font-family: Helvetica, Arial, sans-serif;}
			A.navigate  { font-weight: bold }
			P   		{ font-size:10pt; font-weight:plain; font-size:10pt;  font-family: Helvetica, Arial, sans-serif;}
			PRE 		{ font-family: Courier }
		</style>
	</head>
	<body>
		<h1>Summary</h1>
		<table width='600px'>
			<tr>
				<th width='200px'>Test case</th>
				<th width='300px'>Result</th>
			</tr>
			<tr>
				<td width='200px'>Total</td>
				<td>
					<xsl:choose>
						<xsl:when test="not(//testcase/@errors)"><font color="green">PASSED</font></xsl:when>
						<xsl:otherwise><font color="red">FAILED (<xsl:value-of select="sum(//testcase/@errors)"/>)</font></xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<xsl:for-each select="//testcase">
				<xsl:sort select="@name"/>
				<tr>
					<td width='200px'><a href='#{normalize-space(@name)}'><xsl:value-of select="@name"/></a></td>
					<td>
						<xsl:choose>
							<xsl:when test="@absent"><font color="green">ABSENT</font></xsl:when>
							<xsl:when test="not(@errors)"><font color="green">PASSED</font></xsl:when>
							<xsl:otherwise><font color="red">FAILED (<xsl:value-of select="@errors"/>)</font></xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<th align='left' colspan='2'>Information</th>
			</tr>
			<tr>
				<td width='200px'>Date</td>
				<td width='300px'>
					<xsl:value-of select="substring(run/@time,0,9)"/>
				</td>
			</tr>
			<tr>
				<td width='200px'>Applicant</td>
				<td width='300px'>
					<xsl:value-of select="//compliance/@applicant"/>
				</td>
			</tr>
			<tr>
				<td width='200px'>Program</td>
				<td width='300px'>
					<xsl:value-of select="//compliance/@program"/>
				</td>
			</tr>
			<tr>
				<td width='200px'>Campaign</td>
				<td width='300px'>
					<xsl:value-of select="//compliance/@campaign"/>
				</td>
			</tr>
			<tr>
				<td width='200px'>Target machine</td>
				<td width='300px'>
					<xsl:value-of select="run/@host"/>:<xsl:value-of select="run/@port"/>
				</td>
			</tr>
			<tr>
				<td width='200px'>Security enabled</td>
				<td width='300px'>
					<xsl:choose>
						<xsl:when test="//property[@key='java.security.manager']">
							<font color="green">Target Security Enabled</font>
						</xsl:when>
						<xsl:otherwise>
							<b><font color="red">TARGET SECURITY NOT ENABLED</font></b>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<tr>
				<td width='200px'>Framework vendor</td>
				<td width='300px'>
					<xsl:value-of select="//property[@key='org.osgi.framework.vendor']"/>
					<xsl:value-of select="//property[@key='org.osgi.framework.version']"/>
				</td>
			</tr>
			<tr>
				<td width='200px'>Operating System</td>
				<td width='300px'>
					<xsl:value-of select="//property[@key='org.osgi.framework.os.name']"/> 
					<xsl:value-of select="//property[@key='org.osgi.framework.os.version']"/>
				</td>
			</tr>
			<tr>
				<td width='200px'>Processor</td>
				<td width='300px'>
					<xsl:value-of select="//property[@key='org.osgi.framework.processor']"/> 
				</td>
			</tr>
			<tr>
				<td width='200px'>Java VM</td>
				<td width='300px'>
					<a href="{normalize-space(//property[@key='java.vendor.url'])}">
						<xsl:value-of select="//property[@key='java.vm.name']"/>
						<xsl:value-of select="//property[@key='java.vm.version']"/>
					</a>
				</td>
			</tr>
		</table>
		
		
		<xsl:apply-templates select="//testcase"/>
		
		<h2>Properties</h2>
		<table width='600px'>
			<tr>
				<th width='200px'>Property</th>
				<th width='300px'>Value</th>
			</tr>
			
			<xsl:apply-templates select="//property"/>
			
		</table>
	</body>
</html>
</xsl:template>


<!-- Test case report -->

<xsl:template match="testcase">
	<a name='{normalize-space(@name)}'/><h2>Test case: <xsl:value-of select="@name"/></h2>
	<table  width='600px'>
		<tr>
			<td valign='top' align='left' width='200px'>Status</td>
			<td valign='top' align='left' width='300px'>
				<xsl:choose>
					<xsl:when test="@absent"><font color="orange">ABSENT</font></xsl:when>
					<xsl:when test="not(@errors)"><font color="green">PASSED</font></xsl:when>
					<xsl:otherwise><font color="red">FAILED (<xsl:value-of select="@errors"/>)</font></xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
		<tr>
			<td valign='top' align='left' width='200px'>Started</td>
			<td valign='top' align='left'><xsl:value-of select="@starting"/></td>
		</tr>
		<tr>
			<td valign='top' align='left' width='200px'>Ended</td>
			<td valign='top' align='left'><xsl:value-of select="@ended"/></td>
		</tr>
		<tr>
			<td valign='top' align='left' width='200px'><i>Bundle Information</i></td>
		</tr>
		<tr>
			<td valign='top' align='left' width='200px'>Name</td>
			<td valign='top' align='left'><xsl:value-of select="testbundle/@name"/></td>
		</tr>
		<tr>
			<td valign='top' align='left' width='200px'>Location</td>
			<td valign='top' align='left'><xsl:value-of select="testbundle/@location"/></td>
		</tr>
		<tr>
			<td valign='top' align='left' width='200px'>Version</td>
			<td valign='top' align='left'><xsl:value-of select="testbundle/@version"/></td>
		</tr>
		<tr>
			<td valign='top' align='left' width='200px'>MD5 Fingerprint</td>
			<td valign='top' align='left'><xsl:value-of select="testbundle/@md5"/></td>
		</tr>
	</table>
	
	<xsl:if test="message">
		<h3>Messages</h3>
		<ol>
			<xsl:for-each select="message">
				<li><xsl:value-of select="."/></li>
			</xsl:for-each>
		</ol>		
	</xsl:if>
	
	<h3>Log</h3>
	<font size='-2'>
	<table width='600px'>
		<tr>
			<th valign='top' align='left' width='100px'>Time</th>
			<th valign='top' align='left' width='80px'>Source</th>
			<th valign='top' align='left' width='20px'>St</th>
			<th valign='top' align='left' width='400px'>Message</th>
		</tr>
		<xsl:for-each select="log">
			<tr>
				<td valign='top' align='left'><xsl:value-of select="substring(@time,9)"/></td>
				<td valign='top' align='left'><xsl:value-of select="@name"/></td>
				<xsl:choose>
					<xsl:when test="not(@match='true')">
						<td><font color="red">X</font></td>
						<td>
							<font color="red"><xsl:value-of select="normalize-space(raw-result)"/></font> [result]<br/>
							<xsl:value-of select="normalize-space(reference)"/> [reference]
						</td>
					</xsl:when>
					<xsl:otherwise>
						<td> </td>
						<td width='350px'>
							<xsl:value-of select="normalize-space(raw-result)"/>
						</td>
					</xsl:otherwise>
				</xsl:choose>
			</tr>
		</xsl:for-each>
	</table>
	</font>
</xsl:template>


<xsl:template match='property'>
	<tr>
		<td width='200px'><xsl:value-of select="@key"/></td>
		<td width='300px'><xsl:value-of select="."/></td>
	</tr>
</xsl:template>
</xsl:stylesheet>

