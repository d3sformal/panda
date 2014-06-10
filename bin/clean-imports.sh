#!/bin/sh

cleanfile () {
    FILE=$1

    BEFORE=$(awk 'BEGIN {silence = 0} /^import .*;$/ {silence = 1} {if (silence == 0) print $0}' ${FILE})
    IMPORTS=$(grep '^import .*;$' ${FILE} | sort -u)
    AFTER=$(sed '1!G;h;$!d' ${FILE} | awk 'BEGIN {silence = 0} /^import .*;$/ {silence = 1} {if (silence == 0) print $0}' | sed '1!G;h;$!d')

    if [ -n "${IMPORTS}" ]
    then
        JAVAIMPORTS=$(echo "${IMPORTS}" | grep '^import java')
        JAVASTATICIMPORTS=$(echo "${IMPORTS}" | grep '^import static java')

        OTHERCATEGORIES="$(echo "${IMPORTS}" | grep -v '^import static ' | sed -n '/^import java/!s/^import \([^.]*\).*$/\1/p' | sort -u)"
        OTHERSTATICCATEGORIES="$(echo "${IMPORTS}" | grep '^import static ' | sed -n '/^import static java/!s/^import static \([^.]*\).*$/\1/p' | sort -u)"

        echo "${BEFORE}" > ${FILE}
        echo >> ${FILE}

        if [ -n "${JAVAIMPORTS}" ]
        then
            echo "${JAVAIMPORTS}" >> ${FILE}
            echo >> ${FILE}
        fi

        if [ -n "${JAVASTATICIMPORTS}" ]
        then
            echo "${JAVASTATICIMPORTS}" >> ${FILE}
            echo >> ${FILE}
        fi

        for CATEGORY in ${OTHERCATEGORIES}
        do
            if [ -n "${CATEGORY}" ]
            then
                OTHERIMPORTS=$(echo "${IMPORTS}" | grep '^import '"${CATEGORY}")

                echo "${OTHERIMPORTS}" >> ${FILE}
                echo >> ${FILE}
            fi
        done

        for CATEGORY in ${OTHERSTATICCATEGORIES}
        do
            if [ -n "${CATEGORY}" ]
            then
                OTHERSTATICIMPORTS=$(echo "${IMPORTS}" | grep '^import static '"${CATEGORY}")

                echo "${OTHERSTATICIMPORTS}" >> ${FILE}
                echo >> ${FILE}
            fi
        done

        echo "${AFTER}" | tail -n +2 >> ${FILE}
    fi
}

for FILE in $(find src -name "*.java")
do
    cleanfile ${FILE}
done
