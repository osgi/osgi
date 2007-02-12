<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon" version="1.1">
	<xsl:output method="xml" indent="yes"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<title>Changes from <xsl:value-of select="differences/old"/> to <xsl:value-of select="differences/new"/></title>
			</head>
			<body>
				<h1 class="Heading2">Changes from <xsl:value-of select="differences/old"/> to <xsl:value-of select="differences/new"/></h1>
				<h2 class="Heading3">New Packages</h2>
				<xsl:for-each select="//new-packages/*">
					<xsl:sort select="."/>
					<p class="ee">
						<xsl:value-of select="."/>
					</p>
				</xsl:for-each>
				<h2 class="Heading3">New Classes and Interfaces</h2>
				<xsl:for-each select="//new-classes/* | //new-interfaces/*">
					<xsl:sort select="@name"/>
					<p class="ee">
						<xsl:value-of select="@name"/>
					</p>
				</xsl:for-each>
				<h2 class="Heading3">Modified Classes and Interfaces</h2> 
				<xsl:for-each select="//class">
					<xsl:sort select="name"/>
					<h2 class="ee-class"><xsl:value-of select="name"/>
						<xsl:if test="modified-declaration">
							(~)
						</xsl:if>
					</h2>
					<xsl:apply-templates select="new-fields/declaration" mode="new"/>
					<xsl:apply-templates select="new-constructors/declaration" mode="new"/>
					<xsl:apply-templates select="new-methods/declaration" mode="new"/>
					<xsl:apply-templates select="modified-fields/feature/modified-declaration/new-declaration" mode="modified"/>
					<xsl:apply-templates select="modified-constructors/feature/modified-declaration/new-declaration" mode="modified"/>
					<xsl:apply-templates select="modified-methods/feature/modified-declaration/new-declaration" mode="modified"/>
					<xsl:apply-templates select="deprecated-fields/declaration" mode="deprecated"/>
					<xsl:apply-templates select="deprecated-constructors/declaration" mode="deprecated"/>
					<xsl:apply-templates select="deprecated-methods/*" mode="deprecated"/>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
		
	<xsl:template match="declaration" mode="new">
		<xsl:call-template name="declaration">
			<xsl:with-param name="value" select="."/>
			<xsl:with-param name="prefix" select="'+'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="new-declaration" mode="modified">
		<xsl:call-template name="declaration">
			<xsl:with-param name="value" select="."/>
			<xsl:with-param name="prefix" select="'~'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="declaration" mode="deprecated">
		<xsl:call-template name="declaration">
			<xsl:with-param name="value" select="."/>
			<xsl:with-param name="prefix" select="'-'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="declaration">
		<xsl:param name="prefix" select="' '"/>
		<xsl:param name="value"/>
		
		<p class="ee">
			<xsl:value-of select="$prefix"/>
			<xsl:text> </xsl:text>		
			<xsl:if test="$value/@static='yes'">static </xsl:if>
			<xsl:choose>
				<xsl:when test="$value/@type">
					<xsl:value-of select="$value/@type"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="$value/@signature"/>
				</xsl:when>
				<xsl:when test="$value/@return-type">
					<xsl:value-of select="$value/@return-type"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="$value/@signature"/>
					<xsl:text> </xsl:text>
					<xsl:if test="$value/@throws!=''">
						throws <xsl:value-of select="$value/@throws"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$value/@signature"/>
					<xsl:if test="$value/@throws">
						<xsl:value-of select="$value/@throws"/>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</p>
	</xsl:template>
</xsl:stylesheet>