all: libNative.so

libNative.so: ../NativeCode.h NativeCodeI.c
	gcc -I /usr/jdk118/include/ -I $(EJDK)/include/linux/ NativeCodeI.c \
	-shared -o libNative.so

clean:
	-rm -rf *% *~ *.so

