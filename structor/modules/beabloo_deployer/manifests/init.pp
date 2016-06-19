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

class beabloo_deployer {

  $path="/bin:/usr/bin"

  exec { 'cp -r /vagrant/files/beabloo/jt/rpm/data-integration/ /opt/':
    cwd => '/',
    path => "$path"
  }
  file { "/opt/data-integration/kitchen.sh":
    ensure => "present",
    content => template('beabloo_deployer/kettle/kitchen.erb')
  }
  ->
  exec { 'chmod +x *.sh':
    cwd => '/opt/data-integration/',
    path => "$path"
  }
  ->
  exec { 'mkdir -p /opt/data-integration/oozie/subversion/':
    cwd => '/',
    path => "$path"
  }
  ->
  exec { 'mkdir -p /opt/oozie/':
    cwd => '/',
    path => "$path"
  }
  ->
  exec { 'mkdir -p /opt/sql/scripts/kettle/':
    cwd => '/',
    path => "$path"
  }
  ->
  file { "/opt/data-integration/oozie/subversion/cluster.conf":
    ensure => "present",
    content => template('beabloo_deployer/oozie/cluster.erb')
  }
  ->
  file { "/opt/data-integration/oozie/subversion/common.properties":
    ensure => "present",
    content => template('beabloo_deployer/oozie/common.erb')
  }
  ->
  file { "/opt/data-integration/oozie/subversion/update_from_svn.sh":
    ensure => "present",
    content => template('beabloo_deployer/oozie/update_from_svn.erb')
  }
  ->
  exec { 'chmod +x /opt/data-integration/oozie/subversion/update_from_svn.sh':
    cwd => '/',
    path => "$path"
  }
  ->
  exec { 'mkdir -p /opt/data-integration/beabloo_kettle/subversion/':
    cwd => '/',
    path => "$path"
  }
  ->
  file { "/opt/data-integration/beabloo_kettle/subversion/cluster.conf":
    ensure => "present",
    content => template('beabloo_deployer/kettle/cluster.erb')
  }
  ->
  file { "/opt/data-integration/beabloo_kettle/subversion/common.properties":
    ensure => "present",
    content => template('beabloo_deployer/kettle/common.erb')
  }
  ->
  file { "/opt/data-integration/beabloo_kettle/subversion/update_from_svn.sh":
    ensure => "present",
    content => template('beabloo_deployer/kettle/update_from_svn.erb')
  }
  ->
  exec { 'chmod +x /opt/data-integration/beabloo_kettle/subversion/update_from_svn.sh':
    cwd => '/',
    path => "$path"
  }
}