<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:exsl="http://exslt.org/common"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:xlink='http://www.w3.org/1999/xlink'
  exclude-result-prefixes="exsl"
  version="1.0">

<xsl:import href="../../../licensed/docbook-xsl-ns/profiling/profile.xsl"/>

<xsl:param name="profile.condition" select="/d:book/@status"/>

</xsl:stylesheet>