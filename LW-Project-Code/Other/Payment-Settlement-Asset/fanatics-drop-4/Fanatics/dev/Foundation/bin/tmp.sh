#!/bin/sh
# Licensed Materials - Property of IBM
# IBM Sterling Selling and Fulfillment Suite
# (C) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
# US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

# This script is included by many, many scripts, to pick up
# environment information.
# To avoid overly-large variable defs, within nested scripts,
# key off of alreadyIncluded.
#
# Os-specific dynamic information is now in dynamic_os.sh.in
# these include shared library paths, path, and classpath.
# tmp.sh.in must contain static information only, so we can
# redistribute this script for fixes (e.g. if we need to change
# fix jvm flags in a patch)

if [ "${alreadyIncluded}" != "true" ]
then
  alreadyIncluded="true"
  export alreadyIncluded

APP_PID=/opt/IBM/Sterling94/noapp.pid
APPUI_PID=/opt/IBM/Sterling94/noappUI.pid
OPS_PID=/opt/IBM/Sterling94/ops.pid
BI_PID=/opt/IBM/Sterling94/bilisten.pid
INSTALL_DIR=/opt/IBM/Sterling94
CLASS_DIR=/opt/IBM/Sterling94/jar
JAR_DIR=/opt/IBM/Sterling94/jar
NO_JAVA_WRAPPER=0
SERVER_FLAGS=""
OVERRIDE_ANT_JAVA_SERVER=

#See if this is 64-bit JDK
JDK64BIT="false"


#The ISERIES_JIT_PACKAGE_EXCLUSIONS variable is for use in extensions to this file
ISERIES_JIT_PACKAGE_EXCLUSIONS=""

export APP_PID APPUI_PID OPS_PID BI_PID CLASS_DIR NO_JAVA_WRAPPER SERVER_FLAGS

# TD#25702 - set default temp dir so it doesn't default to /tmp or /var/tmp
# also see woodstock/install/bin/ServiceInstaller.sh.in

JAVA_FLAGS="-Djava.io.tmpdir=/opt/IBM/Sterling94/tmp"

# set default encoding to be UTF-8
JAVA_FLAGS="${JAVA_FLAGS} -Dfile.encoding=UTF-8 -Dnet.sf.ehcache.skipUpdateCheck=true -DINSTALL_DIR=/opt/IBM/Sterling94 -DINSTALL_LOCALE=en -XX:-UseGCOverheadLimit -XX:HeapDumpPath=/opt/IBM/Sterling94/logs/agent_logs -XX:+HeapDumpOnOutOfMemoryError -javaagent:/opt/appdynamics/javaagent/javaagent.jar"

# set java home, so we get the correct one. we have probs if people
# happen to set JAVA_HOME in their .profiles.
JAVA_HOME=/opt/IBM/Sterling94/jdk
AGENT_JAVA_HOME="/opt/IBM/Sterling94/jdk"
export INSTALL_DIR CLASS_DIR JAR_DIR JAVA_HOME AGENT_JAVA_HOME

# get Java parameters for users to verify the version
JAVA=$JAVA_HOME/bin/java
$JAVA -XX:CompileCommandFile=.hotspot_compiler -cp "/opt/IBM/Sterling94/jar/install_foundation.jar" com.sterlingcommerce.woodstock.util.JDKTool -jdkversion > jdkVersion.txt
JDK_VERSION=$(< jdkVersion.txt)
rm jdkVersion.txt

$JAVA -XX:CompileCommandFile=.hotspot_compiler -cp "/opt/IBM/Sterling94/jar/install_foundation.jar" com.sterlingcommerce.woodstock.util.JDKTool -jdkvendor > jdkVendor.txt
JDK_VENDOR=$(< jdkVendor.txt)
rm jdkVendor.txt

$JAVA -XX:CompileCommandFile=.hotspot_compiler -cp "/opt/IBM/Sterling94/jar/install_foundation.jar" com.sterlingcommerce.woodstock.util.JDKTool -jdkvmname >jdkVmName.txt
JDK_VM_NAME=$(< jdkVmName.txt)
rm jdkVmName.txt

