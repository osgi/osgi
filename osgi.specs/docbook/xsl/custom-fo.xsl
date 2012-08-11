<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY comment.block.parents "parent::d:answer|
parent::d:appendix|parent::d:article|parent::d:bibliodiv|
parent::d:bibliography|parent::d:blockquote|parent::d:caution|parent::d:chapter|
parent::d:glossary|parent::d:glossdiv|parent::d:important|parent::d:index|
parent::d:indexdiv|parent::d:listitem|parent::d:note|parent::d:orderedlist|
parent::d:partintro|parent::d:preface|parent::d:procedure|parent::d:qandadiv|
parent::d:qandaset|parent::d:question|parent::d:refentry|parent::d:refnamediv|
parent::d:refsect1|parent::d:refsect2|parent::d:refsect3|parent::d:refsection|
parent::d:refsynopsisdiv|parent::d:sect1|parent::d:sect2|parent::d:sect3|parent::d:sect4|
parent::d:sect5|parent::d:section|parent::d:setindex|parent::d:sidebar|
parent::d:simplesect|parent::d:taskprerequisites|parent::d:taskrelated|
parent::d:tasksummary|parent::d:warning|parent::d:topic">
]>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:exsl="http://exslt.org/common"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  exclude-result-prefixes="exsl"
  version="1.0">

<!-- $Id$ -->
<xsl:import href="../../../licensed/docbook-xsl-ns/fo/docbook.xsl"/>

<xsl:output indent="no"/>

<!--==============================================================-->
<!--  Parameter settings                                          -->
<!--==============================================================-->
<xsl:param name="double.sided" select="1"/>
<xsl:param name="body.start.indent">1in</xsl:param>
<xsl:param name="body.offset">0.090in</xsl:param>
<xsl:param name="alignment">start</xsl:param>
<!-- Include only top-level section in Table of Contents -->
<xsl:param name="toc.section.depth" select="1"/>
<xsl:param name="show.comments" select="1"/>
<xsl:param name="marker.section.level" select="1"/>
<xsl:param name="autotoc.label.separator"/>
<xsl:param name="generate.toc">
book toc,title
</xsl:param>
<xsl:param name="section.label.includes.component.label" select="1"/>


<xsl:param name="page.height.portrait">24.587cm</xsl:param>
<xsl:param name="page.width.portrait">18.898cm</xsl:param>
<xsl:param name="draft.mode">no</xsl:param>
<xsl:param name="fop1.extensions" select="1"/>
<xsl:param name="xep.extensions" select="0"/>
<xsl:param name="front.logo.image"/>

<xsl:param name="body.font.family">Proforma-Book</xsl:param>
<xsl:param name="title.font.family">ProductusSemibold</xsl:param>
<xsl:param name="monospace.font.family">ProductusOSGiMonoc</xsl:param>
<xsl:param name="monospace.inline.font.family">ProductusOSGiBookc</xsl:param>
<xsl:param name="header.font.family">ProductusBook</xsl:param>
<xsl:param name="header.fontset">
  <xsl:value-of select="$header.font.family"/>
  <xsl:if test="$header.font.family != ''
                and $symbol.font.family  != ''">,</xsl:if>
    <xsl:value-of select="$symbol.font.family"/>
</xsl:param>
<xsl:param name="monospace.fontset">
  <xsl:value-of select="$monospace.font.family"/>
  <xsl:if test="$header.font.family != ''
                and $symbol.font.family  != ''">,</xsl:if>
    <xsl:value-of select="$symbol.font.family"/>
</xsl:param>
<xsl:param name="monospace.inline.fontset">
  <xsl:value-of select="$monospace.inline.font.family"/>
  <xsl:if test="$header.font.family != ''
                and $symbol.font.family  != ''">,</xsl:if>
    <xsl:value-of select="$symbol.font.family"/>
</xsl:param>
<xsl:param name="body.font.master">9</xsl:param>
<xsl:param name="line-height">1.222em</xsl:param>
<xsl:param name="text-align">start</xsl:param>
<xsl:param name="page.margin.top">1.022cm</xsl:param>
<xsl:param name="page.margin.bottom">.587cm</xsl:param>
<xsl:param name="page.margin.inner">1.796cm</xsl:param>
<xsl:param name="page.margin.outer">1.398cm</xsl:param>
<xsl:param name="body.margin.top">.728cm</xsl:param>
<xsl:param name="body.margin.bottom">.853cm</xsl:param>
<xsl:param name="region.before.extent">.356cm</xsl:param>
<xsl:param name="region.after.extent">.356cm</xsl:param>
<!-- put all header content in single full-width centered column -->
<xsl:param name="header.column.widths">1 0 0</xsl:param>
<!-- put all footer content in single full-width centered column -->
<xsl:param name="footer.column.widths">1 0 1</xsl:param>
<xsl:param name="header.table.height">9pt</xsl:param>
<xsl:param name="footer.table.height">9pt</xsl:param>
<xsl:param name="header.rule" select="0"/>
<xsl:param name="footer.rule" select="0"/>
<xsl:param name="chapter.autolabel" select="1"/>
<xsl:param name="appendix.autolabel" select="1"/>
<xsl:param name="section.autolabel" select="1"/>
<xsl:param name="itemizedlist.label.width">12pt</xsl:param>
<xsl:param name="orderedlist.label.width">20pt</xsl:param>
<xsl:param name="glossary.as.blocks" select="1"/>
<xsl:param name="description.bullet"><fo:inline font-family="Wingdings"
     font-weight="normal" font-style="normal" font-size="7pt">o</fo:inline></xsl:param>

