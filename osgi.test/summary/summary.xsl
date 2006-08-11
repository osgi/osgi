<?xml  version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method='xml' encoding='UTF-8'/>

	<xsl:template match="tests">
		<summary>
			<xsl:attribute name="date">
				<xsl:value-of select="normalize-space(document('../../builddate')//date)"/> 
			</xsl:attribute>
			<xsl:apply-templates select="*"/>
		</summary>
	</xsl:template>
	
	<xsl:template match="test">
		<xsl:variable name="set" select="document(concat('../director/run-',@type,'.xml'))"/>
		<report id="{@type}">
			<xsl:attribute name="error">
				<xsl:value-of select="sum($set//@errors) + count($set//@exception)"/>	
			</xsl:attribute>
			<xsl:attribute name="absent">
				<xsl:value-of select="count($set//@absent)"/>	
			</xsl:attribute>
			<xsl:attribute name="exception">
				<xsl:value-of select="count($set//@exception)"/>	
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="@title"/>	
			</xsl:attribute>
			
			<failing>
				<xsl:for-each select="$set//testcase[@errors]">
					<testcase errors="{@errors}" name="{@name}"/>
					<redflag/>
				</xsl:for-each>
				<xsl:for-each select="$set//testcase[@exception]">
					<testcase errors="{@errors}" name="{@name} (exception)"/>
					<redflag/>
				</xsl:for-each>
			</failing>
		</report>
	</xsl:template>

</xsl:stylesheet>

