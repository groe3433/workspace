echo "PID        Agent"
ps -aef | grep java | grep 'AGENTDynamicclasspath.cfg' | awk '{print $2 " " $23}' | grep -v 'AGENTDynamicclasspath.cfg'
