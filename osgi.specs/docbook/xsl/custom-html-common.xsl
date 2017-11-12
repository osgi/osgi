<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:d="http://docbook.org/ns/docbook"
    xmlns:db="http://docbook.org/ns/docbook"
    xmlns:exsl="http://exslt.org/common"
    xmlns:ng="http://docbook.org/docbook-ng"
    xmlns:redirect="http://xml.apache.org/xalan/redirect"
    xmlns:saxon="http://icl.com/saxon"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="d db exsl ng redirect saxon"
    version="1.0">

    <xsl:param name="chunker.output.method">
        <xsl:choose>
            <xsl:when test="contains(system-property('xsl:vendor'), 'SAXON 6')">saxon:xhtml</xsl:when>
            <xsl:otherwise>html</xsl:otherwise>
        </xsl:choose>
    </xsl:param>

    <xsl:param name="doc.title">
      <xsl:call-template name="get.doc.title"/>
    </xsl:param>

    <!-- Set some reasonable defaults for webhelp output -->
    <xsl:param name="webhelp.common.dir"></xsl:param>
    <xsl:param name="img.src.path" select="concat($webhelp.common.dir,'images/')" />
    <xsl:param name="chunker.output.indent">yes</xsl:param>
    <xsl:param name="navig.showtitles">0</xsl:param>
    <xsl:param name="manifest.in.base.dir" select="0"/>
    <xsl:param name="base.dir" select="concat($webhelp.base.dir,'/')"/>
    <xsl:param name="suppress.navigation">0</xsl:param>
    <!-- Generate the end-of-the-book index -->
    <xsl:param name="generate.index" select="1"/>
    <xsl:param name="inherit.keywords" select="'0'"/>
    <xsl:param name="para.propagates.style" select="1"/>
    <xsl:param name="phrase.propagates.style" select="1"/>
    <xsl:param name="chunk.first.sections" select="1"/>
    <xsl:param name="chunk.section.depth" select="3"/>
    <xsl:param name="use.id.as.filename" select="1"/>
    <xsl:param name="branding">OSGi Alliance</xsl:param>
    <xsl:param name="brandname">OSGi Alliance</xsl:param>
    <xsl:param name="release.version"></xsl:param>

    <xsl:param name="section.label.includes.component.label" select="1"/>

    <xsl:param name="component.label.includes.part.label" select="1"/>
    <xsl:param name="suppress.footer.navigation">0</xsl:param>
    <xsl:param name="graphic.default.extension">png</xsl:param>

<xsl:template name="user.head.title">
    <xsl:param name="node" select="."/>
    <xsl:param name="title">
        <xsl:apply-templates select="$node" mode="object.title.markup.textonly"/>
    </xsl:param>
    <xsl:param name="document-title">
        <xsl:apply-templates select="/*" mode="object.title.markup.textonly"/>
    </xsl:param>

    <title>
        <xsl:copy-of select="$title"/><xsl:if test="parent::*"> - <xsl:copy-of select="$document-title"/></xsl:if>&#160;<xsl:value-of select="$release.version"/>
    </title>
</xsl:template>

<xsl:template name="system.head.content">
    <!--
    The meta tag tells the IE rendering engine that it should use the latest, or edge, version of the IE rendering environment;It prevents IE from entring compatibility mode.
    -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</xsl:template>

<!-- HTML <head> section customizations -->
<xsl:template name="user.head.content">
    <xsl:param name="title">
        <xsl:apply-templates select="." mode="object.title.markup.textonly"/>
    </xsl:param>

    <meta name="Section-title" content="{$title}"/>
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <link rel="shortcut icon" href="{$webhelp.common.dir}images/favicon.png" type="image/x-icon"/>
    <link rel="stylesheet" type="text/css" href="{$webhelp.common.dir}css/custom.css"/>
    <xsl:if test="/d:book/@status = 'draft'">
        <link rel="stylesheet" type="text/css" href="{$webhelp.common.dir}css/draft.css"/>
    </xsl:if>
    <link rel="stylesheet" type="text/css" href="{$webhelp.common.dir}css/github.css"/>
    <link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Ubuntu:regular,bold&amp;subset=Latin" />
    <script type="text/javascript" src="{$webhelp.common.dir}js/highlight.pack.js"></script>
    <script type="text/javascript" src="{$webhelp.common.dir}js/js.cookie.js"></script>
    <script type="text/javascript" src="{$webhelp.common.dir}js/jquery-3.2.1.slim.min.js"></script>
    <script type="text/javascript" src="{$webhelp.common.dir}js/jquery.modal.min.js"></script>
    <script type="text/javascript" src="{$webhelp.common.dir}js/main.js"></script>
