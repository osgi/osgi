<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:saxon="http://icl.com/saxon" extension-element-prefixes="saxon" version="1.1">
	<xsl:output method='html' encoding='utf-8'/>
	<xsl:param name="specs" select="''"/>
	<xsl:param name="tests" select="''"/>
		
	<xsl:template name="html">
		<xsl:param name="file" select=""/>
		<xsl:param name="title" select=""/>
		<xsl:param name="body" select=""/>
		
		<saxon:output href="tov/{$file}.html" method="html" encoding="utf-8">
			<html>
				<head>
					<link rel='stylesheet' href='osgi.css' type='text/css'/>
					<title><xsl:value-of select="$title"/></title>
				</head>
				<body>
					<div width="700px">
						<h1><xsl:value-of select="$title"/></h1>
						<xsl:copy-of select="$body"/>
					</div>
				</body>
			</html>
		</saxon:output>
	</xsl:template>




	<xsl:template match="/">
		<xsl:variable name="packages" select="//package"/>
		
		<xsl:call-template name="html">
			<xsl:with-param name="file" select="'index'"/>
			<xsl:with-param name="title" select="'OSGi R4 Test Suite Summary'"/>
			<xsl:with-param name="body">
			
				<!-- Provide a summary of specifications and associated test suites -->
				
				<table width="700px">
					<tr>
						<th>Specification</th>
						<th>Test Cases</th>
						<th>Tests</th>
					</tr>
					
					<xsl:for-each select="saxon:tokenize($specs,',')">
						<xsl:sort select="normalize-space(.)"/>
						<xsl:variable name="spec" select="normalize-space(.)"/>
						<xsl:variable name="spec-package" select="$packages[@name=$spec]"/>
						<xsl:variable name="test-classes" select="$packages/class[testcase=$spec]"/>
						<xsl:variable name="test-packages" >
							<xsl:for-each select="$test-classes">
								<xsl:copy-of select="$packages[starts-with(@name,current()/@package)]"/>
							</xsl:for-each>
						</xsl:variable>

						<tr>
							<td width="350">
									<a href="{$spec}.html"><xsl:value-of select="$spec"/></a>
							</td>
							<td width="330">
								<xsl:for-each select="$test-classes">
									<xsl:sort select="@name"/>
									<xsl:variable name="test-case" select="parent::node()/@name"/>
									<xsl:variable name="tbcs" select="$packages[starts-with(@name,$test-case)]"/>									
									<a href="{$test-case}.html"><xsl:value-of select="$test-case"/></a><br/>
								</xsl:for-each>								
							</td>
							<td width="30">
								<xsl:value-of select="count(saxon:distinct($test-packages//method/spec))"/>
							</td>
						</tr>	
					</xsl:for-each>			
				</table>
			</xsl:with-param>
		</xsl:call-template>


		<xsl:for-each select="saxon:tokenize($specs,',')">
			<xsl:sort select="."/>
			<xsl:variable name="spec" select="normalize-space(.)"/>
			<xsl:variable name="spec-package" select="$packages[@name=$spec]"/>
			
			<xsl:call-template name="html">
				<xsl:with-param name="title" select="concat('Test Matrix for ',$spec)"/>
				<xsl:with-param name="file" select="$spec"/>
				<xsl:with-param name="body">
					<p><xsl:copy-of select="$spec-package/description"/><br/></p>
					<xsl:variable name="testcases" select="$packages//class[testcase=$spec]"/>
					
					<xsl:choose>
						<xsl:when test="$testcases">
							<table>
								<tr>
									<th colspan="2">Tested by:</th>
								</tr>
								<xsl:for-each select="$testcases">
									<tr>
										<td><a href="{@package}.html"><xsl:value-of select="@package"/></a></td>
									</tr>
								</xsl:for-each>
							</table>
							<br/>
							<br/>
						</xsl:when>
					</xsl:choose>
					
					<table width="300">
						<tr>
							<th></th>
							<th>Class</th>
							<th>Methods</th>
							<th>Tests</th>
							<th>Ratio</th>
						</tr>
						
						<xsl:for-each select="$spec-package/class">
							<xsl:sort select="@name"/>
							<xsl:variable name="m" select="count(saxon:distinct(method))"/>
							<xsl:variable name="t" select="count(saxon:distinct($packages//method[starts-with(spec,concat(current()/@name,'.'))]))"/>
							<xsl:variable name="r" select="$t div ($m + 1)"/>
							<xsl:variable name="class" select="saxon:if($r &lt; 1,'passed', 'early')"/>
								
							<tr>
								<td> </td>
								<td>
									<a href="#{@fqn}"><xsl:value-of select="@name"/></a>
								</td>
								<td>
									<xsl:value-of select="$m"/>
								</td>
								<td>
									<xsl:value-of select="$t"/>
								</td>
								<td class="{$class}">
									<xsl:value-of select="format-number($r,'0.00')"/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
					
					<saxon:group select="$spec-package/class/method" group-by="parent::node()/@fqn">
						<xsl:sort select="parent::node()/@fqn"/>
						<xsl:sort select="@name"/>
						
						<xsl:variable name="class" select="parent::node()"/>
						<br/>
						<table width="700">
							<tr>
								<th class="pointer" colspan="2"><a name="{$class/@fqn}"/>Class <xsl:value-of select="$class/@fqn"/></th>
							</tr>			
							<tr>
								<td colspan="2"><xsl:value-of select="$class/lead"/></td>
							</tr>			
							
							<saxon:item>
								<xsl:variable name="test-method" select="//method[spec=current()/@qn]"/>
								<xsl:variable name="test-package" select="$test-method/@package"/>
								
								<tr>
									<th width="350">
										<a name="{@qn}"/><xsl:value-of select="concat(@name,@flatSignature)"/>
									</th>
									<th width="350">
										<xsl:choose>
											<xsl:when test="$test-method">Tests:</xsl:when>
											<xsl:otherwise>
												<b class="passed"> NO TEST FOUND </b>
											</xsl:otherwise>
										</xsl:choose>
									</th>
								</tr>
								<tr>
									<td width="350">
										<xsl:value-of select="lead"/>
									</td>
									<td width="350">
										<xsl:for-each select="$test-method">
											<b><xsl:value-of select="@name"/></b>
											<xsl:value-of select="description"/><br/>
										</xsl:for-each>
									</td>
								</tr>
							</saxon:item>
						</table>
					</saxon:group>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:for-each>	

		<!-- Create a document for every testcase -->
						
		<xsl:for-each select="saxon:tokenize($tests,',')">
			<xsl:sort select="."/>
			
			<xsl:variable name="test" select="normalize-space(.)"/>
			<xsl:variable name="test-packages" select="$packages[starts-with(@name,$test)]"/>
			<xsl:variable name="test-class" select="$test-packages/class[testcase]"/>
			<xsl:variable name="testcase" select="$test-class/testcase"/>
		
		
			<xsl:call-template name="html">
				<xsl:with-param name="title" select="concat('Overview Test Case',' ', $test)"/>
				<xsl:with-param name="file" select="$test"/>
				<xsl:with-param name="body">
					<p>
						This Testcase tests the OSGi specification: <a href="{$testcase}.html"><xsl:value-of select="$testcase"/></a>.
					</p>
					<p>
						<xsl:copy-of select="$test-class/parent::node()/description"/>
						<xsl:copy-of select="$test-class/description"/>
					</p>
					<table>
						<tr>
							<th>Class</th>
							<th>Tests</th>
						</tr>
						<xsl:for-each select="saxon:distinct($test-packages/class[method/spec])">
							<tr>
								<td><a href="#{@qn}"><xsl:value-of select="@name"/></a></td>
								<td><xsl:value-of select="count(saxon:distinct(method/spec))"/></td>
							</tr>
						</xsl:for-each>
					</table>
										
					<saxon:group select="$test-packages/class/method" group-by="parent::node()/@fqn">
						<xsl:sort select="parent::node()/@fqn"/>
						<xsl:sort select="@name"/>
						
						<xsl:variable name="class" select="parent::node()"/>
						
						<xsl:choose>
							<xsl:when test="$class//spec">
								<h2><a name="{$class/@qn}"/><xsl:value-of select="$class/@fqn"/></h2>
								<table width="700px">
									<tr>
										<th colspan="2">References</th>
									</tr>
															
									<xsl:for-each select="saxon:distinct($class/method/spec)">
										<tr>
											<td>
												<a href="{$testcase}.html#{.}"><xsl:value-of select="."/></a>
											</td>
										</tr>
									</xsl:for-each>
											
																
									<saxon:item>
										<xsl:choose>
											<xsl:when test="spec">
												<tr>
													<th width="350">
														<xsl:value-of select="@name"/>
													</th>
													<th width="350">
													</th>
												</tr>
												<tr>
													<td width="350">
														<xsl:value-of select="description"/>
													</td>
													<td  width="350">
														<xsl:for-each select="spec">																
															<a href="{$testcase}.html#{.}"><xsl:value-of select="."/></a>
															<xsl:choose>
																<xsl:when test="$packages//method[@qn=current()]"/>
																<xsl:otherwise><b class="passed"> X </b></xsl:otherwise>
															</xsl:choose>
															<br/>
														</xsl:for-each>
													</td>
												</tr>
											</xsl:when>
										</xsl:choose>
									</saxon:item>
								</table>
							</xsl:when>
						</xsl:choose>
					</saxon:group>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>	
</xsl:stylesheet>