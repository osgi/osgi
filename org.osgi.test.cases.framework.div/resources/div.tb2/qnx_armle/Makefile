all: libNative.so

libNative.so: ../NativeCode.h NativeCodeI.c
	qcc -Vgcc_ntoarmle -I $(IVEHOME)/bin/include NativeCodeI.c \
	-shared -o libNative.so

clean:
	-rm -rf *% *~ *.so

