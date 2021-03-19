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
  xmlns:xlink='http://www.w3.org/1999/xlink'
  exclude-result-prefixes="exsl"
  version="1.0">

<xsl:import href="../../../licensed/docbook-xsl-ns/fo/docbook.xsl"/>

<xsl:output indent="no"/>

<!--==============================================================-->
<!--  Parameter settings                                          -->
<!--==============================================================-->
<xsl:param name="double.sided" select="1"/>
<xsl:param name="body.start.indent">1in</xsl:param>
<xsl:param name="body.offset">0.090in</xsl:param>
<xsl:param name="alignment">start</xsl:param>
<xsl:param name="osgi.blue.color">#004370</xsl:param>
<xsl:param name="osgi.grey.color">#808080</xsl:param>
<!-- Include only top-level section in Table of Contents -->
<xsl:param name="toc.section.depth" select="1"/>
<xsl:param name="show.comments" select="1"/>
<xsl:param name="marker.section.level" select="1"/>
<xsl:param name="autotoc.label.separator"/>
<xsl:param name="end.of.document.message">End Of Document</xsl:param>
<xsl:param name="generate.toc">
book toc,title
</xsl:param>
<xsl:param name="section.label.includes.component.label" select="1"/>


<xsl:param name="page.height.portrait">24.587cm</xsl:param>
<xsl:param name="page.width.portrait">18.898cm</xsl:param>
<xsl:param name="draft.mode">yes</xsl:param>
<xsl:param name="draft.watermark.image">../graphics/draft.svg</xsl:param>
<xsl:param name="fop1.extensions" select="1"/>
<xsl:param name="xep.extensions" select="0"/>
<xsl:param name="front.logo.image">../graphics/OSGi_Alliance.svg</xsl:param>

<xsl:param name="body.font.family">Proforma-Book</xsl:param>
<xsl:param name="title.font.family">ProductusSemibold</xsl:param>
<xsl:param name="monospace.font.family">ProductusOSGiMonoc</xsl:param>
<xsl:param name="monospace.inline.font.family">ProductusOSGiBookc</xsl:param>
<xsl:param name="header.font.family">ProductusBook</xsl:param>
<xsl:param name="symbol.font.family">ArialUnicodeMS</xsl:param>
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
<xsl:param name="orderedlist.label.width">14pt</xsl:param>
<xsl:param name="glossary.as.blocks" select="1"/>
<xsl:param name="bibliography.numbered" select="1"/>
<xsl:param name="biblioentry.item.separator"></xsl:param>

<xsl:param name="description.bullet"><fo:inline 
     font-weight="normal" font-style="normal" 
     baseline-shift="-1pt" font-size="9pt">&#x25A1;</fo:inline></xsl:param>

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
       <l:template name="table" text="Table %n"/>
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
  <xsl:attribute name="space-before.optimum">2pt</xsl:attribute>
  <xsl:attribute name="space-before.minimum">2pt</xsl:attribute>
  <xsl:attribute name="space-before.maximum">2pt</xsl:attribute>
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
  <xsl:attribute name="font-size">9pt</xsl:attribute>
  <xsl:attribute name="line-height">11pt</xsl:attribute>
  <xsl:attribute name="space-before">5pt</xsl:attribute>
  <xsl:attribute name="space-after">3pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="section.title.level5.properties">
  <xsl:attribute name="font-size">9pt</xsl:attribute>
  <xsl:attribute name="line-height">11pt</xsl:attribute>
  <xsl:attribute name="space-before">5pt</xsl:attribute>
  <xsl:attribute name="space-after">3pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="section.title.level6.properties">
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
  <xsl:attribute name="letter-spacing">0.08em</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="monospace.verbatim.properties">
  <xsl:attribute name="space-before">5pt</xsl:attribute>
  <xsl:attribute name="space-after">3pt</xsl:attribute>
  <xsl:attribute name="space-after.conditionality">retain</xsl:attribute>
  <!-- This does not seem to be needed. pgwide="1" does not use it
       and removing it gets rid of a silly FOP severe error message.
  <xsl:attribute name="margin-left">
    <xsl:choose>
      <xsl:when test="@role = 'pgwide'">auto</xsl:when>
      <xsl:otherwise>12pt</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  -->
  <xsl:attribute name="start-indent">
    <xsl:choose>
      <xsl:when test="@role = 'pgwide'">0pt</xsl:when>
      <xsl:otherwise>inherit</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="font-family"><xsl:value-of
            select="$monospace.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">
    <xsl:choose>
      <xsl:when test="@role = 'pgwide'">7pt</xsl:when>
      <xsl:otherwise>9pt</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="line-height">
    <xsl:choose>
      <xsl:when test="@role = 'pgwide'">8pt</xsl:when>
      <xsl:otherwise>11pt</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="letter-spacing">-.0em</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.title.properties">
  <xsl:attribute name="text-align">start</xsl:attribute>
  <xsl:attribute name="font-family"><xsl:value-of
            select="$title.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">24pt</xsl:attribute>
  <xsl:attribute name="line-height">26pt</xsl:attribute>
  <xsl:attribute name="font-style">normal</xsl:attribute>
  <xsl:attribute name="color">white</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.author.properties">
  <xsl:attribute name="text-align">start</xsl:attribute>
  <xsl:attribute name="font-family"><xsl:value-of
            select="$title.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">18pt</xsl:attribute>
  <xsl:attribute name="line-height">26pt</xsl:attribute>
  <xsl:attribute name="font-style">normal</xsl:attribute>
  <xsl:attribute name="color">white</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.release.properties">
  <xsl:attribute name="padding-left">0.2in</xsl:attribute>
  <xsl:attribute name="text-align">start</xsl:attribute>
  <xsl:attribute name="font-family"><xsl:value-of
            select="$title.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">18pt</xsl:attribute>
  <xsl:attribute name="line-height">18pt</xsl:attribute>
  <xsl:attribute name="font-style">normal</xsl:attribute>
  <xsl:attribute name="color">black</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="recto.logo.image.properties">
  <xsl:attribute name="content-width">4.25in</xsl:attribute>
  <xsl:attribute name="src"><xsl:value-of
        select="concat('url(', $front.logo.image, ')')"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="verso.properties">
  <xsl:attribute name="start-indent"><xsl:value-of 
            select="$body.start.indent"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="verso.title.level1.properties">
  <xsl:attribute name="font-family"><xsl:value-of
        select="$title.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">12pt</xsl:attribute>
  <xsl:attribute name="line-height">14pt</xsl:attribute>
  <xsl:attribute name="space-before">8pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="verso.title.level2.properties">
  <xsl:attribute name="font-family"><xsl:value-of
        select="$title.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">9pt</xsl:attribute>
  <xsl:attribute name="line-height">11pt</xsl:attribute>
  <xsl:attribute name="space-before">6pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="verso.copyright.properties"
      use-attribute-sets="verso.title.level1.properties">
