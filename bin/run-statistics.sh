#!/bin/sh

PATTERN='s/^.*\.\([a-zA-Z0-9]\+\)*$/\1/'

max=0

for benchmark in ${BENCHMARKS}
do
    name=$(echo ${benchmark} | sed ${PATTERN})
    len=$(echo ${name} | wc -m)

    if [ ${len} -gt ${max} ]
    then
        max=${len}
    fi
done

for benchmark in ${BENCHMARKS}
do
    prefix=$(echo ${benchmark} | sed 's:\.:/:g')
    name=$(echo ${benchmark} | sed ${PATTERN})

    printf "%s" ${name}

    for i in $(seq 1 $(expr ${max} - $(echo ${name} | wc -m)))
    do
        printf " "
    done

    bin/run.sh src/examples/${prefix}.jpf 2>&1 | awk '
        BEGIN {
            STATES=0;
            TIME=0;
            MEMORY=0;
            ERROR=0;
        }

        /states/ {
            match($2, "new=([[:digit:]]+)", a);

            STATES=a[1];
        }

        /time/ {
            TIME=$3;
        }

        /max memory/ {
            MEMORY=$3;
        }

        /=+ error 1/ {
            ERROR=1;
        }

        END {
            if (!ERROR) {
                printf "	states: %s	memory: %s	time: ", STATES, MEMORY;

                system("expr $(date -d " TIME " +%s) - $(date -d 00:00:00 +%s) | sed '\''s/$/ s/'\''");
            } else {
                print "	error";
            }
        }'
done