echo JDK_VERSION: $JDK_VERSION
echo JDK_VENDOR: $JDK_VENDOR
echo JDK_VM_NAME: $JDK_VM_NAME

# set Metaspace for JAVA 8 or above
if [ `echo ${JDK_VERSION}|cut -c 3` -gt 7 ]; then
	JAVA_FLAGS="${JAVA_FLAGS} -XX:MetaspaceSize=1024m"
fi  

cygwin=false;




AWK="awk"
export AWK
HOTSPOT="-server"
# Set up our OS-specific JVM flags.
case `uname -s` in
  Linux)
    ARCH=linux;
    AGENT_HEAP_FLAGS="-Xms512m -Xmx1024m"

    #If 64-bit JVM, then increase heap size
    if [ "${JDK64BIT}" = "true" ]
    then
	JAVA_FLAGS="${JAVA_FLAGS} -d64"
        HEAP_FLAGS="-Xms1280m -Xmx1280m"
        ANT_HEAP_FLAGS="-Xms1536m -Xmx2048m"
    else
        HEAP_FLAGS="-Xms512m -Xmx1024m"
        ANT_HEAP_FLAGS="-Xms768m -Xmx1024m"
    fi

    # common Linux memory settings

    # we do not want -server on linux
    HOTSPOT=""
    INSTALL_HEAP_FLAGS="-Xmx256m"
    JAVA_FLAGS="${JAVA_FLAGS} -XX:MaxPermSize=256m"

# need all memory-related settings first, before starting -D options. 
if [ "${JDK64BIT}" = "true" ]
then
	SERVER_FLAGS="${SERVER_FLAGS} -Xss1024k -Djava.awt.headless=true"
else
	SERVER_FLAGS="${SERVER_FLAGS} -Xss256k -Djava.awt.headless=true"
fi
    SERVER_FLAGS="${SERVER_FLAGS} -Dorg.apache.xml.dtm.DTMManager=org.apache.xml.dtm.ref.DTMManagerDefault"

    ###################################
    # s390x settings, if applicable.  #
    ###################################

    # Flag to enable verboseGc
  if [ -f "/opt/IBM/Sterling94/properties/performance.properties" ]   
  then
    if [ "`awk -F"=" '$1=="EnableVerboseGc"{print $2}'  /opt/IBM/Sterling94/properties/performance.properties`" = "true" ]
    then
         SERVER_FLAGS="${SERVER_FLAGS} -verbose:gc -XX:+PrintGCTimeStamps"
  	 
  	         IBM_HEAP_DUMP=true
  	         export IBM_HEAP_DUMP
  	         IBM_HEAPDUMP=true
  	         export IBM_HEAPDUMP
  	         IBM_HEAPDUMP_OUTOFMEMORY=true
  	         export IBM_HEAPDUMP_OUTOFMEMORY
    fi
  fi

    if [ "${LOOPING_TESTS}" = "true" ]
    then
      IBM_HEAP_DUMP=true
      export IBM_HEAP_DUMP
      IBM_HEAPDUMP=true
      export IBM_HEAPDUMP
      IBM_HEAPDUMP_OUTOFMEMORY=true
      export IBM_HEAPDUMP_OUTOFMEMORY
      IBM_JAVACORE_OUTOFMEMORY=true
      export IBM_JAVACORE_OUTOFMEMORY
    fi


    ;;
  SunOS)
    # use munger value for architecture
    ARCH=linux

    #If 64-bit JVM, then increase heap size
    if [ "${JDK64BIT}" = "true" ]
    then
	JAVA_FLAGS="${JAVA_FLAGS} -d64"
        HEAP_FLAGS="-Xms1280m -Xmx1280m"
        ANT_HEAP_FLAGS="-Xms1280m -Xmx1280m"
    else
	HEAP_FLAGS="-Xms512m -Xmx1024m"
	ANT_HEAP_FLAGS="${HEAP_FLAGS}"
    fi

    JAVA_FLAGS="${JAVA_FLAGS} -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError"
    INSTALL_HEAP_FLAGS="${HEAP_FLAGS}"
    ANT_HEAP_FLAGS="${HEAP_FLAGS}"
    AGENT_HEAP_FLAGS="-Xms512m -Xmx1024m"

