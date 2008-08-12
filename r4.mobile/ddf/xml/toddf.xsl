<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="/">
		<MgmtTree>
			<VerDTD>1.2</VerDTD>
			<Man>OSGi Alliance</Man>
			<xsl:apply-templates select="Node"/>
		</MgmtTree>
	</xsl:template>
	
	<!-- Node ::= (NodeName, Path?, RTProperties?, DFProperties, (Node* | Value?)) -->
	
	<xsl:template match="Node">
		<Node>
			<NodeName>
				<xsl:value-of select="@name"/>
			</NodeName>
			
			<DFProperties>
				
				<!-- AccessType (Add?, Copy?, Delete?, Exec?, Get?, Replace?) -->
				<AccessType>
					<xsl:if test="@access and contains(@access,'G')">
						<Get/>
					</xsl:if>
					<xsl:if test="@access and contains(@access,'E')">
						<Exec/>
					</xsl:if>
					<xsl:if test="@access and contains(@access,'R')">
						<Replace/>
					</xsl:if>
					<xsl:if test="@access and contains(@access,'A')">
						<Add/>
					</xsl:if>
					<xsl:if test="@access and contains(@access,'D')">
						<Delete/>
					</xsl:if>
				</AccessType>
				
				<xsl:copy-of select="Description"/>
				
				<DFFormat>
					<xsl:element name="{@type}"/>
				</DFFormat>
				
				<!-- One | ZeroOrOne | ZeroOrMore | OneOrMore | ZeroOrN | OneOrN) -->
				
				<Occurrence>
					<xsl:choose>
						<xsl:when test="@min=1 and @max=1">
							<One/>
						</xsl:when>
						<xsl:when test="@min=0 and @max=1">
							<ZeroOrOne/>
						</xsl:when>
						<xsl:when test="@min=0 and not(@max)">
							<ZeroOrMore/>
						</xsl:when>
						<xsl:when test="@min=1 and not(@max)">
							<OneOrMore/>
						</xsl:when>
						<xsl:when test="@min=0 and @max">
							<ZeroOr>
								<xsl:value-of select="@max"/>
							</ZeroOr>
						</xsl:when>
						<xsl:when test="@min=1 and @max">
							<ZeroOrMore>
								<xsl:value-of select="@max"/>
							</ZeroOrMore>
						</xsl:when>
					</xsl:choose>
				</Occurrence>
				
				<Scope>
					<xsl:choose>
						<xsl:when test="contains(@scope,'P')">
							<Permanent/>
						</xsl:when>
						<xsl:when test="contains(@scope,'D')">
							<Dynamic/>
						</xsl:when>
						<xsl:otherwise>
							<Permanent/>
						</xsl:otherwise>
					</xsl:choose>
				</Scope>
				
				<xsl:if test="Title">
					<DFTitle>
						<xsl:value-of select="Title"/>
					</DFTitle>
				</xsl:if>
				
				<xsl:if test="MIME">
					<DFType>
						<xsl:copy-of select="MIME"/>
					</DFType>
				</xsl:if>
				
			</DFProperties>
			<xsl:apply-templates select="Node"/>
		</Node>
	</xsl:template>
</xsl:stylesheet>