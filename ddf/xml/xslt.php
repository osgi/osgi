<?php
 echo system('/usr/bin/xsltproc ../xml/ddf2html.xsl ' . escapeshellarg($file));
?>