if [ "${JDK64BIT}" = "true" ]
then
	SERVER_FLAGS="${SERVER_FLAGS} -Xss1024k"
        if [ "${JDK64BIT}" = "true" ]
        then
            #removed "-XX:SurvivorRatio=&SunOS-64.SURVIVOR_RATIO;" from the SERVER_FLAGS values for Sun JDK 1.5 and newer
            SERVER_FLAGS="${SERVER_FLAGS} -XX:CompileThreshold=1000 -XX:NewSize=128m -Xmn256m -XX:+DisableExplicitGC -Djava.awt.headless=true" 
        else
            #removed "-XX:SurvivorRatio=&SunOS-64.SURVIVOR_RATIO;" from the SERVER_FLAGS values for Sun JDK 1.5 and newer
            SERVER_FLAGS="${SERVER_FLAGS} -XX:CompileThreshold=1000 -XX:NewSize=128m -Xmn256m -XX:+DisableExplicitGC -Djava.awt.headless=true"

        fi

else
	SERVER_FLAGS="${SERVER_FLAGS} -Xss256k"
fi

    SERVER_FLAGS="${SERVER_FLAGS} -Dorg.apache.xml.dtm.DTMManager=org.apache.xml.dtm.ref.DTMManagerDefault" 

  if [ -f "/opt/IBM/Sterling94/properties/performance.properties" ]   
   then 

    # Flag to enable verboseGc
    if [ "`awk -F"=" '$1=="EnableVerboseGc"{print $2}'  /opt/IBM/Sterling94/properties/performance.properties`" = "true" ]
    then
        SERVER_FLAGS="${SERVER_FLAGS} -verbose:gc -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError"
    fi
   fi

    if [ "${LOOPING_TESTS}" = "true" ] 
    then
      SERVER_FLAGS="${SERVER_FLAGS} -XX:-HeapDumpOnOutOfMemoryError"
    fi

    if [ "`/opt/IBM/Sterling94/jdk/bin/java -version  2>&1 |grep 1.5 | wc -l`" -gt  "0" ] 
    then 
      HOTSPOT="${HOTSPOT} -XX:+OverrideDefaultLibthread"
    fi

    AWK="nawk";
    export AWK
    ;;
  HP-UX)
    AGENT_HEAP_FLAGS="-Xms512m -Xmx1024m"

    if [ "`uname -m`" = "ia64" ]
    then
      # 64-bit HP Itanium boxes.
      # shared libs are not compatabile with hpux 11 libs.
      ARCH=hpux64
      HEAP_FLAGS="-Xms1280m -Xmx1280m"
      JAVA_FLAGS="${JAVA_FLAGS} -d64 -Xmpas:on"
      SERVER_FLAGS="${SERVER_FLAGS} -d64 -Xmpas:on -Xss1024k"
      # TD# 31133. Need to increase regression jvm heap on IA64 only.
      # has a larger memory footprint, causing OOMs during tests.
      ANT_HEAP_FLAGS="-Xms1280m -Xmx1280m"
    else
      ARCH=hpux;
      HEAP_FLAGS="-Xms1024m -Xmx1024m"
      SERVER_FLAGS="${SERVER_FLAGS} -Xss256k"
      ANT_HEAP_FLAGS="${HEAP_FLAGS}"
    fi

    INSTALL_HEAP_FLAGS="${HEAP_FLAGS}"

    JAVA_FLAGS="${JAVA_FLAGS} -XX:MaxPermSize=384m -XX:+HeapDumpOnOutOfMemoryError" 
        if [ "${JDK64BIT}" = "true" ]
        then
             SERVER_FLAGS="${SERVER_FLAGS} -Xnocatch -XX:NewSize=256m -Xmn256m -XX:SurvivorRatio=4 -XX:+DisableExplicitGC -Djava.awt.headless=true -Djava.finalizer.verbose=true -Djava.finalizer.verbose.rate=10 -Djava.finalizer.threadCount=5"
        else
             SERVER_FLAGS="${SERVER_FLAGS} -Xnocatch -XX:NewSize=256m -Xmn256m -XX:SurvivorRatio=4 -XX:+DisableExplicitGC -Djava.awt.headless=true -Djava.finalizer.verbose=true -Djava.finalizer.verbose.rate=10 -Djava.finalizer.threadCount=5"
        fi

    SERVER_FLAGS="${SERVER_FLAGS} -Dorg.apache.xml.dtm.DTMManager=org.apache.xml.dtm.ref.DTMManagerDefault"

    if [ "${LOOPING_TESTS}" = "true" ] 
    then
      SERVER_FLAGS="${SERVER_FLAGS} -XX:-HeapDumpOnOutOfMemoryError"
    fi

    # There is a bug in the HP 1.3.x JDK.  No longer need to turn
    # off hotspot, but we do need to set this environment variable
    # to increase the number of malloc arenas.  This may be fixed
    # in a version higher than 1.3.x, but I wouldn't count on it.
    # HP tech support seemed happy with this being the final solution.
    _M_ARENA_OPTS=16:8
    export _M_ARENA_OPTS

    # Flag to enable verboseGc
  if [ -f "/opt/IBM/Sterling94/properties/performance.properties" ]   
  then
    if [ "`awk -F"=" '$1=="EnableVerboseGc"{print $2}'  /opt/IBM/Sterling94/properties/performance.properties`" = "true" ]
    then
        SERVER_FLAGS="${SERVER_FLAGS} -verbose:gc -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError"
    fi
  fi

    ;;
  AIX)
    ARCH=aix;

    AGENT_HEAP_FLAGS="-Xms512m -Xmx1024m"
    #If 64-bit JVM, then increase heap size
    if [ "${JDK64BIT}" = "true" ]
    then
	JAVA_FLAGS="${JAVA_FLAGS} -d64"
        HEAP_FLAGS="-Xms1280m -Xmx1280m"
        ANT_HEAP_FLAGS="-Xms1280m -Xmx1280m"
    else
        HEAP_FLAGS="-Xms512m -Xmx1024m"
        ANT_HEAP_FLAGS="${HEAP_FLAGS}"
    fi

    INSTALL_HEAP_FLAGS="${HEAP_FLAGS}"
    JAVA_FLAGS="${JAVA_FLAGS} -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.PollSelectorProvider"