</xsl:template>

<xsl:template name="user.header.navigation">
    <xsl:param name="prev"/>
    <xsl:param name="next"/>
    <xsl:param name="nav.context"/>

    <xsl:call-template name="webhelpheader">
        <xsl:with-param name="prev" select="$prev"/>
        <xsl:with-param name="next" select="$next"/>
        <xsl:with-param name="nav.context" select="$nav.context"/>
    </xsl:call-template>
</xsl:template>

<xsl:template name="user.header.content">
    <xsl:comment> <!-- KEEP this code. --> </xsl:comment>
</xsl:template>

<xsl:template name="user.footer.navigation">
    <xsl:call-template name="webhelptoc">
        <xsl:with-param name="currentid" select="generate-id(.)"/>
    </xsl:call-template>
</xsl:template>

<xsl:template match="/">
	<xsl:choose>
        <!-- include extra test for Xalan quirk -->
        <xsl:when test="namespace-uri(*[1]) != 'http://docbook.org/ns/docbook'">
            <xsl:call-template name="log.message">
                <xsl:with-param name="level">Note</xsl:with-param>
                <xsl:with-param name="source"><xsl:call-template name="get.doc.title"/></xsl:with-param>
                <xsl:with-param name="context-desc">
                    <xsl:text>namesp. add</xsl:text>
                </xsl:with-param>
                <xsl:with-param name="message">
                    <xsl:text>added namespace before processing</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:variable name="addns">
                <xsl:apply-templates mode="addNS"/>
            </xsl:variable>
            <xsl:apply-templates select="exsl:node-set($addns)"/>
        </xsl:when>
        <!-- Can't process unless namespace removed -->
        <xsl:when test="namespace-uri(*[1]) != 'http://docbook.org/ns/docbook'">
            <xsl:call-template name="log.message">
                <xsl:with-param name="level">Note</xsl:with-param>
                <xsl:with-param name="source"><xsl:call-template name="get.doc.title"/></xsl:with-param>
                <xsl:with-param name="context-desc">
                    <xsl:text>namesp. add</xsl:text>
                </xsl:with-param>
                <xsl:with-param name="message">
                    <xsl:text>added namespace before processing</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:variable name="addns">
                <xsl:apply-templates mode="addNS"/>
            </xsl:variable>
            <xsl:apply-templates select="exsl:node-set($addns)"/>
        </xsl:when>
        <xsl:otherwise>
    		<xsl:choose>
                <xsl:when test="$rootid != ''">
                    <xsl:choose>
                        <xsl:when test="count(key('id',$rootid)) = 0">
                            <xsl:message terminate="yes">
                                <xsl:text>ID '</xsl:text>
                                <xsl:value-of select="$rootid"/>
                                <xsl:text>' not found in document.</xsl:text>
                            </xsl:message>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:if test="$collect.xref.targets = 'yes' or $collect.xref.targets = 'only'">
                                <xsl:apply-templates select="key('id', $rootid)" mode="collect.targets"/>
                            </xsl:if>
                            <xsl:if test="$collect.xref.targets != 'only'">
                                <xsl:apply-templates select="key('id',$rootid)" mode="process.root"/>
                                <xsl:if test="$tex.math.in.alt != ''">
                                    <xsl:apply-templates select="key('id',$rootid)" mode="collect.tex.math"/>
                                </xsl:if>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:if test="$collect.xref.targets = 'yes' or $collect.xref.targets = 'only'">
                        <xsl:apply-templates select="/" mode="collect.targets"/>
                    </xsl:if>
                    <xsl:if test="$collect.xref.targets != 'only'">
                        <xsl:apply-templates select="/" mode="process.root"/>
                        <xsl:if test="$tex.math.in.alt != ''">
                            <xsl:apply-templates select="/" mode="collect.tex.math"/>
                        </xsl:if>
                    </xsl:if>
                </xsl:otherwise>
    		</xsl:choose>
        </xsl:otherwise>
	</xsl:choose>

    <xsl:call-template name="index.html"/>
