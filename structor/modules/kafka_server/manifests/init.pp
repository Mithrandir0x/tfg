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

class kafka_server {
  require repos_setup
  require jdk

  $path="/bin:/usr/bin"
 
  # Install and enable. 
  package { "kafka" :
    ensure => installed,
  }
  ->
  # Remove default kafka properties
  exec { 'rm /etc/kafka/conf/consumer.properties':
    cwd => '/',
    path => "$path"
  }
  ->
  exec { 'rm /etc/kafka/conf/producer.properties':
    cwd => '/',
    path => "$path"
  }
  ->
  exec { 'rm /etc/kafka/conf/server.properties':
    cwd => '/',
    path => "$path"
  }
  ->
  # Configure.
  file { '/etc/kafka/conf/consumer.properties':
    ensure => "present",
    content => template('kafka_server/consumer.properties.erb'),
  }
  ->
  file { '/etc/kafka/conf/producer.properties':
    ensure => "present",
    content => template('kafka_server/producer.properties.erb'),
  }
  ->
  file { '/etc/kafka/conf/server.properties':
    ensure => "present",
    content => template('kafka_server/server.properties.erb'),
  }
  ->
  file { "/etc/init.d/kafka":
    ensure => "present",
    content => template('kafka_server/kafka_initd.erb'),
    mode => '755',
  }
  ->
  file { "/usr/bin/kafka-topics":
    ensure => "present",
    mode => '755',
    content => template('kafka_server/kafka-topics.erb'),
  }
  ->
  file { "/usr/bin/kafka-configs":
    ensure => "present",
    mode => '755',
    content => template('kafka_server/kafka-configs.erb'),
  }
  ->
  service { 'kafka':
    ensure => running,
    enable => true,
  }
}
