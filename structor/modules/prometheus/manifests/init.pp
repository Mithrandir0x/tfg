
class prometheus {

    $PATH = "/bin:/usr/bin:/usr/sbin"

    $PROMETHEUS_DIR = "/opt/prometheus"
    $PROMETHEUS_DEFAULT_DIR = "$PROMETHEUS_DIR"
    $PROMETHEUS_LOCAL_DIR = "/etc/prometheus"

    $PUSHGATEWAY_DIR = "/opt/prometheus/pushgateway"
    $PUSHGATEWAY_DEFAULT_DIR = "$PUSHGATEWAY_DIR"
    $PUSHGATEWAY_LOCAL_DIR = "$$PROMETHEUS_LOCAL_DIR/pushgateway"

    $GROUP_ID = '63001'

    $VERSION_SANS_PLATFORM = '0.20.0'
    $VERSION = '0.20.0.linux-amd64'

    $PUSHGATEWAY_VERSION_SANS_PLATFORM = '0.3.0'
    $PUSHGATEWAY_VERSION = '0.3.0.linux-amd64'

    exec { "create-prometheus-group-prometheus":
        command => "groupadd -g $GROUP_ID prometheus",
        path => $PATH,
    }
    ->
    exec { "create-home-folder-prometheus":
        command => "mkdir -p /user/prometheus",
        path => $PATH,
    }
    ->
    exec { "create-pid-folder-prometheus":
        command => "mkdir -p /var/run/prometheus/",
        path => $PATH,
    }
    ->
    exec { "create-log-folder-prometheus":
        command => "mkdir -p /var/log/prometheus",
        path => $PATH,
    }
    ->
    exec { "create-pid-folder-pushgateway":
        command => "mkdir -p /var/run/pushgateway/",
        path => $PATH,
    }
    ->
    exec { "create-log-folder-pushgateway":
        command => "mkdir -p /var/log/pushgateway",
        path => $PATH,
    }
    ->
    exec { "create-prometheus-user-prometheus":
        command => "useradd -u $GROUP_ID -g $GROUP_ID -d /user/prometheus -s /bin/bash prometheus -c 'Prometheus service account'",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "set-perms-home-folder-prometheus":
        command => "chmod 700 /user/prometheus & chown -R prometheus:prometheus /user/prometheus",
        path => $PATH,
    }
    ->
    exec { "chown-pid-folder-prometheus":
        command => "chmod 700 /var/run/prometheus & chown -R prometheus:prometheus /var/run/prometheus",
        path => $PATH,
    }
    ->
    exec { "chown-log-folder-prometheus":
        command => "chmod 700 /var/log/prometheus & chown -R prometheus:prometheus /var/log/prometheus",
        path => $PATH,
    }
    ->
    exec { "set-pwd-exp-prometheus":
        command => "chage -I -1 -E -1 -m -1 -M -1 -W -1 -E -1 prometheus",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "download-prometheus":
        command => "cp /vagrant/files/prometheus/prometheus-$VERSION.tar.gz .",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "download-prometheus-pushgateway":
        command => "cp /vagrant/files/prometheus/pushgateway-$PUSHGATEWAY_VERSION.tar.gz .",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "untar-prometheus":
        command => "tar -zxvf /tmp/prometheus-$VERSION.tar.gz",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "untar-prometheus-pushgateway":
        command => "tar -zxvf /tmp/pushgateway-$PUSHGATEWAY_VERSION.tar.gz",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "move-prometheus-folder":
        command => "mv /tmp/prometheus-$VERSION $PROMETHEUS_DIR",
        path => $PATH,
    }
    ->
    exec { "move-prometheus-pushgateway-folder":
        command => "mv /tmp/pushgateway-$PUSHGATEWAY_VERSION $PUSHGATEWAY_DIR",
        path => $PATH,
    }
    ->
    exec { "chown-prometheus":
        command => "chown -R prometheus:prometheus $PROMETHEUS_DIR",
        path => $PATH,
    }
    ->
    exec { "create-prometheus-app-folder":
        command => "mkdir -p $PROMETHEUS_LOCAL_DIR",
        path => $PATH,
    }
    ->
    exec { "create-prometheus-pushgateway-app-folder":
        command => "mkdir -p $PUSHGATEWAY_LOCAL_DIR",
        path => $PATH,
    }
    ->
    file { "$PROMETHEUS_LOCAL_DIR/prometheus.yml":
        ensure => "present",
        content => template("prometheus/prometheus.yml.erb"),
    }
    ->
    exec { "chown-prometheus-app-folder":
        command => "chown -R prometheus:prometheus $PROMETHEUS_LOCAL_DIR",
        path => $PATH,
    }
    ->
    exec { "set-perms-app-folder":
        command => "chmod 750 $PROMETHEUS_LOCAL_DIR",
        path => $PATH,
    }
    ->
    file { "/etc/init.d/prometheus":
        ensure => "file",
        content => template('prometheus/prometheus.erb'),
        mode => 'a+rx',
    }
    ->
    file { "/etc/init.d/pushgateway":
        ensure => "file",
        content => template('prometheus/pushgateway.erb'),
        mode => 'a+rx',
    }
    ->
    service { 'prometheus':
        ensure => running,
        enable => true,
        status => "/usr/sbin/service  ${service} status",
    }
    ->
    service { 'pushgateway':
        ensure => running,
        enable => true,
        status => "/usr/sbin/service  ${service} status",
    }
}
