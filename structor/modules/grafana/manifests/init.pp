
class grafana {

    require prometheus

    $PATH = "/bin:/usr/bin:/usr/sbin"
    $VERSION = '3.0.4-1464167696'

    exec { "download-grafana":
        command => "wget https://grafanarel.s3.amazonaws.com/builds/grafana-$VERSION.linux-x64.tar.gz",
        cwd => "/tmp",
        path => $PATH,
    }
    ->
    exec { "install-grafana":
        command => "yum install -y grafana-$VERSION.linux-x64.tar.gz",
        cwd => "/tmp",
        path => $PATH,
    }
    ->
    service { 'grafana-server':
        ensure => running,
        enable => true,
    }
}
