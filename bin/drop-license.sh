#!/bin/sh

for FILE in $(find . -name "*.java")
do
    CONTENT="$(awk '
       BEGIN {may_license = 1; must_license = 0;}
        !/^\/\// && !/^$/ {may_license = 0; must_license = 0;}
        /^\/\/ Copyright/ {may_license = 1; must_license = 1;}
        {if (!may_license) print $0}
        !/^\/\/$/ {if (!must_license) may_license = 0;}
    ' ${FILE})"

    echo "${CONTENT}" > ${FILE}
done