</xsl:template>

<!-- The WebHelp output structure. similar to main() method.
basic format:
<html>
	<head> calls-appropriate-template </head>
	<body>
		       some-generic-content
	       <div id="content">
	       		All your docbook document content goes here
			....
	       </div>
	       some-other-generic-content-at-footer
	</body>
</html>
-->
<xsl:template name="chunk-element-content">
    <xsl:param name="prev"/>
    <xsl:param name="next"/>
    <xsl:param name="nav.context"/>
    <xsl:param name="content">
        <xsl:apply-imports/>
    </xsl:param>

    <xsl:call-template name="user.preroot"/>

    <html>
        <xsl:call-template name="html.head">
            <xsl:with-param name="prev" select="$prev"/>
            <xsl:with-param name="next" select="$next"/>
        </xsl:call-template>

        <body>
            <div id="fullbody">
                <xsl:call-template name="body.attributes"/>

                <xsl:call-template name="user.header.navigation">
                    <xsl:with-param name="prev" select="$prev"/>
                    <xsl:with-param name="next" select="$next"/>
                    <xsl:with-param name="nav.context" select="$nav.context"/>
                </xsl:call-template>

                <div id="mobile-menu-icon">&#x22EE;</div>

                <div id="column-two">
                    <div id="content">
                        <xsl:call-template name="user.header.content"/>

                        <xsl:copy-of select="$content"/>

                        <xsl:call-template name="user.footer.content"/>

        				<!-- Redundant since the upper navigation bar always visible -->
                        <xsl:call-template name="footer.navigation">
                            <xsl:with-param name="prev" select="$prev"/>
                            <xsl:with-param name="next" select="$next"/>
                            <xsl:with-param name="nav.context" select="$nav.context"/>
                        </xsl:call-template>

                        <xsl:call-template name="user.webhelp.content.footer" />
                    </div>

                    <xsl:call-template name="user.footer.navigation"/>
                </div>
            </div>

            <form action="https://www.osgi.org/developer/downloads/release-6/" id="ninja_forms_form_13" method="post" onsubmit="return false;">
             <input type="hidden" id="_wpnonce" name="_wpnonce" value="b02bd313df"/>
             <input type="hidden" name="_wp_http_referer" value="."/>
             <input type="hidden" name="_ninja_forms_display_submit" value="1"/>
             <input type="hidden" name="_form_id" id="_form_id" value="13"/>
             <div class="hp-wrap">
                <label>If you are a human and are seeing this field, please leave it blank.
                   <input type="text" value="" name="_EQZzD"/>
                   <input type="hidden" value="_EQZzD" name="_hp_name"/>
                   </label>
             </div>

             <div id="ninja_forms_form_13_all_fields_wrap" class="ninja-forms-all-fields-wrap">
                <h4>We would like to find out more information about the users of the OSGi Compendium. Please fill out the form below.</h4>

                <p>No thanks, I’d like to go straight to the <a href="javascript:dismissForm();">documentation</a>.</p>

                <p>To find out how OSGi treats data we collect from you, please read our <a href="https://www.osgi.org/osgi-alliance-privacy-policy/">Privacy Policy</a>.</p>

                <div class="ninja-forms-required-items">Fields marked with <span class="ninja-forms-req-symbol">*</span> are required</div>
                <div id="ninja_forms_field_26_div_wrap" data-visible="1">
                   <input type="hidden" id="ninja_forms_field_26_type" value="text"/>
                   <label for="ninja_forms_field_26" id="ninja_forms_field_26_label" class="">First Name <span class="ninja-forms-req-symbol"><strong>*</strong></span></label>
                   <input id="ninja_forms_field_26" data-mask="" data-input-limit="" data-input-limit-type="char" data-input-limit-msg="" name="ninja_forms_field_26" type="text" placeholder="" class="ninja-forms-field  ninja-forms-req " value="" rel="26"/>
                   <div id="ninja_forms_field_26_error" style="display:none;" class="ninja-forms-field-error"></div>
                </div>
                <div id="ninja_forms_field_27_div_wrap" data-visible="1">
                   <input type="hidden" id="ninja_forms_field_27_type" value="text"/>
                   <label for="ninja_forms_field_27" id="ninja_forms_field_27_label" class="">Last Name <span class="ninja-forms-req-symbol"><strong>*</strong></span></label>
                   <input id="ninja_forms_field_27" data-mask="" data-input-limit="" data-input-limit-type="char" data-input-limit-msg="" name="ninja_forms_field_27" type="text" placeholder="" class="ninja-forms-field  ninja-forms-req " value="" rel="27"/>
                   <div id="ninja_forms_field_27_error" style="display:none;" class="ninja-forms-field-error"></div>
                </div>
                <div id="ninja_forms_field_28_div_wrap" data-visible="1">
                   <input type="hidden" id="ninja_forms_field_28_type" value="text"/>
                   <label for="ninja_forms_field_28" id="ninja_forms_field_28_label" class="">Title</label>
                   <input id="ninja_forms_field_28" data-mask="" data-input-limit="" data-input-limit-type="char" data-input-limit-msg="character(s) left" name="ninja_forms_field_28" type="text" placeholder="" class="ninja-forms-field  " value="" rel="28"/>
                   <div id="ninja_forms_field_28_error" style="display:none;" class="ninja-forms-field-error"></div>
                </div>
                <div id="ninja_forms_field_29_div_wrap" data-visible="1">
                   <input type="hidden" id="ninja_forms_field_29_type" value="text"/>
                   <label for="ninja_forms_field_29" id="ninja_forms_field_29_label" class="">Company</label>
                   <input id="ninja_forms_field_29" data-mask="" data-input-limit="" data-input-limit-type="char" data-input-limit-msg="character(s) left" name="ninja_forms_field_29" type="text" placeholder="" class="ninja-forms-field  " value="" rel="29"/>
                   <div id="ninja_forms_field_29_error" style="display:none;" class="ninja-forms-field-error"></div>
                </div>
                <div id="ninja_forms_field_30_div_wrap" data-visible="1">
                   <input type="hidden" id="ninja_forms_field_30_type" value="text"/>
                   <label for="ninja_forms_field_30" id="ninja_forms_field_30_label" class="">Email <span class="ninja-forms-req-symbol"><strong>*</strong></span></label>
                   <input id="ninja_forms_field_30" data-mask="" data-input-limit="" data-input-limit-type="char" data-input-limit-msg="" name="ninja_forms_field_30" type="text" placeholder="" class="ninja-forms-field  ninja-forms-req email " value="" rel="30"/>
                   <div id="ninja_forms_field_30_error" style="display:none;" class="ninja-forms-field-error"></div>
                </div>
                <div class="checkbox-wrap" id="ninja_forms_field_31_div_wrap" data-visible="1">
                   <input type="hidden" id="ninja_forms_field_31_type" value="checkbox"/>
                   <input name="ninja_forms_field_31" type="hidden" value="unchecked"/>
                   <input id="ninja_forms_field_31" name="ninja_forms_field_31" type="checkbox" class="ninja-forms-field " value="checked" rel="31"/>
                   <label for="ninja_forms_field_31" id="ninja_forms_field_31_label">May we contact you again with OSGi related information?</label>
                   <div id="ninja_forms_field_31_error" style="display:none;" class="ninja-forms-field-error"></div>
                </div>

                <div class="submit-wrap" id="ninja_forms_field_32_div_wrap" data-visible="1">
                   <input type="hidden" id="ninja_forms_field_32_type" value="submit"/>
                   <div id="nf_submit_13">
                      <input type="submit" name="_ninja_forms_field_32" class="ninja-forms-field " id="ninja_forms_field_32" value="Submit" rel="32" onclick=""/>
                   </div>
                   <div id="nf_processing_13" style="display:none;">
                      <input type="submit" name="_ninja_forms_field_32" class="ninja-forms-field " id="ninja_forms_field_32" value="Processing" rel="32" disabled=""/>
                   </div>
                   <div id="ninja_forms_field_32_error" style="display:none;" class="ninja-forms-field-error"></div>
                </div>
             </div>
            </form>

            <script type="text/javascript">
                hljs.initHighlightingOnLoad();
                fixAnchors();
                checkContactCookie();
            </script>
        </body>
    </html>

    <xsl:value-of select="$chunk.append"/>
