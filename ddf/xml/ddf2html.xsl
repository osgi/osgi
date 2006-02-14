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
				<h1>
					<xsl:value-of select="Node/@name"/> Managed Object</h1>
				<p>
					<xsl:copy-of select="Node/Description"/></p>
				
				<h2>Overview Tree</h2>
				<xsl:apply-templates select="Node" mode="tree"/>
				<h2>Tables</h2>
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
		<tr>
			<td>
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
		<table>
			<tr>
				<td colspan="{$cnt}" style="vertical-align:middle; width:200px;margin-top:30px;">
					<div style="background-color:yellow;width:100%">
						<xsl:value-of select="@name"/>
					</div>
				</td>
				<xsl:if test="Node">
					<td style="width:1px;background-color:black;height:60%;padding:0"/>
					<td style="vertical-align:bottom;">
						
						<xsl:for-each select="Node">
							<xsl:apply-templates select="." mode="tree"/>
						</xsl:for-each>
					</td>
				</xsl:if>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>