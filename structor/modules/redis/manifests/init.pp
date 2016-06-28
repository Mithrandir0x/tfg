
class redis {

	$PATH = "/bin:/usr/bin:/usr/sbin"

	exec { "download-redis":
		command => "cp /vagrant/files/redis/redis-2.8.3.tar.gz .",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "untar-redis":
		command => "tar -zxvf redis-2.8.3.tar.gz",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "move-redis":
		command => "mv redis-2.8.3 /opt/",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "make-redis":
		command => "make && make install",
		path => $PATH,
		cwd => "/opt/redis-2.8.3",
	}
	->
	exec { "install-redis":
		command => "make install",
		path => $PATH,
		cwd => "/opt/redis-2.8.3",
	}
	->
	exec { "test-redis":
		command => "make test",
		path => $PATH,
		cwd => "/opt/redis-2.8.3",
	}

}