</xsl:template>

<!-- This is for the USERS. Users who want to customize webhelp may over-ride this template to add content to the footer of the content DIV.
 i.e. within <div id="content"> ... </div> -->
<xsl:template name="user.webhelp.content.footer"/>

<!-- The Header with the company logo -->
<xsl:template name="webhelpheader">
    <div id="header">
        <div class="menu-top-container"></div>
        <div id="shadow-block">
            <a class="logo" href="index.html">
                <img src='{$webhelp.common.dir}images/logo.svg' alt="{$brandname} Documentation"/>

                <h1>
                    <xsl:apply-templates select="/*[1]" mode="title.markup"/>&#160;<xsl:value-of select="$release.version"/>
                </h1>
            </a>
        </div>
    </div>
</xsl:template>

<xsl:template name="webhelptoc">
    <xsl:param name="currentid"/>
    <xsl:choose>
        <xsl:when test="$rootid != ''">
            <xsl:variable name="title">
                <xsl:if test="$webhelp.autolabel = 1">
                    <xsl:variable name="label.markup">
                        <xsl:apply-templates select="key('id',$rootid)" mode="label.markup"/>
                    </xsl:variable>
                    <xsl:if test="normalize-space($label.markup)">
                        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
                    </xsl:if>
                </xsl:if>
                <xsl:apply-templates select="key('id',$rootid)" mode="titleabbrev.markup"/>
            </xsl:variable>
            <xsl:variable name="href">
                <xsl:choose>
                    <xsl:when test="$manifest.in.base.dir != 0">
                        <xsl:call-template name="href.target">
                            <xsl:with-param name="object" select="key('id',$rootid)"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="href.target.with.base.dir">
                            <xsl:with-param name="object" select="key('id',$rootid)"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
        </xsl:when>
        <xsl:otherwise>
            <xsl:variable name="title">
                <xsl:if test="$webhelp.autolabel = 1">
                    <xsl:variable name="label.markup">
                        <xsl:apply-templates select="/*" mode="label.markup"/>
                    </xsl:variable>
                    <xsl:if test="normalize-space($label.markup)">
                        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
                    </xsl:if>
                </xsl:if>
                <xsl:apply-templates select="/*" mode="titleabbrev.markup"/>
            </xsl:variable>
            <xsl:variable name="href">
                <xsl:choose>
                    <xsl:when test="$manifest.in.base.dir != 0">
                        <xsl:call-template name="href.target">
                            <xsl:with-param name="object" select="/"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="href.target.with.base.dir">
                            <xsl:with-param name="object" select="/"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <div id="sidebar">
                <ul id="tree" class="filetree">
                    <xsl:apply-templates select="/*/*" mode="webhelptoc">
                        <xsl:with-param name="currentid" select="$currentid"/>
                    </xsl:apply-templates>
                </ul>
            </div>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- Generates the webhelp table-of-contents (TOC). -->
