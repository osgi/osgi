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
<!-- converts OSGi's ee.xml to DocBook XML -->


<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<xsl:variable name="uppercase">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
<xsl:variable name="lowercase">abcdefghijklmnopqrstuvwxyz</xsl:variable>
<xsl:variable name="ncname1">_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz</xsl:variable>
<xsl:variable name="ncname" select="concat($ncname1, '-.0123456789')"/>
<xsl:variable name="ns">http://docbook.org/ns/docbook</xsl:variable>
<xsl:variable name="A" select="//jar[@tagged='minimum']/*"/>
<xsl:variable name="B" select="//jar[@tagged='foundation']/*"/>

<xsl:template match="/">
  <!-- discarable section container -->
  <xsl:element name="section" namespace="{$ns}">
    <xsl:attribute name="role">ee.container</xsl:attribute>
    <xsl:attribute name="version">5</xsl:attribute>

    <xsl:element name="title" namespace="{$ns}">
      <xsl:text>EE</xsl:text>
    </xsl:element>

    <saxon:group group-by="@package" select="//class">
      <xsl:sort select="@package"/>
      <xsl:variable name="package" select="@package"/>
      
      <xsl:message>
        <xsl:text>Processing ee package: </xsl:text>
        <xsl:value-of select="$package"/>
      </xsl:message>

      <xsl:element name="section" namespace="{$ns}">
        <xsl:attribute name="role">ee-package</xsl:attribute>
        <xsl:element name="title" namespace="{$ns}">
          <xsl:value-of select="$package"/>
        </xsl:element>

        <!-- generate a table for package -->
        <xsl:element name="informaltable" namespace="{$ns}">
          <xsl:attribute name="tabstyle">ee-package</xsl:attribute>
          <xsl:attribute name="frame">none</xsl:attribute>
          <xsl:attribute name="rowsep">0</xsl:attribute>
          <xsl:attribute name="colsep">0</xsl:attribute>
          <xsl:element name="tgroup" namespace="{$ns}">
            <xsl:attribute name="cols">2</xsl:attribute>
    
            <xsl:element name="colspec" namespace="{$ns}">
              <xsl:attribute name="colname">col1</xsl:attribute>
              <xsl:attribute name="colwidth">1*</xsl:attribute>
            </xsl:element>
            <xsl:element name="colspec" namespace="{$ns}">
              <xsl:attribute name="colname">col2</xsl:attribute>
              <xsl:attribute name="colwidth">1*</xsl:attribute>
            </xsl:element>
    
            <xsl:element name="tbody" namespace="{$ns}">
              <xsl:element name="row" namespace="{$ns}">
                <!-- package name first -->
                <xsl:attribute name="role">package.label</xsl:attribute>
                <xsl:element name="entry" namespace="{$ns}">
                  <xsl:attribute name="namest">col1</xsl:attribute>
                  <xsl:attribute name="nameend">col2</xsl:attribute>
                  <xsl:element name="phrase" namespace="{$ns}">
                    <xsl:attribute name="role">ee.included</xsl:attribute>
                    <xsl:value-of select="saxon:if($A//class[@package=$package],'g','c')"/>
                  </xsl:element>
                  <xsl:text> </xsl:text>
                  <xsl:element name="phrase" namespace="{$ns}">
                    <xsl:attribute name="role">ee.included</xsl:attribute>
                    <xsl:value-of select="saxon:if($B//class[@package=$package],'g','c')"/>
                  </xsl:element>
                  <xsl:text> </xsl:text>
                  <xsl:element name="phrase" namespace="{$ns}">
                    <xsl:attribute name="role">ee.package.label</xsl:attribute>
                    <xsl:text>package </xsl:text>
                    <xsl:value-of select="$package"/>
                  </xsl:element>
                </xsl:element> <!-- end of entry -->
              </xsl:element> <!-- end of row -->
    
              <!-- generate the rows for class title and methods -->
              <saxon:group group-by="@className" 
                           select="//class[@package=$package and @public]">
                <xsl:sort select="@className"/>
                <xsl:variable name="class" select="@className"/>
                <xsl:variable name="verb" 
                              select="saxon:if(@interface,' extends ',' implements ')"/>
    
                <xsl:element name="row" namespace="{$ns}">
                  <!-- class name first -->
                  <xsl:attribute name="role">ee.class.label</xsl:attribute>
                  <xsl:element name="entry" namespace="{$ns}">
                    <xsl:attribute name="namest">col1</xsl:attribute>
                    <xsl:attribute name="nameend">col2</xsl:attribute>

                    <xsl:element name="para" namespace="{$ns}">
                      <xsl:attribute name="role">ee.class.label</xsl:attribute>

                      <xsl:element name="phrase" namespace="{$ns}">
                        <xsl:attribute name="role">ee.included</xsl:attribute>
                        <xsl:value-of select="saxon:if($A//class[@className=$class],'g','c')"/>
                      </xsl:element>
                      <xsl:text> </xsl:text>
                      <xsl:element name="phrase" namespace="{$ns}">
                        <xsl:attribute name="role">ee.included</xsl:attribute>
                        <xsl:value-of select="saxon:if($B//class[@className=$class],'g','c')"/>
                      </xsl:element>
                      <xsl:text> </xsl:text>
                      <xsl:element name="phrase" namespace="{$ns}">
                        <xsl:attribute name="role">ee.class.label</xsl:attribute>
                        <xsl:call-template name="modifiers">
                          <xsl:with-param name="mods" select="@access"/>
                        </xsl:call-template>
      
                        <xsl:choose>
                          <xsl:when test="@interface"> interface </xsl:when>
                          <xsl:otherwise> class </xsl:otherwise>
                        </xsl:choose>
                        
                        <xsl:value-of select="@short"/>
                        <xsl:if test="@extends">
                          <xsl:if test="not(@extends='Object')">
                            extends <xsl:value-of select="@extends"/>
                          </xsl:if>
                        </xsl:if>
                        <xsl:for-each select="implements">
                          <xsl:if test="position()=1"> <xsl:value-of select="$verb"/> </xsl:if>
                          <xsl:if test="position()!=1">, </xsl:if>
                          <xsl:value-of select="."/>
                        </xsl:for-each>
                      </xsl:element> <!-- end of class label phrase -->
                    </xsl:element> <!-- end of class label para -->
                  </xsl:element> <!-- end of class label entry -->
                </xsl:element> <!-- end of class label row -->
    
                <!-- process the class children into a variable so they
                     can be counted and divided between two columns -->
    
                <xsl:variable name="class.children">
                  <saxon:group group-by="concat(@name,@signature)" 
                    select="//class[@className=$class]/*[@public or @protected]">
                    <xsl:sort select="not(@constructor)"/>
                    <xsl:sort select="@name"/>
                    <xsl:sort select="@signature"/>
                    
                    <xsl:variable name="name" select="@name"/>
                    <xsl:variable name="signature" select="@signature"/>
    
                    <xsl:element name="para" namespace="{$ns}">
                      <xsl:attribute name="role">ee.class.child</xsl:attribute>
    
                      <xsl:element name="phrase" namespace="{$ns}">
                        <xsl:attribute name="role">ee.included</xsl:attribute>
                        <xsl:value-of select="saxon:if($A//class[@className=$class]/*[@name=$name and @signature=$signature],'g','c')"/>
                      </xsl:element>
                      <xsl:text> </xsl:text>
                      <xsl:element name="phrase" namespace="{$ns}">
                        <xsl:attribute name="role">ee.included</xsl:attribute>
                        <xsl:value-of select="saxon:if($B//class[@className=$class]/*[@name=$name and @signature=$signature],'g','c')"/>
                      </xsl:element>
                      <xsl:text> </xsl:text>
    
                      <xsl:variable name="candidate">
                        <xsl:call-template name="modifiers">
                          <xsl:with-param name="mods" select="@access"/>
                        </xsl:call-template>
                        
                        <!-- <xsl:value-of select="substring-after(@access,'public')"/> -->
      
                        <xsl:if test="not(@constructor)">
                          <xsl:value-of select="signature/returns/@toString"/>
                          <xsl:text> </xsl:text>
                        </xsl:if>
      
                        <xsl:value-of select="@name"/>
      
                        <xsl:if test="signature/@function">
                          <xsl:text>(</xsl:text>
                          <xsl:for-each select="signature/argument">
                            <xsl:if test="position()!=1">,</xsl:if>
                            <xsl:value-of select="@toString"/>
                          </xsl:for-each>)
                        </xsl:if>
      
                        <xsl:for-each select="throws">
                          <xsl:choose>
                            <xsl:when test="position()=1"> throws&#160;</xsl:when>
                            <xsl:otherwise>, </xsl:otherwise>
                          </xsl:choose><xsl:value-of select="normalize-space(.)"/>
                        </xsl:for-each>
                      </xsl:variable>
                      <xsl:call-template name="insert.breaks">
                        <xsl:with-param name="text" select="$candidate"/>
                      </xsl:call-template>
                    </xsl:element> <!-- end of para element -->
                    <saxon:item/>
                  </saxon:group>
                </xsl:variable>
    
                <xsl:variable name="child.nodes" 
                              select="exsl:node-set($class.children/*)"/>
                <xsl:variable name="child.count" select="count($child.nodes)"/>
    
                <!-- row containing children of class element -->
                <xsl:element name="row" namespace="{$ns}">
                  <xsl:attribute name="role">ee.class.children</xsl:attribute>
                  <!-- First column  entry -->
                  <xsl:element name="entry" namespace="{$ns}">
                    <xsl:for-each select="$child.nodes">
                      <xsl:if test="position() &lt;= (($child.count + 1) div 2)">
                        <xsl:copy-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:element> <!-- end of class children first entry -->
                  <xsl:element name="entry" namespace="{$ns}">
                    <xsl:for-each select="$child.nodes">
                      <xsl:if test="position() &gt; (($child.count + 1) div 2)">
                        <xsl:copy-of select="."/>
                      </xsl:if>
                    </xsl:for-each>
                  </xsl:element> <!-- end of class children first entry -->
                </xsl:element> <!-- end of class children row -->
                <saxon:item/>
              </saxon:group> <!-- end of classes group -->
            </xsl:element> <!-- end of tbody -->
    
          </xsl:element> <!-- end of tgroup -->
        </xsl:element> <!-- end of informaltable -->
      </xsl:element> <!-- end of package section -->
      <saxon:item/>
    </saxon:group> <!-- end of group by package -->
  </xsl:element> <!-- end of discarable container section -->
</xsl:template>

<!-- Inserts zero-width space characters after commas to allow breaks -->
<xsl:template name="insert.breaks">
  <xsl:param name="text" select="''"/>
  <xsl:param name="char" select="','"/>

  <xsl:choose>
    <xsl:when test="contains($text, $char)">
      <xsl:variable name="pre" select="substring-before($text, $char)"/>
      <xsl:variable name="post" select="substring-after($text, $char)"/>
      <xsl:value-of select="$pre"/>
      <xsl:value-of select="$char"/>
      <xsl:text>&#x200B;</xsl:text>
      <!-- recursively call the template on the remaining text -->
      <xsl:call-template name="insert.breaks">
        <xsl:with-param name="text" select="$post"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <!-- we are done -->
      <xsl:value-of select="$text"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="modifiers">
  <xsl:param name="mods" select="NOTHING"/>
  <xsl:choose>
    <xsl:when test="contains($mods,'public')">
      <xsl:value-of select="concat(substring-before($mods,'public '),substring-after($mods,'public '))"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$mods"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text> </xsl:text>
</xsl:template>  
  
</xsl:stylesheet>
