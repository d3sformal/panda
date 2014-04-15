#!/bin/sh

EXAMPLES=$(find src/examples -name "*.jpf" | sed 's:^src/examples/\(.*\)\.jpf$:\1:')

PATTERN='s:^.*/\([a-zA-Z0-9]\+\)*$:\1:'

max=0

for example in ${EXAMPLES}
do
    name=$(echo ${example} | sed ${PATTERN})
    len=$(echo ${name} | wc -m)

    if [ ${len} -gt ${max} ]
    then
        max=${len}
    fi
done

for example in ${EXAMPLES}
do
    name=$(echo ${example} | sed ${PATTERN})

    printf "%s" ${name}

    for i in $(seq 1 $(expr ${max} - $(echo ${name} | wc -m)))
    do
        printf " "
    done

    bin/run.sh src/examples/${example}.jpf 2>&1 | awk '
        BEGIN {
            STATES=0;
            TIME=0;
            ERROR=false;
        }

        /states/ {
            match($2, "new=([[:digit:]]+)", a);

            STATES=a[1];
        }

        /time/ {
            TIME=$3;
        }

        /=\+ error 1/ {
            ERROR=true;
        }

        END {
            if (!ERROR) {
                printf "	states: %s	time: ", STATES;

                system("expr $(date -d " TIME " +%s) - $(date -d 00:00:00 +%s) | sed '\''s/$/ s/'\''");
            } else {
                print "error";
            }
        }'
done
