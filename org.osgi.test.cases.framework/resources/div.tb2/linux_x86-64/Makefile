all: libNative.so

libNative.so: ../NativeCode.h NativeCodeI.c
	gcc -m64 -I /usr/local/lib/jdk1.6.0_11/include -I /usr/local/lib/jdk1.6.0_11/include/linux/ NativeCodeI.c \
	-shared -o libNative.so

clean:
	-rm -rf *% *~ *.so