</xsl:attribute-set>

<xsl:attribute-set name="verso.frontmatter.properties">
  <xsl:attribute name="font-family"><xsl:value-of
        select="$body.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">8pt</xsl:attribute>
  <xsl:attribute name="line-height">8pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="remark.properties">
  <xsl:attribute name="color">red</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="releaseinfo.properties">
  <xsl:attribute name="font-family">ProductusOSGiBookcItalic</xsl:attribute>
  <xsl:attribute name="font-size">18pt</xsl:attribute>
  <xsl:attribute name="line-height">25pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="ee.table.properties">
  <xsl:attribute name="font-family">ProductusOSGiBookc</xsl:attribute>
  <xsl:attribute name="font-size">7pt</xsl:attribute>
  <xsl:attribute name="line-height">7pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="end.of.document.properties">
  <xsl:attribute name="font-family"><xsl:value-of 
           select="$title.fontset"/></xsl:attribute>
  <xsl:attribute name="font-size">18pt</xsl:attribute>
  <xsl:attribute name="hyphenate">false</xsl:attribute>
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="start-indent">0pt</xsl:attribute>
  <xsl:attribute name="text-align">center</xsl:attribute>
  <xsl:attribute name="space-before">5in</xsl:attribute>
  <xsl:attribute name="space-before.conditionality">retain</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="bibliomixed.properties" 
                   use-attribute-sets="normal.para.spacing">
  <xsl:attribute name="provisional-distance-between-starts">
    <xsl:value-of select="$body.start.indent"/>
  </xsl:attribute>
  <xsl:attribute name="provisional-label-separation">14pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="xref.properties">
  <xsl:attribute name="color">
    <xsl:choose>
      <xsl:when test="@xrefstyle = 'hyperlink'">blue</xsl:when>
      <xsl:otherwise>inherit</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="font-family">
    <xsl:choose>
      <xsl:when test="@xrefstyle = 'hyperlink'">
        <xsl:value-of select="$monospace.inline.fontset"/>
      </xsl:when>
      <xsl:otherwise>inherit</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="letter-spacing">
    <xsl:choose>
      <xsl:when test="@xrefstyle = 'hyperlink'">0.08em</xsl:when>
      <xsl:otherwise>inherit</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
  <xsl:attribute name="hyphenate">
    <xsl:choose>
      <xsl:when test="@xrefstyle = 'hyperlink'">false</xsl:when>
      <xsl:otherwise>inherit</xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="table.cell.padding">
  <xsl:attribute name="padding-start">0pt</xsl:attribute>
  <xsl:attribute name="padding-end">4pt</xsl:attribute>
  <xsl:attribute name="padding-top">1pt</xsl:attribute>
  <xsl:attribute name="padding-bottom">1pt</xsl:attribute>
</xsl:attribute-set>
<!--==============================================================-->
<!--  Template customizations                                     -->
<!--==============================================================-->
<!-- These two templates force consecutive page numbering -->
<xsl:template name="initial.page.number">auto-odd</xsl:template>
<xsl:template name="page.number.format">1</xsl:template>

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
            <xsl:apply-templates select="$object" mode="title.markup">
              <xsl:with-param name="allow-anchors" select="1"/>
            </xsl:apply-templates>
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
            <xsl:apply-templates select=".." mode="title.markup">
              <xsl:with-param name="allow-anchors" select="1"/>
            </xsl:apply-templates>
            <!-- Put the target of releaseinfo xrefs with title since
            the releaseinfo text is not output -->
            <xsl:if test="../d:info/d:releaseinfo[@xml:id]">
              <fo:inline>
                <xsl:attribute name="id">
                  <xsl:value-of select="../d:info/d:releaseinfo/@xml:id"/>
                </xsl:attribute>
              </fo:inline>
            </xsl:if>
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
            <xsl:apply-templates select="$node" mode="title.markup">
              <xsl:with-param name="allow-anchors" select="1"/>
            </xsl:apply-templates>
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

<xsl:template match="processing-instruction('line-break')" mode="title.markup">
  <fo:block line-height="0pt"/>
</xsl:template>

