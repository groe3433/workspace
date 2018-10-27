<?xml version="1.0" encoding="UTF-8" ?>
<!-- Copyright 2006, Sterling Commerce, Inc. All rights reserved. -->
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j='http://jakarta.apache.org/log4j/'>

    <!--
        ConversionPattern: 
        The value of the ConversionPattern field specified for the layout class 
        dictates the format of the output.
        In this sample ":" is used as the separator, Implementations can change this
        any separator of their choice.

        For a complete list of Patterns that could be specified, refer to PatternLayout 
        javadocs under log4j documentation.
    -->

	<appender name="VERBOSE_APPENDER" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
             <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
            </layout>
	    <filter class="com.yantra.yfc.log.YFCLevelMatchFilter">
                  <param name="LevelToMatch" value="VERBOSE" />
                  <param name="AcceptOnMatch" value="true" />
	    </filter>
	</appender>

	<appender name="DEBUG_APPENDER" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
             <!-- 
                if the component being traced is needed in the output, include a 
                %X{YantraComponent} in the Pattern below. This will output the Yantra
                component being traced.
                For example:- 
                 <param name="ConversionPattern" value="%d:%-7p:%t:%-20X{YantraComponent}:%-60m: %-25c{1}%n"/>
             -->
             <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
            </layout>
	    <filter class="com.yantra.yfc.log.YFCLevelMatchFilter">
                  <param name="LevelToMatch" value="DEBUG" />
                  <param name="AcceptOnMatch" value="true" />
	    </filter>
	</appender>

	<appender name="SQLDEBUG_APPENDER" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
             <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
            </layout>
	    <filter class="com.yantra.yfc.log.YFCLevelMatchFilter">
                  <param name="LevelToMatch" value="SQLDEBUG" />
                  <param name="AcceptOnMatch" value="true" />
	    </filter>
	</appender>

	<appender name="TIMER_APPENDER" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
             <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
            </layout>
	    <filter class="com.yantra.yfc.log.YFCLevelMatchFilter">
                  <param name="LevelToMatch" value="TIMER" />
                  <param name="AcceptOnMatch" value="true" />
	    </filter>
	</appender>

	<appender name="INFO_APPENDER" class="org.apache.log4j.ConsoleAppender">
		<layout class="org.apache.log4j.PatternLayout">
             <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
		</layout>
		<filter class="com.yantra.yfc.log.YFCLevelMatchFilter">
                  <param name="LevelToMatch" value="INFO" />
                  <param name="AcceptOnMatch" value="true" />
		</filter>
	</appender>

    <!-- 
        This appender (ERROR_APPENDER) will be a one line message of all errors that occured 
        in the system. Include this appender and redirect the output to any destination of 
        your choice.
    -->
	<appender name="ERROR_APPENDER" class="org.apache.log4j.ConsoleAppender">
		<layout class="org.apache.log4j.PatternLayout">
	             <!-- 
                if the error code is needed in the output, include a
                %X{YantraErrorCode} in the Pattern below. This will output the Yantra error code for a error.
                For example:- 
                 <param name="ConversionPattern" value="%d:%-7p:%t:%-20X{YantraErrorCode}:%-60m: %-25c{1}%n"/>
             -->
             <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
		</layout>
		<filter class="com.yantra.yfc.log.YFCLevelMatchFilter">
                  <param name="LevelToMatch" value="ERROR" />
                  <param name="AcceptOnMatch" value="true" />
		</filter>
	</appender>

	<appender name="ERRORDTL_APPENDER" class="org.apache.log4j.ConsoleAppender">
		<layout class="org.apache.log4j.PatternLayout">
             <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
		</layout>
		<filter class="com.yantra.yfc.log.YFCLevelMatchFilter">
                  <param name="LevelToMatch" value="ERRORDTL" />
                  <param name="AcceptOnMatch" value="true" />
		</filter>
	</appender>

	<appender name="ALL" class="org.apache.log4j.ConsoleAppender">
            <layout class="org.apache.log4j.PatternLayout">
             <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
            </layout>
	</appender>

    <appender name="ROLLINGFILE_APPENDER" class="org.apache.log4j.RollingFileAppender">
            <param name="MaxFileSize" value="2048KB" />
            <param name="MaxBackupIndex" value="2" />
            <!-- 
                Instead of hardcoding the absolute path here for the log file, 
                Customers could also use a ${LOG_DIRECTORY}/yantra_application.log
                and invoke jvm with a -DLOG_DIRECTORY=/application_path/log option 
            -->
            <param name="File" value="/opt/apps/projects/USFS/Yantra77MC/logs/yantra_application_mc.log" />


            <layout class="org.apache.log4j.PatternLayout">
                <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
            </layout>
    </appender>

    <!-- 
        In case of multiple applications using log4j in a application server, add the following category element to the log4j xml that is used for the applications. Yantra application will use the following category as the root category. Yantra logging categories are child categories of this category. Any change in level done via the system management console will apply to this category.
    -->
   <category  name="com.yantra" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
       <priority class="com.yantra.yfc.log.YFCLogLevel" value="INFO"  /> 
       <appender-ref ref="ALL" />
       <appender-ref ref="ROLLINGFILE_APPENDER" />
   </category>
	<category  name="com.nwcg" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
       <priority class="com.yantra.yfc.log.YFCLogLevel" value="VERBOSE"  /> 
       <appender-ref ref="ALL" />
       <appender-ref ref="ROLLINGFILE_APPENDER" />
   </category>

	<root>
	   <priority class="com.yantra.yfc.log.YFCLogLevel" value="INFO"  /> 
       <!-- 
	   <appender-ref ref="VERBOSE_APPENDER" />
	   <appender-ref ref="DEBUG_APPENDER" />
	   <appender-ref ref="SQLDEBUG_APPENDER" />
	   <appender-ref ref="TIMER_APPENDER" />
	   <appender-ref ref="INFO_APPENDER" />
	   <appender-ref ref="ERROR_APPENDER" />
	   <appender-ref ref="ERRORDTL_APPENDER" />
	   <appender-ref ref="ROLLINGFILE_APPENDER" />
       -->
	   <appender-ref ref="ALL" />
	</root>
</log4j:configuration>
