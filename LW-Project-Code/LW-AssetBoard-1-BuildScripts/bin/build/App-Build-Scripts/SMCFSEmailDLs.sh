#!/bin/bash

#### Dummy Email as placeholder
#MORE_DETAILED_EMAIL=dummy_email@lightwellinc.com
#### QA
#MORE_DETAILED_EMAIL=TOG-OMS-SUPPORT@lightwellinc.com
#### Preprod
#MORE_DETAILED_EMAIL=oms_system@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
#### Prod
MORE_DETAILED_EMAIL=oms_system@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com,app_ops_warning@wwpdl.vnet.ibm.com
export MORE_DETAILED_EMAIL

#### Dummy Email as placeholder
#GENERAL_EMAIL=dummy_email@lightwellinc.com
#### QA
#GENERAL_EMAIL=TOGMaintenance@lightwellinc.com
#### QA/Preprod
#GENERAL_EMAIL=oms_system@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
#### Prod
GENERAL_EMAIL=oms_system@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com,app_ops_warning@wwpdl.vnet.ibm.com
export GENERAL_EMAIL

#### Dummy Email as placeholder
#MANUAL_PROCESS=dummy_email@lightwellinc.com
#### QA
#MANUAL_PROCESS=TOG-OMS-SUPPORT@lightwellinc.com
#### Preprod
#MANUAL_PROCESS=oms@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
#### Prod
MANUAL_PROCESS=oms@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
export MANUAL_PROCESS

#### Dummy Email as placeholder
#REPORT_EMAIL=dummy_email@lightwellinc.com
#### QA
#REPORT_EMAIL=TOG-OMS-SUPPORT@lightwellinc.com
#### Preprod
#REPORT_EMAIL=oms@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
#### Prod
REPORT_EMAIL=oms@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
export REPORT_EMAIL

#### Dummy Email as placeholder
#ORDER_RECON_ALERT=dummy_email@lightwellinc.com
#### QA
#ORDER_RECON_ALERT=TOG-OMS-SUPPORT@lightwellinc.com
#### Preprod
#ORDER_RECON_ALERT=oms_system@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
#### Prod
ORDER_RECON_ALERT=oms_system@thirtyonegifts.com,Stagingstorealerts@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
export ORDER_RECON_ALERT