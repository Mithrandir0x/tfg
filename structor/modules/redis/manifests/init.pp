
class redis {

	$PATH = "/bin:/usr/bin:/usr/sbin"
	$VERSION = '3.2.1'

	exec { "download-redis":
		command => "cp /vagrant/files/redis/redis-$VERSION.tar.gz .",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "untar-redis":
		command => "tar -zxvf redis-$VERSION.tar.gz",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "move-redis":
		command => "mv redis-$VERSION /opt/",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "make-redis":
		command => "make",
		path => $PATH,
		cwd => "/opt/redis-$VERSION",
	}
	->
	exec { "install-redis":
		command => "make install",
		path => $PATH,
		cwd => "/opt/redis-$VERSION",
	}

}