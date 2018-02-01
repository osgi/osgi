<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:exsl="http://exslt.org/common"
   xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:saxon="http://icl.com/saxon"
    extension-element-prefixes="saxon" 
  exclude-result-prefixes="exsl saxon"
version="1.1">

<!-- $Id$ -->
<!-- converts output of OSGi doclet to docbook XML -->


<xsl:strip-space elements="*"/>
<xsl:output method="xml" omit-xml-declaration="yes"/>

<xsl:key name="pqn" match="package|class|method|field" use="concat(@package, '#', @qn)"/>

<xsl:param name="destdir">xml</xsl:param>

<xsl:variable name="uppercase">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
<xsl:variable name="lowercase">abcdefghijklmnopqrstuvwxyz</xsl:variable>
<xsl:variable name="ncname1">_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz</xsl:variable>
<xsl:variable name="ncname" select="concat($ncname1, '-.0123456789')"/>
<xsl:variable name="ns">http://docbook.org/ns/docbook</xsl:variable>
<!-- variant for residential spec depends on presence of this element -->
<!-- set this to 1 to select only ddf packages -->
<xsl:param name="ddf.only" select="0"/>
<xsl:template match="/">
    <xsl:apply-templates select="//package"/>
</xsl:template>

<xsl:template match="package">
  <!-- Is this a ddf package? -->
  <xsl:variable name="ddf" select="org.osgi.dmt.ddf.DDF"/>

  <xsl:if test="($ddf.only = 0 and not($ddf) ) or ($ddf.only = 1 and $ddf)">
    <xsl:variable name="package.file">
      <xsl:choose>
        <xsl:when test="$ddf">
          <xsl:value-of select="concat($destdir, '/', @name, '-ddf.xml')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat($destdir, '/', @name, '.xml')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:message>
      <xsl:choose>
        <xsl:when test="$ddf">
          <xsl:text>Processing DDF package: </xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>Generating package file: </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$package.file"/>
    </xsl:message>
  
    <xsl:document href="{$package.file}" method="xml" 
                  indent="yes" encoding="UTF-8">
      <xsl:element name="section" namespace="{$ns}">
        <xsl:attribute name="xreflabel">
            <xsl:value-of select="@name"/>
        </xsl:attribute>
        <xsl:attribute name="version">5</xsl:attribute>
        <xsl:attribute name="role">package</xsl:attribute>
        <xsl:call-template name="clean.id.att"/>

        <xsl:element name="title" namespace="{$ns}">
          <xsl:value-of select="@name"/>
        </xsl:element>

        <xsl:if test="org.osgi.annotation.versioning.Version|version">
          <xsl:variable name="version.number">
            <xsl:choose>
              <xsl:when test="org.osgi.annotation.versioning.Version">
                <xsl:apply-templates select="org.osgi.annotation.versioning.Version//value[not(./value)]" mode="html"/>
              </xsl:when>
              <xsl:when test="version">
                <xsl:apply-templates select="version" mode="html"/>
              </xsl:when>
            </xsl:choose>
          </xsl:variable>
          <xsl:variable name="version.id">
            <xsl:call-template name="clean.id">
              <xsl:with-param name="string" select="@name"/>
            </xsl:call-template>
            <xsl:text>-version</xsl:text>
          </xsl:variable>
          <xsl:variable name="version.number.id">
            <xsl:value-of select="$version.id"/>
            <xsl:text>.number</xsl:text>
          </xsl:variable>

          <xsl:element name="info" namespace="{$ns}">
            <xsl:element name="releaseinfo" namespace="{$ns}">
              <xsl:attribute name="xml:id">
                <xsl:value-of select="$version.id"/>
              </xsl:attribute>
              <xsl:text>Version </xsl:text>
              <xsl:value-of select="$version.number"/>
            </xsl:element>
            <xsl:element name="edition" namespace="{$ns}">
              <xsl:attribute name="xml:id">
                <xsl:value-of select="$version.number.id"/>
              </xsl:attribute>
              <xsl:value-of select="$version.number"/>
            </xsl:element>
          </xsl:element>
        </xsl:if>
    
        <xsl:apply-templates select=".//formattingerror"/>
  
        <xsl:if test="$ddf">
          <xsl:apply-templates select=".//remark"/>
        </xsl:if>
  
        <xsl:apply-templates select="description"/>
    
        <xsl:call-template name="descriptors">
          <xsl:with-param name="target" select="."/>
        </xsl:call-template>

        <xsl:variable name="classes" select="class[not(skip)]"/>
  
        <!-- Summary only for non-DDF output -->
        <xsl:if test="count($classes) &gt; 0 and not($ddf)">
          <xsl:element name="section" namespace="{$ns}">
            <xsl:attribute name="role">summary</xsl:attribute>
            <xsl:element name="title" namespace="{$ns}">
              <xsl:text>Summary</xsl:text>
            </xsl:element>
    
            <xsl:element name="itemizedlist" namespace="{$ns}">
              <xsl:apply-templates select="$classes" mode="index">
                <xsl:sort select="translate(@name, $uppercase, $lowercase)"/>
              </xsl:apply-templates>
            </xsl:element>
          </xsl:element>
        </xsl:if>
    
        <xsl:if test="count($classes) &gt; 0 and .//security and not($ddf)">
          <xsl:element name="section" namespace="{$ns}">
    
            <xsl:element name="title" namespace="{$ns}">
              <xsl:text>Permissions</xsl:text>
            </xsl:element>
    
            <xsl:for-each select="$classes">
              <xsl:sort select="translate(@name, $uppercase, $lowercase)"/>
              <xsl:if test="method/security">
                <xsl:element name="section" namespace="{$ns}">
                  <xsl:element name="title" namespace="{$ns}">
                    <xsl:element name="link" namespace="{$ns}">
                      <xsl:attribute name="linkend">
                        <xsl:call-template name="object.id"/>
                      </xsl:attribute>
                      <xsl:value-of select="@name"/>
                    </xsl:element> <!-- end para -->
                  </xsl:element> <!-- end para -->
  
                  <xsl:element name="itemizedlist" namespace="{$ns}">
                    <xsl:for-each select="method[security]">
                      <xsl:element name="listitem" namespace="{$ns}">
                        <xsl:element name="para" namespace="{$ns}">
                          <xsl:element name="code" namespace="{$ns}">
                            <xsl:element name="link" namespace="{$ns}">
                              <xsl:attribute name="linkend">
                                <xsl:call-template name="object.id">
                                  <xsl:with-param name="object" select="."/>
                                </xsl:call-template>
                              </xsl:attribute>
                              <xsl:value-of select="@name"/>
                              <xsl:value-of select="@flatSignature"/>
                            </xsl:element> <!-- end  link -->
                          </xsl:element> <!-- end code -->
                        </xsl:element> <!-- end para -->
      
                        <xsl:element name="itemizedlist" namespace="{$ns}">
                          <xsl:for-each select="security">
                            <xsl:element name="listitem" namespace="{$ns}">
                              <xsl:element name="para" namespace="{$ns}">
                                <xsl:element name="code" namespace="{$ns}">
                                  <xsl:value-of select="@name"/>
                                  <xsl:text>[</xsl:text>
                                  <xsl:value-of select="@resource"/>
                                  <xsl:text>,</xsl:text>
                                  <xsl:value-of select="@actions"/>
                                  <xsl:text>]</xsl:text>
                                </xsl:element>
                                <xsl:if test="text()|*">
                                  <xsl:text> - </xsl:text>
                                  <xsl:apply-templates mode="html"/>
                                </xsl:if>
                              </xsl:element> <!-- end para -->
                            </xsl:element> <!-- end listitem -->
                          </xsl:for-each>
                        </xsl:element> <!-- end itemizedlist for security -->
      
                      </xsl:element> <!-- end listitem for method -->
                    </xsl:for-each>
                  </xsl:element> <!-- end itemizedlist for method -->
                </xsl:element> <!-- end class section -->
    
              </xsl:if>
            </xsl:for-each>
          </xsl:element> <!-- end Permissions section -->
        </xsl:if>
  
        <xsl:if test="$ddf">
          <xsl:apply-templates select="remark"/>
        </xsl:if>
  
        <xsl:apply-templates select="$classes">
          <xsl:sort select="translate(@name, $uppercase, $lowercase)"/>
        </xsl:apply-templates>
  
       </xsl:element>
    </xsl:document>
  </xsl:if>
