<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:d="http://docbook.org/ns/docbook"
  version="1.0">

<xsl:output method="text" encoding="UTF-8" omit-xml-declaration="yes" indent="no" />

<xsl:template match="/">
	<xsl:for-each select="//d:imagedata">
	    <xsl:variable name="imagePath">
	        <xsl:call-template name="relative-uri">
	            <xsl:with-param name="filename" select="."/>
	        </xsl:call-template>
	    </xsl:variable>
	    <xsl:value-of select="concat($imagePath, @fileref)" />
	    <xsl:text>&#xa;</xsl:text>
	</xsl:for-each>
</xsl:template>

<xsl:template name="relative-uri">
  <xsl:param name="filename" select="."/>
  <xsl:param name="destdir" select="''"/>

  <xsl:variable name="srcurl">
    <xsl:call-template name="strippath">
      <xsl:with-param name="filename">
        <xsl:call-template name="xml.base.dirs">
          <xsl:with-param name="base.elem"
                          select="$filename/ancestor-or-self::*
                                   [@xml:base != ''][1]"/>
        </xsl:call-template>
        <xsl:value-of select="$filename"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="srcurl.trimmed">
    <xsl:call-template name="trim.common.uri.paths">
      <xsl:with-param name="uriA" select="$srcurl"/>
      <xsl:with-param name="uriB" select="$destdir"/>
      <xsl:with-param name="return" select="'A'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="destdir.trimmed">
    <xsl:call-template name="trim.common.uri.paths">
      <xsl:with-param name="uriA" select="$srcurl"/>
      <xsl:with-param name="uriB" select="$destdir"/>
      <xsl:with-param name="return" select="'B'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="depth">
    <xsl:call-template name="count.uri.path.depth">
      <xsl:with-param name="filename" select="$destdir.trimmed"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:call-template name="copy-string">
    <xsl:with-param name="string" select="'../'"/>
    <xsl:with-param name="count" select="$depth"/>
  </xsl:call-template>
  <xsl:value-of select="$srcurl.trimmed"/>
</xsl:template>

<xsl:template name="strippath">
  <xsl:param name="filename" select="''"/>
  <xsl:choose>
    <!-- Leading .. are not eliminated -->
    <xsl:when test="starts-with($filename, '../')">
      <xsl:value-of select="'../'"/>
      <xsl:call-template name="strippath">
        <xsl:with-param name="filename" select="substring-after($filename, '../')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="contains($filename, '/../')">
      <xsl:call-template name="strippath">
        <xsl:with-param name="filename">
          <xsl:call-template name="getdir">
            <xsl:with-param name="filename" select="substring-before($filename, '/../')"/>
          </xsl:call-template>
          <xsl:value-of select="substring-after($filename, '/../')"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$filename"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="xml.base.dirs">
  <xsl:param name="base.elem" select="NONODE"/>

  <!-- Recursively resolve xml:base attributes, up to a
       full path with : in uri -->
  <xsl:if test="$base.elem/ancestor::*[@xml:base != ''] and
                not(contains($base.elem/@xml:base, ':'))">
    <xsl:call-template name="xml.base.dirs">
      <xsl:with-param name="base.elem"
                      select="$base.elem/ancestor::*[@xml:base != ''][1]"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:call-template name="getdir">
    <xsl:with-param name="filename" select="$base.elem/@xml:base"/>
  </xsl:call-template>

</xsl:template>

<xsl:template name="trim.common.uri.paths">
  <xsl:param name="uriA" select="''"/>
  <xsl:param name="uriB" select="''"/>
  <xsl:param name="return" select="'A'"/>

  <!-- Resolve any ../ in the path -->
  <xsl:variable name="trimmed.uriA">
    <xsl:call-template name="resolve.path">
      <xsl:with-param name="filename" select="$uriA"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="trimmed.uriB">
    <xsl:call-template name="resolve.path">
      <xsl:with-param name="filename" select="$uriB"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="contains($trimmed.uriA, '/') and contains($trimmed.uriB, '/')                     and substring-before($trimmed.uriA, '/') = substring-before($trimmed.uriB, '/')">
      <xsl:call-template name="trim.common.uri.paths">
        <xsl:with-param name="uriA" select="substring-after($trimmed.uriA, '/')"/>
        <xsl:with-param name="uriB" select="substring-after($trimmed.uriB, '/')"/>
        <xsl:with-param name="return" select="$return"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="$return = 'A'">
          <xsl:value-of select="$trimmed.uriA"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$trimmed.uriB"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="resolve.path">
  <xsl:param name="filename" select="''"/>
  <xsl:choose>
    <!-- Leading .. are not eliminated -->
    <xsl:when test="starts-with($filename, '../')">
      <xsl:value-of select="'../'"/>
      <xsl:call-template name="resolve.path">
        <xsl:with-param name="filename" select="substring-after($filename, '../')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="contains($filename, '/../')">
      <xsl:call-template name="resolve.path">
        <xsl:with-param name="filename">
          <xsl:call-template name="dirname">
            <xsl:with-param name="filename" select="substring-before($filename, '/../')"/>
          </xsl:call-template>
          <xsl:value-of select="substring-after($filename, '/../')"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$filename"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="dirname">
  <xsl:param name="filename" select="''"/>
  <xsl:if test="contains($filename, '/')">
    <xsl:value-of select="substring-before($filename, '/')"/>
    <xsl:text>/</xsl:text>
    <xsl:call-template name="dirname">
      <xsl:with-param name="filename" select="substring-after($filename, '/')"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<xsl:template name="count.uri.path.depth">
  <xsl:param name="filename" select="''"/>
  <xsl:param name="count" select="0"/>

  <xsl:choose>
    <xsl:when test="contains($filename, '/')">
      <xsl:call-template name="count.uri.path.depth">
        <xsl:with-param name="filename" select="substring-after($filename, '/')"/>
        <xsl:with-param name="count" select="$count + 1"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$count"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="copy-string">
  <!-- returns 'count' copies of 'string' -->
  <xsl:param name="string"/>
  <xsl:param name="count" select="0"/>
  <xsl:param name="result"/>

  <xsl:choose>
    <xsl:when test="$count&gt;0">
      <xsl:call-template name="copy-string">
        <xsl:with-param name="string" select="$string"/>
        <xsl:with-param name="count" select="$count - 1"/>
        <xsl:with-param name="result">
          <xsl:value-of select="$result"/>
          <xsl:value-of select="$string"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$result"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="getdir">
  <xsl:param name="filename" select="''"/>
  <xsl:if test="contains($filename, '/')">
    <xsl:value-of select="substring-before($filename, '/')"/>
    <xsl:text>/</xsl:text>
    <xsl:call-template name="getdir">
      <xsl:with-param name="filename" select="substring-after($filename, '/')"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>