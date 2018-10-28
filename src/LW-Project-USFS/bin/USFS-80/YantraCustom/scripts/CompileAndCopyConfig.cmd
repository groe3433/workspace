set classpath=%YFS_HOME%\lib\jgo.jar;%YFS_HOME%\lib\ycpui.jar;%YFS_HOME%\lib\ycmui.jar;%YFS_HOME%\lib\yifui.jar;%YFS_HOME%\lib\yfcui.jar;%YFS_HOME%\lib\xercesImpl.jar
REM Change this to a folder one level above your source path
SET SOURCE_HOME=D:\Eclipse_USFS\eclipse\Workspace\USFS\YantraCustom
REM set this as your source folder name, one level above your com folder
SET SOURCE_DIR=src
cd %SOURCE_HOME%\%SOURCE_DIR%\com\nwcg\icbs\yantra\ui\item
javac NWCGYCMItemNodeDefnDtl.java
cd %SOURCE_HOME%\%SOURCE_DIR%
jar -cvf yfsextn.jar com/nwcg/icbs/yantra/ui/item/*.class
copy yfsextn.jar %YFS_HOME%\extn\ui\
copy yfsextn.jar %YFS_HOME%\webpages\yfscommon\
copy yfsextn.jar %YFS_HOME%\lib\
del yfsextn.jar
cd %SOURCE_HOME%\scripts