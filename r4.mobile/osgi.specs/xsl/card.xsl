<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon" version="1.1">
	<xsl:strip-space elements="*"/>
	<xsl:output method="xml"/>
	
	<xsl:template match="package">
		<h2 class="ee-package"><xsl:value-of select="@name"/></h2>
		<xsl:apply-templates select="class">
			<xsl:sort select="@name"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="class">
		<h2 class="ee-class">
			<xsl:call-template name="modifiers">
				<xsl:with-param name="mods" select="@modifiers"/>
			</xsl:call-template>
			<xsl:value-of select="@name"/>
			<xsl:if test="@superclass and @superclass!='Object'">
				extends 
				<xsl:value-of select="@superclass"/>
			</xsl:if>
			<xsl:for-each select="implements[@local]">
				<xsl:choose>
					<xsl:when test="position()=1">
						implements
					</xsl:when>
					<xsl:otherwise>
						,
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="@name"/>
			</xsl:for-each>
		</h2>
		<xsl:if test="field">
			<xsl:apply-templates select="field">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
		</xsl:if>
		<xsl:if test="method[@isConstructor]">
			<xsl:apply-templates select="method[@isConstructor]"/>
		</xsl:if>
		<xsl:if test="method[not(@isConstructor)]">
			<xsl:apply-templates select="method[not(@isConstructor)]">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="field">
		<xsl:variable name="mods">
			<xsl:call-template name="modifiers">
				<xsl:with-param name="mods" select="@modifiers"/>
			</xsl:call-template>
		</xsl:variable>
		
		<h3 class="ee"><xsl:value-of select="concat($mods,' ',@typeName,@dimension,' ', @name)"/>
		</h3>
	</xsl:template>		
	
	<xsl:template match="method">
		<xsl:variable name="mods">
			<xsl:call-template name="modifiers">
				<xsl:with-param name="mods" select="@modifiers"/>
			</xsl:call-template>
		</xsl:variable>
		<h3 class="ee">
			<xsl:value-of select="concat($mods,' ',@typeName,@dimension,' ', @name)"/>(<xsl:apply-templates select="parameter" mode="head"/>)
			<xsl:for-each select="exception">
				<xsl:if test="position()=1"> throws </xsl:if>
				<xsl:if test="position()!=1">, </xsl:if>
				<xsl:value-of select="@name"/>
			</xsl:for-each>
		</h3>
	</xsl:template>		
	
	<xsl:template match="parameter" mode="head">
		<xsl:if test="position()!=1">,</xsl:if>
		<xsl:value-of select="@typeName"/>
		<xsl:value-of select="@dimension"/>
		<!-- <xsl:text> </xsl:text>
		<xsl:value-of select="@name"/> -->
	</xsl:template>
	
	<xsl:template name="modifiers">
		<xsl:param name="mods" select="NOTHING"/>
		<xsl:variable name="clean-public">
			<xsl:call-template name="remove">
				<xsl:with-param name="kill" select="'public'"/>
				<xsl:with-param name="string" select="$mods"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="remove">
			<xsl:with-param name="kill" select="'synchronized'"/>
			<xsl:with-param name="string" select="$clean-public"/>
		</xsl:call-template>
		<xsl:text> </xsl:text>
	</xsl:template>	
	
	<xsl:template name="remove">
		<xsl:param name="kill" select="---NOTHING"/>
		<xsl:param name="string"/>
		<xsl:choose>
			<xsl:when test="contains($string,$kill)">
				<xsl:value-of select="concat(substring-before($string,$kill),substring-after($string,$kill))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="/">
		<html>
			<head>
				<title>Index Card</title>
			</head>
			<body>
				<xsl:apply-templates select="//package">
					<xsl:sort select="@name"/>
				</xsl:apply-templates>
			</body>
		</html>
	</xsl:template>	
</xsl:stylesheet>