if [ "${JDK64BIT}" = "true" ]
then
	SERVER_FLAGS="${SERVER_FLAGS} -Xss1024k -Djava.awt.headless=true"
else
	SERVER_FLAGS="${SERVER_FLAGS} -Xss256k -Djava.awt.headless=true"
fi

    SERVER_FLAGS="${SERVER_FLAGS} -Dorg.apache.xml.dtm.DTMManager=org.apache.xml.dtm.ref.DTMManagerDefault"
    # need higher memory settings for regressions

    # Flag to enable heap dumps
  if [ -f "/opt/IBM/Sterling94/properties/performance.properties" ]   
  then
    if [ "`awk -F"=" '$1=="EnableHeapDump"{print $2}'  /opt/IBM/Sterling94/properties/performance.properties `" = "true" ]
    then
       export IBM_HEAP_DUMP="true"
    fi
    # Flag to enable verboseGc
    if [ "`awk -F"=" '$1=="EnableVerboseGc"{print $2}'  /opt/IBM/Sterling94/properties/performance.properties`" = "true" ]
    then
        SERVER_FLAGS="${SERVER_FLAGS} -verbose:gc -XX:+PrintGCTimeStamps"
  	 
  	         IBM_HEAP_DUMP=true
  	         export IBM_HEAP_DUMP
  	         IBM_HEAPDUMP=true
  	         export IBM_HEAPDUMP
  	         IBM_HEAPDUMP_OUTOFMEMORY=true
  	         export IBM_HEAPDUMP_OUTOFMEMORY
    fi
  fi

    if [ "${LOOPING_TESTS}" = "true" ]
    then
      IBM_HEAP_DUMP=true
      export IBM_HEAP_DUMP
      IBM_HEAPDUMP=true
      export IBM_HEAPDUMP
      IBM_HEAPDUMP_OUTOFMEMORY=true
      export IBM_HEAPDUMP_OUTOFMEMORY
      IBM_JAVACORE_OUTOFMEMORY=true
      export IBM_JAVACORE_OUTOFMEMORY
    fi

    # hotspot is not supported on AIX
    HOTSPOT=""

    JITC_COMPILEOPT=${JITC_COMPILEOPT}:NQCOPYPROPA{com/certicom/*}{*}
    export JITC_COMPILEOPT
    ;;
  OS400)

    HEAP_FLAGS="-Xms768m  -Xmx1024m"
    AGENT_HEAP_FLAGS="-Xms512m -Xmx1024m"
    INSTALL_HEAP_FLAGS="${HEAP_FLAGS}"
    # TD# 29133 -- much larger memory footprint on os400, versus
    # other OS's.  even larger than 64-bit on HP IA64.
    ANT_HEAP_FLAGS="-Xms32m  -Xmx1536m"
    SERVER_FLAGS="${SERVER_FLAGS} -Dos400.awt.native=true -Djava.awt.headless=true"
    SERVER_FLAGS="${SERVER_FLAGS} -Dorg.apache.xml.dtm.DTMManager=org.apache.xml.dtm.ref.DTMManagerDefault"
    
      if [ "${JDK64BIT}" = "true" ]
	  then
	     JAVA_FLAGS="${JAVA_FLAGS} -Dcom.ibm.vm.bitmode=64"
	fi

    ARCH=OS400;
    HOTSPOT="";
    NO_JAVA_WRAPPER=1;
    JAVA_FLAGS="${JAVA_FLAGS} -Dos400.security.properties=&JAVA_LIB_SECURITY_DIR;/java.security -Djava.security.properties=&JAVA_LIB_SECURITY_DIR;/java.security -Djava.ext.dirs=&JAVA_LIB_EXT_DIR; -Djava.endorsed.dirs=&JAVA_LIB_ENDORSED_DIR;";
    JAVA_FLAGS="${JAVA_FLAGS} -Dos400.jit.nocompile.class=com/sterlingcommerce/woodstock/services/sapsuite/common/InstanceParameter:com/certicom/ecc/scheme/AES"
    if [ -n "${ISERIES_JIT_PACKAGE_EXCLUSIONS}" ]
    then
        JAVA_FLAGS="${JAVA_FLAGS} -Dos400.jit.nocompile.package=${ISERIES_JIT_PACKAGE_EXCLUSIONS}"
    fi
    QIBM_JAVA_PASE_STARTUP="/usr/lib/start64"
    export QIBM_JAVA_PASE_STARTUP
    ;;
  Darwin)
    ARCH=Darwin;
    AGENT_HEAP_FLAGS="-Xms&Macos.AGENT.INIT_HEAP;m -Xmx&Macos.AGENT.MAX_HEAP;m"
    ANT_HEAP_FLAGS="-Xms1536m -Xmx1536m"
    JAVA_FLAGS="${JAVA_FLAGS} -d64"
    HEAP_FLAGS="-Xms1280m -Xmx1280m"

    HOTSPOT=""
    INSTALL_HEAP_FLAGS="-Xmx256m"
    JAVA_FLAGS="${JAVA_FLAGS} -XX:MaxPermSize=256m"
    SERVER_FLAGS="${SERVER_FLAGS} -Xss1024k -Djava.awt.headless=true"

    SERVER_FLAGS="${SERVER_FLAGS} -Dorg.apache.xml.dtm.DTMManager=org.apache.xml.dtm.ref.DTMManagerDefault"


    # Flag to enable verboseGc
  if [ -f "/opt/IBM/Sterling94/properties/performance.properties" ]   
  then
    if [ "`awk -F"=" '$1=="EnableVerboseGc"{print $2}'  /opt/IBM/Sterling94/properties/performance.properties`" = "true" ]
    then
         SERVER_FLAGS="${SERVER_FLAGS} -verbose:gc -XX:+PrintGCTimeStamps"
  	 
    fi
  fi

    ;;
  *)
    ARCH="unknown architecture";
    ;;
esac
DEBUG_FLAGS=""
if [ "${JAVA_DEBUG}" = "1" ] || [ "${JAVA_DEBUG}" = "true" ]
then
   DEBUG_FLAGS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=8084,server=y,suspend=n"
fi

# first, set up the correct binary.  mainframe cannot use the wrapper script.
if [ ${NO_JAVA_WRAPPER} -eq 1 ]
then
  JAVABIN="/opt/IBM/Sterling94/jdk/bin/java"
else 
  JAVABIN="/opt/IBM/Sterling94/bin/java_wrapper.sh"
fi


# $JAVA is for client-side applications
# now, append our jvm flags. these flags are always needed, to work
# around various JVM issues.
JAVA="${JAVABIN} ${JAVA_FLAGS}"

# Flag to enable verboseGc
 if [ -f "/opt/IBM/Sterling94/properties/performance.properties" ]   
  then
    if [ "`awk -F"=" '$1=="EnableVerboseGc"{print $2}'  /opt/IBM/Sterling94/properties/performance.properties`" = "true" ]
    then
        JAVA="${JAVA} -verbose:gc"
    fi
  fi

# for install
INSTALL_JAVA="${JAVABIN} ${INSTALL_HEAP_FLAGS} ${JAVA_FLAGS}"

# for the servers.  usually have different flags than client-side pids.
JAVA_SERVER="${JAVABIN} ${HOTSPOT} ${HEAP_FLAGS} ${SERVER_FLAGS} ${DEBUG_FLAGS} ${JAVA_FLAGS}"

# for our regressions.. need more memory than noapp. no debug
if [ "${OVERRIDE_ANT_JAVA_SERVER}" != "" ]  
then
ANT_JAVA_SERVER="${OVERRIDE_ANT_JAVA_SERVER}"
else
ANT_JAVA_SERVER="${JAVABIN} ${HOTSPOT} ${ANT_HEAP_FLAGS} ${SERVER_FLAGS} ${JAVA_FLAGS}"
fi

# for agent servers
AGENT_JAVA_SERVER="${JAVABIN} ${HOTSPOT} ${AGENT_HEAP_FLAGS} ${JAVA_FLAGS}"

  if [ -f "/opt/IBM/Sterling94/properties/performance.properties" ]   
  then
 # Flag to enable verboseGc
    if [ "`awk -F"=" '$1=="EnableVerboseGc"{print $2}'  /opt/IBM/Sterling94/properties/performance.properties`" = "true" ]
    then
        AGENT_JAVA_SERVER="${AGENT_JAVA_SERVER} -verbose:gc"
    fi
  fi
export JAVA JAVA_SERVER ARCH ANT_JAVA_SERVER AGENT_JAVA_SERVER INSTALL_JAVA


PROP_DIR=/opt/IBM/Sterling94/properties
DOC_DIR=/opt/IBM/Sterling94
DBDIST_DIR=/opt/IBM/Sterling94
EDITEST_DIR=/opt/IBM/Sterling94
BPDEFS_DIR=/opt/IBM/Sterling94


export PROP_DIR DOC_DIR 
export DBDIST_DIR EDITEST_DIR BPDEFS_DIRVENDORJARS

# get the dynamic OS-specific information, such as classpath and
# shared library paths.
. /opt/IBM/Sterling94/bin/dynamic_os.sh

# extra configuration info.  Only used internally.
for f in ${INSTALL_DIR}/utils/sandbox*.sh
do
  # import any extra config parms.
  # if no extra config files, then f will be our wildcard cmd
  # above, and not a real file.  Skip..
  if [ -f $f ]
  then
    . ${f}
  fi
done


if [ "`uname -s`" = "OS400" ]
then
  if [ -z "${LIBPATH}" ]
  then
    # there is a known bug in iseries V5R4.  If LIBPATH = "", then java
    # crashes trying to load any class.  Workaround is to set it to
    # " " (or unset LIBPATH)
    unset LIBPATH
  fi

  # If VENDORJARS is very long then Runtime.exec() call fails with
  # java.io.IOException: Error converting envvars. This happens in JDK 1.5 
  unset VENDORJARS
fi


if $cygwin ; then
   CLASSPATH=`cygpath -p -a -w ${CLASSPATH}`
   export CLASSPATH
fi

# if !alreadyIncluded
fi

