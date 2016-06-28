#!/bin/bash

SECURE_MYSQL=$(expect -c "
set timeout 10
spawn mysql_secure_installation

expect \"Please select the redis port for this instance: [6379]\"
send \"\r\"

expect \"Please select the redis config file name [/etc/redis/6379.conf]\"
send \"\r\"

expect \"Please select the redis log file name [/var/log/redis_6379.log]\"
send \"\r\"

expect \"Please select the data directory for this instance [/var/lib/redis/6379]\"
send \"\r\"

expect \"Please select the redis executable path [/usr/local/bin/redis-server]\"
send \"\r\"

expect \"Is this ok? Then press ENTER to go on or Ctrl-C to abort.\"
send \"\r\"

expect eof
")

echo "$SECURE_MYSQL"
