<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="html" indent="yes"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="Node/@name"/> Managed Object</title>
				<link rel="stylesheet" href="http://bundles.osgi.org/www/osgi.css" type="text/css"/>
			</head>
			<body>
				<h1>OSGi DMT Node Definitions</h1>
				<h2>
					<xsl:value-of select="Node/@name"/> Managed Object</h2>
				<p>
					<xsl:copy-of select="Node/Description"/></p>
				
				<h3>Overview Tree</h3>
				<xsl:apply-templates select="Node" mode="tree"/>
				<h3>Tables</h3> 
				<table>
					<tr>
						<th>Path</th>
						<th>Access</th>
						<th>Type</th>
						<th>-/+</th>
						<th>Scope</th>
						<th>Description</th>
					</tr>
					<xsl:apply-templates select="Node"/>
				</table>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="Node">
		<xsl:variable name="path">
			<xsl:for-each select="ancestor-or-self::node()/@name">
				<xsl:text>/</xsl:text>
				<xsl:value-of select="normalize-space(.)"/>
			</xsl:for-each>
		</xsl:variable>
		<tr>
			<td>
				<a name="{$path}"/>
				<code>
					<xsl:for-each select="ancestor::node()[parent::node()]">
						<xsl:text>-</xsl:text>
					</xsl:for-each>
					<xsl:value-of select="@name"/>
				</code>
			</td>
			<td>
				<xsl:value-of select="@access"/>
			</td>
			<td>
				<xsl:value-of select="@type"/>
			</td>
			<td>
				<xsl:value-of select="@min"/>
				<xsl:if test="@max">
					<xsl:text>..</xsl:text>
					<xsl:value-of select="@max"/>
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="@scope"/>
			</td>
			<td>
				<xsl:value-of select="Description"/>
				<xsl:if test="Name">
					<table style="width:100%; ">
						<tr>
							<th colspan="2">Names</th>
						</tr>
						<xsl:for-each select="Name">
							<tr>
								<td style="width:150px">
									<i>
										<xsl:value-of select="@pattern"/>
									</i>
								</td>
								<td>
									<xsl:value-of select="."/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
					<hr/>
				</xsl:if>
				<xsl:if test="Value">
					<table style="margin-top:0; padding:top:0; border:0px solid black; width:100%; ">
						<tr>
							<th colspan="2">Values</th>
						</tr>
						<xsl:for-each select="Value">
							<tr>
								<td style="width:150px">
									<i>
										<xsl:value-of select="@min"/>
										<xsl:value-of select="@value"/>
										<xsl:if test="@max"> ..
											<xsl:value-of select="@max"/> </xsl:if>
									</i>
								</td>
								<td>
									<xsl:value-of select="."/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
					<hr/>
				</xsl:if>
			</td>
		</tr>
		<xsl:apply-templates select="Node"/>
	</xsl:template>
	
	<xsl:template match="Node" mode="tree">
		<xsl:variable name="cnt" select="count(child::node())"/>
		<xsl:variable name="path">
			<xsl:for-each select="ancestor-or-self::node()/@name">
				<xsl:text>/</xsl:text>
				<xsl:value-of select="normalize-space(.)"/>
			</xsl:for-each>
		</xsl:variable>
		<table style="border:0px solid white;margin:0;padding:0;" border="0" cellpadding="0" cellspacing="0">
			<tr style="margin:0;padding:0;">
				<td colspan="{$cnt}" style="vertical-align:middle; width:150px">
					<div style="background-color:yellow;width:80%; text-align:left;font-height:smaller;">
						<a href="#{$path}">
							<xsl:value-of select="@name"/>
						</a>
					</div>
				</td>
				<xsl:if test="Node">
					<td style="vertical-align:middle;padding:4px; margin:4px;">
						
						<xsl:for-each select="Node">
							<xsl:apply-templates select="." mode="tree"/>
						</xsl:for-each>
					</td>
				</xsl:if>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>