<xsl:param name="formal.title.placement">
figure before
table before
example before
</xsl:param>

<xsl:param name="local.l10n.xml" select="document('')"/>
<l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0">
  <l:l10n language="en">
     <l:context name="xref-number-and-title">
       <l:template name="figure" text="Figure %n"/>
       <l:template name="chapter" text="%t on page %p"/>
       <l:template name="appendix" text="%t on page %p"/>
       <l:template name="section" text="%t on page %p"/>
     </l:context>
  </l:l10n>
</l:i18n>


<!--==============================================================-->
<!--  Attribute sets                                              -->
<!--==============================================================-->
<!-- This attribute set specifies vertical spacing between
most blocks of text.  It is overwritten by para.properties for
actual para elements -->
<xsl:attribute-set name="normal.para.spacing">
  <xsl:attribute name="space-before.optimum">5pt</xsl:attribute>
  <xsl:attribute name="space-before.minimum">5pt</xsl:attribute>
  <xsl:attribute name="space-before.maximum">5pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="formal.title.properties">
  <xsl:attribute name="start-indent">0pt</xsl:attribute>
  <xsl:attribute name="font-family"><xsl:value-of
         select="$body.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">9pt</xsl:attribute>
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="font-style">italic</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="para.properties">
</xsl:attribute-set>

<xsl:attribute-set name="parameter.block.properties"
                   use-attribute-sets="normal.para.spacing">
  <xsl:attribute name="start-indent">0pt</xsl:attribute>
  <xsl:attribute name="relative-align">baseline</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="parameter.properties">
  <xsl:attribute name="text-align">end</xsl:attribute>
  <xsl:attribute name="font-style">italic</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="list.item.spacing">
  <xsl:attribute name="space-before.optimum">0pt</xsl:attribute>
  <xsl:attribute name="space-before.minimum">0pt</xsl:attribute>
  <xsl:attribute name="space-before.maximum">0pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="header.content.properties">
  <xsl:attribute name="font-family"><xsl:value-of 
           select="$header.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">10pt</xsl:attribute>
  <xsl:attribute name="line-height">10pt</xsl:attribute>
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="hyphenate">false</xsl:attribute>
  <xsl:attribute name="letter-spacing">0.03em</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="footer.content.properties">
  <xsl:attribute name="font-family"><xsl:value-of 
           select="$header.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">9pt</xsl:attribute>
  <xsl:attribute name="line-height">9pt</xsl:attribute>
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="hyphenate">false</xsl:attribute>
  <xsl:attribute name="letter-spacing">0.03em</xsl:attribute>
</xsl:attribute-set>

