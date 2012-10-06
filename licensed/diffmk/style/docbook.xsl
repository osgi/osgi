<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:diffmk="http://diffmk.sf.net/ns/diff"
		xmlns="http://docbook.org/ns/docbook"
		exclude-result-prefixes="diffmk"
		version="1.0">

<xsl:output method="xml" encoding="utf-8" indent="no"/>

<xsl:preserve-space elements="*"/>

<xsl:template match="diffmk:wrapper" priority="5000">
  <!-- handle block elements here -->
  <phrase>
    <xsl:call-template name="diffmark"/>
    <xsl:apply-templates/>
  </phrase>
</xsl:template>

<xsl:template match="diffmk:wrapper" priority="2000">
  <phrase>
    <xsl:call-template name="diffmark"/>
    <xsl:apply-templates/>
  </phrase>
</xsl:template>

<xsl:template match="*[@diffmk:change]" priority="500">
  <xsl:copy>
    <xsl:copy-of select="@*[not(name(.) = 'diffmk:change')]"/>
    <xsl:call-template name="diffmark"/>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>

<xsl:template match="*">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>

<xsl:template match="comment()|processing-instruction()|text()">
  <xsl:copy/>
</xsl:template>

<!-- ============================================================ -->

<xsl:template name="diffmark">
  <xsl:attribute name="revisionflag">
    <xsl:choose>
      <xsl:when test="@diffmk:change = 'added'">added</xsl:when>
      <xsl:when test="@diffmk:change = 'changed'">changed</xsl:when>
      <xsl:when test="@diffmk:change = 'deleted'">deleted</xsl:when>
      <xsl:otherwise>
	<xsl:message>
	  <xsl:text>Unexpected value for @diffmk:change: </xsl:text>
	  <xsl:value-of select="@diffmk:change"/>
	</xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
</xsl:template>

</xsl:stylesheet>
