@echo off
if "%OS%"=="Windows_NT" @setlocal

rem %~dp0 is expanded pathname of the current script under NT
set PWD=%~dp0

if "%ANT_HOME%"=="" set ANT_HOME=%PWD%\..\cnf\ant

"%ANT_HOME%\bin\ant" %*

if "%OS%"=="Windows_NT" @endlocal
