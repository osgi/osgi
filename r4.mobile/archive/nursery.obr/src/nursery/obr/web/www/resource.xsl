<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:include href="main.xsl"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<title>OSGi Bundle Repository</title>
				<LINK REL="stylesheet" HREF="portal.css" TYPE="text/css"/>
				<script type="text/javascript" src="obr.js"/>
			</head>
			<body>
				<div class="body">
					<xsl:call-template name="main"/>
					
					<xsl:for-each select="//resource">
						<div class="header">Resource <xsl:value-of select="@name"/>-<xsl:value-of select="@version"/></div>
						<div class="window">
							<form method="GET" enctype="application/x-www-form-urlencoded" >
								<input type="hidden" name="cmd" value="resolve"/>
								<table style="width:100%">
									<tr>
										<td width="30%">Name</td>
										<td width="70%"><xsl:value-of select="@name"/></td>
									</tr>
									<tr>
										<td width="30%">Version</td>
										<td width="70%"><xsl:value-of select="@version"/></td>
									</tr>
									<tr>
										<td width="30%">Description</td>
										<td width="70%"><xsl:value-of select="description"/></td>
									</tr>
									<tr>
										<td width="30%">Documentation</td>
										<td width="70%"><xsl:value-of select="@documentation"/></td>
									</tr>
									<tr>
										<td width="30%">Copyright</td>
										<td width="70%"><xsl:value-of select="copyright"/></td>
									</tr>
									<tr>
										<td width="30%">License</td>
										<td width="70%"><xsl:value-of select="@license"/></td>
									</tr>
									<tr>
										<td width="30%">Size</td>
										<td width="70%"><xsl:value-of select="@size"/></td>
									</tr>
									<tr>
										<td width="30%">Categories</td>
										<td width="70%">
											<xsl:for-each select="category">
												<xsl:value-of select="@id"/><br/>
											</xsl:for-each>
										</td>
									</tr>
									<tr>
										<td width="30%">Capabilities</td>
										<td width="70%">
											<table>
												<xsl:for-each select="capability">
													<tr>
														<td colspan="2">
															<h4><xsl:value-of select="@name"/></h4>
														</td>
													</tr>
													<xsl:for-each select="p">
														<tr>
															<td><xsl:value-of select="@n"/></td>
															<td><xsl:value-of select="@v"/></td>
														</tr>
													</xsl:for-each>
												</xsl:for-each>
											</table>
										</td>
									</tr>
									<tr>
										<td width="30%">Requirements</td>
										<td width="70%">
											<xsl:for-each select="require">
												<xsl:value-of select="."/><br/>
											</xsl:for-each>
										</td>
									</tr>
									<tr>
										<td width="30%">Extends</td>
										<td width="70%">
											<xsl:for-each select="extend">
												<xsl:value-of select="."/><br/>
											</xsl:for-each>
										</td>
									</tr>
								</table>
							</form>
						</div>
					</xsl:for-each>
				</div>    
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
