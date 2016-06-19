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

class beabloo_deployer_local {

  $path="/bin:/usr/bin"

  define commonClusterOozieFile($template = 'beabloo_deployer_local/new/oozie/common.erb') {
    file { $name:
      content => template($template)
    }
  }

  define commonClusterKettleFile($template = 'beabloo_deployer_local/new/kettle/common.erb') {
    file { $name:
      content => template($template)
    }
  }

  # Pentaho Data Integration base installation
  exec { 'cp -r /vagrant/files/beabloo/jt/rpm/data-integration/ /opt/':
    cwd => '/',
    path => "$path"
  }
  ->
  file { "/opt/data-integration/kitchen.sh":
    ensure => "present",
    content => template('beabloo_deployer_local/common/kettle/kitchen.erb')
  }
  ->
  exec { 'chmod +x *.sh':
    cwd => '/opt/data-integration/',
    path => "$path"
  }
  ->
  # Required folders
  exec { 'mkdir -p /opt/data-integration/oozie/subversion/':
    cwd => '/',
    path => "$path"
  }
  ->
  exec { 'mkdir -p /opt/oozie/ /opt/oozie-hq/ /opt/oozie-pre/':
    cwd => '/',
    path => "$path"
  }
  ->
  exec { 'mkdir -p /opt/data-integration/beabloo_kettle-hq/ /opt/data-integration/beabloo_kettle-pre/':
    cwd => '/',
    path => "$path"
  }
  ->
  exec { 'mkdir -p /opt/scripts/':
    cwd => '/',
    path => "$path"
  }
  ->
  file { "/opt/scripts/library.sh":
    ensure => "present",
    content => template('beabloo_deployer_local/new/oozie/library.erb')
  }
  ->
  exec { 'mkdir -p /opt/sql/scripts/kettle/':
    cwd => '/',
    path => "$path"
  }
  ->
  # Oozie common resources
  file { "/opt/data-integration/oozie/subversion/cluster.conf":
    ensure => "present",
    content => template('beabloo_deployer_local/common/oozie/cluster.erb')
  }
  ->
  # Oozie old deployer
  file { "/opt/data-integration/oozie/subversion/common.properties":
    ensure => "present",
    content => template('beabloo_deployer_local/old/oozie/common.erb')
  }
  ->
  file { "/opt/data-integration/oozie/subversion/update_from_svn.sh.old":
    ensure => "present",
    content => template('beabloo_deployer_local/old/oozie/update_from_svn.erb')
  }
  ->
  exec { 'chmod +x /opt/data-integration/oozie/subversion/update_from_svn.sh.old':
    cwd => '/',
    path => "$path"
  }
  ->
  # Oozie deployer
  commonClusterOozieFile { "/opt/data-integration/oozie/subversion/common-hq.properties":
  }
  ->
  commonClusterOozieFile { "/opt/data-integration/oozie/subversion/common-pre.properties":
  }
  ->
  file { "/opt/data-integration/oozie/subversion/update_from_svn.sh":
    ensure => "present",
    content => template('beabloo_deployer_local/new/oozie/update_from_svn.erb')
  }
  ->
  exec { 'chmod +x /opt/data-integration/oozie/subversion/update_from_svn.sh':
    cwd => '/',
    path => "$path"
  }
  ->
  # Kettle deployer
  exec { 'mkdir -p /opt/data-integration/beabloo_kettle/subversion/':
    cwd => '/',
    path => "$path"
  }
  ->
  file { "/opt/data-integration/beabloo_kettle/subversion/cluster.conf":
    ensure => "present",
    content => template('beabloo_deployer_local/common/kettle/cluster.erb')
  }
  ->
  file { "/opt/data-integration/beabloo_kettle/subversion/common.properties":
    ensure => "present",
    content => template('beabloo_deployer_local/old/kettle/common.erb')
  }
  ->
  file { "/opt/data-integration/beabloo_kettle/subversion/update_from_svn.sh.old":
    ensure => "present",
    content => template('beabloo_deployer_local/old/kettle/update_from_svn.erb')
  }
  ->
  exec { 'chmod +x /opt/data-integration/beabloo_kettle/subversion/update_from_svn.sh.old':
    cwd => '/',
    path => "$path"
  }
  ->
  commonClusterKettleFile { "/opt/data-integration/beabloo_kettle/subversion/common-hq.properties":
  }
  ->
  commonClusterKettleFile { "/opt/data-integration/beabloo_kettle/subversion/common-pre.properties":
  }
  ->
  file { "/opt/data-integration/beabloo_kettle/subversion/update_from_svn.sh":
    ensure => "present",
    content => template('beabloo_deployer_local/new/kettle/update_from_svn.erb')
  }
  ->
  exec { 'chmod +x /opt/data-integration/beabloo_kettle/subversion/update_from_svn.sh':
    cwd => '/',
    path => "$path"
  }
  ->
  # Expression Language Functions deployer (ELFunctions)
  exec { 'mkdir -p /opt/data-integration/elfunctions/subversion/':
    cwd => '/',
    path => "$path"
  }
  ->
  file { "/opt/data-integration/elfunctions/subversion/cluster.conf":
    ensure => "present",
    content => template('beabloo_deployer_local/new/elfunctions/cluster.erb')
  }
  ->
  commonClusterKettleFile { "/opt/data-integration/elfunctions/subversion/common-hq.properties":
  }
  ->
  commonClusterKettleFile { "/opt/data-integration/elfunctions/subversion/common-pre.properties":
  }
  ->
  file { "/opt/data-integration/elfunctions/subversion/update_from_svn.sh":
    ensure => "present",
    content => template('beabloo_deployer_local/new/elfunctions/update_from_svn.erb')
  }
  ->
  exec { 'chmod +x /opt/data-integration/elfunctions/subversion/update_from_svn.sh':
    cwd => '/',
    path => "$path"
  }
}