<xsl:template
        match="d:book|d:preface|d:chapter|d:appendix|d:section"
        mode="webhelptoc">
    <xsl:param name="currentid"/>
    <xsl:param name="depth" select="'1'"/>
    <xsl:variable name="title">
        <xsl:if test="$webhelp.autolabel=1">
            <xsl:variable name="label.markup">
                <xsl:apply-templates select="." mode="label.markup"/>
            </xsl:variable>
            <xsl:if test="normalize-space($label.markup)">
                <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
            </xsl:if>
        </xsl:if>
        <xsl:apply-templates select="." mode="titleabbrev.markup"/>
    </xsl:variable>

    <xsl:variable name="href">
        <xsl:choose>
            <xsl:when test="$manifest.in.base.dir != 0">
                <xsl:call-template name="href.target"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="href.target.with.base.dir"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="id" select="generate-id(.)"/>

    <xsl:if test="not(self::d:index) or (self::d:index and not($generate.index = 0))">
        <li>
            <span class="file">
                <a href="{substring-after($href, $base.dir)}"  tabindex="1">
                    <xsl:value-of select="$title"/>
                </a>
            </span>
            <xsl:if test="d:part|d:reference|d:preface|d:chapter|d:bibliography|d:appendix|d:article|d:topic|d:glossary|d:section|d:simplesect|d:sect1|d:sect2|d:sect3|d:sect4|d:sect5|d:refentry|d:colophon|d:bibliodiv">
                <ul>
                    <xsl:if test="not($depth = 2)">
                        <xsl:apply-templates
                                select="d:part|d:reference|d:preface|d:chapter|d:bibliography|d:appendix|d:article|d:topic|d:glossary|d:section|d:simplesect|d:sect1|d:sect2|d:sect3|d:sect4|d:sect5|d:refentry|d:colophon|d:bibliodiv"
                                mode="webhelptoc">
                            <xsl:with-param name="currentid" select="$currentid"/>
                            <xsl:with-param name="depth" select="$depth + 1"/>
                        </xsl:apply-templates>
                    </xsl:if>
                </ul>
            </xsl:if>
        </li>
    </xsl:if>
