
# --without-libsodium no crypto support?

class zeromq {
	require jdk

	$VERSION = "4.1.4"

	$PATH = "/bin:/usr/bin"

	package { 'gcc':
		ensure => installed,
	}
	->
	package { 'gcc-c++':
		ensure => installed,
	}
	->
	package { 'libuuid-devel':
		ensure => installed,
	}
	->
	package { 'libtool':
		ensure => installed,
	}
	->
	exec { "download-zmq":
		command => "wget http://download.zeromq.org/zeromq-$VERSION.tar.gz",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "unzip-zmq":
		command => "tar -xzf zeromq-$VERSION.tar.gz",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "configure-zmq":
		command => "sh configure --without-libsodium",
		path => $PATH,
		cwd => "/tmp/zeromq-$VERSION",
	}
	->
	exec { "compile-and-install-zmq":
		command => "make install",
		path => $PATH,
		cwd => "/tmp/zeromq-$VERSION",
	}
	->
	exec { "download-jzmq":
		command => "cp /vagrant/files/storm/jzmq-master.zip .",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "unzip-jzmq":
		command => "unzip jzmq-master.zip",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "autogen-jzmq":
		command => "sh autogen.sh",
		path => $PATH,
		cwd => "/tmp/jzmq-master/jzmq-jni",
	}
	->
	exec { "configure-jzmq":
		command => "sh configure",
		path => $PATH,
		cwd => "/tmp/jzmq-master/jzmq-jni",
	}
	->
	exec { "make-jzmq":
		command => "make",
		path => $PATH,
		cwd => "/tmp/jzmq-master/jzmq-jni",
	}
	->
	exec { "make-install-jzmq":
		command => "make install",
		path => $PATH,
		cwd => "/tmp/jzmq-master/jzmq-jni",
	}
}