</xsl:template>

<!-- contains mixed content, some needing para wrapper -->
<xsl:template match="description">
  <xsl:apply-templates mode="description"/>
</xsl:template>

<!-- handle block elements in normal html mode -->
<xsl:template match="*[not(self::code or
                                self::a or
                                self::i or
                                self::em or
                                self::b or
                                self::strong or
                                self::br or
                                self::sup or
                                self::sub or
                                self::tt)]" mode="description">
  <xsl:apply-templates select="." mode="html"/>
</xsl:template>

<!-- inline elements must be wrapped in para. -->
<!-- match on the first node of a sequence of inline content -->
<xsl:template match="node()[self::text() or 
                                self::code or
                                self::a or
                                self::i or
                                self::em or
                                self::b or
                                self::strong or
                                self::br or
                                self::sup or
                                self::sub or
                                self::tt
                                ]
                            [not(preceding-sibling::node()[1]
                               [self::text() or 
                                self::code or
                                self::a or
                                self::i or
                                self::em or
                                self::b or
                                self::strong or
                                self::br or
                                self::sup or
                                self::sub or
                                self::tt])]"
             mode="description">

  <xsl:variable name="inline.content">
    <xsl:apply-templates select="." mode="html"/>
    <xsl:call-template name="next.paratext"/>
  </xsl:variable>

  <xsl:if test="string-length($inline.content) != 0">
    <xsl:element name="para" namespace="{$ns}">
      <xsl:if test="parent::description and ancestor::method
                    and not(preceding-sibling::node())
                    and not(ancestor-or-self::package/org.osgi.dmt.ddf.DDF)">
        <xsl:attribute name="role">description</xsl:attribute>
      </xsl:if>
      <xsl:copy-of select="$inline.content"/>
    </xsl:element>
  </xsl:if>
</xsl:template>

<!-- Subsequent inline nodes are handled recursively -->
<xsl:template match="node()[self::text() or 
                                self::code or
                                self::a or
                                self::i or
                                self::em or
                                self::b or
                                self::strong or
                                self::br or
                                self::sup or
                                self::sub or
                                self::tt
                                ]
                            [preceding-sibling::node()[1]
                               [self::text() or 
                                self::code or
                                self::a or
                                self::i or
                                self::em or
                                self::b or
                                self::strong or
                                self::br or
                                self::sup or
                                self::sub or
                                self::tt]]"
             mode="paratext">
  <xsl:apply-templates select="." mode="html"/>
  <xsl:call-template name="next.paratext"/>
</xsl:template>

<!-- subsequent inline nodes bypassed in description mode -->
<xsl:template match="node()[self::text() or 
                                self::code or
                                self::a or
                                self::i or
                                self::em or
                                self::b or
                                self::strong or
                                self::br or
                                self::sup or
                                self::sub or
                                self::tt
                                ]
                            [preceding-sibling::node()[1]
                               [self::text() or 
                                self::code or
                                self::a or
                                self::i or
                                self::em or
                                self::b or
                                self::strong or
                                self::br or
                                self::sup or
                                self::sub or
                                self::tt]]"
             mode="description"/>

<!-- recursive processing of uncontained content -->
<!--
<xsl:template match="node()" mode="paratext">
  <xsl:apply-templates select="." mode="html"/>
  <xsl:call-template name="next.paratext"/>
</xsl:template>
-->

<xsl:template name="next.paratext">
  <xsl:apply-templates select="following-sibling::node()[1]
                                [self::text() or 
                                self::code or
                                self::a or
                                self::i or
                                self::em or
                                self::b or
                                self::strong or
                                self::br or
                                self::sup or
                                self::sub or
                                self::tt]"
                      mode="paratext"/>
</xsl:template>

