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

<xsl:import href="../../../licensed/docbook-xsl-ns/xhtml/chunkfast.xsl"/>
<xsl:import href="custom-html-common.xsl"/>
<xsl:include href="../../../licensed/docbook-xsl-ns/webhelp/xsl/titlepage.templates.xsl"/>
<xsl:output omit-xml-declaration="yes"/>

<!--==============================================================-->
<!--  Parameter settings                                          -->
<!--==============================================================-->
<xsl:param name="table.borders.with.css" select="1" />
<!--
<xsl:param name="table.cell.border.style">none</xsl:param>
<xsl:param name="table.frame.border.style">none</xsl:param>
-->
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
<xsl:param name="description.bullet" select="'&#x25A1;'" />

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
<xsl:param name="show.comments">
  <xsl:choose>
    <xsl:when test="$book.status = 'draft'">
      <xsl:value-of select="1"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="0"/>
    </xsl:otherwise>
  </xsl:choose>
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

<xsl:template match="processing-instruction('line-break')">
  <br />
</xsl:template>

<xsl:template match="processing-instruction('line-break')" mode="title.markup">
  <br />
</xsl:template>

<xsl:template match="processing-instruction('line-break')" mode="bibliomixed.mode">
  <br />
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
          <div id="{$id}">
            <xsl:text>Error: no bibliography entry: </xsl:text>
            <xsl:value-of select="$id"/>
            <xsl:text> found in </xsl:text>
            <xsl:value-of select="$bibliography.collection"/>
          </div>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <div class="bibliomixed">
        <a class="anchor" id="{$id}" />
        <p class="bibliomixed">
          <xsl:text>[</xsl:text>
          <xsl:copy-of select="$label"/>
          <xsl:text>]</xsl:text>
          <xsl:apply-templates mode="bibliomixed.mode"/>
        </p>
      </div>
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
      <em>
        <xsl:value-of select="normalize-space(d:title)"/>
      </em>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="d:bibliomixed" mode="label.markup">
  <xsl:number from="d:bibliography|d:chapter|d:appendix"
              count="d:biblioentry|d:bibliomixed"
              level="any" format="1"/>
</xsl:template>

<!-- validate linkends -->

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

<!-- Javadoc modifications -->

<xsl:template match="d:formalpara[@role = 'parameter']">
  <xsl:variable name="this.label" select="normalize-space(d:title)"/>
  <xsl:variable name="prev.label"
                select="normalize-space(preceding-sibling::*[1]
                           [self::d:formalpara[@role = 'parameter']]/d:title)"/>
  <p class="parameter">
    <label>
      <xsl:call-template name="anchor"/>
      <xsl:choose>
        <!-- Output only first "Throws" label in sequence -->
        <xsl:when test="$this.label = 'Throws' and $prev.label = 'Throws'">
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="d:title"/>
        </xsl:otherwise>
      </xsl:choose>
    </label>
    <span>
      <xsl:apply-templates select="d:para"/>
    </span>
  </p>
</xsl:template>

<xsl:template match="d:formalpara[@role='parameter']/d:title">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="d:para[@role = 'description']">
  <p class="description">
    <label>
      <xsl:copy-of select="$description.bullet"/>
    </label>
    <span>
      <xsl:apply-templates/>
    </span>
  </p>
</xsl:template>

<!-- remark modifications -->

<xsl:template match="d:remark | d:remark[&comment.block.parents;]">
  <xsl:if test="$show.comments != 0">
    <span class="remark">
      <xsl:text>### </xsl:text>
      <xsl:apply-templates/>
    </span>
    <xsl:message>
      <xsl:text>[</xsl:text>
      <xsl:apply-templates select="ancestor::d:section[1]" mode="label.markup"/>
      <xsl:text>] ### </xsl:text>
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:message>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
