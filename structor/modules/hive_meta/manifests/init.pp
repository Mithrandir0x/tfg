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

class hive_meta {
  require hive_client
  require hive_db

  $path="/bin:/usr/bin"
  $component = "hive-metastore"
  if ($hdp_version_major+0 <= 2 and $hdp_version_minor+0 <= 2) {
    $start_script="/usr/hdp/$hdp_version/etc/$platform_start_script_path/$component"
  }
  else {
    $start_script="/usr/hdp/$hdp_version/hive/etc/$platform_start_script_path/$component"
  }

  if $security == "true" {
    require hive_keytab
  }

  package { "hive${package_version}-metastore":
    ensure => installed,
  }
  ->
  exec { "hdp-select set hive-metastore ${hdp_version}":
    cwd => "/",
    path => "$path",
  }
  ->
  exec { "schematool -dbType mysql -initSchema":
    user => "hive",
    cwd => "/",
    path => "/usr/hdp/current/hive-metastore/bin:$path",
    unless => 'schematool -dbType mysql -info',
  }
  ->
  service { 'hive-metastore':
    ensure => running,
    enable => true,
  }

  # Startup.
  if ($operatingsystem == "centos" and $operatingsystemmajrelease == "7") {
    file { "/etc/systemd/system/hive-metastore.service":
      ensure => 'file',
      source => "/vagrant/files/systemd/hive-metastore.service",
      before => Service["hive-metastore"],
    }
    file { "/etc/systemd/system/hive-metastore.service.d":
      ensure => 'directory',
    } ->
    file { "/etc/systemd/system/hive-metastore.service.d/default.conf":
      ensure => 'file',
      source => "/vagrant/files/systemd/hive-metastore.service.d/default.conf",
      before => Service["hive-metastore"],
    }
  } else {
    if ($hdp_version_major+0 == 2 and $hdp_version_minor+0 <= 2) {
      file { "$start_script":
        ensure => file,
        content => template('hive_meta/hive-metastore.erb'),
        mode => '755',
        owner => root,
        group => root,
        replace => true,
        before => Service["hive-metastore"],
      }
    }
    file { "/etc/init.d/hive-metastore":
      ensure => 'link',
      target => "$start_script",
      before => Service["hive-metastore"],
    }
  }
}
