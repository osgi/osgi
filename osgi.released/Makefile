.PHONY: clean localclean $(CLEAN) all echo

PROJECTS = $(shell cat project.order)

JARS = $(addsuffix .jar,$(PROJECTS))
CLEAN = $(JARS:%.jar=%.clean)
ANT = ../cnf/ant/bin/ant

all: $(JARS)


echo:
	@echo $(PROJECTS)


%.jar :  
	cd ../$(basename $@); $(ANT) -Drelease.dir=../osgi.released


clean: localclean $(CLEAN)

localclean:
	-rm -f $(JARS)

%.clean :
	cd ../$(basename $@); $(ANT) clean

