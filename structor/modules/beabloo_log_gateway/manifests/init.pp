#  Licensed to the Apache Software Foundation (ASF) under one or more
#   contributor license agreements.  See the NOTICE file distributed with
#   this work for additional information regarding copyright ownership.
#   The ASF licenses this file to You under the Apache License, Version 2.0
#   (the "License"); you may not use this file except in compliance with
#   the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

class beabloo_log_gateway {
  require jdk

  $PATH = "/bin:/usr/bin:/usr/sbin"
  
  $VERSION = "9.3.8"
  $JETTY_DEFAULT_DIR = "/opt/jetty-$VERSION"

  exec { "create-jetty-group":
    command => "groupadd -g 55001 jetty",
    path => $PATH,
  }
  ->
  exec { "create-home-folder":
    command => "mkdir -p /home/jetty",
    path => $PATH,
  }
  ->
  exec { "create-pid-folder":
    command => "mkdir -p /var/run/jetty/",
    path => $PATH,
  }
  ->
  exec { "create-log-folder":
    command => "mkdir -p /var/log/jetty",
    path => $PATH,
  }
  ->
  exec { "create-jetty-user":
    command => "useradd -u 55001 -g 55001 -d /home/jetty -s /bin/bash jetty -c 'Jetty service account'",
    path => $PATH,
    cwd => "/tmp",
  }
  ->
  exec { "set-perms-home-folder":
    command => "chmod 700 /user/jetty & chown -R jetty:jetty /home/jetty",
    path => $PATH,
  }
  ->
  exec { "chown-pid-folder":
    command => "chmod 700 /var/run/jetty & chown -R jetty:jetty /var/run/jetty",
    path => $PATH,
  }
  ->
  exec { "chown-log-folder":
    command => "chmod 700 /var/log/jetty & chown -R jetty:jetty /var/log/jetty",
    path => $PATH,
  }
  exec { "untar-jetty":
    command => "tar -zxvf /vagrant/files/beabloo/log_gateway/jetty-$VERSION.tar.gz",
    cwd => "/opt",
    path => $PATH
  }
  ->
  file { "$JETTY_DEFAULT_DIR/start.ini":
    ensure => "present",
    content => template("beabloo_log_gateway/start.erb")
  }
  ->
  exec { "mkdir -p /opt/scripts/jetty":
    cwd => "/opt",
    path => $PATH
  }
  ->
  file { "/opt/scripts/jetty/deploy_log_gateway.sh":
    ensure => "present",
    content => template("beabloo_log_gateway/deploy_log_gateway.erb"),
    mode => "700"
  }
  ->
  file { "/etc/profile.d/jetty.sh":
    ensure => "present",
    content => template("beabloo_log_gateway/jetty_env.erb"),
    mode => "700"
  }
  ->
  file { "/etc/init.d/jetty":
    ensure => "present",
    content => template("beabloo_log_gateway/jetty_initd.erb"),
    mode => "700"
  }
  ->
  service {"jetty":
    ensure => running,
    enable => true,
  }

}
