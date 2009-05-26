all: libNative.so

libNative.so: ../NativeCode.h NativeCodeI.c
	qcc -Vgcc_ntox86 -I $(IVEHOME)/bin/include NativeCodeI.c \
	-shared -o libNative.so

clean:
	-rm -rf *% *~ *.so

