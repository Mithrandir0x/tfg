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

class hive_server2 {
  require hive_client
  require os_performance

  $path="/bin:/usr/bin"
  $component = "hive-server2"
  if ($hdp_version_major+0 <= 2 and $hdp_version_minor+0 <= 2) {
    $start_script="/usr/hdp/$hdp_version/etc/$platform_start_script_path/$component"
  }
  else {
    $start_script="/usr/hdp/$hdp_version/hive/etc/$platform_start_script_path/$component"
  }

  if $security == "true" {
    require hive_keytab
  }

  package { "hive${package_version}-server2":
    ensure => installed,
  }
  ->
  exec { "hdp-select set hive-server2 ${hdp_version}":
    cwd => "/",
    path => "$path",
  }
  ->
  service { 'hive-server2':
    ensure => running,
    enable => true,
  }

  file { "/home/vagrant/extractHiveServer2Queries.py":
    ensure => "file",
    source => 'puppet:///modules/hive_server2/extractHiveServer2Queries.py',
    owner => vagrant,
    group => vagrant,
  }

  # Startup.
  if ($hdp_version_major+0 == 2 and $hdp_version_minor+0 <= 2) {
    file { "$start_script":
      ensure => file,
      source => "puppet:///modules/hive_server2/hive-server2",
      mode => '755',
      owner => root,
      group => root,
      replace => true,
      before => Service["hive-server2"],
    }
  }
  file { "/etc/init.d/hive-server2":
    ensure => 'link',
    target => "$start_script",
    before => Service["hive-server2"],
  }
}
