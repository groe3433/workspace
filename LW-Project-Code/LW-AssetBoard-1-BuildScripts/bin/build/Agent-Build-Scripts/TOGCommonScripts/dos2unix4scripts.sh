#!/bin/bash

path=$1;
for file in $1/*.sh; do
	echo "$file";
	cat $file | tr -d '\r' > test.sh;
	rm $file;
	cat test.sh > $file;
	chmod 775 $file;
	rm test.sh;
done