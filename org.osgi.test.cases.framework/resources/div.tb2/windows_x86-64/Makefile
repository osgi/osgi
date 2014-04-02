# The dll file in this directory is built using the MinGW 64 bit compiler installed via cygwin
GCC=x86_64-w64-mingw32-gcc

all: Native.dll

Native.dll: ../NativeCode.h NativeCodeI.c Makefile
	-mkdir x86_64
	$(GCC) -x c -m64 -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" -D __int64="long long" -c NativeCodeI.c -o x86_64/NativeCodeI.o
	$(GCC) -m64 -shared -Wl,--add-stdcall-alias -o Native.dll x86_64/NativeCodeI.o