<xsl:template match="processing-instruction('line-break')" mode="bibliomixed.mode">
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
        <xsl:if test="$pageclass != 'titlepage' and $pageclass != 'lot'">
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
        <xsl:if test="$pageclass != 'titlepage' and $pageclass != 'lot'">
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
          <xsl:if test="/*[1]/d:info/d:releaseinfo">
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="/*[1]/d:info/d:releaseinfo/node()"/>
          </xsl:if>
        </xsl:if>
      </xsl:when>

      <xsl:when test="($sequence='even' or $sequence='blank') and $position='right'">
        <xsl:if test="$pageclass != 'titlepage'">
          <xsl:apply-templates select="/*[1]" mode="title.markup"/>
          <xsl:if test="/*[1]/d:info/d:releaseinfo">
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="/*[1]/d:info/d:releaseinfo/node()"/>
          </xsl:if>
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

<xsl:template name="check.id.unique">
  <xsl:param name="linkend"></xsl:param>
  <xsl:if test="$linkend != ''">
    <xsl:variable name="targets" select="key('id',$linkend)"/>
    <xsl:variable name="target" select="$targets[1]"/>

    <xsl:if test="count($targets)=0">
      <xsl:if test="count(ancestor::d:section[@role = 'package'])=0">
        <xsl:message>
          <xsl:text>[</xsl:text>
          <xsl:apply-templates select="ancestor::d:section[1]" mode="label.markup"/>
          <xsl:text>] Error: no ID for constraint linkend: </xsl:text>
          <xsl:value-of select="$linkend"/>
          <xsl:text>.</xsl:text>
        </xsl:message>
      </xsl:if>
    </xsl:if>

    <xsl:if test="count($targets)>1">
      <xsl:message>
        <xsl:text>[</xsl:text>
        <xsl:apply-templates select="ancestor::d:section[1]" mode="label.markup"/>
        <xsl:text>] Warning: multiple "IDs" for constraint linkend: </xsl:text>
        <xsl:value-of select="$linkend"/>
        <xsl:text>.</xsl:text>
      </xsl:message>
    </xsl:if>
  </xsl:if>
</xsl:template>

<xsl:template name="simple.xlink">
  <xsl:param name="node" select="."/>
  <xsl:param name="content">
    <xsl:apply-templates/>
  </xsl:param>
  <xsl:param name="linkend" select="$node/@linkend"/>
  <xsl:param name="xhref" select="$node/@xlink:href"/>

  <xsl:choose>
    <xsl:when test="$xhref
                    and (not($node/@xlink:type) or 
                         $node/@xlink:type='simple')">

      <!-- Is it a local idref? -->
      <xsl:variable name="is.idref">
        <xsl:choose>
          <!-- if the href starts with # and does not contain an "(" -->
          <!-- or if the href starts with #xpointer(id(, it's just an ID -->
          <xsl:when test="starts-with($xhref,'#')
                          and (not(contains($xhref,'&#40;'))
                          or starts-with($xhref,
                                     '#xpointer&#40;id&#40;'))">1</xsl:when>
          <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <!-- Is it an olink ? -->
      <xsl:variable name="is.olink">
        <xsl:choose>
          <!-- If xlink:role="http://docbook.org/xlink/role/olink" -->
          <!-- and if the href contains # -->
          <xsl:when test="contains($xhref,'#') and
               @xlink:role = $xolink.role">1</xsl:when>
          <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:choose>
        <xsl:when test="$is.olink = 1">
          <xsl:call-template name="olink">
            <xsl:with-param name="content" select="$content"/>
          </xsl:call-template>
        </xsl:when>

        <xsl:when test="$is.idref = 1">

          <xsl:variable name="idref">
            <xsl:call-template name="xpointer.idref">
              <xsl:with-param name="xpointer" select="$xhref"/>
            </xsl:call-template>
          </xsl:variable>

          <xsl:variable name="targets" select="key('id',$idref)"/>
          <xsl:variable name="target" select="$targets[1]"/>

          <xsl:call-template name="check.id.unique">
            <xsl:with-param name="linkend" select="$idref"/>
          </xsl:call-template>

          <xsl:choose>
            <xsl:when test="count($target) = 0">
              <xsl:message>
                <xsl:text>[</xsl:text>
                <xsl:apply-templates select="ancestor::d:section[1]" mode="label.markup"/>
                <xsl:text>] XLink to nonexistent id: </xsl:text>
                <xsl:value-of select="$idref"/>
              </xsl:message>
              <xsl:copy-of select="$content"/>
            </xsl:when>

            <xsl:otherwise>
              <fo:basic-link internal-destination="{$idref}">
                <xsl:apply-templates select="." mode="simple.xlink.properties"/>
                <xsl:copy-of select="$content"/>
              </fo:basic-link>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>

        <!-- otherwise it's a URI -->
        <xsl:otherwise>
          <fo:basic-link external-destination="url({$xhref})">
            <xsl:apply-templates select="." mode="simple.xlink.properties"/>
            <xsl:copy-of select="$content"/>
          </fo:basic-link>
          <!-- * Call the template for determining whether the URL for this -->
          <!-- * hyperlink is displayed, and how to display it (either inline or -->
          <!-- * as a numbered footnote). -->
          <xsl:call-template name="hyperlink.url.display">
            <xsl:with-param name="url" select="$xhref"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>

    <xsl:when test="$linkend">
      <xsl:variable name="targets" select="key('id',$linkend)"/>
      <xsl:variable name="target" select="$targets[1]"/>

      <xsl:call-template name="check.id.unique">
        <xsl:with-param name="linkend" select="$linkend"/>
      </xsl:call-template>

      <xsl:choose>
        <xsl:when test="count($target) = 0">
          <xsl:if test="count(ancestor::d:section[@role = 'package'])=0">
            <xsl:message>
              <xsl:text>[</xsl:text>
              <xsl:apply-templates select="ancestor::d:section[1]" mode="label.markup"/>
              <xsl:text>] XLink to nonexistent id: </xsl:text>
              <xsl:value-of select="$linkend"/>
            </xsl:message>
          </xsl:if>
          <xsl:copy-of select="$content"/>
        </xsl:when>

        <xsl:otherwise>
          <fo:basic-link internal-destination="{$linkend}">
            <xsl:apply-templates select="." mode="simple.xlink.properties"/>
            <xsl:copy-of select="$content"/>
          </fo:basic-link>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>

    <xsl:otherwise>
      <xsl:copy-of select="$content"/>
    </xsl:otherwise>
  </xsl:choose>
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
      <xsl:text>### </xsl:text>
      <xsl:apply-templates/>
    </fo:block>
    <xsl:message>
      <xsl:text>[</xsl:text>
      <xsl:apply-templates select="ancestor::d:section[1]" mode="label.markup"/>
      <xsl:text>] ### </xsl:text>
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:message>
    <xsl:if test="$draft.mode = 'no'">
      <xsl:message terminate="yes">
        A non-draft book must not have any remark elements.
      </xsl:message>
    </xsl:if>
  </xsl:if>
