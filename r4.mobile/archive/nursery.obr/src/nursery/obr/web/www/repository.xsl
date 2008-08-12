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
					
					<div class="header">Repository Management</div>
					<div class="window">
						<form method="POST" enctype="application/x-www-form-urlencoded">
							<table>
								<tr>
									<td>
										<table>
											<xsl:for-each select="//repository">
												<tr id="row">
													<td><input type="checkbox" name="resolve" value=""/></td>
													<td><xsl:value-of select="@name"/></td>
													<td><xsl:value-of select="@url"/></td>
													<td><xsl:value-of select="@size"/></td>
												</tr>
											</xsl:for-each>
										</table>
									</td>
									<td>
									</td>
								</tr>
								<tr>
									<td colspan="7">
										<input name="url" type="text" value=""  style="width:100%"/>
									</td>
									<td><input type="submit" value="Add" style="width:100%"/></td>
								</tr>
							</table>
						</form>
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
