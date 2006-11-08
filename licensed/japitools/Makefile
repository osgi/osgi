all: jar

classes: src/net/wuffies/japi/*.java
	cd src && jikes -d .. -classpath $(BOOT_CLASSPATH):$(CLASSPATH) net/wuffies/japi/*.java

jar: classes
	jar -0cvf share/java/japitools.jar net/wuffies/japi/*.class

tarball: jar
	cd .. && tar zcvf japitools.tar.gz japitools/

