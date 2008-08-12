<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<title>Test Matrix
					<xsl:value-of select="//target"/></title>
			</head>
			<body>
				<xsl:for-each select="//package">
					<h1>
						<xsl:value-of select="@name"/></h1>
					<xsl:for-each select="class">
						<h2>
							<xsl:value-of select="@name"/></h2>
						<xsl:for-each select="method">
							<h3>Testing of
								<xsl:value-of select="@pretty"/></h3>
							<p>Called by:
								<xsl:for-each select="callers/method">
									<xsl:if test="position()!=0"> , </xsl:if>
									<xsl:value-of select="@class"/>.
									<xsl:value-of select="@pretty"/> </xsl:for-each>
								</p>
							<p>Implemented by:
								<xsl:for-each select="callers/method">
									<xsl:if test="position()!=0"> , </xsl:if>
									<xsl:value-of select="@class"/>.
									<xsl:value-of select="@pretty"/> </xsl:for-each>
								</p>
						</xsl:for-each>
					</xsl:for-each>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>