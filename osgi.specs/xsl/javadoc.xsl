<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon"
	version="1.1">
	<xsl:strip-space elements="*" />
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />

	<xsl:template match="package">
		<xsl:document href="xml/{@name}.xml" method="xml" indent="yes"
			encoding="UTF-8">
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
					<xsl:apply-templates select="description" mode="html" />
					<xsl:variable name="classes" select="class[not(skip)]" />
					<xsl:if test="count($classes)!=1">
						<h2 class="Heading2">Summary</h2>
						<xsl:apply-templates select="$classes" mode="index">
							<xsl:sort select="@name" />
						</xsl:apply-templates>

						<xsl:if test="//security">
							<h2 class="Heading2">Permissions</h2>
							<xsl:for-each select="$classes">
								<xsl:if test="method/security">
									<h3 class="Heading3">
										<a href="#{@name}">
											<xsl:value-of select="@name" />
										</a>
									</h3>
									<xsl:for-each select="method[security]">
										<p class="Bulleted">
											<code>
												<a href="@qn"><xsl:value-of select="@name" /><xsl:value-of select="@flatSignature" /></a>
											</code>
										</p>
										<xsl:for-each select="security">
											<p class="Bulleted2">
												<code><xsl:value-of select="@name" />[<xsl:value-of select="@resource" />,<xsl:value-of select="@actions" />]</code>
												<xsl:if test=".">
													–
													<xsl:value-of select="." />
												</xsl:if>
											</p>
										</xsl:for-each>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
					</xsl:if>
					<xsl:call-template name="descriptors">
						<xsl:with-param name="target" select="." />
					</xsl:call-template>
					<xsl:apply-templates select="$classes">
						<xsl:sort select="@name" />
					</xsl:apply-templates>
				</body>
			</html>
		</xsl:document>
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
				<h6 class='anchor'>
					<a name="{@qn}" />
					<a index="{@name}" />
					<a index="{saxon:if(@interface,'interface','class')}:{@name}" />
					<xsl:value-of select="@name" />
				</h6>
				<h2 class="Heading2">
					<xsl:choose>
						<xsl:when test="@kind='ENUM'">
								enum <xsl:value-of select="@qn" />
						</xsl:when>
						<xsl:when test="@kind='ANNOTATION'">
								@<xsl:value-of select="@qn" />
						</xsl:when>
						<xsl:when test="@kind='INTERFACE'">
							<xsl:value-of select="@modifiers" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="@name" />
							<xsl:value-of select="@typeParam" />
							<xsl:for-each select="implements[@local]">
								<xsl:choose>
									<xsl:when test="position()=1">
										<br />
										<tab />
										extends
									</xsl:when>
									<xsl:otherwise>
										,
									</xsl:otherwise>
								</xsl:choose>
								<xsl:value-of select="@qn" />
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@modifiers" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="@name" />
							<xsl:value-of select="@typeParam" />
							<xsl:if test="@superclass and @superclass!='Object'">
								<br />
								<tab />
								extends
								<xsl:value-of select="@superclass" />
							</xsl:if>
							<xsl:for-each select="implements[@local]">
								<xsl:choose>
									<xsl:when test="position()=1">
										<br />
										<tab />
										implements
									</xsl:when>
									<xsl:otherwise>
										,
									</xsl:otherwise>
								</xsl:choose>
								<xsl:value-of select="@qn" />
							</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>
				</h2>
				<xsl:for-each select="param">
					<p class="parameter">
						<tab />
						<em class="key">
							<xsl:value-of select="@name" />
						</em>
						<tab />
						<xsl:apply-templates select="." mode="html" />
					</p>
				</xsl:for-each>
				<xsl:apply-templates select="description" mode="html" />
				<xsl:call-template name="descriptors">
					<xsl:with-param name="target" select="." />
				</xsl:call-template>
				<xsl:choose>
					<xsl:when test="@kind='ENUM'">
						<xsl:if test="field">
							<xsl:apply-templates select="field[not(skip)]" mode="enum"/>
						</xsl:if>					
					</xsl:when>
					<xsl:when test="@kind='ANNOTATION'">
						<xsl:apply-templates select="method[not(@isConstructor) and not(skip)]" mode="annotation"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="field">
							<xsl:apply-templates select="field[not(skip)]">
								<xsl:sort select="@name" />
							</xsl:apply-templates>
						</xsl:if>
						<xsl:if test="method[@isConstructor]">
							<xsl:apply-templates select="method[@isConstructor and not(skip)]" />
						</xsl:if>
						<xsl:if test="method[not(@isConstructor)]">
							<xsl:apply-templates select="method[not(@isConstructor) and not(skip)]">
								<xsl:sort select="@name" />
							</xsl:apply-templates>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
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
				select="concat(@modifiers,' ',@typeName,@dimension,' ', @name)" />
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

	<xsl:template match="field" mode="enum">
		<h6 class='anchor'>
			<a name="{@qn}" />
			<a index="{parent::node()/@name}:{@name}" />
			<a index="{@name}" />
			<xsl:value-of select="@name" />
		</h6>
		<h3 class="Heading3">
			<xsl:value-of
				select="@name" />
		</h3>
		<xsl:apply-templates select="description" mode="html" />
		<xsl:call-template name="descriptors">
			<xsl:with-param name="target" select="." />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="method">
		<h6 class='anchor'>
			<a name="{@qn}" />
			<a index="{parent::node()/@name}:{@name}" />
			<a index="{@name}" />
			<xsl:value-of select="concat(@name,@flatSignature)" />
		</h6>
		<h3 class="Heading3">
			<xsl:value-of
				select="concat(@modifiers,' ',@typeName,@dimension,' ', @name)" />
			(
			<xsl:apply-templates select="parameter" mode="head" />
			)
			<xsl:for-each select="exception">
				<xsl:if test="position()=1">
					throws
				</xsl:if>
				<xsl:if test="position()!=1">
					,
				</xsl:if>
				<xsl:value-of select="@name" />
			</xsl:for-each>
		</h3>
		<xsl:if test="normalize-space(@typeArgs)">
			<p class="parameter">
				<tab />
				<em class="key">Type Arguments</em>
				<tab />
				<em class="Code">
					<xsl:value-of select="normalize-space(@typeArgs)" />
				</em>
			</p>
		</xsl:if>
		<xsl:for-each select="param">
			<p class="parameter">
				<tab />
				<em class="key">
					<xsl:value-of select="@name" />
				</em>
				<tab />
				<xsl:apply-templates select="." mode="html" />
			</p>
		</xsl:for-each>
		<xsl:if test="normalize-space(description)">
			<p class="description">
				<tab />
			</p>
			<xsl:apply-templates select="description" mode="html" />
		</xsl:if>
		<xsl:for-each select="return">
			<p class="parameter">
				<tab />
				<em class="key">Returns</em>
				<tab />
				<xsl:apply-templates select="." mode="html" />
			</p>
		</xsl:for-each>
		<xsl:if test="throws">
			<xsl:for-each select="throws">
				<p class="parameter">
					<tab />
					<xsl:if test="position()=1">
						<em class="key">Throws</em>
					</xsl:if>
					<tab />
					<tt>
						<xsl:value-of select="@name" />
					</tt>
					–&#160;
					<xsl:apply-templates select="." mode="html" />
				</p>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="security">
            <xsl:for-each select="security">
                <p class="parameter">
                    <tab />
                    <xsl:if test="position()=1">
                        <em class="key">Security</em>
                    </xsl:if>
                    <tab />
                    <tt>
                        <tt><xsl:value-of select="@name"/>[<xsl:value-of select="@resource"/>,<xsl:value-of select="@actions"/>]</tt>
                    </tt>
                    –&#160;
                    <xsl:apply-templates select="." mode="html" />
                </p>
            </xsl:for-each>
		</xsl:if>
		<xsl:call-template name="descriptors">
			<xsl:with-param name="target" select="." />
		</xsl:call-template>
	</xsl:template>


	<xsl:template match="method" mode="annotation">
		<h6 class='anchor'>
			<a name="{@qn}" />
			<a index="{parent::node()/@name}:{@name}" />
			<a index="{@name}" />
			<xsl:value-of select="@name" />
		</h6>
		<h3 class="Heading3">
			<xsl:value-of
				select="concat(@typeName,@dimension,' ', @name)" />
			<xsl:if test="@default">
				default <xsl:value-of select="@default"/>
			</xsl:if>
		</h3>
		<xsl:if test="normalize-space(description)">
			<p class="description">
				<tab />
			</p>
			<xsl:apply-templates select="description" mode="html" />
		</xsl:if>
		<xsl:for-each select="return">
			<p class="parameter">
				<tab />
				<em class="key">Returns</em>
				<tab />
				<xsl:apply-templates select="." mode="html" />
			</p>
		</xsl:for-each>
		<xsl:call-template name="descriptors">
			<xsl:with-param name="target" select="." />
		</xsl:call-template>
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
		<xsl:if test="ThreadSafe|GuardedBy|Immutable|NotThreadSafe">
			<p class="parameter">
				<tab />
				<em class="key">Concurrency</em>
				<tab />
				<xsl:if test="ThreadSafe">
					Thread-safe
					<xsl:text> </xsl:text>
				</xsl:if>
				<xsl:if test="GuardedBy">
					Guarded by:
					<xsl:value-of select="GuardedBy" />
					<xsl:text> </xsl:text>
				</xsl:if>
				<xsl:if test="Immutable">
					Immutable
					<xsl:text> </xsl:text>
				</xsl:if>
				<xsl:if test="NotThreadSafe">
					Not Thread-safe
					<xsl:text> </xsl:text>
				</xsl:if>
			</p>
		</xsl:if>
		<xsl:if test="noimplement">
			<p class="parameter">
				<tab />
				<em class="key">No Implement</em>
				<tab />
				Consumers of this API must not implement this interface
				<xsl:text> </xsl:text>
			</p>
		</xsl:if>
		<xsl:if test="java.lang.annotation.Retention">
			<p class="parameter">
				<tab />
				<em class="key">Retention</em>
				<tab />
				<xsl:for-each select="java.lang.annotation.Retention//value[not(./value)]">
					<xsl:if test="not(position()=1)">, </xsl:if>
				<code><xsl:value-of select="."/></code>
				</xsl:for-each>
			</p>		
		</xsl:if>
		<xsl:if test="java.lang.annotation.Target">
			<p class="parameter">
				<tab />
				<em class="key">Target</em>
				<tab />
				<xsl:for-each select="java.lang.annotation.Target//value[not(./value)]">
					<xsl:if test="not(position()=1)">, </xsl:if>
				<code><xsl:value-of select="."/></code>
				</xsl:for-each>
			</p>		
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




	<xsl:template match="/">
		<xsl:apply-templates select="//package" />
	</xsl:template>
</xsl:stylesheet>
