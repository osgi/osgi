@echo off

::----------------------------------------------------------------------
:: Please specify the location of Sun JDK 1.4.x here
::----------------------------------------------------------------------
set JDK=c:\j2sdk1.4

::----------------------------------------------------------------------
:: The location of DocFlex/Doclet home directory (specify the absolute 
:: pathname if you want to run this file from any location)
::----------------------------------------------------------------------
set DFH=.

::----------------------------------------------------------------------
:: Javadoc options: DocFlex Doclet class
::----------------------------------------------------------------------
set options=-docletpath %DFH%\lib\docflex-doclet-1.4.jar -doclet com.docflex.javadoc.Doclet

::----------------------------------------------------------------------
:: -breakiterator option is processed by Javadoc itself (not a doclet).
:: Setting it may improve summary annotations as well as suppress 
:: great a lots of warnings that Javadoc 1.4 would display without it. 
::----------------------------------------------------------------------
set options=-breakiterator %options%

::----------------------------------------------------------------------
:: -J-Xmx option sets the maximum heap size allocated by JVM.
:: Check this option when running on big projects!
::
:: DocFlex generator is pretty hungry for memory as it stores lots
:: of temporary data in hash-tables in order to boost performance.
:: However, according to our tests, 512 Mb heap seemed to be quite
:: enough to generate an HTML documentation for the entire JDK 6
:: Java API sources containing more than 4000 classes.
::----------------------------------------------------------------------
set options=-J-Xmx512m %options%

::----------------------------------------------------------------------
:: GENERATING DOCUMENTATION
::----------------------------------------------------------------------
:: The command following this comment will generate sample documentation
:: by a single Java package.
::
:: Note, DocFlex/Doclet supports -link/-linkoffline options. 
::
:: For example, if you are connected to Internet, adding the following 
:: option will generate the same sample documentation however with the 
:: direct hyperlinks to the online Java API docs located at Sun 
:: Java Technology web-site:
::
:: set options=%options% -link http://java.sun.com/j2se/1.4.2/docs/api/
::----------------------------------------------------------------------

%JDK%\bin\javadoc %options% -d out -sourcepath %DFH%\demo java4