<xsl:template match="version" mode="html">
  <xsl:apply-templates mode="html"/>
</xsl:template>

<xsl:template match="p" mode="html">
  <xsl:element name="para" namespace="{$ns}">
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="lead/p" mode="html">
  <xsl:apply-templates mode="html"/>
</xsl:template>

<xsl:template match="ul" mode="html">
  <xsl:element name="itemizedlist" namespace="{$ns}">
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="ol" mode="html">
    <xsl:element name="orderedlist" namespace="{$ns}">
      <xsl:apply-templates mode="html"/>
     </xsl:element>
</xsl:template>

<xsl:template match="li" mode="html">
    <xsl:element name="listitem" namespace="{$ns}">
       <xsl:apply-templates mode="description"/>
     </xsl:element>
</xsl:template>

<xsl:template match="pre" mode="html">
    <xsl:element name="programlisting" namespace="{$ns}">
      <xsl:apply-templates mode="html"/>
     </xsl:element>
</xsl:template>

<xsl:template match="pre/br" mode="html">
  <!-- line break in programlisting -->
  <xsl:text>&#x0A;</xsl:text>
</xsl:template>

<xsl:template match="pre/text()" mode="html">
  <xsl:value-of select="translate(., '&#x2002;', ' ')"/>
</xsl:template>

<xsl:template match="tt|code" mode="html">
  <xsl:element name="code" namespace="{$ns}">
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="i|em" mode="html">
  <xsl:element name="emphasis" namespace="{$ns}">
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="b|strong" mode="html">
  <xsl:element name="emphasis" namespace="{$ns}">
    <xsl:attribute name="role">strong</xsl:attribute>
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="sup" mode="html">
  <xsl:element name="superscript" namespace="{$ns}">
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="sub" mode="html">
  <xsl:element name="subscript" namespace="{$ns}">
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<!-- copy html tables through to DocBook -->
<xsl:template match="table[caption]" mode="html">
  <xsl:element name="table" namespace="{$ns}">
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="table[not(caption)]" mode="html">
  <xsl:element name="informaltable" namespace="{$ns}">
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="thead|tbody|tfoot|tr|td|th|col|colgroup|caption" mode="html">
  <xsl:element name="{local-name()}" namespace="{$ns}">
    <xsl:copy-of select="@*[local-name() != 'width']"/>
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="a[@href]" mode="html">
  <xsl:choose>
    <!-- external link -->
    <xsl:when test="starts-with(@href, 'http:') or starts-with(@href, 'https:')">
      <xsl:element name="link" namespace="{$ns}">
        <xsl:attribute name="xlink:href">
          <xsl:value-of select="@href"/>
        </xsl:attribute>
        <xsl:apply-templates mode="html"/>
      </xsl:element>
    </xsl:when>
    <!-- internal link -->
    <xsl:when test="contains(@href, '#')">
      <xsl:variable name="key">
        <xsl:choose>
          <!-- link within the same package --> 
          <xsl:when test="starts-with(@href, '#')">
            <xsl:value-of select="concat(ancestor::package/@qn, @href)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@href"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="target" select="key('pqn', $key)"/>

      <xsl:variable name="linkend">
        <xsl:choose>
          <xsl:when test="count($target) = 0 and starts-with(@href, 'java')">
            <!-- external link is not active -->
          </xsl:when>
          <xsl:when test="count($target) = 0">
            <xsl:message>
              <xsl:text>ERROR: unresolved href: '</xsl:text>
              <xsl:value-of select="@href"/>
              <xsl:text>' in class '</xsl:text>
              <xsl:value-of select="ancestor::class/@qn"/>
              <xsl:text>' in package '</xsl:text>
              <xsl:value-of select="ancestor::package/@qn"/>
              <xsl:text>'.</xsl:text>
            </xsl:message>
            <xsl:text>UNRESOLVED</xsl:text>
          </xsl:when>
          <xsl:when test="count($target) = 1">
            <xsl:call-template name="object.id">
              <xsl:with-param name="object" select="$target"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="count($target) &gt; 1">
            <xsl:message>
              <xsl:text>WARNING: href '</xsl:text>
              <xsl:value-of select="@href"/>
              <xsl:text>' in class '</xsl:text>
              <xsl:value-of select="ancestor::class/@qn"/>
              <xsl:text>' in package '</xsl:text>
              <xsl:value-of select="ancestor::package/@qn"/>
              <xsl:text>' matches more than one element. Using the first.</xsl:text>
            </xsl:message>
            <xsl:call-template name="object.id">
              <xsl:with-param name="object" select="$target[1]"/>
            </xsl:call-template>
          </xsl:when>
        </xsl:choose>
      </xsl:variable>

      <xsl:choose>
        <xsl:when test="string-length($linkend) != 0">
          <xsl:element name="link" namespace="{$ns}">
            <xsl:attribute name="linkend">
              <xsl:value-of select="$linkend"/>
            </xsl:attribute>
            <xsl:apply-templates mode="html"/>
          </xsl:element>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates mode="html"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <!-- Not sure what to do in this case -->
      <xsl:message>
        <xsl:text>WARNING: href '</xsl:text>
        <xsl:value-of select="@href"/>
        <xsl:text>' has unrecognized syntax.</xsl:text>
      </xsl:message>
      <xsl:variable name="target" select="@href"/>
      <xsl:element name="link" namespace="{$ns}">
        <xsl:attribute name="linkend">
          <xsl:call-template name="clean.id">
            <xsl:with-param name="string" select="@href"/>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:apply-templates mode="html"/>
      </xsl:element>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="a[@name]" mode="html">
  <xsl:element name="anchor" namespace="{$ns}">
    <xsl:attribute name="xml:id">
      <xsl:call-template name="clean.id">
        <xsl:with-param name="string" select="@name"/>
      </xsl:call-template>
    </xsl:attribute>
  </xsl:element>
</xsl:template>

<!-- Omit <a index=""> -->
<xsl:template match="a[@index]" mode="html"/>

<xsl:template match="text()" mode="html">
  <xsl:copy/>
</xsl:template>

