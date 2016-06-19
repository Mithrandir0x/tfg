
class kafka_manager {
    $PATH = "/bin:/usr/bin:/usr/sbin"

    exec { "install-kafka-manager":
        command => "yum -y install /vagrant/files/beabloo/nn/rpm/kafka-manager-1.3.0.7-1.noarch.rpm",
        path => $PATH
    }
    ->
    file { '/etc/kafka-manager/application.conf':
        ensure => file,
        content => template('kafka_manager/application.conf.erb'),
    }
    ->
    file { '/etc/kafka-manager/application.ini':
        ensure => file,
        content => template('kafka_manager/application.ini.erb'),
    }
    ->
    service { 'kafka-manager':
        ensure => running,
        enable => true,
    }

}
