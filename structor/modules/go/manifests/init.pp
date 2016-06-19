
class go {

    $PATH = "/bin:/usr/bin:/usr/sbin"

    exec { "download-go":
        command => "wget https://storage.googleapis.com/golang/go1.6.2.linux-amd64.tar.gz",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    exec { "untar-go":
        command => "tar -C /usr/local -xvf go1.6.2.linux-amd64.tar.gz",
        path => $PATH,
        cwd => "/tmp",
    }
    ->
    file { "/etc/profile.d/go.sh":
        ensure => "file",
        content => template('go/go.erb'),
    }

}
