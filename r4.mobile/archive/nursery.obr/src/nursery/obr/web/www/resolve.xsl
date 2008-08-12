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
					
					<!-- create reason messages -->
					<xsl:for-each select="//resource[reason]">
						<div style="display:none" id="tt{@id}" >
							Selected by: 
							<xsl:for-each select="reason">
								<xsl:value-of select="."/><br/>
							</xsl:for-each>
						</div>
					</xsl:for-each>
					
					<!-- Resolver results -->
					<div class="header">Resolve Results</div>
					<div class="window">
						<form method="GET" enctype="application/x-www-form-urlencoded" >
							<input type="hidden" name="cmd" value="resolve"/>
							<table style="width:100%">
								<tr>
									<th>Added</th>
									<th>Required</th>
									<th>Optional</th>
								</tr>
								<tr>
									<td width="30%">
										<table width="100%">
											<xsl:for-each select="//added/resource">
												<tr>
													<td width="200px">
														<a href="cgi?cmd=resource&amp;id={@id}" 
															onmouseout="clear()" 
															onmouseover="messageById({@id})">
															<xsl:value-of select="@name"/>-<xsl:value-of select="@version"/>
														</a>
														<input type="hidden" name="in" value="{@id}"/>
													</td>
												</tr>
											</xsl:for-each>
										</table>
									</td>
									<td>
										<table width="100%">
											<xsl:for-each select="//required/resource">
												<tr>
													<td width="200px">
														<a href="cgi?cmd=resource&amp;id={@id}" 
															onmouseout="clear()" 
															onmouseover="messageById({@id})">
															<xsl:value-of select="@name"/>-<xsl:value-of select="@version"/>
														</a>
													</td>
												</tr>
											</xsl:for-each>
										</table>
									</td>
									<td width="30%">
										<table width="100%">
											<xsl:for-each select="//optional/resource">
												<tr>
													<td colspan="1"><input type="checkbox" name="in" value="{@id}"/></td>
													<td colspan="7">
														<a href="cgi?cmd=resource&amp;id={@id}" 
															onmouseout="clear()" 
															onmouseover="messageById({@id})">
															<xsl:value-of select="@name"/>-<xsl:value-of select="@version"/>
														</a>
													</td>
												</tr>
											</xsl:for-each>
										</table>
									</td>
								</tr>
							</table>
						</form>    
						<center><input type="submit" value="Re-Resolve" style="margin:0" disabled="true"/></center>
					</div>
					
					<!-- Unsatisfied window -->
					<xsl:choose>
						<xsl:when test="//unsatisfied/require">
							<div class="header">Unsatisfied Requirements</div>
							<div class="window">
								<table width="100%">
									<tr>
										<th>#</th>
										<th>Name</th>
										<th>Reason</th>
									</tr>
									
									<xsl:for-each select="//unsatisfied/require">
										<tr style="color:red">
											<td><xsl:value-of select="@cardinality"/></td>
											<td><xsl:value-of select="@name"/></td>
											<td><xsl:value-of select="."/></td>
										</tr>
									</xsl:for-each>
								</table>
							</div>
						</xsl:when>
					</xsl:choose>
					
					<div class="header">Messages</div>
					<div class="window" id="message" style="height:100px"/>				
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
