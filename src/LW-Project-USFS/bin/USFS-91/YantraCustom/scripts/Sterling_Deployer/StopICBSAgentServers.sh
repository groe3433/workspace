ps -aef | grep "opt/apps/projects/Sterling/jdk/bin/java" | grep was | grep -v grep | awk '{print $2}' | xargs kill -9