</xsl:template>

<xsl:template match="d:remark">
  <xsl:if test="$show.comments != 0">
    <fo:inline xsl:use-attribute-sets="remark.properties">
      <xsl:text>### </xsl:text>
      <xsl:apply-templates/>
    </fo:inline>
   <xsl:message>
      <xsl:text>[</xsl:text>
      <xsl:apply-templates select="ancestor::d:section[1]" mode="label.markup"/>
      <xsl:text>] ### </xsl:text>
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:message>
    <xsl:if test="$draft.mode = 'no'">
      <xsl:message terminate="yes">
        A non-draft book must not have any remark elements.
      </xsl:message>
    </xsl:if>
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
    <xsl:if test="@xml:id">
      <xsl:attribute name="id">
        <xsl:value-of select="@xml:id"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates/>
  </fo:block>
</xsl:template>

<!-- Do not display javadoc section releaseinfo, but allow it as
   a target of a cross reference, which is handled in section.heading -->
<xsl:template match="d:releaseinfo[ancestor::d:section]" mode="titlepage.mode"/>

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

<!-- special ee.container element holds sequence of sections, but
should be discarded -->
<xsl:template match="d:section[@role = 'ee.container']">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="d:section" mode="label.markup">
  <!-- if this is a nested section, label the parent -->
  <xsl:choose>
    <xsl:when test="parent::d:section[@role='ee.container']">
      <xsl:variable name="parent.section.label">
        <xsl:call-template name="label.this.section">
          <xsl:with-param name="section" select="../.."/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:if test="$parent.section.label != '0'">
        <xsl:apply-templates select="../.." mode="label.markup"/>
        <xsl:apply-templates select="../.." mode="intralabel.punctuation"/>
      </xsl:if>
    </xsl:when>
    <xsl:when test="local-name(..) = 'section'">
      <xsl:variable name="parent.section.label">
        <xsl:call-template name="label.this.section">
          <xsl:with-param name="section" select=".."/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:if test="$parent.section.label != '0'">
        <xsl:apply-templates select=".." mode="label.markup"/>
        <xsl:apply-templates select=".." mode="intralabel.punctuation"/>
      </xsl:if>
    </xsl:when>
  </xsl:choose>

  <!-- if the parent is a component, maybe label that too -->
  <xsl:variable name="parent.is.component">
    <xsl:call-template name="is.component">
      <xsl:with-param name="node" select=".."/>
    </xsl:call-template>
  </xsl:variable>

  <!-- does this section get labelled? -->
  <xsl:variable name="label">
    <xsl:call-template name="label.this.section">
      <xsl:with-param name="section" select="."/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:if test="$section.label.includes.component.label != 0
                and $parent.is.component != 0">
    <xsl:variable name="parent.label">
      <xsl:apply-templates select=".." mode="label.markup"/>
    </xsl:variable>
    <xsl:if test="$parent.label != ''">
      <xsl:apply-templates select=".." mode="label.markup"/>
      <xsl:apply-templates select=".." mode="intralabel.punctuation"/>
    </xsl:if>
  </xsl:if>

<!--
  <xsl:message>
    test: <xsl:value-of select="$label"/>, <xsl:number count="d:section"/>
  </xsl:message>
-->

  <xsl:choose>
    <xsl:when test="@label">
      <xsl:value-of select="@label"/>
    </xsl:when>
    <xsl:when test="$label != 0">      
      <xsl:variable name="format">
        <xsl:call-template name="autolabel.format">
          <xsl:with-param name="format" select="$section.autolabel"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:number format="{$format}" count="d:section[not(@role = 'ee.container')]"/>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!-- and ignore it in bookmarks -->
<xsl:template match="d:section[@role = 'ee.container']" mode="fop1.outline">
  <xsl:apply-templates mode="fop1.outline"/>
</xsl:template>
<xsl:template match="d:section[@role = 'ee.container']" mode="fop1.foxdest">
  <xsl:apply-templates mode="fop1.foxdest"/>
</xsl:template>

