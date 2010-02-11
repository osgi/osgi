<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent='yes' />

	<xsl:template match="/">
		<html>
			<head>
				<title>Test</title>
				<link rel='stylesheet' href='http://www.osgi.org/www/osgi.css'
					type='text/css' />
				<style type="text/css">
					.code { font-size:8; font-family: Courier,
					sans-serif; }
					.error,.ok, .info,
					.warning {
					background-position:center
					center;
					background-repeat:no-repeat;
					width:10px; }
					.ok {
					background-image:url("http://www.osgi.org/www/greenball.png"); }
					.warning {
					background-image:url("http://www.osgi.org/www/orangeball.png"); }
					.error {
					background-image:url("http://www.osgi.org/www/redball.png"); }
					.info {
					background-image:url("http://www.osgi.org/www/info.png"); }
					.class { padding-top:20px; padding-bottom: 10px; font-size:12;
					font-weight:bold; }

					h2 { margin-top : 20px; margin-bottom:10px; }
					table, th, td { border: 1px solid black; padding:5px; }
					table {
					border-collapse:collapse; width:100%; }
					th { height:20px; }
					} 
				</style>
				<script language="javascript">
					function toggle(name) {
					var el =
					document.getElementById(name);
					if ( el.style.display != 'none' ) {
					el.style.display = 'none';
					}
					else {
					el.style.display = '';
					}
					}				
				</script>
			</head>
			<body style="width:700px">
				<h2>Summary</h2>
				<table>
					<tr>
						<td width="50%">Target</td>
						<td>
							<xsl:value-of select="testreport/@target" />
						</td>
					</tr>
					<tr>
						<td width="50%">Framework</td>
						<td>
							<xsl:value-of select="testreport/@framework" />
						</td>
					</tr>
					<tr>
						<td width="50%">Testrun</td>
						<td>
							<xsl:value-of select="testreport/@time" />
						</td>
					</tr>
				</table>
				<h2>Testcases</h2>
				<table width="100%">
					<tr>
						<th>St</th>
						<th>Test</th>
						<th>Failures</th>
						<th>Error</th>
						<th>Info</th>
					</tr>
					<xsl:for-each select="/testreport/test">
						<xsl:variable name="total" select="count(error) + count(failure)" />
						<tr>
							<td>
								<xsl:attribute name="class">
                                <xsl:choose>
                                    <xsl:when test="$total = 0">
                                        ok
                                    </xsl:when>
                                    <xsl:when test="$total &lt; 2">
                                        warning
                                    </xsl:when>
                                    <xsl:otherwise>
                                        error
                                    </xsl:otherwise>                                        
                                </xsl:choose>           
                                </xsl:attribute>
							</td>
							<td class="code">
								<xsl:value-of select="@name" />
								<xsl:if test="failure">
									<div id="{@name}" style="display:none">
										<xsl:for-each select="failure">
											<xsl:value-of select="@message" />
											<br />
											<pre>
												<xsl:value-of select="." />
											</pre>
										</xsl:for-each>
									</div>
								</xsl:if>
							</td>
							<td>
								<xsl:value-of select="count(failure)" />
							</td>
							<td>
								<xsl:value-of select="count(error)" />
							</td>
							<td>
								<xsl:if test="failure or error">
									<img src="http://www.osgi.org/www/info.png" onclick="toggle('{@name}')" />
								</xsl:if>
							</td>
						</tr>

					</xsl:for-each>
				</table>
				<br />

				<h2>Coverage</h2>
				<table width="100%">
					<xsl:for-each select="//coverage/class">
						<tr>
							<th>St</th>
							<th colspan="3">
								<xsl:value-of select="@name" />
							</th>
						</tr>
						<xsl:for-each select="method">
							<xsl:variable name="count" select="count(ref)" />
							<tr>
								<td>
									<xsl:attribute name="class">
										<xsl:choose>
                                            <xsl:when test="$count &gt; 3">
                                                ok
                                            </xsl:when>
                                            <xsl:when test="$count &gt; 1">
                                                warning
                                            </xsl:when>
											<xsl:otherwise>
												error
											</xsl:otherwise>                                        
										</xsl:choose>			
									</xsl:attribute>
								</td>
								<td class="code">
									<xsl:value-of select="@pretty" />
								</td>
								<td>
									<xsl:if test="ref">
										<img src="http://www.osgi.org/www/info.png" onclick="toggle('{@pretty}')" />
									</xsl:if>
								</td>
							</tr>
							<xsl:if test="ref">
								<tr style="display:none" id="{@pretty}">
									<td />

									<td class="code" style="font-size:8">
										<xsl:for-each select="ref">
											<xsl:value-of select="@pretty" />
											<br />
										</xsl:for-each>
									</td>
								</tr>
							</xsl:if>
						</xsl:for-each>
					</xsl:for-each>
				</table>
				<h2>Installed Bundles</h2>
				<table>
				    <tr>
                        <th width="50%">Bundle Symbolic Name</th>
                        <th>Version</th>
				    </tr>
					<xsl:for-each select="//bundle">
						<tr>
							<td>
								<xsl:value-of select="@bsn" />
							</td>
							<td>
								<xsl:value-of select="@version" />
							</td>
						</tr>

					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>