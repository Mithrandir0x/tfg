#!/bin/bash

REDIS_VERSION='<%= scope.lookupvar('redis::VERSION') %>'

SECURE_REDIS=$(expect -c "
set timeout 10
spawn /opt/redis-$REDIS_VERSION/utils/install_server.sh

expect \"Please select the redis port for this instance:\"
send \"\r\"

expect \"Please select the redis config file name\"
send \"\r\"

expect \"Please select the redis log file name\"
send \"\r\"

expect \"Please select the data directory for this instance\"
send \"\r\"

expect \"Please select the redis executable path\"
send \"\r\"

expect \"Is this ok? Then press ENTER to go on or Ctrl-C to abort.\"
send \"\r\"

expect eof
")

echo "$SECURE_REDIS"
