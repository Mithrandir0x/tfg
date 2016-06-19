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

class hue {
  $path="/usr/bin"
  
  package { "cyrus-sasl-gssapi":
    ensure => installed
  }
  ->
  package { "cyrus-sasl-plain":
    ensure => installed
  }
  ->
  package { "libxml2":
    ensure => installed
  }
  ->
  package { "libxslt":
    ensure => installed
  }
  ->
  package { "zlib":
    ensure => installed
  }
  ->
  package { "python":
    ensure => installed
  }
  ->
  package { "sqlite":
    ensure => installed
  }
  ->
  package { "python-psycopg2":
    ensure => installed
  }
  ->
  package { "postgresql-devel":
    ensure => installed
  }
  ->
  package { "libyaml":
    ensure => installed
  }
  ->
  exec { "hue-install-hue-common-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-common-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-useradmin-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-useradmin-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-security-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-security-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-doc-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-doc-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-server-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-server-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-zookeeper-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-zookeeper-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-beeswax-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-beeswax-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-hbase-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-hbase-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-impala-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-impala-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-pig-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-pig-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-rdbms-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-rdbms-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-search-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-search-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-spark-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-spark-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-sqoop-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-sqoop-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  exec { "hue-install-hue-3.8.1-1.el6.x86_64":
    cwd => '/vagrant/files/beabloo/nn/rpm/hue-3.8.1',
    command => 'yum -y install ./hue-3.8.1-1.el6.x86_64.rpm',
    path => "$path"
  }
  ->
  file { "/etc/hue/conf/hue.ini":
    ensure => "file",
    content => template('hue/hue.ini.erb')
  }
  ->
  service { "hue":
    ensure => running,
    enable => true,
  }
}