
class grafana {

    require prometheus

    $PATH = "/bin:/usr/bin:/usr/sbin"
    $VERSION = '3.0.4-1464167696'

    exec { "download-grafana":
        command => "cp /vagrant/files/grafana/grafana-$VERSION.x86_64.rpm .",
        cwd => "/tmp",
        path => $PATH,
    }
    ->
    exec { "install-grafana":
        command => "yum -y install grafana-$VERSION.x86_64.rpm",
        cwd => "/tmp",
        path => $PATH,
    }
    ->
    service { 'grafana-server':
        ensure => running,
        enable => true,
    }
}
