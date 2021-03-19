<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:exsl="http://exslt.org/common"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns:xlink='http://www.w3.org/1999/xlink'
  exclude-result-prefixes="exsl"
  version="1.0">

<xsl:import href="../../../licensed/docbook-xsl-ns/profiling/profile.xsl"/>

<xsl:param name="draft.mode">yes</xsl:param>

<xsl:param name="profile.status">
  <xsl:choose>
    <xsl:when test="$draft.mode = 'no'">final</xsl:when>
    <xsl:otherwise>draft</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<xsl:param name="copyright.year">
  <xsl:value-of select="/d:book/d:info/d:copyright/d:year"/>
</xsl:param>

<xsl:template match="/d:book/d:info/d:copyright" mode="profile">
  <xsl:copy>
    <xsl:element name="year" namespace="http://docbook.org/ns/docbook"><xsl:value-of select="$copyright.year"/></xsl:element>
    <xsl:copy-of select="d:holder"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
