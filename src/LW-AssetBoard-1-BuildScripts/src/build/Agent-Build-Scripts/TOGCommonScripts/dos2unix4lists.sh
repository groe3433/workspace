#!/bin/bash

mkdir /opt/ssfs/runtime/test;
chmod 775 -R /opt/ssfs/runtime/test;
cp -p /opt/ssfs/runtime/*.integration.txt test/.;
cp -p /opt/ssfs/runtime/*.agent.txt test/.;
cd /opt/ssfs/runtime/test;

for file in *.txt; do
	echo "$file";
	cat $file | tr -d '\r' > test.txt;
	rm $file;
	cat test.txt > $file;
	chmod 775 $file;
	rm test.txt;
done

cp -p *.integration.txt ../.;
cp -p *.agent.txt ../.;
cd ../;
rm -R test;