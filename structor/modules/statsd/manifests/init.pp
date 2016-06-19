
class statsd {
    require nodejs
    require go

    $PATH = "/bin:/usr/bin:/usr/sbin"

    $GROUP_ID = '63002'

    $PORT = '8125'

    exec { "create-statsd-group":
        command => "groupadd -g $GROUP_ID statsd",
        path => $PATH,
    }
    ->
    exec { "create-home-folder-statsd":
        command => "mkdir -p /user/statsd",
        path => $PATH,
    }
    ->
    exec { "create-pid-folder-statsd":
        command => "mkdir -p /var/run/statsd/",
        path => $PATH,
    }
    ->
    exec { "create-log-folder-statsd":
        command => "mkdir -p /var/log/statsd",
        path => $PATH,
    }
    ->
    exec { "create-statsd-user-statsd":
        command => "useradd -u $GROUP_ID -g $GROUP_ID -d /user/statsd -s /bin/bash statsd -c 'Prometheus service account'",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "set-perms-home-folder-statsd":
        command => "chmod 700 /user/statsd & chown -R statsd:statsd /user/statsd",
        path => $PATH,
    }
    ->
    exec { "chown-pid-folder-statsd":
        command => "chmod 700 /var/run/statsd & chown -R statsd:statsd /var/run/statsd",
        path => $PATH,
    }
    ->
    exec { "chown-log-folder-statsd":
        command => "chmod 700 /var/log/statsd & chown -R statsd:statsd /var/log/statsd",
        path => $PATH,
    }
    ->
    exec { "set-pwd-exp-statsd":
        command => "chage -I -1 -E -1 -m -1 -M -1 -W -1 -E -1 statsd",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "download-statsd":
        command => "wget https://github.com/etsy/statsd/archive/v0.7.2.tar.gz -O statsd-v0.7.2.tar.gz",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "unzip-statsd":
        command => "tar -zxvf statsd-v0.7.2.tar.gz",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "mv-statsd":
        command => "mv /tmp/statsd-0.7.2 /opt/statsd",
        path => $PATH,
    }
    ->
    exec { "etc-folder-statsd":
        command => "mkdir -p /etc/statsd",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    file { "/etc/statsd/config.js":
        ensure => "file",
        content => template('statsd/config.js.erb'),
    }
    ->
    file { "/etc/init.d/statsd":
        ensure => "file",
        content => template('statsd/statsd.erb'),
        mode => 'a+rx',
    }
    ->
    exec { "set-statsd-bin":
        command => "ln -s /usr/local/bin/node /usr/bin/node",
        path => $PATH,
    }
    ->
    service { 'statsd':
        ensure => running,
        enable => true,
        status => "/usr/sbin/service  ${service} status",
    }

}
