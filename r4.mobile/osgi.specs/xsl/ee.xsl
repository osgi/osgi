<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon" version="1.1">
	<xsl:output method="xml"/>
	<xsl:strip-space elements="*"/>
	<xsl:variable name="B" select="//jar[@tagged='foundation']/*"/>
	<xsl:variable name="A" select="//jar[@tagged='minimum']/*"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<title>EE</title>
			</head>
			<body>
				<!-- 
				For Each Package 
				-->
				
				<saxon:group group-by="@package" select="//class">
					<xsl:sort select="@package"/>
					<xsl:variable name="package" select="@package"/>
					
					<h1 class="ee-package">
						<xsl:value-of select="$package"/>
					</h1>
					<h2 class="ee-class">
						<em class="included">
							<xsl:value-of select="saxon:if($A//class[@package=$package],'g','c')"/>
						</em>
						<tab/>
						<em class="included">
							<xsl:value-of select="saxon:if($B//class[@package=$package],'g','c')"/>
						</em>
						<tab/>
						package <xsl:value-of select="$package"/>
					</h2>					
					<saxon:group group-by="@className" select="//class[@package=$package and @public]">
						<xsl:sort select="@className"/>
						<xsl:variable name="class" select="@className"/>
						<xsl:variable name="verb" select="saxon:if(@interface,' extends ',' implements ')"/>

						<h2 class="ee-class">							
							<em class="included">
								<xsl:value-of select="saxon:if($A//class[@className=$class],'g','c')"/>
							</em>
							<tab/>
							<em class="included">
								<xsl:value-of select="saxon:if($B//class[@className=$class],'g','c')"/>
							</em>
							<tab/>
							<xsl:call-template name="modifiers">
								<xsl:with-param name="mods" select="@access"/>
							</xsl:call-template>
							
							<xsl:text> </xsl:text>
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
						</h2>
						<saxon:group group-by="concat(@name,@signature)" 
							select="//class[@className=$class]/*[@public or @protected]">
							<xsl:sort select="not(@constructor)"/>
							<xsl:sort select="@name"/>
							<xsl:sort select="@signature"/>
							
							<xsl:variable name="name" select="@name"/>
							<xsl:variable name="signature" select="@signature"/>
							
							<p class="ee">
								<em class="included">
									<xsl:value-of select="saxon:if($A//class[@className=$class]/*[@name=$name and @signature=$signature],'g','c')"/>
								</em>
								<tab/>
								<em class="included">
									<xsl:value-of select="saxon:if($B//class[@className=$class]/*[@name=$name and @signature=$signature],'g','c')"/>
								</em>
								<tab/>
								<xsl:call-template name="modifiers">
									<xsl:with-param name="mods" select="@access"/>
								</xsl:call-template>
								<!-- <xsl:value-of select="substring-after(@access,'public')"/> -->
								<xsl:text> </xsl:text>
								<xsl:if test="not(@constructor)">
									<xsl:value-of select="signature/returns/@toString"/><xsl:text> </xsl:text>
								</xsl:if>
								<xsl:value-of select="@name"/><xsl:if test="signature/@function">(<xsl:for-each select="signature/argument">
										<xsl:if test="position()!=1">,</xsl:if> <xsl:value-of select="@toString"/>
									</xsl:for-each>)
								</xsl:if>
								<xsl:for-each select="throws">
									<xsl:choose>
										<xsl:when test="position()=1"> throws&#160;</xsl:when>
										<xsl:otherwise>, </xsl:otherwise>
									</xsl:choose><xsl:value-of select="normalize-space(.)"/>
								</xsl:for-each>
							</p>
							<saxon:item/>
						</saxon:group>
						
						<saxon:item/>
					</saxon:group>
					<saxon:item/>
				</saxon:group>
			</body>
		</html>
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