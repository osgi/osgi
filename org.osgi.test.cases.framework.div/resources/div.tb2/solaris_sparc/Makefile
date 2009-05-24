all: libNative.so

# 
# change the build environment to use Solaris version of JDK;
# gcc command line should remain the same.
#

libNative.so: ../NativeCode.h NativeCodeI.c
	gcc -I /usr/jdk118/include/ -I /usr/jdk118/include/solaris/ NativeCodeI.c \
	-shared -o libNative.so

clean:
	-rm -rf *% *~ *.so