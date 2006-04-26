<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="/">
		<html>
			<body>
				<h1 class="Heading1">
					<xsl:value-of select="Node/@name"/> Managed Object<a
						name="{Node/@name}"/><a index="{Node/@name}"/></h1>
				<p>
					<xsl:copy-of select="Node/Description/*"/></p>
				
				<table class="Classes">
					<tr>
						<th width="3cm">Path</th>
						<th width="1cm" angle="270">Access</th>
						<th width="1cm" angle="270">Type</th>
						<th width="0.7cm" angle="270">Cardin.</th>
						<th width="0.5cm" angle="270">Scope</th>
						<th width="6.5cm" >Description</th>
					</tr>
					<xsl:apply-templates select="//Node[current()!=.]"/>
				</table>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="Node">
		<xsl:variable name="path">
			<xsl:text>$</xsl:text>
			<xsl:for-each select="ancestor-or-self::node()/@name">
				<xsl:text>/</xsl:text>
				<xsl:value-of select="normalize-space(.)"/>
			</xsl:for-each>
		</xsl:variable>
		<tr>
			<td>
				<p class="td">
					<a name="{$path}"/>
					<code class="Box">
						<xsl:for-each
							select="ancestor::node()[parent::node()]">
							<xsl:text>—</xsl:text>
						</xsl:for-each>
					</code>
					<code class="BoxBold">
						<xsl:value-of select="@name"/>
					</code>
				</p>
			</td>
			<td>
				<p class="td">
					<code class="Box">
						<xsl:value-of select="@access"/>
					</code>
				</p>
			</td>
			<td>
				<p class="td">
					<code class="Box">
						<xsl:value-of select="@type"/>
					</code>
				</p>
			</td>
			<td>
				<p class="td">
				<code class="Box">
					<xsl:value-of select="@min"/>
					<xsl:if test="@max">
						<xsl:text>..</xsl:text>
						<xsl:value-of select="@max"/>
					</xsl:if>
				</code>
					</p>
			</td>
			<td>
				<p class="td">
				<code class="Box">
					<xsl:value-of select="@scope"/>
				</code>
					</p>
			</td>
			<td>
				<p class="td"><xsl:value-of select="Description"/></p>
				
				<xsl:if test="Name">
					<h4 class="Heading4">Name<tab/>Description</h4>
					<xsl:for-each select="Name">
						<p class="property">
							<code>
								<xsl:value-of select="@pattern"/>
							</code>
							<tab/>
							<xsl:value-of select="."/>
						</p>
					</xsl:for-each>
				</xsl:if>
				<xsl:if test="Value">
					<h4 class="Heading4">Value<tab/>Description</h4>
					<xsl:for-each select="Value">
						<p class="property">
							<code>
								<xsl:value-of select="@min"/>
								<xsl:value-of select="@value"/>
								<xsl:if test="@max"> ..
									<xsl:value-of select="@max"/> </xsl:if>
							</code>
							<tab/>
							<xsl:value-of select="."/>
						</p>
					</xsl:for-each>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	
</xsl:stylesheet>