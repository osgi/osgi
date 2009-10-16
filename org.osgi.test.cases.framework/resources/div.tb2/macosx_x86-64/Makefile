all: libNative.jnilib

libNative.jnilib: ../NativeCode.h NativeCodeI.c Makefile
	-mkdir x86_64
	/Developer/usr/bin/gcc-4.2 -x c -arch x86_64 -fmessage-length=0 -pipe -Wno-trigraphs -fpascal-strings -fasm-blocks -Os -Wunused-variable -Wmost -Wno-four-char-constants -Wno-unknown-pragmas -F. -I/System/Library/Frameworks/JavaVM.framework/Headers -c NativeCodeI.c -o x86_64/NativeCodeI.o
	/Developer/usr/bin/gcc-4.2 -arch x86_64 -dynamiclib -L. -F. x86_64/NativeCodeI.o -install_name /usr/local/lib/libNative.jnilib -Wl,-single_module -compatibility_version 1 -current_version 1 -o x86_64/libNative.jnilib
	/usr/bin/lipo -create x86_64/libNative.jnilib -output libNative.jnilib

clean:
	-rm -rf *% *~ *.jnilib x86_64