<!-- Used for chapter, appendix, and preface titles -->
<xsl:attribute-set name="component.title.properties">
  <xsl:attribute name="font-family"><xsl:value-of 
           select="$title.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">28pt</xsl:attribute>
  <xsl:attribute name="line-height">35pt</xsl:attribute>
  <xsl:attribute name="hyphenate">false</xsl:attribute>
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="text-align">start</xsl:attribute>
  <xsl:attribute name="space-before.minimum">0in</xsl:attribute>
  <xsl:attribute name="space-before.optimum">0in</xsl:attribute>
  <xsl:attribute name="space-before.maximum">0in</xsl:attribute>
  <xsl:attribute name="space-after">24pt</xsl:attribute>
  <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="toc.title.properties">
  <xsl:attribute name="font-family"><xsl:value-of 
           select="$title.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">18pt</xsl:attribute>
  <xsl:attribute name="line-height">22pt</xsl:attribute>
  <xsl:attribute name="hyphenate">false</xsl:attribute>
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="text-align">start</xsl:attribute>
  <xsl:attribute name="space-before">10pt</xsl:attribute>
  <xsl:attribute name="space-before.conditionality">retain</xsl:attribute>
  <xsl:attribute name="space-after">14pt</xsl:attribute>
  <xsl:attribute name="start-indent">84pt</xsl:attribute>
  <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="section.title.properties">
  <xsl:attribute name="font-family"><xsl:value-of 
           select="$title.fontset"/></xsl:attribute>
  <xsl:attribute name="hyphenate">false</xsl:attribute>
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="text-align">start</xsl:attribute>
  <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="section.title.level1.properties">
  <xsl:attribute name="font-size">18pt</xsl:attribute>
  <xsl:attribute name="line-height">25pt</xsl:attribute>
  <xsl:attribute name="space-before">12.5pt</xsl:attribute>
  <xsl:attribute name="space-after">12pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="section.title.level2.properties">
  <xsl:attribute name="font-size">12pt</xsl:attribute>
  <xsl:attribute name="line-height">14pt</xsl:attribute>
  <xsl:attribute name="space-before">8pt</xsl:attribute>
  <xsl:attribute name="space-after">5pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="section.title.level3.properties">
  <xsl:attribute name="font-size">9pt</xsl:attribute>
  <xsl:attribute name="line-height">11pt</xsl:attribute>
  <xsl:attribute name="space-before">5pt</xsl:attribute>
  <xsl:attribute name="space-after">5pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="section.title.level4.properties">
  <xsl:attribute name="font-family"><xsl:value-of
           select="$body.fontset"/></xsl:attribute>
  <xsl:attribute name="font-style">italic</xsl:attribute>
  <xsl:attribute name="font-size">9pt</xsl:attribute>
  <xsl:attribute name="line-height">11pt</xsl:attribute>
  <xsl:attribute name="space-before">5pt</xsl:attribute>
  <xsl:attribute name="space-after">3pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="toc.line.properties">
  <xsl:attribute name="start-indent">52pt</xsl:attribute>
  <xsl:attribute name="end-indent">24pt</xsl:attribute>
  <xsl:attribute name="hyphenate">false</xsl:attribute>
  <xsl:attribute name="provisional-distance-between-starts">32pt</xsl:attribute>
  <xsl:attribute name="provisional-label-separation">3pt</xsl:attribute>
  <xsl:attribute name="font-size">
    <xsl:choose>
      <xsl:when test="self::d:section">8pt</xsl:when>
      <xsl:otherwise>12pt</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="line-height">
    <xsl:choose>
      <xsl:when test="self::d:section">9pt</xsl:when>
      <xsl:otherwise>14pt</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="font-family">
    <xsl:choose>
      <xsl:when test="self::d:section"><xsl:value-of select="$header.fontset"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="$title.fontset"/></xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="letter-spacing">
    <xsl:choose>
      <xsl:when test="self::d:section">0em</xsl:when>
      <xsl:otherwise>0.02em</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="space-before">
    <xsl:choose>
      <xsl:when test="self::d:section">5pt</xsl:when>
      <xsl:otherwise>8pt</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="space-after">
    <xsl:choose>
      <xsl:when test="self::d:section">0pt</xsl:when>
      <xsl:otherwise>7pt</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="figure.title.properties">
  <xsl:attribute name="space-before.minimum">4pt</xsl:attribute>
  <xsl:attribute name="space-before.optimum">4pt</xsl:attribute>
  <xsl:attribute name="space-before.maximum">4pt</xsl:attribute>
  <xsl:attribute name="space-after">14pt</xsl:attribute>
  <xsl:attribute name="font-weight">bold</xsl:attribute>
  <xsl:attribute name="font-size">11pt</xsl:attribute>
  <xsl:attribute name="line-height">13pt</xsl:attribute>
  <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
  <xsl:attribute name="keep-with-previous.within-column">always</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="monospace.properties">
  <xsl:attribute name="font-family"><xsl:value-of
            select="$monospace.inline.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">9pt</xsl:attribute>
  <xsl:attribute name="letter-spacing">0.05em</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="monospace.verbatim.properties">
  <xsl:attribute name="space-before">5pt</xsl:attribute>
  <xsl:attribute name="margin-left">12pt</xsl:attribute>
  <xsl:attribute name="font-family"><xsl:value-of
            select="$monospace.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">9pt</xsl:attribute>
  <xsl:attribute name="line-height">11pt</xsl:attribute>
  <xsl:attribute name="letter-spacing">-.0em</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.title.properties">
  <xsl:attribute name="space-before">1in</xsl:attribute>
  <xsl:attribute name="space-before.conditionality">retain</xsl:attribute>
  <xsl:attribute name="text-align">center</xsl:attribute>
  <xsl:attribute name="font-size">28pt</xsl:attribute>
  <xsl:attribute name="line-height">32pt</xsl:attribute>
  <xsl:attribute name="font-weight">bold</xsl:attribute>
  <xsl:attribute name="font-style">normal</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.subtitle.properties">
  <xsl:attribute name="space-before">.5in</xsl:attribute>
  <xsl:attribute name="text-align">center</xsl:attribute>
  <xsl:attribute name="font-size">22pt</xsl:attribute>
  <xsl:attribute name="line-height">26pt</xsl:attribute>
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="font-style">normal</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.publisher.properties">
  <xsl:attribute name="space-before">1.5in</xsl:attribute>
  <xsl:attribute name="text-align">center</xsl:attribute>
  <xsl:attribute name="font-size">14pt</xsl:attribute>
  <xsl:attribute name="line-height">17pt</xsl:attribute>
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="font-style">normal</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.address.properties"
      use-attribute-sets="verbatim.properties">
  <xsl:attribute name="text-align">center</xsl:attribute>
  <xsl:attribute name="space-before.minimum">0pt</xsl:attribute>
  <xsl:attribute name="space-before.maximum">0pt</xsl:attribute>
  <xsl:attribute name="space-before.optimum">0pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.logo.block.properties">
  <xsl:attribute name="space-before">12pt</xsl:attribute>
  <xsl:attribute name="text-align">center</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.logo.image.properties">
  <xsl:attribute name="content-width">2in</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="verso.properties">
  <xsl:attribute name="font-size">10pt</xsl:attribute>
  <xsl:attribute name="text-align">center</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="remark.properties">
  <xsl:attribute name="color">red</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="releaseinfo.properties">
  <xsl:attribute name="font-family">ProductusOSGiBookcItalic</xsl:attribute>
  <xsl:attribute name="font-size">18pt</xsl:attribute>
  <xsl:attribute name="line-height">25pt</xsl:attribute>
</xsl:attribute-set>

