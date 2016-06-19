
class grafana {

    require prometheus

    $PATH = "/bin:/usr/bin:/usr/sbin"

    exec { "download-grafana":
        command => "wget https://grafanarel.s3.amazonaws.com/builds/grafana-2.6.0-1.x86_64.rpm",
        cwd => "/tmp",
        path => $PATH,
    }
    ->
    exec { "install-grafana":
        command => "yum install -y grafana-2.6.0-1.x86_64.rpm",
        cwd => "/tmp",
        path => $PATH,
    }
    ->
    service { 'grafana-server':
        ensure => running,
        enable => true,
    }
}
