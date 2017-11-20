<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:exsl="http://exslt.org/common"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:xlink='http://www.w3.org/1999/xlink'
  exclude-result-prefixes="exsl"
  version="1.0">

<xsl:import href="../../../licensed/docbook-xsl-ns/xhtml/chunkfast.xsl"/>
<xsl:import href="custom-html-common.xsl"/>
<xsl:include href="../../../licensed/docbook-xsl-ns/webhelp/xsl/titlepage.templates.xsl"/>
<xsl:output omit-xml-declaration="yes"/>

<!--==============================================================-->
<!--  Parameter settings                                          -->
<!--==============================================================-->
<xsl:param name="table.borders.with.css" select="1" />
<xsl:param name="table.cell.border.style">none</xsl:param>
<xsl:param name="table.frame.border.style">none</xsl:param>
<xsl:param name="generate.css.header" select="1" />
<xsl:param name="webhelp.include.search.tab" select="0" />
<xsl:param name="generate.section.toc.level" select="0"/>
<xsl:param name="chunk.section.depth" select="1" />
<xsl:param name="chunk.quietly" select="0" />

<xsl:param name="appendix.autolabel">0</xsl:param>
<xsl:param name="chapter.autolabel">1</xsl:param>
<xsl:param name="part.autolabel">0</xsl:param>
<xsl:param name="qandadiv.autolabel">0</xsl:param>
<xsl:param name="reference.autolabel">1</xsl:param>
<xsl:param name="section.autolabel">1</xsl:param>
<xsl:param name="toc.section.depth">1</xsl:param>
<xsl:param name="webhelp.autolabel">1</xsl:param>
<xsl:param name="bibliography.numbered">1</xsl:param>
<xsl:param name="root.filename">index</xsl:param>

<xsl:param name="autotoc.label.separator" select="'&#160;'" />

<xsl:param name="generate.toc">
appendix  toc,title
article/appendix  nop
article   nop
book      toc,title,figure,table,example,equation
chapter   nop
part      nop
preface   nop
qandadiv  nop
qandaset  nop
reference nop
sect1     nop
sect2     nop
sect3     nop
sect4     nop
sect5     nop
section   nop
set       nop
</xsl:param>

<xsl:param name="formal.title.placement">
figure before
table before
example before
</xsl:param>

<xsl:param name="local.l10n.xml" select="document('')"/>
<l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0">
  <l:l10n language="en">
    <l:context name="title">
      <l:template name="appendix" text="Appendix %n. %t"/>
      <l:template name="chapter" text="%n %t"/>
      <l:template name="example" text="Example %n %t"/>
      <l:template name="figure" text="Figure %n %t"/>
      <l:template name="part" text="%n %t"/>
      <l:template name="table" text="Table %n %t"/>
    </l:context>
    <l:context name="xref-number-and-title">
      <l:template name="figure" text="Figure %n"/>
      <l:template name="table" text="Table %n"/>
      <l:template name="chapter" text="%t"/>
      <l:template name="appendix" text="%t"/>
      <l:template name="section" text="%t"/>
    </l:context>
    <l:context name="title-numbered">
      <l:template name="appendix" text="%n %t"/>
      <l:template name="article/appendix" text="%n %t"/>
      <l:template name="bridgehead" text="%n %t"/>
      <l:template name="chapter" text="%n %t"/>
      <l:template name="part" text="%n %t"/>
      <l:template name="sect1" text="%n %t"/>
      <l:template name="sect2" text="%n %t"/>
      <l:template name="sect3" text="%n %t"/>
      <l:template name="sect4" text="%n %t"/>
      <l:template name="sect5" text="%n %t"/>
      <l:template name="section" text="%n %t"/>
      <l:template name="simplesect" text="%t"/>
      <l:template name="topic" text="%t"/>
    </l:context>
    <l:context name="iso690">
<!--       <l:template name="primary.sep" text=". "/>
 -->      <l:template name="title.sep" text=" "/>
    </l:context>
  </l:l10n>
</l:i18n>

<xsl:param name="book.status">
  <xsl:value-of select="/d:book/@status"/>
</xsl:param>

<xsl:template match="d:programlisting">
  <xsl:call-template name="anchor"/>

  <xsl:variable name="div.element">pre</xsl:variable>

  <xsl:element name="{$div.element}">
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:call-template name="id.attribute"/>
    <xsl:if test="@width != ''">
      <xsl:attribute name="width">
        <xsl:value-of select="@width"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:element name="code">
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template name="chunk" >
  <xsl:param name="node" select="."/>
  <!-- returns 1 if $node is a chunk -->

  <!-- ==================================================================== -->
  <!-- What's a chunk?

       The root element
       appendix
       book
       chapter
       preface
                                                                            -->
  <!-- ==================================================================== -->