<!-- the package/lead element is ignored, only class lead is used -->
<xsl:template match="package/lead"/>

<xsl:template match="lead" mode="html">
  <xsl:apply-templates mode="html"/>
</xsl:template>

<xsl:template match="class" mode="index">
  <xsl:element name="listitem" namespace="{$ns}">
    <xsl:element name="para" namespace="{$ns}">
      <xsl:element name="link" namespace="{$ns}">
        <xsl:attribute name="linkend">
          <xsl:call-template name="object.id"/>
        </xsl:attribute>
        <xsl:element name="code" namespace="{$ns}">
          <xsl:value-of select="@name"/>
        </xsl:element>
      </xsl:element>
      <xsl:if test="lead">
        <xsl:text> - </xsl:text>
      </xsl:if>
      <xsl:apply-templates select="lead" mode="html"/>
    </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template match="class">
  <xsl:variable name="ddf" select="ancestor-or-self::package/org.osgi.dmt.ddf.DDF"/>
  <xsl:variable name="package.id" select="ancestor::package/@name"/>
  <xsl:choose>
    <xsl:when test="skip">
      <!-- Skipping this class because it has the skip tag -->
    </xsl:when>
    <xsl:otherwise>
      <xsl:element name="section" namespace="{$ns}">
        <xsl:attribute name="xreflabel">
          <xsl:value-of select="@name"/>
        </xsl:attribute>
        <xsl:attribute name="role">class</xsl:attribute>
        <xsl:call-template name="clean.id.att"/>
        <xsl:element name="title" namespace="{$ns}">
          <xsl:choose>
            <xsl:when test="$ddf">
              <xsl:value-of select="@name"/>
            </xsl:when>
            <xsl:when test="@kind='ENUM'">
              <xsl:text>enum </xsl:text>
              <xsl:value-of select="@qn" />
            </xsl:when>
            <xsl:when test="@kind='ANNOTATION'">
              <xsl:text>@</xsl:text>
              <xsl:value-of select="@qn" />
            </xsl:when>
            <xsl:when test="@kind='INTERFACE'">
              <xsl:value-of select="@modifiers" />
              <xsl:text> </xsl:text>
              <xsl:value-of select="@name" />
              <xsl:value-of select="@typeParam" />
              <xsl:for-each select="implements[@local]">
                <xsl:choose>
                  <xsl:when test="position()=1">
                    <xsl:processing-instruction name="line-break"/>
                    <xsl:text> extends </xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>, </xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:value-of select="@qn" />
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@modifiers" />
              <xsl:text> </xsl:text>
              <xsl:value-of select="@name" />
              <xsl:value-of select="@typeParam" />
              <xsl:if test="@superclass and @superclass!='Object'">
                <xsl:processing-instruction name="line-break"/>
                <xsl:text> extends </xsl:text>
                <xsl:value-of select="@superclass" />
              </xsl:if>
              <xsl:for-each select="implements[@local]">
                <xsl:choose>
                  <xsl:when test="position()=1">
                    <xsl:processing-instruction name="line-break"/>
                    <xsl:text> implements </xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>, </xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:value-of select="@qn" />
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>

        <xsl:if test="not($ddf)">
          <xsl:for-each select="param">
            <xsl:element name="formalpara" namespace="{$ns}">
              <xsl:attribute name="role">parameter</xsl:attribute>
              <xsl:element name="title" namespace="{$ns}">
                <xsl:value-of select="@name" />
              </xsl:element>
              <!-- may contain <p> after first text  -->
              <xsl:element name="para" namespace="{$ns}">
                <xsl:apply-templates select="node()[not(self::p)]" mode="html"/>
              </xsl:element>
            </xsl:element>
            <xsl:apply-templates select="p" mode="html"/>
          </xsl:for-each>
        </xsl:if>

        <xsl:if test="$ddf">
          <xsl:apply-templates select="remark"/> 
        </xsl:if>

        <xsl:apply-templates select="description"/> 

        <xsl:call-template name="descriptors">
          <xsl:with-param name="target" select="." />
        </xsl:call-template>

        <xsl:choose>
          <xsl:when test="$ddf">
            <xsl:apply-templates select="field"/>
            <!-- normally the last field includes this table in its section -->
            <xsl:if test="not(field)">
              <xsl:call-template name="ddf.subtree.table"/>
            </xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="@kind='ENUM'">
                <xsl:if test="field">
                  <xsl:apply-templates select="field[not(skip)]" mode="enum"/>
                </xsl:if>          
              </xsl:when>
              <xsl:when test="@kind='ANNOTATION'">
                <xsl:apply-templates select="method[not(@isConstructor) and not(skip)]" 
                                     mode="annotation"/>
                <xsl:if test="field">
                  <xsl:apply-templates select="field[not(skip)]" mode="annotation"/>
                </xsl:if>          
              </xsl:when>
              <xsl:otherwise>
                <xsl:if test="field">
                  <xsl:apply-templates select="field[not(skip)]">
                    <xsl:sort select="translate(@name, $uppercase, $lowercase)"/>
                  </xsl:apply-templates>
                </xsl:if>
                <xsl:if test="method[@isConstructor]">
                  <xsl:apply-templates select="method[@isConstructor and not(skip)]" />
                </xsl:if>
                <xsl:if test="method[not(@isConstructor)]">
                  <xsl:apply-templates select="method[not(@isConstructor) and not(skip)]">
                    <xsl:sort select="translate(@name, $uppercase, $lowercase)"/>
                  </xsl:apply-templates>
                </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="ddf.subtree.table">
  <xsl:if test="ancestor-or-self::class/method">
    <xsl:element name="table" namespace="{$ns}">
      <xsl:attribute name="tabstyle">ddfsubtree</xsl:attribute>
      <xsl:attribute name="pgwide">1</xsl:attribute>
      <xsl:element name="title" namespace="{$ns}">
        <xsl:text>Sub-tree Description for </xsl:text>
        <xsl:value-of select="ancestor-or-self::class/@name"/>
      </xsl:element>
      <xsl:element name="tgroup" namespace="{$ns}">
        <xsl:attribute name="cols">6</xsl:attribute>
        <xsl:element name="colspec" namespace="{$ns}">
          <xsl:attribute name="colwidth">1*</xsl:attribute>
        </xsl:element>
        <xsl:element name="colspec" namespace="{$ns}">
          <xsl:attribute name="colwidth">0.6*</xsl:attribute>
        </xsl:element>
        <xsl:element name="colspec" namespace="{$ns}">
          <xsl:attribute name="colwidth">1.5*</xsl:attribute>
        </xsl:element>
        <xsl:element name="colspec" namespace="{$ns}">
          <xsl:attribute name="colwidth">0.5*</xsl:attribute>
        </xsl:element>
        <xsl:element name="colspec" namespace="{$ns}">
          <xsl:attribute name="colwidth">0.2*</xsl:attribute>
        </xsl:element>
        <xsl:element name="colspec" namespace="{$ns}">
          <xsl:attribute name="colwidth">2.5*</xsl:attribute>
        </xsl:element>

        <xsl:element name="thead" namespace="{$ns}">
  
          <xsl:element name="row" namespace="{$ns}">
            <xsl:element name="entry" namespace="{$ns}">
              <xsl:text>Name</xsl:text>
            </xsl:element>
            <xsl:element name="entry" namespace="{$ns}">
              <xsl:text>Act</xsl:text>
            </xsl:element>
            <xsl:element name="entry" namespace="{$ns}">
              <xsl:text>Type</xsl:text>
            </xsl:element>
            <xsl:element name="entry" namespace="{$ns}">
              <xsl:text>Card.</xsl:text>
            </xsl:element>
            <xsl:element name="entry" namespace="{$ns}">
              <xsl:text>S</xsl:text>
            </xsl:element>
            <xsl:element name="entry" namespace="{$ns}">
              <xsl:text>Description</xsl:text>
            </xsl:element>
          </xsl:element>
        </xsl:element>
        <xsl:element name="tbody" namespace="{$ns}">
          <xsl:apply-templates select="ancestor-or-self::class/method" mode="ddf"/>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:if>
