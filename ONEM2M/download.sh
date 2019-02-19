#!/bin/sh


wget 'http://ftp.jaist.ac.jp/pub/eclipse/om2m/releases/eclipse-om2m-v1.3.0.zip'
unzip -d om2m eclipse-om2m-v1.3.0.zip 

TOP=`cd ..;pwd`
echo '$TOP=' $TOP

DEST=$TOP/org.osgi.test.cases.onem2m.http.xml/om2m/
echo '$DEST=' $DEST


cp jp.co.ntt.om2m.setting_1.0.0.jar $DEST

 
SUB=om2m/in-cse/plugins
cp $SUB/org.apache.httpcomponents.httpcore_4.3.3.v201411290715.jar $DEST
cp $SUB/org.eclipse.om2m.commons.logging_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.eclipse.om2m.interworking.service_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.apache.commons.codec_1.6.0.v201305230611.jar $DEST
cp $SUB/org.apache.commons.logging_1.1.1.v201101211721.jar $DEST
cp $SUB/org.eclipse.om2m.core.service_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.eclipse.om2m.binding.http_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.eclipse.om2m.datamapping.jaxb_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.eclipse.om2m.datamapping.service_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.eclipse.om2m.commons_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.eclipse.om2m.flexcontainer.service_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.apache.httpcomponents.httpclient_4.3.6.v201511171540.jar $DEST
cp $SUB/org.eclipse.om2m.binding.service_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.eclipse.om2m.persistence.service_1.1.0.20180313-1100.jar $DEST
cp $SUB/org.eclipse.om2m.persistence.eclipselink_1.1.0.20180313-1100.jar  $DEST
cp $SUB/org.eclipse.om2m.core_1.1.0.20180313-1100.jar $DEST



ls $DEST


cp $DEST/* $TOP/org.osgi.test.cases.onem2m.http.json/om2m/
cp $DEST/* $TOP/org.osgi.test.cases.onem2m.service/om2m/
