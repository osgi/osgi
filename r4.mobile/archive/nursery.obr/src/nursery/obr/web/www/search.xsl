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
					
					<div class="header">Repository Maintenance</div>
					<div class="window">
						<form method="GET" enctype="application/x-www-form-urlencoded" >
							<input type="hidden" name="cmd" value="resolve"/>
							<table style="width:100%">
								<xsl:for-each select="//resource">
									<tr>
										<td width="20px"><input type="checkbox" name="in" value="{@id}"/></td>
										<td width="200px"><a href="cgi?cmd=resource&amp;id={@id}"><xsl:value-of select="@name"/></a></td>
										<td width="200px"><xsl:value-of select="@version"/></td>
										<td width="100px"><xsl:value-of select="@size"/></td>
									</tr>
								</xsl:for-each>
								<tr>
									<td width="12.5%"><input type="submit" value="Resolve" style="width:100%"/></td>
								</tr>
							</table>
						</form>
					</div>		
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
