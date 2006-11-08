@echo off
rem -------------------------------------------------------------------------
rem Copied from JBoss Bootstrap Script for Win32
rem -------------------------------------------------------------------------

@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
rem set PROGNAME=run.bat
rem if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

rem Read all command line arguments

set ARGS=
:loop
if [%1] == [] goto endloop
        set ARGS=%ARGS% %1
        shift
        goto loop
:endloop

set JAPI_HOME=%DIRNAME%\..

if not "%JAVA_HOME%" == "" goto ADD_TOOLS

set JAVA=java

echo JAVA_HOME is not set.  Unexpected results may occur.
echo Set JAVA_HOME to the directory of your local JVM to avoid this message.
goto SKIP_TOOLS

:ADD_TOOLS

set JAVA=%JAVA_HOME%\bin\java

if exist "%JAVA_HOME%\bin\java.exe" goto SKIP_TOOLS
echo Could not locate %JAVA_HOME%\bin\java. Unexpected results may occur.
echo Make sure that JAVA_HOME points to a compatible JVM installation.

:SKIP_TOOLS

rem set JAPI_CLASSPATH=%JAPI_CLASSPATH%;%RUNJAR%;%JAPI_HOME%\share\java\java-getopt-1.0.9.jar;%JAPI_HOME%\share\java\jode-1.1.1.jar;%JAPI_HOME%\share\java\JSX1.0.5.3.jar

rem Setup program sepecific properties
rem set JAVA_OPTS=%JAVA_OPTS% -Dprogram.name=%PROGNAME%

rem echo =========================================================================
rem echo.
rem echo   JAPI_HOME: %JAPI_HOME%
rem echo.
rem echo   JAVA: %JAVA%
rem echo.
rem echo   JAVA_OPTS: %JAVA_OPTS%
rem echo.
rem echo   CLASSPATH: %JAPI_CLASSPATH%
rem echo.
rem echo =========================================================================
rem echo.

%JAVA% %JAVA_OPTS% -classpath "%CLASSPATH%;%JAPI_CLASSPATH%" "%PROGRAM_CLASS%" %ARGS%

:END
rem if "%NOPAUSE%" == "" pause

:END_NO_PAUSE
