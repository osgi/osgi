#!/bin/sh
### ====================================================================== ###
##                                                                          ##
##  Copied from JBoss Bootstrap Script                                      ##
##                                                                          ##
### ====================================================================== ###

DIRNAME=`dirname $0`

#PROGNAME=`basename $0`
GREP="grep"

#
# Helper to complain.
#
warn() {
    echo "${PROGNAME}: $*"
}

#
# Helper to puke.
#
die() {
    warn $*
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false;
darwin=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;

    Darwin*)
        darwin=true
        ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$JAPI_HOME" ] &&
        JAPI_HOME=`cygpath --unix "$JAPI_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
fi

# Setup JAPI_HOME
if [ "x$JAPI_HOME" = "x" ]; then
    # get the full path (without any relative bits)
    JAPI_HOME=`cd $DIRNAME/..; pwd`
fi
export JAPI_HOME

# Setup the JVM
if [ "x$JAVA_HOME" != "x" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA="java"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    JAPI_HOME=`cygpath --path --windows "$JAPI_HOME"`
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
    JAPI_CLASSPATH=`cygpath --path --windows "$JAPI_CLASSPATH"`
fi

# Display our environment
#echo "======================================================================="
#echo ""
#echo "  JAPI_HOME: $JAPI_HOME"
#echo ""
#echo "  JAVA: $JAVA"
#echo ""
#echo "  JAVA_OPTS: $JAVA_OPTS"
#echo ""
#echo "  CLASSPATH: $JAPI_CLASSPATH"
#echo ""
#echo "======================================================================="
#echo ""

# Execute the JVM
exec $JAVA $JAVA_OPTS -classpath "$CLASSPATH:$JAPI_CLASSPATH" "$PROGRAM_CLASS" "$@"