</xsl:template>

<xsl:template match="field">
  <xsl:variable name="ddf" select="ancestor-or-self::package/org.osgi.dmt.ddf.DDF"/>
  <xsl:variable name="package.id" select="ancestor::package/@name"/>
  <xsl:element name="section" namespace="{$ns}">
    <xsl:attribute name="xreflabel">
      <xsl:value-of select="@name"/>
    </xsl:attribute>
    <xsl:attribute name="role">field</xsl:attribute>
    <xsl:call-template name="clean.id.att"/>
    <xsl:element name="title" namespace="{$ns}">
      <xsl:choose>
        <xsl:when test="$ddf">
          <xsl:value-of select="@name"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat(@modifiers,' ',@typeName,@dimension,' ', @name)" />
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="@constantValue">
        <xsl:text> = </xsl:text>
        <xsl:value-of select="@constantValue" />
        <xsl:if test="string(number(@constantValue))='NaN'">
          <xsl:variable name="index.anchor"
             select="substring(@constantValue,2,string-length(@constantValue)-2)" />
          <!--
          <xsl:comment>
             <xsl:value-of select="$index.anchor"/>
          </xsl:comment>
          -->
        </xsl:if>
      </xsl:if>
      <xsl:if test="not(@constantValue) and value">
        <xsl:text> = </xsl:text>
        <xsl:value-of select="value" />
      </xsl:if>
    </xsl:element>

    <xsl:apply-templates select="description" />

    <xsl:call-template name="descriptors">
      <xsl:with-param name="target" select="." />
    </xsl:call-template>

    <!-- Include this table in the last field section -->
    <xsl:if test="$ddf and not(following-sibling::field)">
      <xsl:call-template name="ddf.subtree.table"/>
    </xsl:if>

  </xsl:element>
</xsl:template>

<xsl:template match="field" mode="annotation">
  <xsl:variable name="package.id" select="ancestor::package/@name"/>
  <xsl:element name="section" namespace="{$ns}">
    <xsl:attribute name="xreflabel">
      <xsl:value-of select="@name"/>
    </xsl:attribute>
    <xsl:attribute name="role">field</xsl:attribute>
    <xsl:call-template name="clean.id.att"/>
    <xsl:element name="title" namespace="{$ns}">
      <xsl:value-of select="concat(@typeName,@dimension,' ', @name)" />
      <xsl:if test="@constantValue">
        <xsl:text> = </xsl:text>
        <xsl:value-of select="@constantValue" />
        <xsl:if test="string(number(@constantValue))='NaN'">
          <xsl:variable name="index.anchor"
             select="substring(@constantValue,2,string-length(@constantValue)-2)" />
          <!--
          <xsl:comment>
             <xsl:value-of select="$index.anchor"/>
          </xsl:comment>
          -->
        </xsl:if>
      </xsl:if>
      <xsl:if test="not(@constantValue) and value">
        <xsl:text> = </xsl:text>
        <xsl:value-of select="value" />
      </xsl:if>
    </xsl:element>

    <xsl:apply-templates select="description" />

    <xsl:call-template name="descriptors">
      <xsl:with-param name="target" select="." />
    </xsl:call-template>
  </xsl:element>
</xsl:template>

<xsl:template match="field" mode="enum">
  <xsl:element name="section" namespace="{$ns}">
    <xsl:attribute name="xreflabel">
        <xsl:value-of select="@name"/>
    </xsl:attribute>
    <xsl:attribute name="role">field</xsl:attribute>
    <xsl:call-template name="clean.id.att"/>

    <xsl:element name="title" namespace="{$ns}">
      <xsl:value-of select="@name" />
    </xsl:element>

    <xsl:apply-templates select="description" />

    <xsl:call-template name="descriptors">
      <xsl:with-param name="target" select="." />
    </xsl:call-template>

  </xsl:element>
</xsl:template>