<xsl:template name="section.level">
  <xsl:param name="node" select="."/>
  <xsl:choose>
    <xsl:when test="local-name($node)='sect1'">1</xsl:when>
    <xsl:when test="local-name($node)='sect2'">2</xsl:when>
    <xsl:when test="local-name($node)='sect3'">3</xsl:when>
    <xsl:when test="local-name($node)='sect4'">4</xsl:when>
    <xsl:when test="local-name($node)='sect5'">5</xsl:when>
    <xsl:when test="local-name($node)='section'">
      <xsl:variable name="section.count">
        <xsl:choose>
          <xsl:when test="$node/../../../../../../d:section">6</xsl:when>
          <xsl:when test="$node/../../../../../d:section">5</xsl:when>
          <xsl:when test="$node/../../../../d:section">4</xsl:when>
          <xsl:when test="$node/../../../d:section">3</xsl:when>
          <xsl:when test="$node/../../d:section">2</xsl:when>
          <xsl:otherwise>1</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="ancestor::d:section[@role = 'ee.container']">
          <xsl:value-of select="$section.count - 1"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$section.count"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="local-name($node)='refsect1' or
                    local-name($node)='refsect2' or
                    local-name($node)='refsect3' or
                    local-name($node)='refsection' or
                    local-name($node)='refsynopsisdiv'">
      <xsl:call-template name="refentry.section.level">
        <xsl:with-param name="node" select="$node"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="local-name($node)='simplesect'">
      <xsl:choose>
        <xsl:when test="$node/../../d:sect1">2</xsl:when>
        <xsl:when test="$node/../../d:sect2">3</xsl:when>
        <xsl:when test="$node/../../d:sect3">4</xsl:when>
        <xsl:when test="$node/../../d:sect4">5</xsl:when>
        <xsl:when test="$node/../../d:sect5">5</xsl:when>
        <xsl:when test="$node/../../d:section">
          <xsl:choose>
            <xsl:when test="$node/../../../../../d:section">5</xsl:when>
            <xsl:when test="$node/../../../../d:section">4</xsl:when>
            <xsl:when test="$node/../../../d:section">3</xsl:when>
            <xsl:otherwise>2</xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>1</xsl:otherwise>
  </xsl:choose>
</xsl:template><!-- section.level -->

<!-- generate the icons for ee table -->
<xsl:template match="d:phrase[@role = 'ee.included']">
  <xsl:choose>
    <xsl:when test="contains(., 'g')">
      <!-- solid black box -->
      <fo:inline font-size="8pt" baseline-shift="-1.0pt">&#x25A0;</fo:inline>
    </xsl:when>
    <xsl:otherwise>
      <!-- open white box -->
      <fo:inline font-size="8.2pt" baseline-shift="-1.1pt">&#x25A1;</fo:inline>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="d:phrase[@role = 'ee.package.label']">
  <fo:inline font-family="{$title.fontset}"
             line-height="10pt">
    <xsl:apply-templates/>
  </fo:inline>
</xsl:template>

<xsl:template match="d:phrase[@role = 'ee.class.label']">
  <fo:inline font-family="{$title.fontset}"
             line-height="10pt">
    <xsl:apply-templates/>
  </fo:inline>
</xsl:template>

<!-- Turn off all table borders -->
<xsl:template name="table.frame"/>

<xsl:template name="table.cell.properties">
  <xsl:param name="bgcolor.pi" select="''"/>
  <!-- reset defaults for table cells to no border -->
  <xsl:param name="rowsep.inherit" select="0"/>
  <xsl:param name="colsep.inherit" select="0"/>
  <xsl:param name="col" select="1"/>
  <xsl:param name="valign.inherit" select="''"/>
  <xsl:param name="align.inherit" select="''"/>
  <xsl:param name="char.inherit" select="''"/>

  <xsl:variable name="tabstyle">
    <xsl:call-template name="tabstyle"/>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="ancestor::d:tgroup">
      <xsl:if test="$bgcolor.pi != ''">
        <xsl:attribute name="background-color">
          <xsl:value-of select="$bgcolor.pi"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="$rowsep.inherit &lt; 0">
        <xsl:call-template name="border">
          <xsl:with-param name="side" select="'bottom'"/>
        </xsl:call-template>
      </xsl:if>

      <xsl:if test="$colsep.inherit &lt; 0 and 
                      $col &lt; (ancestor::d:tgroup/@cols|ancestor::d:entrytbl/@cols)[last()]">
        <xsl:call-template name="border">
          <xsl:with-param name="side" select="'end'"/>
        </xsl:call-template>
      </xsl:if>

      <xsl:if test="$valign.inherit != ''">
        <xsl:attribute name="display-align">
          <xsl:choose>
            <xsl:when test="$valign.inherit='top'">before</xsl:when>
            <xsl:when test="$valign.inherit='middle'">center</xsl:when>
            <xsl:when test="$valign.inherit='bottom'">after</xsl:when>
            <xsl:otherwise>
              <xsl:message>
                <xsl:text>Unexpected valign value: </xsl:text>
                <xsl:value-of select="$valign.inherit"/>
                <xsl:text>, center used.</xsl:text>
              </xsl:message>
              <xsl:text>center</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="$align.inherit = 'char' and $char.inherit != ''">
          <xsl:attribute name="text-align">
            <xsl:value-of select="$char.inherit"/>
          </xsl:attribute>
        </xsl:when>
        <xsl:when test="$align.inherit != ''">
          <xsl:attribute name="text-align">
            <xsl:value-of select="$align.inherit"/>
          </xsl:attribute>
        </xsl:when>
      </xsl:choose>

    </xsl:when>
    <xsl:otherwise>
      <!-- HTML table -->
      <xsl:if test="$bgcolor.pi != ''">
        <xsl:attribute name="background-color">
          <xsl:value-of select="$bgcolor.pi"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="$align.inherit != ''">
        <xsl:attribute name="text-align">
          <xsl:value-of select="$align.inherit"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="$valign.inherit != ''">
        <xsl:attribute name="display-align">
          <xsl:choose>
            <xsl:when test="$valign.inherit='top'">before</xsl:when>
            <xsl:when test="$valign.inherit='middle'">center</xsl:when>
            <xsl:when test="$valign.inherit='bottom'">after</xsl:when>
            <xsl:otherwise>
              <xsl:message>
                <xsl:text>Unexpected valign value: </xsl:text>
                <xsl:value-of select="$valign.inherit"/>
                <xsl:text>, center used.</xsl:text>
              </xsl:message>
              <xsl:text>center</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:if>

      <xsl:call-template name="html.table.cell.rules"/>

    </xsl:otherwise>
  </xsl:choose>

  <xsl:choose>
    <xsl:when test="$tabstyle = 'ee-package'">
      <xsl:attribute name="padding-top">0pt</xsl:attribute>
      <xsl:attribute name="padding-bottom">0pt</xsl:attribute>
      <xsl:attribute name="padding-start">0pt</xsl:attribute>
      <xsl:attribute name="padding-end">0pt</xsl:attribute>
      <xsl:attribute name="font-family"><xsl:value-of
             select="$monospace.inline.fontset"/></xsl:attribute>
      <xsl:attribute name="font-size">7pt</xsl:attribute>
      <xsl:attribute name="line-height">7pt</xsl:attribute>
      <xsl:attribute name="hyphenate">false</xsl:attribute>
    </xsl:when>
    <xsl:otherwise>
    </xsl:otherwise>
  </xsl:choose>

