<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="/">
		<xsl:apply-templates select="//record"/>
	</xsl:template>

	<xsl:template match="record">
		<xsl:variable name="name" select="normalize-space(h1|h2|h3|h4|h5)"/>
		<xsl:choose>
			<xsl:when test="$name">
				<Node name="{$name}" type="{if (Type) then normalize-space(Type) else 'node'}"
					cardinality="{if ( Cardinality ) then normalize-space(Cardinality) else '1' }"
					scope="{if ( Scope ) then normalize-space(Scope) else 'P'}">
					<Description>
						<xsl:value-of select="normalize-space(Description)"/>
					</Description>
					<xsl:if test="Comment|Response">
						<comment>
							<xsl:value-of select="Comment|Response"/>
						</comment>
					</xsl:if>
				</Node>
			</xsl:when>
			<xsl:when test="Value">
				<Value value="{normalize-space(Value)}">
					<xsl:value-of select="normalize-space(Description)"/>
				</Value>
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>