<xsl:template match="method">
  <xsl:element name="section" namespace="{$ns}">
    <xsl:attribute name="xreflabel">
        <xsl:value-of select="concat(@name,@flatSignature)"/>
    </xsl:attribute>
    <xsl:attribute name="role">method</xsl:attribute>
    <xsl:call-template name="clean.id.att"/>
    <xsl:element name="title" namespace="{$ns}">
      <xsl:value-of select="concat(@modifiers,' ',@typeName,@dimension,' ', @name)" />
      <xsl:text>(</xsl:text>
      <xsl:apply-templates select="parameter" mode="head" />
      <xsl:text>)</xsl:text>
      <xsl:for-each select="exception">
        <xsl:if test="position()=1">
          <xsl:text> throws </xsl:text>
        </xsl:if>
        <xsl:if test="position()!=1">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:value-of select="@name" />
      </xsl:for-each>
    </xsl:element>

    <xsl:if test="normalize-space(@typeArgs)">
      <xsl:element name="formalpara" namespace="{$ns}">
        <xsl:attribute name="role">parameter</xsl:attribute>
        <xsl:element name="title" namespace="{$ns}">
          <xsl:text>Type Parameters</xsl:text>
        </xsl:element>
        <xsl:element name="para" namespace="{$ns}">
          <xsl:element name="code" namespace="{$ns}">
            <xsl:value-of select="normalize-space(@typeArgs)" />
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:if>

    <xsl:for-each select="param">
      <xsl:element name="formalpara" namespace="{$ns}">
        <xsl:attribute name="role">parameter</xsl:attribute>
        <xsl:element name="title" namespace="{$ns}">
          <xsl:value-of select="@name" />
        </xsl:element>
        <!-- may contain <p> after first text  -->
        <xsl:element name="para" namespace="{$ns}">
          <xsl:apply-templates select="node()[not(self::p)]" mode="html"/>
        </xsl:element>
      </xsl:element>
      <xsl:apply-templates select="p" mode="html"/>
    </xsl:for-each>

    <xsl:if test="normalize-space(description)">
      <xsl:apply-templates select="description"/>
    </xsl:if>

    <xsl:for-each select="return">
      <xsl:element name="formalpara" namespace="{$ns}">
        <xsl:attribute name="role">parameter</xsl:attribute>
        <xsl:element name="title" namespace="{$ns}">
          <xsl:text>Returns</xsl:text>
        </xsl:element>
        <xsl:element name="para" namespace="{$ns}">
          <xsl:apply-templates mode="html" />
        </xsl:element>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="throws">
      <xsl:element name="formalpara" namespace="{$ns}">
        <xsl:attribute name="role">parameter</xsl:attribute>
        <xsl:element name="title" namespace="{$ns}">
          <xsl:text>Throws</xsl:text>
        </xsl:element>
        <xsl:element name="para" namespace="{$ns}">
          <xsl:element name="code" namespace="{$ns}">
            <xsl:value-of select="@name" />
          </xsl:element>
          <xsl:text>–&#160;</xsl:text>
          <xsl:apply-templates mode="html" />
        </xsl:element>
      </xsl:element>
    </xsl:for-each>

    <xsl:for-each select="security">
      <xsl:element name="formalpara" namespace="{$ns}">
        <xsl:attribute name="role">parameter</xsl:attribute>
        <xsl:element name="title" namespace="{$ns}">
          <xsl:if test="position()=1">
            <xsl:text>Security</xsl:text>
          </xsl:if>
        </xsl:element>
        <xsl:element name="para" namespace="{$ns}">
          <xsl:element name="code" namespace="{$ns}">
            <xsl:value-of select="@name"/>
            <xsl:text>[</xsl:text>
            <xsl:value-of select="@resource"/>
            <xsl:text>,</xsl:text>
            <xsl:value-of select="@actions"/>
            <xsl:text>]</xsl:text>]
          </xsl:element>
          <xsl:text>–&#160;</xsl:text>
          <xsl:apply-templates mode="html" />
        </xsl:element>
      </xsl:element>
    </xsl:for-each>

    <xsl:call-template name="descriptors">
      <xsl:with-param name="target" select="." />
    </xsl:call-template>

    <!-- If method is empty, add an empty para to make the section valid -->
    <xsl:if test="string-length(normalize-space(*)) = 0">
      <xsl:element name="para" namespace="{$ns}"/>
    </xsl:if>

  </xsl:element>
</xsl:template>


<xsl:template match="method" mode="annotation">
  <xsl:element name="section" namespace="{$ns}">
    <xsl:attribute name="xreflabel">
        <xsl:value-of select="@name"/>
    </xsl:attribute>
    <xsl:attribute name="role">method</xsl:attribute>
    <xsl:call-template name="clean.id.att"/>

    <xsl:element name="title" namespace="{$ns}">
      <xsl:value-of select="concat(@typeName,@dimension,' ', @name)" />
      <xsl:if test="@default">
        <xsl:text> default </xsl:text>
        <xsl:value-of select="@default"/>
      </xsl:if>
    </xsl:element>

    <xsl:if test="normalize-space(description)">
      <xsl:apply-templates select="description"/>
    </xsl:if>

    <xsl:for-each select="return">
      <xsl:element name="formalpara" namespace="{$ns}">
        <xsl:attribute name="role">parameter</xsl:attribute>
        <xsl:element name="title" namespace="{$ns}">
          <xsl:text>Returns</xsl:text>
        </xsl:element>
        <xsl:element name="para" namespace="{$ns}">
          <xsl:apply-templates mode="html" />
        </xsl:element>
      </xsl:element>
    </xsl:for-each>

    <xsl:call-template name="descriptors">
      <xsl:with-param name="target" select="." />
    </xsl:call-template>

    <!-- If method is empty, add an empty para to make the section valid -->
    <xsl:if test="string-length(normalize-space(*)) = 0">
      <xsl:element name="para" namespace="{$ns}"/>
    </xsl:if>

  </xsl:element>
</xsl:template>

