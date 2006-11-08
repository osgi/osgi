@echo off
rem -------------------------------------------------------------------------
rem Copied from JBoss Bootstrap Script for Win32
rem -------------------------------------------------------------------------

@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=japiserialize.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

rem Read all command line arguments

set ARGS=
:loop
if [%1] == [] goto endloop
        set ARGS=%ARGS% %1
        shift
        goto loop
:endloop

set JAPI_HOME=%DIRNAME%\..

rem Find run.bat, or we can't continue

set RUNBAT=%JAPI_HOME%\bin\.run.bat
if exist "%RUNBAT%" goto FOUND_RUN_BAT
echo Could not locate %RUNBAT%. Please check that you are in the
echo bin directory when running this script.
goto END

:FOUND_RUN_BAT

rem Find japitools.jar, or we can't continue

set RUNJAR=%JAPI_HOME%\share\java\japitools.jar
if exist "%RUNJAR%" goto FOUND_RUN_JAR
echo Could not locate %RUNJAR%. Please check that you are in the
echo bin directory when running this script.
goto END

:FOUND_RUN_JAR

set JAPI_CLASSPATH=%JAPI_CLASSPATH%;%RUNJAR%;%JAPI_HOME%\share\java\java-getopt-1.0.9.jar;%JAPI_HOME%\share\java\jode-1.1.1.jar;%JAPI_HOME%\share\java\JSX1.0.5.3.jar

rem Setup program sepecific properties
set JAVA_OPTS=%JAVA_OPTS% -Dprogram.name=%PROGNAME%

set PROGRAM_CLASS=net.wuffies.japi.Japize
%RUNBAT% %ARGS%

:END
rem if "%NOPAUSE%" == "" pause

:END_NO_PAUSE