<!--
  <xsl:message>
    <xsl:text>chunk: </xsl:text>
    <xsl:value-of select="name($node)"/>
    <xsl:text>(</xsl:text>
    <xsl:value-of select="$node/@id"/>
    <xsl:text>)</xsl:text>
    <xsl:text> csd: </xsl:text>
    <xsl:value-of select="$chunk.section.depth"/>
    <xsl:text> cfs: </xsl:text>
    <xsl:value-of select="$chunk.first.sections"/>
    <xsl:text> ps: </xsl:text>
    <xsl:value-of select="count($node/parent::d:section)"/>
    <xsl:text> prs: </xsl:text>
    <xsl:value-of select="count($node/preceding-sibling::d:section)"/>
  </xsl:message>
-->

  <xsl:choose>
    <xsl:when test="$node/parent::*/processing-instruction('dbhtml')[normalize-space(.) = 'stop-chunking']">0</xsl:when>
    <xsl:when test="not($node/parent::*)">1</xsl:when>
    <xsl:when test="local-name($node)='preface'">1</xsl:when>
    <xsl:when test="local-name($node)='chapter'">1</xsl:when>
    <xsl:when test="local-name($node)='appendix'">1</xsl:when>
    <xsl:when test="local-name($node)='book'">1</xsl:when>
    <xsl:otherwise>0</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="@fileref" >
  <xsl:variable name="filename">
    <xsl:choose>
      <xsl:when test="contains(., ':')">
        <!-- it has a uri scheme so it is an absolute uri -->
        <xsl:value-of select="."/>
      </xsl:when>
      <xsl:when test="$keep.relative.image.uris != 0">
        <!-- leave it alone -->
        <xsl:value-of select="."/>
      </xsl:when>
      <xsl:otherwise>
        <!-- its a relative uri -->
        <xsl:call-template name="relative-uri">
          <xsl:with-param name="destdir">
            <xsl:call-template name="dbhtml-dir">
              <xsl:with-param name="context" select=".."/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="chapter">
    <xsl:variable name="label">
      <xsl:value-of select="ancestor::d:chapter/@label"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="string(number($label)) != 'NaN'">
        <xsl:value-of select="format-number($label, '000')"/>
      </xsl:when>
      <xsl:otherwise>0</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="ext">
    <xsl:call-template name="filename-extension">
      <xsl:with-param name="filename" select="$filename"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$ext = 'svg'">
      <xsl:if test="$chapter">
        <xsl:value-of select="concat($chapter, '-')"/>
      </xsl:if>
      <xsl:value-of select="concat(substring($filename,1,string-length($filename)-3), 'png')"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$filename" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="d:section" mode="class.value">
  <xsl:param name="class" select="local-name(.)"/>
  <!-- permit customization of class value -->
  <!-- Use element name by default -->
  <xsl:choose>
    <xsl:when test="@role">
      <xsl:value-of select="concat($class, ' ', @role)"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$class"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="d:chapter|d:part|d:sect1|d:sect2|d:sect3|d:sect4|d:sect5|d:section" mode="label.markup">
  <xsl:variable name="number">
    <xsl:apply-imports/>
  </xsl:variable>

  <xsl:if test="string($number) != ''">
    <span class="number"><xsl:value-of select="$number"/></span>
  </xsl:if>
</xsl:template>

<xsl:template name="anchor">
  <xsl:param name="node" select="."/>
  <xsl:param name="conditional" select="1"/>

  <xsl:choose>
    <xsl:when test="$generate.id.attributes != 0">
      <!-- No named anchors output when this param is set -->
    </xsl:when>
    <xsl:when test="$conditional = 0 or $node/@id or $node/@xml:id">
      <a class="anchor">
        <xsl:attribute name="id">
          <xsl:call-template name="object.id">
            <xsl:with-param name="object" select="$node"/>
          </xsl:call-template>
        </xsl:attribute>
      </a>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template name="label.this.section">
  <xsl:param name="section" select="."/>

  <xsl:variable name="level">
    <xsl:call-template name="section.level"/>
  </xsl:variable>

  <xsl:choose>
    <!-- bridgeheads are not numbered -->
    <xsl:when test="$section/ancestor::*[local-name()='preface']">0</xsl:when>
    <xsl:when test="$section/self::d:bridgehead">0</xsl:when>
    <xsl:when test="$level &lt;= $section.autolabel.max.depth">
      <xsl:value-of select="$section.autolabel"/>
    </xsl:when>
    <xsl:otherwise>0</xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
