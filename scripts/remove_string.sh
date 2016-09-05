#!/bin/bash
#
# Copyright (C) 2013 Reece H. Dunn
# License: GPLv3+
#
# Helper utility for removing a string/string-list item in all string resources.

ls src/main/res/values*/strings.xml | while read STRINGS ; do
    xmlstarlet ed -P -d "/resources/*[@name='${1}']" ${STRINGS} > /tmp/strings.xml
    mv /tmp/strings.xml ${STRINGS}
done