</xsl:template>

<!-- customized to change font in row header -->
<xsl:template name="table.cell.block.properties">
  <xsl:if test="ancestor::d:tbody and 
                  (ancestor::d:table[@rowheader = 'firstcol'] or
                  ancestor::d:informaltable[@rowheader = 'firstcol']) and
                  ancestor-or-self::d:entry[1][count(preceding-sibling::d:entry) = 0]">
    <xsl:attribute name="font-family">
      <xsl:value-of select="$title.fontset"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<!-- customized to change font in header row -->
<xsl:template name="table.row.properties">

  <xsl:variable name="row-height">
    <xsl:if test="processing-instruction('dbfo')">
      <xsl:call-template name="pi.dbfo_row-height"/>
    </xsl:if>
  </xsl:variable>

  <xsl:if test="$row-height != ''">
    <xsl:attribute name="block-progression-dimension">
      <xsl:value-of select="$row-height"/>
    </xsl:attribute>
  </xsl:if>

  <xsl:variable name="bgcolor">
    <xsl:call-template name="pi.dbfo_bgcolor"/>
  </xsl:variable>

  <xsl:if test="$bgcolor != ''">
    <xsl:attribute name="background-color">
      <xsl:value-of select="$bgcolor"/>
    </xsl:attribute>
  </xsl:if>

  <!-- No page break in row -->
  <xsl:attribute name="keep-together.within-column">always</xsl:attribute>

  <!-- Keep header row with next row -->
  <xsl:if test="ancestor::d:thead">
    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
    <xsl:attribute name="font-family">
      <xsl:value-of select="$title.fontset"/>
    </xsl:attribute>
  </xsl:if>

  <!-- Keep footer row with previous row -->
  <xsl:if test="ancestor::d:tfoot">
    <xsl:attribute name="keep-with-previous.within-column">always</xsl:attribute>
    <xsl:attribute name="font-family">
      <xsl:value-of select="$title.fontset"/>
    </xsl:attribute>
  </xsl:if>

  <xsl:if test="@role = 'ee.class.label'">
    <xsl:attribute name="border-top">
      <xsl:text>0.5pt solid black</xsl:text>
    </xsl:attribute>
  </xsl:if>

</xsl:template>

<xsl:template match="d:para[@role = 'ee.class.child']">
  <fo:block line-height="8pt">
    <xsl:apply-templates/>
  </fo:block>
</xsl:template>

<xsl:template match="d:para[@role = 'ee.class.label']">
  <fo:block line-height="10pt">
    <xsl:apply-templates/>
  </fo:block>
</xsl:template>

<!-- links to para in Reference should just display the text -->
<xsl:template match="d:para" mode="xref-to">
  <xsl:param name="referrer"/>
  <xsl:param name="xrefstyle"/>
  <xsl:param name="verbose" select="1"/> 

  <xsl:choose>
    <xsl:when test="$xrefstyle = 'Reference'">
      <xsl:apply-templates select="node()"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-imports/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="book.titlepage.recto">

  <!-- large color block -->
  <fo:block-container absolute-position="absolute"
                      width="5.54in"
                      height="3.75in"
                      top="-0.3in"
                      left="-0.22in"
                      background-color="{$osgi.grey.color}">
    <fo:block/>
  </fo:block-container>

  <!-- title color block -->
  <fo:block-container absolute-position="absolute"
                      width="4.25in"
                      height="1.4in"
                      top="-0.3in + 2.92in"
                      left="-0.22in + 1.29in"
                      background-color="{$osgi.blue.color}">
    <fo:block margin-left="0pt" padding="0.2in">
      <fo:block xsl:use-attribute-sets="recto.author.properties">
        <xsl:apply-templates mode="titlepage.mode" 
                             select="d:info/d:author"/>
      </fo:block>
      <fo:block xsl:use-attribute-sets="recto.title.properties">
        <xsl:choose>
          <xsl:when test="d:info/d:title">
            <xsl:apply-templates mode="titlepage.mode" select="d:info/d:title"/>
          </xsl:when>
          <xsl:when test="d:title">
            <xsl:apply-templates mode="titlepage.mode" select="d:title"/>
          </xsl:when>
        </xsl:choose>
      </fo:block>
    </fo:block>
  </fo:block-container>
  
  <!-- release information -->
  <fo:block-container absolute-position="absolute"
                      width="4.25in"
                      height="0.9in"
                      top="-0.3in + 2.92in + 1.4in"
                      left="-0.22in + 1.29in"
                      display-align="center">
    <fo:block margin-left="0.2in">
      <fo:block xsl:use-attribute-sets="recto.release.properties">
        <xsl:apply-templates mode="titlepage.mode" select="d:info/d:releaseinfo"/>
        <fo:block/>
        <xsl:apply-templates mode="titlepage.mode" select="d:info/d:pubdate"/>
      </fo:block>
    </fo:block>
  </fo:block-container>

  <!-- logo -->
  <fo:block-container absolute-position="absolute"
                      width="4.5in"
                      height="2.8in"
                      top="-0.3in + 2.92in + 2.3in"
                      left="-0.22in + 1.29in">
    <fo:block>
      <fo:external-graphic xsl:use-attribute-sets="recto.logo.image.properties"/>
    </fo:block>
  </fo:block-container>

