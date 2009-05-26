all: libNative.jnilib

libNative.jnilib: ../NativeCode.h NativeCodeI.c Makefile
	-mkdir i386
	-mkdir ppc
	/Developer/usr/bin/gcc-4.0 -x c -arch ppc -fmessage-length=0 -pipe -Wno-trigraphs -fpascal-strings -fasm-blocks -Os -Wunused-variable -mtune=G5 -Wmost -Wno-four-char-constants -Wno-unknown-pragmas -F.  -I/System/Library/Frameworks/JavaVM.framework/Headers -c NativeCodeI.c -o ppc/NativeCodeI.o
	/Developer/usr/bin/gcc-4.0 -x c -arch i386 -fmessage-length=0 -pipe -Wno-trigraphs -fpascal-strings -fasm-blocks -Os -Wunused-variable -Wmost -Wno-four-char-constants -Wno-unknown-pragmas -F. -I/System/Library/Frameworks/JavaVM.framework/Headers -c NativeCodeI.c -o i386/NativeCodeI.o
	/Developer/usr/bin/gcc-4.0 -arch i386 -dynamiclib -L. -F. i386/NativeCodeI.o -install_name /usr/local/lib/libNative.jnilib -Wl,-single_module -compatibility_version 1 -current_version 1 -o i386/libNative.jnilib
	/Developer/usr/bin/gcc-4.0 -arch ppc -dynamiclib -L. -F. ppc/NativeCodeI.o -install_name /usr/local/lib/libNative.jnilib -Wl,-single_module -compatibility_version 1 -current_version 1 -o ppc/libNative.jnilib
	/usr/bin/lipo -create ppc/libNative.jnilib i386/libNative.jnilib -output libNative.jnilib

clean:
	-rm -rf *% *~ *.jnilib ppc i386

