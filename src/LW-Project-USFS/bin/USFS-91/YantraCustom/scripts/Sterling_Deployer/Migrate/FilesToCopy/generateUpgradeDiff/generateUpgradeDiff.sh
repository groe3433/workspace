echo ...Script Executing tmp.sh;
${YFS_HOME}/bin/tmp.sh;
echo ...Script Setting CLASSPATH;
export CLASSPATH=${INSTALL_DIR}/dbjar/jdbc/Oracle/ojdbc6.jar:${INSTALL_DIR}/jar/install_foundation.jar:${INSTALL_DIR}/jar/platform_afc/6_0/platform_dv.jar:${INSTALL_DIR}/jar/platform_afc/6_0/platform_afc.jar:${INSTALL_DIR}/jar/platform_baseutils.jar:${INSTALL_DIR}/ant/xercesImpl.jar:${INSTALL_DIR}/jar/log4j/1_2_15/log4j-1.2.15.jar;
echo ...Script Executing Java;
java com.yantra.tools.entityguru.reports.UpgradeXMLGenerator -ED ${INSTALL_DIR}/repository/entity -DT ${INSTALL_DIR}/repository/datatypes/datatypes.xml -U yfs87 -P yfs87 -D oracle.jdbc.driver.OracleDriver -URL jdbc:oracle:thin:@nd610db003:1521:idlodb;
