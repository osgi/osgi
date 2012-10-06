<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:diffmk="http://diffmk.sf.net/ns/diff"
		exclude-result-prefixes="diffmk"
		version="1.0">

<xsl:output method="xml" encoding="utf-8" indent="no"/>

<xsl:preserve-space elements="*"/>

<xsl:template match="diffmk:wrapper" priority="5000">
  <!-- handle block elements here -->
  <span>
    <xsl:call-template name="diffmark"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="diffmk:wrapper" priority="2000">
  <span>
    <xsl:call-template name="diffmark"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="*[@diffmk:changed]" priority="500">
  <xsl:copy>
    <xsl:copy-of select="@*[not(name(.) = 'diffmk:changed')]"/>
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
  <xsl:attribute name="class">
    <xsl:if test="@class">
      <xsl:value-of select="@class"/>
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="@diffmk:changed = 'added'">added</xsl:when>
      <xsl:when test="@diffmk:changed = 'changed'">changed</xsl:when>
      <xsl:when test="@diffmk:changed = 'deleted'">deleted</xsl:when>
      <xsl:otherwise>
	<xsl:message>
	  <xsl:text>Unexpected value for @diffmk:changed: </xsl:text>
	  <xsl:value-of select="@diffmk:changed"/>
	</xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
</xsl:template>

</xsl:stylesheet>