</xsl:template>

<xsl:template match="d:info/d:pubdate" mode="titlepage.mode" >
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="d:info/d:author" mode="titlepage.mode" >
  <xsl:apply-templates/>
</xsl:template>

<!-- frontmatter preface handled in different mode -->
<xsl:template match="d:preface[@role = 'frontmatter']"/>
<!-- and omitted from the table of contents -->
<xsl:template match="d:preface[@role = 'frontmatter']" mode="toc"/>
<!-- Omit frontmatter sections from bookmarks -->
<xsl:template match="d:preface[@role = 'frontmatter']" mode="fop1.outline"/>
<!-- Omit frontmatter sections from FOP's foxdest  -->
<xsl:template match="d:preface[@role = 'frontmatter']" mode="fop1.foxdest"/>


<xsl:template name="book.titlepage.verso">
  <fo:block xsl:use-attribute-sets="verso.properties">
    <fo:block xsl:use-attribute-sets="verso.copyright.properties">
      <xsl:text>Copyright &#xA9; </xsl:text>
      <xsl:call-template name="copyright.years">
        <xsl:with-param name="years" select="d:info/d:copyright/d:year"/>
        <xsl:with-param name="print.ranges" select="$make.year.ranges"/>
        <xsl:with-param name="single.year.ranges" select="$make.single.year.ranges"/>
      </xsl:call-template>
      <xsl:text> </xsl:text>
      <xsl:value-of select="d:info/d:copyright/d:holder"/>
    </fo:block>

    <fo:block xsl:use-attribute-sets="verso.frontmatter.properties">
      <xsl:apply-templates select="/d:book/d:preface[@role = 'frontmatter']" mode="frontmatter"/>
    </fo:block>
  </fo:block>
</xsl:template>

<xsl:template match="*" mode="frontmatter">
  <!-- switch to normal mode -->
  <xsl:apply-templates select="."/>
</xsl:template>

<xsl:template match="d:preface[@role = 'frontmatter']" mode="frontmatter">
  <xsl:apply-templates mode="frontmatter"/>
</xsl:template>

<xsl:template match="d:preface[@role = 'frontmatter']/d:title" mode="frontmatter">
  <fo:block xsl:use-attribute-sets="verso.title.level1.properties">
    <xsl:apply-templates/>
  </fo:block>
</xsl:template>

<xsl:template match="d:preface[@role = 'frontmatter']/d:section" mode="frontmatter">
  <xsl:apply-templates mode="frontmatter"/>
</xsl:template>

<xsl:template match="d:preface[@role = 'frontmatter']/d:section/d:title" 
              mode="frontmatter">
  <fo:block xsl:use-attribute-sets="verso.title.level1.properties">
    <xsl:apply-templates/>
  </fo:block>
</xsl:template>

<xsl:template match="d:preface[@role = 'frontmatter']/d:section/d:section" 
              mode="frontmatter">
  <xsl:apply-templates mode="frontmatter"/>
</xsl:template>

<xsl:template match="d:preface[@role = 'frontmatter']/d:section/d:section/d:title" 
              mode="frontmatter">
  <fo:block xsl:use-attribute-sets="verso.title.level2.properties">
    <xsl:apply-templates/>
  </fo:block>
</xsl:template>

<!-- links to releaseinfo should display the text -->
<xsl:template match="d:releaseinfo" mode="xref-to">
  <xsl:param name="referrer"/>
  <xsl:param name="xrefstyle"/>
  <xsl:param name="verbose" select="1"/> 

  <xsl:apply-templates select="node()"/>
</xsl:template>

<xsl:template name="back.cover">
  <xsl:call-template name="page.sequence">
    <xsl:with-param name="content">
      <fo:block break-after="page"/>
      <fo:block xsl:use-attribute-sets="end.of.document.properties">
        <xsl:copy-of select="$end.of.document.message"/>
      </fo:block>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template match="d:title" mode="bibliomixed.mode">
  <fo:inline font-style="italic">
    <xsl:apply-templates/>
  </fo:inline>
  <!-- Add a line break after the title -->
  <fo:block/>
</xsl:template>