<!-- In ddf mode, methods generate table rows in a ddf subtree table-->
<xsl:template match="method" mode="ddf">
  <!-- ddf name='name' indent='' add='false' get='true' replace='false' delete='false' 
    longTypeName='java.lang.String' shortTypeName='string' cardinality='1' scope='P' 
    interior='false' mime=''/ -->
  <xsl:variable name="method" select="."/>
  <xsl:variable name="description" select="description" />
  <xsl:variable name="count" select="count(ddf)" />

  <xsl:for-each select="ddf">
    <xsl:element name="row" namespace="{$ns}">
      <!-- Name column -->
      <xsl:element name="entry" namespace="{$ns}">
        <xsl:element name="para" namespace="{$ns}">
          <xsl:if test="$method/@name = @name">
            <xsl:attribute name="xreflabel">
              <xsl:value-of select="@name"/>
            </xsl:attribute>
            <xsl:call-template name="clean.id.att">
              <xsl:with-param name="object" select="$method"/>
            </xsl:call-template>
          </xsl:if>
          <xsl:element name="code" namespace="{$ns}">
            <xsl:value-of select="@indent"/> 
            <xsl:value-of select="@name"/> 
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- Act column -->
      <xsl:element name="entry" namespace="{$ns}">
        <xsl:element name="code" namespace="{$ns}">
          <xsl:if test="@add = 'true'">
            <xsl:text>Add </xsl:text>
          </xsl:if>
          <xsl:if test="@delete = 'true'">
            <xsl:text>Del </xsl:text>
          </xsl:if>
          <xsl:if test="@get = 'true'">
            <xsl:text>Get </xsl:text>
          </xsl:if>
          <xsl:if test="@replace = 'true'">
            <xsl:text>Set </xsl:text>
          </xsl:if>
        </xsl:element>
      </xsl:element>
      <!-- Type column -->
      <xsl:element name="entry" namespace="{$ns}">
        <xsl:element name="code" namespace="{$ns}">
          <xsl:value-of select="@shortTypeName"/>
        </xsl:element>
      </xsl:element>
      <!-- Card column -->
      <xsl:element name="entry" namespace="{$ns}">
        <xsl:element name="code" namespace="{$ns}">
          <xsl:value-of select="@cardinality"/>
        </xsl:element>
      </xsl:element>
      <!-- S column -->
      <xsl:element name="entry" namespace="{$ns}">
        <xsl:element name="code" namespace="{$ns}">
          <xsl:value-of select="@scope"/>
        </xsl:element>
      </xsl:element>
      <!-- Description column is from first method, spans down -->
      <xsl:if test="position() = 1">
        <xsl:element name="entry" namespace="{$ns}">
          <xsl:if test="$count &gt; 1">
            <xsl:attribute name="morerows">
              <xsl:value-of select="$count - 1"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:apply-templates select="$description"/>
        </xsl:element>
      </xsl:if>
    </xsl:element>
  </xsl:for-each>
</xsl:template>

<xsl:template match="remark">
  <xsl:element name="remark" namespace="{$ns}">
    <xsl:apply-templates mode="html"/>
  </xsl:element>
</xsl:template>

<xsl:template match="parameter" mode="head">
  <xsl:if test="position()!=1">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <xsl:value-of select="@typeName" />
  <xsl:value-of select="@dimension" />
  <xsl:text> </xsl:text>
  <xsl:value-of select="@name" />
</xsl:template>

<xsl:template name="clean.id.att">
  <xsl:param name="object" select="."/>

  <xsl:variable name="clean.id">
    <xsl:call-template name="object.id">
      <xsl:with-param name="object" select="$object"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:if test="string-length($clean.id) != 0">
    <xsl:attribute name="xml:id">
      <xsl:value-of select="$clean.id"/>
    </xsl:attribute>
  </xsl:if>

</xsl:template>

