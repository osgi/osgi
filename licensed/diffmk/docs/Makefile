all: refentry.html

refentry.html: refentry.xml doc1-old.xml doc1-new.xml doc1-diff.xml
	xmllint --xinclude $< > ,$<
	xjparse ,$<
	saxon ,$< /sourceforge/docbook/xsl2/base/html/docbook.xsl $@
	rm -f ,$<

doc1-old.xml: ../samples/doc1-old.xml
	cp $< $@

doc1-new.xml: ../samples/doc1-new.xml
	cp $< $@

doc1-diff.xml: doc1-old.xml doc1-new.xml
	diffmk doc1-old.xml doc1-new.xml doc1-diff.xml
