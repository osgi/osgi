<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:strip-space elements="*" />
	<xsl:output method="text" indent="no" encoding="UTF-8" />

	<xsl:template match="/">
		<xsl:message>
			In base
		</xsl:message>
		<xsl:apply-templates select="//component/object" />
	</xsl:template>


	<xsl:template match="object">
		<xsl:message>
			In base
			<xsl:value-of select="@name" />
		</xsl:message>
		<xsl:if test="@name">
			<xsl:variable name="name" select="@name" />
			<xsl:document href="{$name}.java" method="text" indent="no"
				encoding="UTF-8">
				package org.osgi.dmt.residential2;
				/**
				*
				<xsl:value-of select="description" />
				*/
				public interface
				<xsl:value-of select="$name" />
				{
					<xsl:apply-templates select="parameter" />
				}
			</xsl:document>
		</xsl:if>
	</xsl:template>


	<xsl:template match="parameter">
		/**
		*
		<xsl:value-of select="description" />
		*
		<p />
		*
		<xsl:apply-templates select="syntax" />
		*/
		<xsl:for-each select="syntax/*">
			<xsl:value-of select="name()" />
		</xsl:for-each>
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		();

	</xsl:template>

	<xsl:template match="syntax">
		<b>Syntax:</b>
		<br />
		<xsl:for-each select="*">
			<xsl:value-of select="name()" />
			<xsl:choose>
				<xsl:when test="size">
					[
					<xsl:value-of select="@minLength" />
					:
					<xsl:value-of select="@maxLength" />
					]
				</xsl:when>
				<xsl:when test="range">
					[
					<xsl:value-of select="@minInclusive" />
					:
					<xsl:value-of select="@maxInclusive" />
					]
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
