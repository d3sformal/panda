#!/bin/sh

# takes all program states where some thread is just executing the given method, and then performs a diff over each pair of states with the same program counters
# arguments: METHOD=<package>.<class>.<method name> OFFSET=<n> LIMIT=<n>

OFFSET=0
LIMIT=50
PREVIEW=100

while [ $# -gt 0 ]
do
    eval ${1}
    shift 1
done

if [ ${THREAD} ]
then
    THREADSELECTIONINIT='
            head = 0;
            selected = 0;'

    THREADSELECTION='
        /^\W+id: [0-9]+$/ {
            if (head && $2 == '${THREAD}') {
                selected = 1;
            }
        }

        {
            head = 0;
        }

        /=+ Thread =+/ {
            head = 1;
            selected = 0;
        }'

    MASK='
        BEGIN {'${THREADSELECTIONINIT}'
        }
'${THREADSELECTION}'
        {
            if (selected) {
                print $0;
            }
        }'
else
    THREADSELECTIONINIT='
            selected = 1;'
    THREADSELECTION=''
    MASK='{print $0}'
fi

if [ ${METHOD} ]
then
    ESCAPED=$(echo ${METHOD} | sed 's/\./\\./g')
    FILTER='
        BEGIN {'${THREADSELECTIONINIT}'
            matches = 0;
            top = 0;
        }
'${THREADSELECTION}'

        /frame \[depth = 0\]/ {
            top = 1;
        }

        /frame \[depth = 1\]/ {
            top = 0;
        }

        /pc:/ {
            if (selected && top && $2 ~ /^'${ESCAPED}'/) {
                matches = 1;
            }
        }

        END {
            if (matches) {
                print FILENAME;
            }
        }'
else
    FILTER='END {print FILENAME}'
fi

# Collect states where a thread entered the target method
STATES=$(find tmp -name "*.0" -exec awk "${FILTER}" {} \; | sort -t. -k 2n | tail -n +${OFFSET} | head -n ${LIMIT})

if [ -z "${STATES}" ]
then
    COUNT=0
else
    COUNT=$(echo "${STATES}" | wc -l)
fi

# Statistics
echo "Exploring " ${COUNT} " states."

echo "${STATES}" | head -n ${PREVIEW}
if [ ${PREVIEW} -lt ${COUNT} ]
then
    echo "..."
fi

DIFFS=$(

for s1 in ${STATES}
do
    # Compare only to states following states
    STATES=$(echo "${STATES}" | tail -n +2)

    for s2 in ${STATES}
    do
        DIFF=$(diff ${s1} ${s2})

        # Skip states where program counters differ
        if ! echo "${DIFF}" | grep -q pc:
        then
            # Count number of different lines for each pair
            printf "%s %s %i\n" ${s1} ${s2} $(echo "${DIFF}" | sed -n '/^[<>]/p' | wc -l)
        fi
    done
done | sort -s -k 3n

)

if [ -z "${DIFFS}" ]
then
    DIFFCOUNT=0
else
    DIFFCOUNT=$(echo "${DIFFS}" | wc -l)
fi

echo
echo "Found " ${DIFFCOUNT} " interesting differences."
echo

if [ -t 1 ]
then
    (
        for pair in $(echo "${DIFFS}" | sed 's/ [0-9]*$//;s/ /:/')
        do
            s1=$(echo ${pair} | cut -d":" -f1)
            s2=$(echo ${pair} | cut -d":" -f2)

            sh -x -c "diff -y --suppress-common-lines ${s1} ${s2}"

            #read
            echo
        done
    )
else
    echo "${DIFFS}"
fi