</xsl:template>

<xsl:template match="text()" mode="webhelptoc"/>

<xsl:template name="user.footer.content">
</xsl:template>

    <!-- Generates index.html file at docs/. This is simply a redirection to content/$default.topic -->
<xsl:template name="index.html">
    <xsl:variable name="default.topic">
        <xsl:choose>
            <xsl:when test="$webhelp.default.topic != ''">
                <xsl:value-of select="$webhelp.default.topic"/>
            </xsl:when>
            <xsl:when test="$htmlhelp.default.topic != ''">
                <xsl:value-of select="$htmlhelp.default.topic"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="make-relative-filename">
                    <xsl:with-param name="base.dir"/>
                    <xsl:with-param name="base.name">
                        <xsl:choose>
                            <xsl:when test="$rootid != ''">
                                <xsl:apply-templates select="key('id',$rootid)" mode="chunk-filename"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:apply-templates
                                        select="*/*[self::d:preface|self::d:chapter|self::d:appendix|self::d:part][1]"
                                        mode="chunk-filename"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:call-template name="write.chunk">
        <xsl:with-param name="filename">
            <xsl:choose>
                <xsl:when test="$webhelp.start.filename">
                    <xsl:value-of select="concat($webhelp.base.dir,'/',$webhelp.start.filename)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'index.html'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:with-param>
        <xsl:with-param name="method" select="'xml'"/>
        <xsl:with-param name="encoding" select="'utf-8'"/>
        <xsl:with-param name="indent" select="'yes'"/>
        <xsl:with-param name="content">
            <html>
                <head>
                    <link rel="shortcut icon" href="{$webhelp.common.dir}images/favicon.png" type="image/x-icon"/>
                    <meta http-equiv="Refresh" content="0; URL={$default.topic}"/>
                    <title>
                        <xsl:value-of select="//d:title[1]"/>&#160;<xsl:value-of select="$release.version"/>
                    </title>
                </head>
                <body>
                    If not automatically redirected, click <a href="{$default.topic}"><xsl:value-of select="$default.topic"/></a>
                </body>
            </html>
        </xsl:with-param>
    </xsl:call-template>
</xsl:template>
</xsl:stylesheet>