<!-- Customize indentation of bibliomixed -->
<xsl:template match="d:bibliomixed">
  <xsl:param name="label">
    <xsl:apply-templates select="." mode="label.markup"/> 
  </xsl:param>

  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="string(.) = ''">
      <xsl:variable name="bib" select="document($bibliography.collection,.)"/>
      <xsl:variable name="entry" select="$bib/d:bibliography//
                                         *[@id=$id or @xml:id=$id][1]"/>
      <xsl:choose>
        <xsl:when test="$entry">
          <xsl:choose>
            <xsl:when test="$bibliography.numbered != 0">
              <xsl:apply-templates select="$entry">
                <xsl:with-param name="label" select="$label"/>
              </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="$entry"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message>
            <xsl:text>No bibliography entry: </xsl:text>
            <xsl:value-of select="$id"/>
            <xsl:text> found in </xsl:text>
            <xsl:value-of select="$bibliography.collection"/>
          </xsl:message>
          <fo:block id="{$id}" xsl:use-attribute-sets="normal.para.spacing">
            <xsl:text>Error: no bibliography entry: </xsl:text>
            <xsl:value-of select="$id"/>
            <xsl:text> found in </xsl:text>
            <xsl:value-of select="$bibliography.collection"/>
          </fo:block>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <fo:block start-indent="0pt">
        <fo:list-block xsl:use-attribute-sets="bibliomixed.properties">
          <fo:list-item>
            <fo:list-item-label end-indent="label-end()">
              <fo:block text-align="end">
                <xsl:text>[</xsl:text>
                <xsl:copy-of select="$label"/>
                <xsl:text>]</xsl:text>
              </fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
              <fo:block id="{$id}">
                <xsl:apply-templates mode="bibliomixed.mode"/>
              </fo:block> 
            </fo:list-item-body>
          </fo:list-item>
        </fo:list-block>
      </fo:block>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- customized Reference  links -->
<xsl:template match="d:biblioentry|d:bibliomixed" mode="xref-to-prefix">
</xsl:template>

<xsl:template match="d:biblioentry|d:bibliomixed" mode="xref-to-suffix">
</xsl:template>

<xsl:template match="d:biblioentry|d:bibliomixed" mode="xref-to">
  <xsl:param name="referrer"/>
  <xsl:param name="xrefstyle"/>
  <xsl:param name="verbose" select="1"/>

  <!-- handles both biblioentry and bibliomixed -->
  <xsl:choose>
    <xsl:when test="string(.) = ''">
      <xsl:variable name="bib" select="document($bibliography.collection,.)"/>
      <xsl:variable name="id" select="(@id|@xml:id)[1]"/>
      <xsl:variable name="entry" select="$bib/d:bibliography/
                                         *[@id=$id or @xml:id=$id][1]"/>
      <xsl:choose>
        <xsl:when test="$entry">
          <xsl:choose>
            <xsl:when test="$bibliography.numbered != 0">
              <xsl:apply-templates select="." mode="label.markup"/>
            </xsl:when>
            <xsl:when test="local-name($entry/*[1]) = 'abbrev'">
              <xsl:apply-templates select="$entry/*[1]"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="(@id|@xml:id)[1]"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message>
            <xsl:text>No bibliography entry: </xsl:text>
            <xsl:value-of select="$id"/>
            <xsl:text> found in </xsl:text>
            <xsl:value-of select="$bibliography.collection"/>
          </xsl:message>
          <xsl:value-of select="(@id|@xml:id)[1]"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="$bibliography.numbered != 0">
          <xsl:text>[</xsl:text>
          <xsl:apply-templates select="." mode="label.markup"/>
          <xsl:text>]</xsl:text>
          <xsl:text> </xsl:text>
        </xsl:when>
        <xsl:when test="local-name(*[1]) = 'abbrev'">
          <xsl:text>[</xsl:text>
          <xsl:apply-templates select="*[1]"/>
          <xsl:text>]</xsl:text>
          <xsl:text> </xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>[</xsl:text>
          <xsl:value-of select="(@id|@xml:id)[1]"/>
          <xsl:text>]</xsl:text>
          <xsl:text> </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <fo:inline font-style="italic">
        <!-- Need to normalize space in title since xref is used in programlisting
        <xsl:apply-templates select="d:title" mode="bibliography.mode"/>
        -->
        <xsl:value-of select="normalize-space(d:title)"/>
      </fo:inline>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- number from chapter or appendix -->
<xsl:template match="d:bibliomixed" mode="label.markup">
  <xsl:number from="d:bibliography|d:chapter|d:appendix" 
              count="d:biblioentry|d:bibliomixed"
              level="any" format="1"/>
</xsl:template>

<!-- used to force symbol fonts when needed -->
<xsl:template match="d:phrase[@role ='symbol']">
  <fo:inline font-family="{$symbol.font.family}">
    <xsl:apply-templates/>
  </fo:inline>
</xsl:template>

<!-- Turn off page references to para elements -->
<xsl:template match="d:para" mode="page.citation"/>

<!-- revisionflag markup -->
<xsl:template match="text()[ancestor::*[@revisionflag][1][@revisionflag ='added']]">
<fo:inline background-color="#A6C5DC"><!-- blue -->
  <xsl:copy/>
</fo:inline>
</xsl:template>
<xsl:template match="text()[ancestor::*[@revisionflag][1][@revisionflag ='changed']]">
<fo:inline background-color="#CCE5C2"><!-- green -->
  <xsl:copy/>
</fo:inline>
</xsl:template>
<xsl:template match="text()[ancestor::*[@revisionflag][1][@revisionflag ='deleted']]">
<fo:inline background-color="#EBCCCC" text-decoration="line-through"><!-- red -->
  <xsl:copy/>
</fo:inline>
</xsl:template>
</xsl:stylesheet>
