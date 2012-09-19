<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon"
	version="1.1">
	<xsl:strip-space elements="*" />
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />
	<xsl:param name="destdir" />

	<xsl:template match="package">
		<xsl:if test="org.osgi.dmt.ddf.DDF">
			<xsl:message>
				Processing DDF package
			</xsl:message>
			<xsl:document href="{$destdir}/{@name}-ddf.xml" method="xml"
				indent="yes" encoding="UTF-8">
				<html>
					<head>
						<title>
							<xsl:value-of select="@name" />
						</title>
					</head>
					<body>
						<h1 class="Heading1">
							<xsl:value-of select="@name" />
							<a name="{@fqn}" />
							<a index="{@fqn}" />
							<a name="package:{@fqn}" />
						</h1>
						<xsl:apply-templates select=".//formattingerror" />

						<xsl:apply-templates select=".//remark" />

						<xsl:apply-templates select="description"
							mode="html" />
						<xsl:variable name="classes" select="class[not(skip)]" />
						<xsl:call-template name="descriptors">
							<xsl:with-param name="target" select="." />
						</xsl:call-template>
						<xsl:apply-templates select="remark" />
						<xsl:apply-templates select="$classes">
							<xsl:sort select="@name" />
						</xsl:apply-templates>
					</body>
				</html>
			</xsl:document>
		</xsl:if>
	</xsl:template>

	<xsl:template match="formattingerror">
		<h1 class="REMARK">
			<xsl:value-of select="@msg" />
			(x=
			<xsl:value-of select="@cnt" />
			,y=
			<xsl:value-of select="@line" />
			)
			<a href="#{concat(@msg,@cnt,@line)}" />
		</h1>
		<em class="REMARK">
			###
			<xsl:value-of select="." />
		</em>
		<xsl:message terminate="yes">
			Formatting error in
			<xsl:value-of select="@file" />
			#
			<xsl:value-of select='@line' />
			:
			<xsl:value-of select="@msg" />
		</xsl:message>
	</xsl:template>

	<xsl:template match="class" mode="index">
		<p class="{saxon:if(position()=1,'BulletedFirst','Bulleted')}">
			<a href="#{@name}">
				<xsl:value-of select="@name" />
			</a>
			–
			<xsl:apply-templates select="lead" mode="html" />

		</p>
	</xsl:template>

	<xsl:template match="class">
		<xsl:choose>
			<xsl:when test="skip">
				<!-- Skipping this class because it has the @skip tag -->
			</xsl:when>
			<xsl:otherwise>
				<h2 class="Heading2">
					<xsl:value-of select="@name" />
				</h2>
				<h6 class='anchor'>
					<a name="{@qn}" />
					<a index="{@name}" />
					<a index="ddf:{@name}" />
					<xsl:value-of select="@name" />
				</h6>
				<xsl:apply-templates select="remark" />

				<xsl:apply-templates select="description" mode="html" />
				<xsl:call-template name="descriptors">
					<xsl:with-param name="target" select="." />
				</xsl:call-template>


				<xsl:apply-templates select="field" />
				
				<table class="Classes" title="Sub-tree Description for {@name}">
					<tr>
						<th width="1">
							<p class="TableSubHead">Name</p>
						</th>
						<th width="0.6">
							<p class="TableSubHead">Act</p>
						</th>
						<th width="1.5">
							<p class="TableSubHead">Type</p>
						</th>
						<th width="0.5">
							<p class="TableSubHead">Card</p>
						</th>
						<th width="0.2">
							<p class="TableSubHead">S</p>
						</th>
						<th width="2.5">
							<p class="TableSubHead">Description</p>
						</th>
					</tr>
					<xsl:apply-templates select="method" />
				</table>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>





	<xsl:template match="method">
		<!-- ddf name='name' indent='' add='false' get='true' replace='false' delete='false' 
			longTypeName='java.lang.String' shortTypeName='string' cardinality='1' scope='P' 
			interior='false' mime=''/ -->
		<xsl:variable name="description" select="description" />
		<xsl:variable name="count" select="count(ddf)" />
		<xsl:variable name="anchor">
			<h6 class='anchor'>
				<a name="{@qn}" />
				<a index="{parent::node()/@name}:{@name}" />
				<a index="{@name}" />
				<xsl:value-of select="@name" />
			</h6>
		</xsl:variable>

		<xsl:for-each select="ddf">
			<tr>
				<td>
					<p>
						<code>
							<xsl:value-of select="@indent" />
							<xsl:value-of select="@name" />
						</code>
					</p>
				    <xsl:copy-of select="$anchor"/>
				</td>
				<td>
					<code>
						<xsl:if test="@add='true'">
							Add
						</xsl:if>
						<xsl:if test="@delete='true'">
							Del
						</xsl:if>
						<xsl:if test="@get='true'">
							Get
						</xsl:if>
						<xsl:if test="@replace='true'">
							Set
						</xsl:if>
					</code>
				</td>
				<td>
					<code>
						<xsl:value-of select="@shortTypeName" />
					</code>
				</td>
				<td>
					<code>
						<xsl:value-of select="@cardinality" />
					</code>
				</td>
				<td>
					<code>
						<xsl:value-of select="@scope" />
					</code>
				</td>
				<xsl:if test="position()=1">
					<td>
						<xsl:attribute name="rowspan"><xsl:value-of
							select="$count" /></xsl:attribute>
						<p>
							<xsl:apply-templates select="$description"
								mode="html" />
						</p>
					</td>
				</xsl:if>
			</tr>
		</xsl:for-each>
	</xsl:template>



	<xsl:template name="descriptors">
		<xsl:if test="a">
			<p class="parameter">
				<tab />
				<em class="key">See Also</em>
				<tab />
				<xsl:for-each select="a">
					<xsl:if test="position()!=1">
						,
					</xsl:if>
					<xsl:choose>
						<xsl:when test="@href">
							<a href="{@href}" tag="tt">
								<xsl:value-of select="." />
							</a>
						</xsl:when>
						<xsl:otherwise>
							<tt>
								<xsl:value-of select="." />
							</tt>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</p>
		</xsl:if>
		<xsl:if test="since">
			<xsl:for-each select="since">
				<p class="parameter">
					<tab />
					<em class="key">Since</em>
					<tab />
					<xsl:apply-templates select="." mode="html" />
				</p>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="deprecated">
			<xsl:for-each select="deprecated">
				<p class="parameter">
					<tab />
					<em class="key">Deprecated</em>
					<tab />
					<xsl:apply-templates select="." mode="html" />
				</p>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>


	<xsl:template match="parameter" mode="head">
		<xsl:if test="position()!=1">
			,
		</xsl:if>
		<xsl:value-of select="@typeName" />
		<xsl:value-of select="@dimension" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
	</xsl:template>

	<xsl:template match="*" mode="html">
		<xsl:apply-templates select="child::node()" mode="copy" />
	</xsl:template>

	<xsl:template match="@*|node()" mode="copy">
		<xsl:choose>
			<xsl:when test="name()='formattingerror'">
				<a class="Code" name="{concat(@msg,@cnt,@line)}" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates select="@*" mode="copy" />
					<xsl:apply-templates mode="copy" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="field">
		<h6 class='anchor'>
			<a name="{@qn}" />
			<a index="{parent::node()/@name}:{@name}" />
			<a index="{@name}" />
			<xsl:value-of select="@name" />
		</h6>
		<h3 class="Heading3">
			<xsl:value-of
				select="@name" />
			<xsl:if test="@constantValue">
				=
				<xsl:value-of select="@constantValue" />
				<xsl:if test="string(number(@constantValue))='NaN'">
					<a index="{substring(@constantValue,2,string-length(@constantValue)-2)}" />
				</xsl:if>
			</xsl:if>
			<xsl:if test="not(@constantValue) and value">
				=
				<xsl:value-of select="value" />
			</xsl:if>
		</h3>
		<xsl:apply-templates select="description" mode="html" />
		<xsl:call-template name="descriptors">
			<xsl:with-param name="target" select="." />
		</xsl:call-template>
	</xsl:template>


	<xsl:template match="/">
		<xsl:apply-templates select="//package" />
	</xsl:template>


	<xsl:template match="remark">
		<p class="REMARK">
			###
			<xsl:value-of select="." />
		</p>
	</xsl:template>
</xsl:stylesheet>
