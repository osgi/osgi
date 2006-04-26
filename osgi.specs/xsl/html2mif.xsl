<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:saxon="http://icl.com/saxon"
	extension-element-prefixes="saxon" version="1.1">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:strip-space elements="*"/>
	
	<!-- Tables should be placed ahead of the body -->
	<xsl:template match="table" mode="Tbl">
		
		<xsl:variable name="class">
			<xsl:choose>
				<xsl:when test="@class">
					<xsl:value-of select="@class"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="name()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<Tbl TblID="{count(preceding::*)}"
			TblNumColumns="{count( tr[1]/th )}">
			
			<xsl:for-each select="tr[1]/th">
				<TblColumnWidth>
					<xsl:value-of select="@width"/>
				</TblColumnWidth>
			</xsl:for-each>
			
			<TblFormat TblTag="{$class}"/>
			<TblTitle>
				<TblTitleContent>
					<xsl:call-template name="para">
						<xsl:with-param name="tag" select="tabletitle"/>
						<xsl:with-param name="inside">
							<dummy>
								<xsl:value-of select="@title"/>
							</dummy>
						</xsl:with-param>
					</xsl:call-template>
				</TblTitleContent>
			</TblTitle>
			<xsl:if test="tr[th]">
				<TblH>
					<xsl:apply-templates select="tr[th]"/>
				</TblH>
			</xsl:if>
			
			<xsl:if test="tr[td]">
				<TblBody>
					<xsl:apply-templates select="tr[td]"/>
				</TblBody>
			</xsl:if>
		</Tbl>
	</xsl:template>
	
	<xsl:template match="tr">
		<Row>
			<xsl:apply-templates select="th|td" mode="Tbl"/>
		</Row>
	</xsl:template>
	
	<xsl:template match="td|th" mode="Tbl">
		<xsl:variable name="class">
			<xsl:choose>
				<xsl:when test="@class">
					<xsl:value-of select="@class"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="name()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<Cell>
			<xsl:if test="@angle">
				<xsl:attribute name="CellAngle">
					<xsl:value-of select="@angle"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@colspan">
				<xsl:attribute name="CellColumns">
					<xsl:value-of select="@colspan"/>
				</xsl:attribute>
			</xsl:if>
			<CellContent>
				<xsl:choose>
					<xsl:when test="text()">
						<xsl:apply-templates select="."/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="*"/>
					</xsl:otherwise>
				</xsl:choose>
				<!--
						<xsl:call-template name="para">
							<xsl:with-param name="inside" select="."/>
							<xsl:with-param name="tag" select="$class"/>
						</xsl:call-template> -->
			</CellContent>
		</Cell>
	</xsl:template>
	
	<xsl:template match="table">
		<Para>
			<ParaLine ATbl="{count(preceding::*)}"/>
		</Para>
	</xsl:template>
	
	<!-- Images should be placed in anchored frames. -->
	<xsl:template match="img" mode="Frames">
		<Frame FrameType="RunIntoParagraph" AnchorAlign="Left"
			ID="{count(preceding::*)}"
			ShapeRect="0 0 {@width} {@height}">
			<ImportObject ImportObFile="{@src}" RunaroundType="Contour"
				ShapeRect="0 0 {@width} {@height}"/>
		</Frame>
	</xsl:template>
	
	<xsl:template match="img">
		<Para PgfTag="picture">
			<ParaLine>
				<AFrame>
					<xsl:value-of select="count(preceding::*)"/>
				</AFrame>
			</ParaLine>
		</Para>
	</xsl:template>
	
	<xsl:template match="img" mode="inside">
		<ParaLine>
			<AFrame>
				<xsl:value-of select="count(preceding::*)"/>
			</AFrame>
		</ParaLine>
	</xsl:template>
	
	<xsl:template match="a" mode="inside">
		<xsl:variable name="content" select="."/>
		<xsl:choose>
			<xsl:when test="@href">
				<xsl:variable name="file">
					<xsl:value-of select="substring-before(@href,'#')"/>
				</xsl:variable>
				<xsl:variable name="anch">
					<xsl:value-of select="substring-after(@href,'#')"/>
				</xsl:variable>
				<xsl:choose>
					<!-- When we have a WWW reference -->
					<xsl:when
						test="starts-with(@href,'http:') or starts-with(@href,'mailto:') or starts-with(@href,'ftp:') or starts-with(@href,'https:')">
						<ParaLine>
							<String>
								<xsl:value-of select="."/>
							</String>
						</ParaLine>
						<xsl:call-template name="char">
							<xsl:with-param name="tag">url</xsl:with-param>
							<xsl:with-param name="inside"> (
								<xsl:value-of select="@href"/>) </xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="starts-with(@href,'xmlfile:')"> STARTS
						WITH XML FILE </xsl:when>
					<!-- When we have a local reference -->
					<xsl:when test="true() or not(normalize-space($file))">
						<xsl:variable name="tag"
							select="saxon:if(@tag,@tag,'a')"/>
						
						<ParaLine>
							<Font FTag="{$tag}"/>
							<String>
								<xsl:value-of select="$content"/>
							</String>
							<Font FTag=""/>
							<xsl:if test="not(normalize-space($file))">
								<XRef XRefSrcText="`{$anch}'">
									<xsl:choose>
										<xsl:when test="@class">
											<xsl:attribute name="XRefName">`
												<xsl:value-of select="@class"/>
												'</xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="XRefName">
												`Page'</xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:if test="normalize-space($file)">
										
										
										<!--<xsl:attribute name="XRefSrcFile">`<xsl:value-of select="document('../filemap.xml',.)//map[@id=$file]"/>'</xsl:attribute>-->
									</xsl:if>
								</XRef>
							</xsl:if>
						</ParaLine>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<ParaLine>
					<xsl:if test="@name">
						<Marker MType="9" MText="`{@name}'"/>
					</xsl:if>
					<xsl:if test="@index">
						<Marker MType="2" MText="`{@index}'"/>
					</xsl:if>
				</ParaLine>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="tab" mode="inside">
		<ParaLine>
			<Tab/>
		</ParaLine>
	</xsl:template>
	<xsl:template match="br" mode="inside">
		<ParaLine>
			<HardReturn/>
		</ParaLine>
	</xsl:template>
	
	<xsl:template match="text()" mode="inside">
		<ParaLine>
			<String>
				<xsl:value-of select="."/>
			</String>
		</ParaLine>
	</xsl:template>
	
	<xsl:template
		match="text()|char|em|b|i|strong|blink|span|tab|br|a|tt|code">
		<OpenPara/>
		<xsl:apply-templates select="." mode="inside"/>
	</xsl:template>
	
	<xsl:template name="char">
		<xsl:param name="tag" select="Body"/>
		<xsl:param name="inside" select="0"/>
		<xsl:if test="$inside">
			<ParaLine>
				<Font FTag="{normalize-space($tag)}"/>
				<String>
					<xsl:value-of select="$inside"/>
				</String>
				<Font FTag=""/>
			</ParaLine>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="char|em|b|i|strong|blink|span|code|tt"
		mode="inside">
		<xsl:call-template name="char">
			<xsl:with-param name="tag">
				<xsl:choose>
					<xsl:when test="@class">
						<xsl:value-of select="@class"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="name()"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="inside" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="ul|ol">
		<xsl:variable name="tag" select="name()"/>
		
		<xsl:for-each select="li">
			<xsl:call-template name="para">
				<xsl:with-param name="tag">
					<xsl:choose>
						<xsl:when test="@class">
							<xsl:value-of select="@class"/>
						</xsl:when>
						<xsl:when test="position()=1">
							<xsl:value-of select="concat($tag,'First')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$tag"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="inside" select="."/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="ul|ol" mode="inside">
		<xsl:variable name="tag" select="name()"/>
		
		<xsl:for-each select="li">
			<ParaLine>
				<HardReturn/>
			</ParaLine>
			<xsl:call-template name="char">
				<xsl:with-param name="tag">
					<xsl:choose>
						<xsl:when test="@class">
							<xsl:value-of select="@class"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="name()"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="inside" select="."/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="para">
		<xsl:param name="tag" select="0"/>
		<xsl:param name="inside" select="0"/>
		<Para PgfTag="{normalize-space($tag)}">
			<xsl:apply-templates select="$inside/child::node()"
				mode="inside"/>
		</Para>
	</xsl:template>
	
	<xsl:template
		match="para|p|h1|h2|h3|h4|h5|h6|h7|h8|h9|td|th|div|address|pre|blockquote">
		<xsl:call-template name="para">
			<xsl:with-param name="tag">
				<xsl:choose>
					<xsl:when test="@class">
						<xsl:value-of select="@class"/>
					</xsl:when>
					<xsl:when test="name()='p'">
						<xsl:value-of select="'Body'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="name()"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="inside" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="body">
		<MIFFile>
			<AFrames>
				<xsl:apply-templates select="//img" mode="Frames"/>
			</AFrames>
			<Tbls>
				<xsl:apply-templates select="//table" mode="Tbl"/>
			</Tbls>
			<xsl:apply-templates select="child::node()"/>
		</MIFFile>
	</xsl:template>
	
	<xsl:template match="/">
		<xsl:apply-templates select="//body"/>
	</xsl:template>
</xsl:stylesheet>