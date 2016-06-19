
class redis {

	$PATH = "/bin:/usr/bin:/usr/sbin"

	exec { "download-redis":
		command => "wget http://download.redis.io/releases/redis-2.8.3.tar.gz",
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
	exec { "make-install-redis":
		command => "make && make install",
		path => $PATH,
		cwd => "/opt/redis-2.8.3",
	}

}