class nodejs {

    $PATH = "/bin:/usr/bin:/usr/sbin"

    package { "gcc-c++":
        ensure => installed,
    }
    ->
    package { "make":
        ensure => installed,
    }
    ->
    exec { "download-nodejs":
        command => "wget https://nodejs.org/dist/v4.4.3/node-v4.4.3-linux-x64.tar.xz",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "untar-nodejs":
        command => "tar -xf node-v4.4.3-linux-x64.tar.xz",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "move-nodejs":
        command => "mv /tmp/node-v4.4.3-linux-x64 /usr/local/node",
        path => $PATH,
    }

}