<!--==============================================================-->
<!--  Template customizations                                     -->
<!--==============================================================-->
<!-- number and title for figure use list block -->
<xsl:template name="formal.object.heading">
  <xsl:param name="object" select="."/>
  <xsl:param name="placement" select="'before'"/>

  <fo:block xsl:use-attribute-sets="formal.title.properties">
    <xsl:choose>
      <xsl:when test="$placement = 'before'">
        <xsl:attribute
               name="keep-with-next.within-column">always</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute
               name="keep-with-previous.within-column">always</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>

    <fo:list-block provisional-distance-between-starts="{$body.start.indent}"
                   provisional-label-separation="{$body.offset}">
      <fo:list-item>
        <fo:list-item-label end-indent="label-end()" text-align="start">
          <fo:block>
            <xsl:call-template name="gentext">
              <xsl:with-param name="key">
                <xsl:value-of select="local-name($object)"/>
              </xsl:with-param>
            </xsl:call-template>
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="$object" mode="label.markup"/>
          </fo:block>
        </fo:list-item-label>
        <fo:list-item-body start-indent="body-start()">
          <fo:block>
            <xsl:apply-templates select="$object" mode="title.markup"/>
          </fo:block>
        </fo:list-item-body>
      </fo:list-item>
    </fo:list-block>

  </fo:block>
</xsl:template>

<!-- Use list-block -->
<xsl:template name="section.heading">
  <xsl:param name="level" select="1"/>
  <xsl:param name="marker" select="1"/>
  <xsl:param name="title"/>
  <xsl:param name="marker.title"/>

  <xsl:variable name="content">
    <fo:list-block provisional-distance-between-starts="{$body.start.indent}"
                   provisional-label-separation="{$body.offset}">
      <fo:list-item>
        <fo:list-item-label end-indent="label-end()" text-align="start">
          <fo:block>
            <xsl:apply-templates select=".." mode="label.markup"/>
          </fo:block>
        </fo:list-item-label>
        <fo:list-item-body start-indent="body-start()">
          <fo:block>
            <xsl:apply-templates select=".." mode="title.markup"/>
          </fo:block>
        </fo:list-item-body>
      </fo:list-item>
    </fo:list-block>
  </xsl:variable>

  <fo:block xsl:use-attribute-sets="section.title.properties">
    <xsl:if test="$marker != 0">
      <fo:marker marker-class-name="section.head.marker">
        <xsl:copy-of select="$marker.title"/>
      </fo:marker>
    </xsl:if>

    <xsl:choose>
      <xsl:when test="$level=1">
        <fo:block xsl:use-attribute-sets="section.title.level1.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=2">
        <fo:block xsl:use-attribute-sets="section.title.level2.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=3">
        <fo:block xsl:use-attribute-sets="section.title.level3.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=4">
        <fo:block xsl:use-attribute-sets="section.title.level4.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=5">
        <fo:block xsl:use-attribute-sets="section.title.level5.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:otherwise>
        <fo:block xsl:use-attribute-sets="section.title.level6.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:otherwise>
    </xsl:choose>
  </fo:block>
</xsl:template>

<!-- use list-block -->
<xsl:template name="component.title">
  <xsl:param name="node" select="."/>
  <xsl:param name="pagewide" select="0"/>

  <xsl:variable name="id">
    <xsl:call-template name="object.id">
      <xsl:with-param name="object" select="$node"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="title">
    <xsl:apply-templates select="$node" mode="object.title.markup">
      <xsl:with-param name="allow-anchors" select="1"/>
    </xsl:apply-templates>
  </xsl:variable>

  <xsl:variable name="titleabbrev">
    <xsl:apply-templates select="$node" mode="titleabbrev.markup"/>
  </xsl:variable>

  <xsl:variable name="level">
    <xsl:choose>
      <xsl:when test="ancestor::d:section">
        <xsl:value-of select="count(ancestor::d:section)+1"/>
      </xsl:when>
      <xsl:when test="ancestor::d:sect5">6</xsl:when>
      <xsl:when test="ancestor::d:sect4">5</xsl:when>
      <xsl:when test="ancestor::d:sect3">4</xsl:when>
      <xsl:when test="ancestor::d:sect2">3</xsl:when>
      <xsl:when test="ancestor::d:sect1">2</xsl:when>
      <xsl:otherwise>1</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="content">
    <fo:list-block provisional-distance-between-starts="{$body.start.indent}"
                   provisional-label-separation="{$body.offset}">
      <fo:list-item>
        <fo:list-item-label end-indent="label-end()" text-align="start">
          <fo:block>
            <xsl:apply-templates select="$node" mode="label.markup"/>
          </fo:block>
        </fo:list-item-label>
        <fo:list-item-body start-indent="body-start()">
          <fo:block>
            <xsl:apply-templates select="$node" mode="title.markup"/>
          </fo:block>
        </fo:list-item-body>
      </fo:list-item>
    </fo:list-block>
  </xsl:variable>

  <fo:block xsl:use-attribute-sets="component.title.properties">
    <xsl:if test="$axf.extensions != 0">
      <xsl:attribute name="axf:outline-level">
        <xsl:value-of select="count($node/ancestor::*)"/>
      </xsl:attribute>
      <xsl:attribute name="axf:outline-expand">false</xsl:attribute>
      <xsl:attribute name="axf:outline-title">
        <xsl:value-of select="normalize-space($title)"/>
      </xsl:attribute>
    </xsl:if>

    <!-- Let's handle the case where a component (bibliography, for example)
         occurs inside a section; will we need parameters for this?
         Danger Will Robinson: using section.title.level*.properties here
         runs the risk that someone will set something other than
         font-size there... -->
    <xsl:choose>
      <xsl:when test="$level=2">
        <fo:block xsl:use-attribute-sets="section.title.level2.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=3">
        <fo:block xsl:use-attribute-sets="section.title.level3.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=4">
        <fo:block xsl:use-attribute-sets="section.title.level4.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=5">
        <fo:block xsl:use-attribute-sets="section.title.level5.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=6">
        <fo:block xsl:use-attribute-sets="section.title.level6.properties">
          <xsl:copy-of select="$content"/>
        </fo:block>
      </xsl:when>
      <xsl:otherwise>
        <!-- not in a section: do nothing special -->
        <xsl:copy-of select="$content"/>
      </xsl:otherwise>
    </xsl:choose>
  </fo:block>
