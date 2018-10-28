#!/bin/bash

date1="$(date --date='7 day ago' +%Y%m%d)";

find /opt/ssfs/runtime/logs -type d -not -iname "ffdc" -not -newermt $date1 -exec rm -r "{}" \;