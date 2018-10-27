#!/bin/bash

#### QA
#MORE_DETAILED_EMAIL=TOG-OMS-SUPPORT@lightwellinc.com
#### Preprod
#MORE_DETAILED_EMAIL=oms_system@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
#### Prod
MORE_DETAILED_EMAIL=oms_system@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
export MORE_DETAILED_EMAIL

#### QA
#GENERAL_EMAIL=TOGMaintenance@lightwellinc.com
#### Preprod (during PT phase)
#GENERAL_EMAIL=oms_system@thirtyonegifts.com,TOGMaintenance@lightwellinc.com
#### QA/Preprod
#GENERAL_EMAIL=oms_system@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
#### Prod
GENERAL_EMAIL=oms_system@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
export GENERAL_EMAIL

#### QA
#MANUAL_PROCESS=TOG-OMS-SUPPORT@lightwellinc.com
#### Preprod
#MANUAL_PROCESS=oms@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com,sbury@thirtyonegifts.com,moffers@thirtyonegifts.com,mcitro@thirtyonegifts.com,maija.waschke@lightwellinc.com
#### Prod
MANUAL_PROCESS=oms@thirtyonegifts.com,Stagingstorealerts@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
export MANUAL_PROCESS

#### QA
#REPORT_EMAIL=TOG-OMS-SUPPORT@lightwellinc.com
#### Preprod
#REPORT_EMAIL=oms@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com,sbury@thirtyonegifts.com,moffers@thirtyonegifts.com,mreed@thirtyonegifts.com,mcitro@thirtyonegifts.com,maija.waschke@lightwellinc.com
#### Prod
REPORT_EMAIL=oms@thirtyonegifts.com,Stagingstorealerts@thirtyonegifts.com,TOG-OMS-SUPPORT@lightwellinc.com
export REPORT_EMAIL