</xsl:template>

<xsl:template match="processing-instruction('line-break')">
  <fo:block line-height="0pt"/>
</xsl:template>

<!-- Use single cell with leader separator to not limit length of either side -->
<!-- adjust the rule lines -->
<xsl:template name="header.table">
  <xsl:param name="pageclass" select="''"/>
  <xsl:param name="sequence" select="''"/>
  <xsl:param name="gentext-key" select="''"/>

  <!-- default is a single table style for all headers -->
  <!-- Customize it for different page classes or sequence location -->

  <xsl:choose>
      <xsl:when test="$pageclass = 'index'">
          <xsl:attribute name="margin-{$direction.align.start}">0pt</xsl:attribute>
      </xsl:when>
  </xsl:choose>

  <xsl:variable name="column1">
    <xsl:choose>
      <xsl:when test="$double.sided = 0">1</xsl:when>
      <xsl:when test="$sequence = 'first' or $sequence = 'odd'">1</xsl:when>
      <xsl:otherwise>3</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="column3">
    <xsl:choose>
      <xsl:when test="$double.sided = 0">3</xsl:when>
      <xsl:when test="$sequence = 'first' or $sequence = 'odd'">3</xsl:when>
      <xsl:otherwise>1</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="candidate">
    <fo:table xsl:use-attribute-sets="header.table.properties">

      <fo:table-column column-number="1">
        <xsl:attribute name="column-width">
          <xsl:text>proportional-column-width(</xsl:text>
          <xsl:call-template name="header.footer.width">
            <xsl:with-param name="location">header</xsl:with-param>
            <xsl:with-param name="position" select="$column1"/>
            <xsl:with-param name="pageclass" select="$pageclass"/>
            <xsl:with-param name="sequence" select="$sequence"/>
            <xsl:with-param name="gentext-key" select="$gentext-key"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:attribute>
      </fo:table-column>
      <fo:table-column column-number="2">
        <xsl:attribute name="column-width">
          <xsl:text>proportional-column-width(</xsl:text>
          <xsl:call-template name="header.footer.width">
            <xsl:with-param name="location">header</xsl:with-param>
            <xsl:with-param name="position" select="2"/>
            <xsl:with-param name="pageclass" select="$pageclass"/>
            <xsl:with-param name="sequence" select="$sequence"/>
            <xsl:with-param name="gentext-key" select="$gentext-key"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:attribute>
      </fo:table-column>
      <fo:table-column column-number="3">
        <xsl:attribute name="column-width">
          <xsl:text>proportional-column-width(</xsl:text>
          <xsl:call-template name="header.footer.width">
            <xsl:with-param name="location">header</xsl:with-param>
            <xsl:with-param name="position" select="$column3"/>
            <xsl:with-param name="pageclass" select="$pageclass"/>
            <xsl:with-param name="sequence" select="$sequence"/>
            <xsl:with-param name="gentext-key" select="$gentext-key"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:attribute>
      </fo:table-column>

      <fo:table-body>
        <fo:table-row>
          <xsl:attribute name="block-progression-dimension.minimum">
            <xsl:value-of select="$header.table.height"/>
          </xsl:attribute>
          <fo:table-cell text-align="start"
                         display-align="before">
            <xsl:if test="$fop.extensions = 0">
              <xsl:attribute name="relative-align">baseline</xsl:attribute>
            </xsl:if>
            <fo:block text-align-last="justify">
              <xsl:call-template name="header.content">
                <xsl:with-param name="pageclass" select="$pageclass"/>
                <xsl:with-param name="sequence" select="$sequence"/>
                <xsl:with-param name="position" select="$direction.align.start"/>
                <xsl:with-param name="gentext-key" select="$gentext-key"/>
              </xsl:call-template>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="center"
                         display-align="before">
            <xsl:if test="$fop.extensions = 0">
              <xsl:attribute name="relative-align">baseline</xsl:attribute>
            </xsl:if>
            <fo:block>
              <xsl:call-template name="header.content">
                <xsl:with-param name="pageclass" select="$pageclass"/>
                <xsl:with-param name="sequence" select="$sequence"/>
                <xsl:with-param name="position" select="'center'"/>
                <xsl:with-param name="gentext-key" select="$gentext-key"/>
              </xsl:call-template>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="right"
                         display-align="before">
            <xsl:if test="$fop.extensions = 0">
              <xsl:attribute name="relative-align">baseline</xsl:attribute>
            </xsl:if>
            <fo:block text-align-last="justify">
              <xsl:call-template name="header.content">
                <xsl:with-param name="pageclass" select="$pageclass"/>
                <xsl:with-param name="sequence" select="$sequence"/>
                <xsl:with-param name="position" select="$direction.align.end"/>
                <xsl:with-param name="gentext-key" select="$gentext-key"/>
              </xsl:call-template>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell number-columns-spanned="3">
            <fo:block line-height="0pt" text-align-last="justify" >
              <fo:inline baseline-shift="0pt">
                <fo:leader leader-length="{$body.start.indent} - {$body.offset}"
                   leader-pattern="rule"/>
                <fo:leader leader-length="{$body.offset}" leader-pattern="space"/>
                <fo:leader leader-pattern="rule"/>
              </fo:inline>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </xsl:variable>

  <!-- Really output a header? -->
  <xsl:choose>
    <xsl:when test="$pageclass = 'titlepage' and $gentext-key = 'book'
                    and $sequence='first'">
      <!-- no, book titlepages have no headers at all -->
    </xsl:when>
    <xsl:when test="$sequence = 'blank' and $headers.on.blank.pages = 0">
      <!-- no output -->
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="$candidate"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="header.content">
  <xsl:param name="pageclass" select="''"/>
  <xsl:param name="sequence" select="''"/>
  <xsl:param name="position" select="''"/>
  <xsl:param name="gentext-key" select="''"/>

<!--
  <fo:block>
    <xsl:value-of select="$pageclass"/>
    <xsl:text>, </xsl:text>
    <xsl:value-of select="$sequence"/>
    <xsl:text>, </xsl:text>
    <xsl:value-of select="$position"/>
    <xsl:text>, </xsl:text>
    <xsl:value-of select="$gentext-key"/>
  </fo:block>
-->

  <fo:block>

    <!-- sequence can be odd, even, first, blank -->
    <!-- position can be left, center, right -->
    <xsl:choose>

      <xsl:when test="($sequence='odd' or $sequence='first') and $position='left'">
        <xsl:if test="$pageclass != 'titlepage'">
          <xsl:apply-templates select="." mode="titleabbrev.markup"/>
          <xsl:if test="d:info/d:releaseinfo">
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="d:info/d:releaseinfo/node()"/>
          </xsl:if>
          <fo:leader leader-pattern="space"/>
          <fo:retrieve-marker retrieve-class-name="section.head.marker"
                              retrieve-position="first-including-carryover"
                              retrieve-boundary="page-sequence"/>
        </xsl:if>
      </xsl:when>

      <xsl:when test="($sequence='even' or $sequence='blank') and $position='right'">
        <xsl:if test="$pageclass != 'titlepage'">
          <fo:retrieve-marker retrieve-class-name="section.head.marker"
                              retrieve-position="first-including-carryover"
                              retrieve-boundary="page-sequence"/>
          <fo:leader leader-pattern="space"/>
          <xsl:apply-templates select="." mode="titleabbrev.markup"/>
          <xsl:if test="d:info/d:releaseinfo">
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="d:info/d:releaseinfo/node()"/>
          </xsl:if>
        </xsl:if>
      </xsl:when>

    </xsl:choose>
  </fo:block>
</xsl:template>

<xsl:template name="footer.table">
  <xsl:param name="pageclass" select="''"/>
  <xsl:param name="sequence" select="''"/>
  <xsl:param name="gentext-key" select="''"/>

  <!-- default is a single table style for all footers -->
  <!-- Customize it for different page classes or sequence location -->

  <xsl:choose>
      <xsl:when test="$pageclass = 'index'">
          <xsl:attribute name="margin-{$direction.align.start}">0pt</xsl:attribute>
      </xsl:when>
  </xsl:choose>

  <xsl:variable name="column1">
    <xsl:choose>
      <xsl:when test="$double.sided = 0">1</xsl:when>
      <xsl:when test="$sequence = 'first' or $sequence = 'odd'">1</xsl:when>
      <xsl:otherwise>3</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="column3">
    <xsl:choose>
      <xsl:when test="$double.sided = 0">3</xsl:when>
      <xsl:when test="$sequence = 'first' or $sequence = 'odd'">3</xsl:when>
      <xsl:otherwise>1</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="candidate">
    <fo:table xsl:use-attribute-sets="footer.table.properties">
      <fo:table-column column-number="1">
        <xsl:attribute name="column-width">
          <xsl:text>proportional-column-width(</xsl:text>
          <xsl:call-template name="header.footer.width">
            <xsl:with-param name="location">footer</xsl:with-param>
            <xsl:with-param name="position" select="$column1"/>
            <xsl:with-param name="pageclass" select="$pageclass"/>
            <xsl:with-param name="sequence" select="$sequence"/>
            <xsl:with-param name="gentext-key" select="$gentext-key"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:attribute>
      </fo:table-column>
      <fo:table-column column-number="2">
        <xsl:attribute name="column-width">
          <xsl:text>proportional-column-width(</xsl:text>
          <xsl:call-template name="header.footer.width">
            <xsl:with-param name="location">footer</xsl:with-param>
            <xsl:with-param name="position" select="2"/>
            <xsl:with-param name="pageclass" select="$pageclass"/>
            <xsl:with-param name="sequence" select="$sequence"/>
            <xsl:with-param name="gentext-key" select="$gentext-key"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:attribute>
      </fo:table-column>
      <fo:table-column column-number="3">
        <xsl:attribute name="column-width">
          <xsl:text>proportional-column-width(</xsl:text>
          <xsl:call-template name="header.footer.width">
            <xsl:with-param name="location">footer</xsl:with-param>
            <xsl:with-param name="position" select="$column3"/>
            <xsl:with-param name="pageclass" select="$pageclass"/>
            <xsl:with-param name="sequence" select="$sequence"/>
            <xsl:with-param name="gentext-key" select="$gentext-key"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:attribute>
      </fo:table-column>

      <fo:table-body>
        <fo:table-row>
          <fo:table-cell number-columns-spanned="3">
            <fo:block line-height="0pt" text-align-last="justify" >
              <fo:inline baseline-shift="5pt">
                <fo:leader leader-length="{$body.start.indent} - {$body.offset}"
                   leader-pattern="rule"/>
                <fo:leader leader-length="{$body.offset}" leader-pattern="space"/>
                <fo:leader leader-pattern="rule"/>
              </fo:inline>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <xsl:attribute name="block-progression-dimension.minimum">
            <xsl:value-of select="$footer.table.height"/>
          </xsl:attribute>
          <fo:table-cell text-align="start"
                         display-align="after">
            <xsl:if test="$fop.extensions = 0">
              <xsl:attribute name="relative-align">baseline</xsl:attribute>
            </xsl:if>
            <fo:block>
              <xsl:call-template name="footer.content">
                <xsl:with-param name="pageclass" select="$pageclass"/>
                <xsl:with-param name="sequence" select="$sequence"/>
                <xsl:with-param name="position" select="$direction.align.start"/>
                <xsl:with-param name="gentext-key" select="$gentext-key"/>
              </xsl:call-template>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="center"
                         display-align="after">
            <xsl:if test="$fop.extensions = 0">
              <xsl:attribute name="relative-align">baseline</xsl:attribute>
            </xsl:if>
            <fo:block>
              <xsl:call-template name="footer.content">
                <xsl:with-param name="pageclass" select="$pageclass"/>
                <xsl:with-param name="sequence" select="$sequence"/>
                <xsl:with-param name="position" select="'center'"/>
                <xsl:with-param name="gentext-key" select="$gentext-key"/>
              </xsl:call-template>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="end"
                         display-align="after">
            <xsl:if test="$fop.extensions = 0">
              <xsl:attribute name="relative-align">baseline</xsl:attribute>
            </xsl:if>
            <fo:block>
              <xsl:call-template name="footer.content">
                <xsl:with-param name="pageclass" select="$pageclass"/>
                <xsl:with-param name="sequence" select="$sequence"/>
                <xsl:with-param name="position" select="$direction.align.end"/>
                <xsl:with-param name="gentext-key" select="$gentext-key"/>
              </xsl:call-template>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </xsl:variable>

  <!-- Really output a footer? -->
  <xsl:choose>
    <xsl:when test="$pageclass='titlepage' and $gentext-key='book'
                    and $sequence='first'">
      <!-- no, book titlepages have no footers at all -->
    </xsl:when>
    <xsl:when test="$sequence = 'blank' and $footers.on.blank.pages = 0">
      <!-- no output -->
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="$candidate"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="footer.content">
  <xsl:param name="pageclass" select="''"/>
  <xsl:param name="sequence" select="''"/>
  <xsl:param name="position" select="''"/>
  <xsl:param name="gentext-key" select="''"/>

<!--
  <fo:block>
    <xsl:value-of select="$pageclass"/>
    <xsl:text>, </xsl:text>
    <xsl:value-of select="$sequence"/>
    <xsl:text>, </xsl:text>
    <xsl:value-of select="$position"/>
    <xsl:text>, </xsl:text>
    <xsl:value-of select="$gentext-key"/>
  </fo:block>
-->

  <fo:block>
    <!-- pageclass can be front, body, back -->
    <!-- sequence can be odd, even, first, blank -->
    <!-- position can be left, center, right -->
    <xsl:choose>
      <xsl:when test="($sequence='odd' or $sequence='first') and $position='left'">
        <xsl:if test="$pageclass != 'titlepage'">
          <xsl:apply-templates select="/*[1]" mode="title.markup"/>
        </xsl:if>
      </xsl:when>

      <xsl:when test="($sequence='even' or $sequence='blank') and $position='right'">
        <xsl:if test="$pageclass != 'titlepage'">
          <xsl:apply-templates select="/*[1]" mode="title.markup"/>
        </xsl:if>
      </xsl:when>

      <xsl:when test="($sequence='odd' or $sequence='first') and $position='right'">
        <xsl:if test="$pageclass != 'titlepage'">
          <xsl:text>Page </xsl:text>
          <fo:page-number/>
        </xsl:if>
      </xsl:when>

      <xsl:when test="($sequence='even' or $sequence='blank') and $position='left'">
        <xsl:if test="$pageclass != 'titlepage'">
          <xsl:text>Page </xsl:text>
          <fo:page-number/>
        </xsl:if>
      </xsl:when>





      <xsl:when test="$pageclass = 'titlepage'">
        <!-- nop; no footer on title pages -->
      </xsl:when>

      <xsl:when test="$double.sided != 0 and $sequence = 'even'
                      and $position='left'">
        <fo:page-number/>
      </xsl:when>

      <xsl:when test="$double.sided != 0 and ($sequence = 'odd' or $sequence = 'first')
                      and $position='right'">
        <fo:page-number/>
      </xsl:when>

      <xsl:when test="$double.sided = 0 and $position='center'">
        <fo:page-number/>
      </xsl:when>

      <xsl:when test="$sequence='blank'">
        <xsl:choose>
          <xsl:when test="$double.sided != 0 and $position = 'left'">
            <fo:page-number/>
          </xsl:when>
          <xsl:when test="$double.sided = 0 and $position = 'center'">
            <fo:page-number/>
          </xsl:when>
          <xsl:otherwise>
            <!-- nop -->
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>


      <xsl:otherwise>
        <!-- nop -->
      </xsl:otherwise>
    </xsl:choose>
  </fo:block>
</xsl:template>

<xsl:template match="d:formalpara[@role = 'parameter']">
  <xsl:variable name="this.label" select="normalize-space(d:title)"/>
  <xsl:variable name="prev.label" 
                select="normalize-space(preceding-sibling::*[1]
                           [self::d:formalpara[@role = 'parameter']]/d:title)"/>
  <fo:block xsl:use-attribute-sets="parameter.block.properties">
    <fo:list-block provisional-distance-between-starts="{$body.start.indent}"
                   provisional-label-separation="{$body.offset}">
      <xsl:call-template name="anchor"/>
      <fo:list-item>
        <fo:list-item-label end-indent="label-end()" text-align="start">
          <fo:block xsl:use-attribute-sets="parameter.properties">
            <xsl:choose>
              <!-- Output only first "Throws" label in sequence -->
              <xsl:when test="$this.label = 'Throws' and $prev.label = 'Throws'">
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="d:title"/>
              </xsl:otherwise>
            </xsl:choose>
          </fo:block>
        </fo:list-item-label>
        <fo:list-item-body start-indent="body-start()">
          <fo:block>
            <xsl:apply-templates select="d:para"/>
          </fo:block>
        </fo:list-item-body>
      </fo:list-item>
    </fo:list-block>
  </fo:block>
</xsl:template>

<xsl:template match="d:formalpara[@role='parameter']/d:title">
  <fo:block>
    <xsl:apply-templates/>
  </fo:block>
</xsl:template>

<xsl:template match="d:para[@role = 'description']">
  <fo:block xsl:use-attribute-sets="parameter.block.properties">
    <fo:list-block provisional-distance-between-starts="{$body.start.indent}"
                   provisional-label-separation="{$body.offset}">
      <fo:list-item>
        <fo:list-item-label end-indent="label-end()" text-align="start">
          <fo:block xsl:use-attribute-sets="parameter.properties">
            <xsl:copy-of select="$description.bullet"/>
          </fo:block>
        </fo:list-item-label>
        <fo:list-item-body start-indent="body-start()">
          <fo:block>
            <xsl:apply-templates/> 
          </fo:block>
        </fo:list-item-body>
      </fo:list-item>
    </fo:list-block>
  </fo:block>
</xsl:template>

<xsl:template match="d:remark[&comment.block.parents;]">
  <xsl:if test="$show.comments != 0">
    <fo:block xsl:use-attribute-sets="remark.properties">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:if>
</xsl:template>

<xsl:template match="d:remark">
  <xsl:if test="$show.comments != 0">
    <fo:inline xsl:use-attribute-sets="remark.properties">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:if>
</xsl:template>

<!-- make xrefs to section titles italic -->
<xsl:template match="d:section" mode="insert.title.markup">
  <xsl:param name="purpose"/>
  <xsl:param name="xrefstyle"/>
  <xsl:param name="title"/>

  <xsl:choose>
    <xsl:when test="$purpose = 'xref'">
      <fo:inline font-style="italic">
        <xsl:copy-of select="$title"/>
      </fo:inline>
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="$title"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="d:releaseinfo" mode="chapter.titlepage.recto.mode">
  <fo:block xsl:use-attribute-sets="releaseinfo.properties">
    <xsl:apply-templates/>
  </fo:block>
</xsl:template>

<!-- Custom format for table of contents -->
<xsl:template name="toc.line">
  <xsl:param name="toc-context" select="NOTANODE"/>

  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>

  <xsl:variable name="label">
    <xsl:apply-templates select="." mode="label.markup"/>
  </xsl:variable>

  <fo:list-block xsl:use-attribute-sets="toc.line.properties">
    <fo:list-item>
      <fo:list-item-label end-indent="label-end()" text-align="start">
        <fo:block>
          <xsl:if test="$label != ''">
            <fo:basic-link internal-destination="{$id}">
              <xsl:copy-of select="$label"/>
              <xsl:value-of select="$autotoc.label.separator"/>
            </fo:basic-link>
          </xsl:if>
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body start-indent="body-start()">
        <fo:block margin-right="24pt">
          <fo:inline keep-with-next.within-line="always">
            <fo:basic-link internal-destination="{$id}">
              <xsl:apply-templates select="." mode="titleabbrev.markup"/>
            </fo:basic-link>
            <xsl:text> </xsl:text>
          </fo:inline>
          <fo:leader 
                 leader-pattern-width="3pt"
                 leader-alignment="reference-area"
                 keep-with-next.within-line="always">
            <xsl:attribute name="leader-pattern">
              <xsl:choose>
                <xsl:when test="self::d:section">dots</xsl:when>
                <xsl:otherwise>space</xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
          </fo:leader>
          <fo:inline>
            <xsl:text> </xsl:text> 
            <fo:basic-link internal-destination="{$id}">
              <fo:page-number-citation ref-id="{$id}"/>
            </fo:basic-link>
          </fo:inline>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </fo:list-block>
</xsl:template>

<xsl:template name="table.of.contents.titlepage.recto">
  <fo:block xsl:use-attribute-sets="toc.title.properties">
    <xsl:call-template name="gentext">
        <xsl:with-param name="key" select="'TableofContents'"/>
    </xsl:call-template>
  </fo:block>
</xsl:template>

</xsl:stylesheet>
