#!/bin/sh


TOP=`cd ..;pwd`
echo '$TOP=' $TOP

DEST=$TOP/org.osgi.service.onem2m.impl/lib




DEST1=$TOP/org.osgi.test.cases.onem2m.http.xml/plugins/
DEST2=$TOP/org.osgi.test.cases.onem2m.http.json/plugins/
DEST3=$TOP/org.osgi.test.cases.onem2m.service/plugins/



rm $DEST/*
rm $DEST1/*
rm $DEST2/*
rm $DEST3/*


# rm -rf osgi-temp-jar