<xsl:template name="object.id">
  <xsl:param name="object" select="."/>

  <xsl:variable name="candidate">
    <xsl:choose>
      <xsl:when test="$object/self::package">
        <xsl:value-of select="$object/@qn"/>
      </xsl:when>
      <xsl:when test="$object/self::class">
        <xsl:value-of select="concat($object/@package, '.', $object/@qn)"/>
      </xsl:when>
      <xsl:when test="$object/self::method">
        <xsl:value-of select="concat($object/@package, '.', $object/@qn)"/>
      </xsl:when>
      <xsl:when test="$object/self::field">
        <xsl:value-of select="concat($object/@package, '.', $object/@qn)"/>
      </xsl:when>
      <xsl:otherwise>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:call-template name="clean.id">
    <xsl:with-param name="string" select="$candidate"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="clean.id">
  <xsl:param name="string"/>

  <!-- replace parens and comma with underscores -->
  <xsl:variable name="clean1" select="translate($string, ':[]{}(),$@ #', '-----------')"/>

  <xsl:variable name="first" select="substring($clean1, 1, 1)"/>

  <xsl:variable name="clean2">
    <xsl:choose>
      <xsl:when test="contains($ncname1, $first)">
        <xsl:value-of select="$clean1"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat('_', $clean1)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="starts-with($string, 'http:')">
      <!-- pass through absolute URLs -->
      <xsl:value-of select="$string"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$clean2"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="descriptors">
  <xsl:param name="target" select="."/>
  <xsl:variable name="ddf" select="$target/ancestor-or-self::package/org.osgi.dmt.ddf.DDF"/>

  <xsl:if test="$target/a">
    <xsl:element name="formalpara" namespace="{$ns}">
      <xsl:attribute name="role">parameter</xsl:attribute>
      <xsl:element name="title" namespace="{$ns}">
        <xsl:text>See Also</xsl:text>
      </xsl:element>
      <xsl:element name="para" namespace="{$ns}">
        <xsl:for-each select="$target/a">
          <xsl:if test="position() != 1">
            <xsl:text>, </xsl:text>
          </xsl:if>
          <xsl:choose>
            <xsl:when test="@href">
              <xsl:apply-templates select="." mode="html"/>
              <!--
              <xsl:choose>
                <xsl:when test="starts-with(@href, 'http:')">
                  <xsl:element name="link" namespace="{$ns}">
                    <xsl:attribute name="xlink:href">
                      <xsl:value-of select="@href"/>
                    </xsl:attribute>
                    <xsl:apply-templates mode="html"/>
                  </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:element name="link" namespace="{$ns}">
                    <xsl:attribute name="linkend">
                      <xsl:call-template name="clean.id">
                        <xsl:with-param name="string" select="@href"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:element name="code" namespace="{$ns}">
                      <xsl:value-of select="."/>
                    </xsl:element>
                  </xsl:element>
                </xsl:otherwise>
              </xsl:choose>
              -->
            </xsl:when>
            <xsl:otherwise>
              <xsl:element name="code" namespace="{$ns}">
                <xsl:value-of select="."/>
              </xsl:element>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </xsl:element>
    </xsl:element>
  </xsl:if>
    
  <xsl:for-each select="$target/since">
    <xsl:element name="formalpara" namespace="{$ns}">
      <xsl:attribute name="role">parameter</xsl:attribute>
      <xsl:element name="title" namespace="{$ns}">
        <xsl:text>Since</xsl:text>
      </xsl:element>
      <xsl:element name="para" namespace="{$ns}">
        <xsl:apply-templates mode="html" />
      </xsl:element>
    </xsl:element>
  </xsl:for-each>

  <xsl:for-each select="$target/deprecated">
    <xsl:element name="formalpara" namespace="{$ns}">
      <xsl:attribute name="role">parameter</xsl:attribute>
      <xsl:element name="title" namespace="{$ns}">
        <xsl:text>Deprecated</xsl:text>
      </xsl:element>
      <xsl:element name="para" namespace="{$ns}">
        <xsl:apply-templates mode="html" />
      </xsl:element>
    </xsl:element>
  </xsl:for-each>

  <xsl:if test="not($ddf) and 
               ($target/ThreadSafe|
                $target/GuardedBy|
                $target/Immutable|
                $target/NotThreadSafe)">
    <xsl:element name="formalpara" namespace="{$ns}">
      <xsl:attribute name="role">parameter</xsl:attribute>
      <xsl:element name="title" namespace="{$ns}">
        <xsl:text>Concurrency</xsl:text>
      </xsl:element>
      <xsl:element name="para" namespace="{$ns}">
        <xsl:if test="$target/ThreadSafe">
          <xsl:text>Thread-safe</xsl:text>
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:if test="$target/GuardedBy">
          <xsl:text>Guarded by: </xsl:text>
          <xsl:value-of select="GuardedBy" />
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:if test="$target/Immutable">
          <xsl:text>Immutable</xsl:text>
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:if test="$target/NotThreadSafe">
          <xsl:text>Not Thread-safe</xsl:text>
          <xsl:text> </xsl:text>
        </xsl:if>
      </xsl:element>
    </xsl:element>
  </xsl:if>

  <xsl:if test="not($ddf) and 
               ($target/org.osgi.annotation.versioning.ProviderType|
                $target/noimplement)">
    <xsl:element name="formalpara" namespace="{$ns}">
      <xsl:attribute name="role">parameter</xsl:attribute>
      <xsl:choose>
        <xsl:when test="$target/org.osgi.annotation.versioning.ProviderType">
          <xsl:element name="title" namespace="{$ns}">
            <xsl:text>Provider Type</xsl:text>
          </xsl:element>
          <xsl:element name="para" namespace="{$ns}">
            <xsl:text>Consumers of this API must not implement this type</xsl:text>
          </xsl:element>
        </xsl:when>
        <xsl:when test="$target/noimplement">
          <xsl:element name="title" namespace="{$ns}">
            <xsl:text>No Implement</xsl:text>
          </xsl:element>
          <xsl:element name="para" namespace="{$ns}">
            <xsl:text>Consumers of this API must not implement this interface</xsl:text>
          </xsl:element>
        </xsl:when>
      </xsl:choose>
    </xsl:element>
  </xsl:if>

  <xsl:if test="not($ddf) and $target/java.lang.annotation.Retention">
    <xsl:element name="formalpara" namespace="{$ns}">
      <xsl:attribute name="role">parameter</xsl:attribute>
      <xsl:element name="title" namespace="{$ns}">
        <xsl:text>Retention</xsl:text>
      </xsl:element>
      <xsl:element name="para" namespace="{$ns}">
        <xsl:for-each 
             select="$target/java.lang.annotation.Retention//value[not(./value)]">
          <xsl:if test="not(position()=1)">
            <xsl:text>, </xsl:text>
          </xsl:if>
          <xsl:element name="code" namespace="{$ns}">
            <xsl:value-of select="."/>
          </xsl:element>
        </xsl:for-each>
      </xsl:element>
    </xsl:element>
  </xsl:if>

  <xsl:if test="not($ddf) and $target/java.lang.annotation.Target">
    <xsl:element name="formalpara" namespace="{$ns}">
      <xsl:attribute name="role">parameter</xsl:attribute>
      <xsl:element name="title" namespace="{$ns}">
        <xsl:text>Target</xsl:text>
      </xsl:element>
      <xsl:element name="para" namespace="{$ns}">
        <xsl:for-each select="$target/java.lang.annotation.Target//value[not(./value)]">
          <xsl:if test="not(position()=1)">
            <xsl:text>, </xsl:text>
          </xsl:if>
          <xsl:element name="code" namespace="{$ns}">
            <xsl:value-of select="."/>
          </xsl:element>
        </xsl:for-each>
      </xsl:element>
    </xsl:element>
  </xsl:if>

</xsl:template>

<xsl:template match="formattingerror">
  <remark>
    <xsl:text>Formatting error in </xsl:text>
    <xsl:value-of select="@file"/>
    <xsl:text>#</xsl:text>
    <xsl:value-of select="@line"/>
    <xsl:text>:</xsl:text>
    <xsl:value-of select="@msg"/>
  </remark>
  <xsl:message terminate="yes">
    <xsl:text>Formatting error in </xsl:text>
    <xsl:value-of select="@file"/>
    <xsl:text>#</xsl:text>
    <xsl:value-of select="@line"/>
    <xsl:text>:</xsl:text>
    <xsl:value-of select="@msg"/>
  </xsl:message>
</xsl:template>

</xsl:stylesheet>
