<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="main">
		<h1>OSGi Bundle Repository Manager</h1>
		
		<!-- Error window -->
		<xsl:choose>
			<xsl:when test="result/@error">
				<div class="header" style="background-color:red;border:1px solid red">Error occurred</div>
				<div class="window" style="border:1px solid red">
					<xsl:value-of select="result/@error"/>
				</div>
			</xsl:when>
		</xsl:choose>
		
		<!-- Search window -->
		<div class="header">Searches</div>
		<div class="window">
			<form method="GET" enctype="application/x-www-form-urlencoded" >
				<input type="hidden" name="cmd" value="search"/>
				<table style="width:100%">
					<tr>
						<td colspan="7"><input name="keywords" type="text" value="{@keywords}"  style="width:100%"/></td>
						<td width="12.5%"><input type="submit" value="Search" style="width:100%"/></td>
					</tr>
				</table>
			</form>
		</div>			
	</xsl:template>
</xsl:stylesheet>
				