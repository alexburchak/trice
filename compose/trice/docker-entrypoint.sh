#!/bin/bash

if [ "$1" = '' ]; then
    /usr/lib/trice/bin/trice.sh
fi

exec "$@"
