#!/bin/sh

git clone https://github.com/ntt-nilab/osgi-temp-jar

TOP=`cd ..;pwd`
echo '$TOP=' $TOP

DEST=$TOP/org.osgi.service.onem2m.impl/lib

cp osgi-temp-jar/impl-lib/* $DEST


DEST1=$TOP/org.osgi.test.cases.onem2m.http.xml/plugins/
DEST2=$TOP/org.osgi.test.cases.onem2m.http.json/plugins/
DEST3=$TOP/org.osgi.test.cases.onem2m.service/plugins/

cp osgi-temp-jar/felix-lib/* $DEST1
cp osgi-temp-jar/felix-lib/* $DEST2
cp osgi-temp-jar/felix-lib/* $DEST3

