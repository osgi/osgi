<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<title>Test Matrix
					<xsl:value-of select="//target"/></title>
				<LINK REL="stylesheet"
					HREF="http://bundles.osgi.org/www/osgi.css"
					TYPE="text/css"/>
				
			</head>
			<body>
				<h1>Overview</h1>
				<table>
					<tr>
						<td>Target files</td>
						<td>
							<xsl:for-each select="//target">
								<xsl:value-of select="."/>
								<xsl:text>
								</xsl:text>
							</xsl:for-each>
						</td>
					</tr>
					<tr>
						<td>Filter pattern</td>
						<td>
							<xsl:for-each select="//pattern">
								<xsl:value-of select="."/>
								<xsl:text>
								</xsl:text>
							</xsl:for-each>
						</td>
					</tr>
					<tr>
						<td>Test files</td>
						<td>
							<xsl:for-each select="//input">
								<xsl:value-of select="."/>
								<xsl:text>
								</xsl:text>
							</xsl:for-each>
						</td>
					</tr>
				</table>
				<xsl:for-each select="//package">
					<h2><xsl:value-of select="@name"/></h2>
					<table  style="width:600px">
						<xsl:for-each select="class">
							<tr>
								<th colspan="2">
									<xsl:value-of select="@name"/>
								</th>
							</tr>
							<xsl:for-each select="method">
								<tr>
									<td>
										<a name="{@class}.{@pretty}"/><xsl:value-of select="@pretty"/>
									</td>
									<td style="width:200px">
										<xsl:for-each select="callers/method"> <a
												href="#{@class}.{@pretty}" alt="{@jar}">C<xsl:value-of 
												select="position()"/></a><xsl:text> </xsl:text>
										</xsl:for-each>
										<xsl:for-each select="implementers/method"> <a
												href="#{@class}.{@pretty}" alt="{@jar}">I<xsl:value-of 
												select="position()"/></a><xsl:text> </xsl:text>
										</xsl:for-each>
									</td>
								</tr>
							</xsl:for-each>
						</xsl:for-each>
					</table>
				</xsl:for-each>
				<!--
				<hr/>
				<xsl:variable name="methods" select="//method/*/method"/>
				<xsl:variable name="t" select="$methods[ not( @jar=following::method/@jar ) ]/@jar"/>
				<xsl:for-each select="$t">
					<xsl:sort select="."/>
					<xsl:variable name="jar" select="."/>
					<h2><xsl:value-of select="$jar"/></h2>
					<table style="width:600px">
							<xsl:variable name="classes" select="$methods[@jar=$jar and not(@class=following::method/@class)]/@class"/>
							<xsl:for-each select="$classes">
									<xsl:variable name="class" select="."/>
								<tr>
									<th colspan="2">
										<xsl:value-of select="$class"/>
									</th>
								</tr>
								<xsl:variable name="classmethods" select="$methods[@class=$class]"/>
								<xsl:for-each select="$classmethods">		
									<xsl:sort select="@pretty"/>
									<xsl:variable name="proto" select="@proto"/>
									<tr>
										<td >
											<a name="{$class}.{@pretty}"/><xsl:value-of select="@pretty"/>
										</td>
										<td style="width:200px">
											<xsl:variable name="parent" select="parent::node()/parent::node()"/>
											<a href="#{$parent/@class}.{$parent/@pretty}">
												<xsl:choose>
													<xsl:when test="parent::node()/name()='callers'">
														c
													</xsl:when>
													<xsl:otherwise>
														i
													</xsl:otherwise>
												</xsl:choose>
												<xsl:value-of select="$parent/@pretty"/>
											</a>
										</td>
									</tr>
								</xsl:for-each>
							</xsl:for-each>
					</table>
				</xsl:for-each>
					-->
				</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>