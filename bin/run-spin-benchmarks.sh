#!/bin/sh

BENCHMARKS="
    dataflow.DataFlowAnalysis
    cycling.CyclingRace
    image.Image
    scheduler.Scheduler

    svcomp.loops.ArrayTrueUnreachableLabel
    svcomp.loops.Eureka01TrueUnreachableLabel
    svcomp.loops.TREX03TrueUnreachableLabel
    svcomp.loops.InvertStringTrueUnreachableLabel
"
ROOT="../jpf-abstraction"
PATTERN='s/^.*\.\([A-Z]*[a-z]*\)[a-zA-Z0-9]*$/\1/'

cd ${ROOT}

export max=0

for benchmark in $BENCHMARKS
do
    name=$(echo ${benchmark} | sed ${PATTERN})
    len=$(echo ${name} | wc -m)

    if [ ${len} -gt ${max} ]
    then
        max=${len}
    fi
done

for benchmark in $BENCHMARKS
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
        }

        /states/ {
            match($2, "new=([[:digit:]]+)", a);

            STATES=a[1];
        }

        /time/ {
            TIME=$3;
        }

        END {
            printf "	states: %s	time: ", STATES;

            system("expr $(date -d " TIME " +%s) - $(date -d 00:00:00 +%s) | sed '\''s/$/ s/'\''");
        }'
done
