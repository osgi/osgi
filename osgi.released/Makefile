
JARS = ee.minimum.jar ee.foundation.jar osgi.jar osgi.internal.jar osgi.refimpl.jar osgi.test.jar

DIRS = $(JARS:%.jar=../%)

all: $(JARS)


echo:
	echo $(DIRS)


%.jar :  
	cd ../`basename $@ .jar`; ant -Drelease.dir=../osgi.released


clean: $(JARS:%.jar=%.clean)
	-rm $(JARS)

%.clean :
	cd ../`basename $@ .clean`; ant clean

