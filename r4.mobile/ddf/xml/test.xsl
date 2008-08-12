<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" indent="yes"/>

	
	<xsl:template match="/">
		<xsl:call-template name="record">
			<xsl:with-param name="record" select="//record[0]"/>
		</xsl:call-template>		
	</xsl:template>
	
	
	<xsl:template name="record">
		<xsl:param name="record"/>
		
	</xsl:template>
</xsl:stylesheet>