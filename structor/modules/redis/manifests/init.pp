
class redis {

	$PATH = "/bin:/usr/bin:/usr/sbin:/usr/local/bin/"
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
	->
	file { '/tmp/install_redis_service.sh':
		ensure => file,
		content => template('redis/install_redis_service.erb'),
		mode => '+x',
	}
	->
	exec { "install-service-redis":
		command => "/tmp/install_redis_service.sh",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	service { 'redis_6379':
		ensure => running,
		enable => true,
	}

}