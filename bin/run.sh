#!/bin/sh

java -Xmx2g -jar ./lib/jpf-core/build/RunJPF.jar +site=site.properties $@
