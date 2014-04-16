#!/bin/sh

METHOD=${1}

if [ ${METHOD} ]
then
    ESCAPED=$(echo ${METHOD} | sed 's/\./\\./g')
    FILTER='BEGIN {matches = 0; top = 0} /frame \[depth = 0\]/ {top = 1} /frame \[depth = 1\]/ {top = 0} /pc:/ {if ($2 ~ /^'${ESCAPED}'/) matches = 1} END {if (matches) print FILENAME}'
else
    FILTER='END {print FILENAME}'
fi

LIMIT=500

# Collect states where a thread entered the target method
STATES=$(find tmp -name "*.0" -exec awk "${FILTER}" {} \; | sort -t. -k 2n | head -n ${LIMIT})

# Statistics
echo "Exploring " $(wc -l <<END
${STATES}
END
) " states."

for s1 in ${STATES}
do
    # Compare only to states following states
    STATES=$(tail -n +2 <<END
${STATES}
END
)

    for s2 in ${STATES}
    do
        DIFF=$(diff ${s1} ${s2})

        # Skip states where program counters differ
        if ! grep -q pc: <<END
${DIFF}
END
        then
            # Count number of different lines for each pair
            printf "%s %s %i\n" ${s1} ${s2} $(sed -n '/^[<>]/p' <<END | wc -l
${DIFF}
END
)
        fi
    done
done | sort -t. -k 2n | sort -s -k 3n
