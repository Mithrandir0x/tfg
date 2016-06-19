
class storm_base {
	require zeromq

	$PATH = "/bin:/usr/bin:/usr/sbin"

	$STORM_DIR = "/usr/hdp/current/storm"
	$STORM_DEFAULT_DIR = "$STORM_DIR/storm"
	$STORM_LOCAL_DIR = "/etc/storm"

	$VERSION = "${storm_version}"

	if $VERSION == '1.0.0' {
		$STORM_YAML_TEMPLATE = 'storm_base/storm.new.yaml.erb'
	} else {
		$STORM_YAML_TEMPLATE = 'storm_base/storm.yaml.erb'
	}

	exec { "create-storm-group":
		command => "groupadd -g 53001 storm",
		path => $PATH,
	}
	->
	exec { "create-home-folder-storm":
		command => "mkdir -p /user/storm",
		path => $PATH,
	}
	->
	exec { "create-pid-folder-storm":
		command => "mkdir -p /var/run/storm/",
		path => $PATH,
	}
	->
	exec { "create-log-folder-storm":
		command => "mkdir -p /var/log/storm",
		path => $PATH,
	}
	->
	exec { "create-storm-user":
		command => "useradd -u 53001 -g 53001 -d /user/storm -s /bin/bash storm -c 'Storm service account'",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "set-perms-home-folder-storm":
		command => "chmod 700 /user/storm & chown -R storm:storm /user/storm",
		path => $PATH,
	}
	->
	exec { "chown-pid-folder-storm":
		command => "chmod 700 /var/run/storm & chown -R storm:storm /var/run/storm",
		path => $PATH,
	}
	->
	exec { "chown-log-folder-storm":
		command => "chmod 700 /var/log/storm & chown -R storm:storm /var/log/storm",
		path => $PATH,
	}
	->
	exec { "set-pwd-exp-storm":
		command => "chage -I -1 -E -1 -m -1 -M -1 -W -1 -E -1 storm",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "download-storm":
		command => "cp /vagrant/files/storm/storm-$VERSION.zip storm-$VERSION.zip",
		path => $PATH,
		cwd => "/tmp",
	}
	->
	exec { "create-storm-folder":
		command => "mkdir -p $STORM_DIR",
		path => $PATH,
	}
	->
	exec { "unzip-storm":
		command => "unzip /tmp/storm-$VERSION.zip",
		path => $PATH,
		cwd => "$STORM_DIR",
	}
	->
	exec { "chown-storm":
		command => "chown -R storm:storm apache-storm-$VERSION",
		path => $PATH,
		cwd => "$STORM_DIR",
	}
	->
	exec { "set-default-storm":
		command => "ln -s apache-storm-$VERSION storm",
		path => $PATH,
		cwd => "$STORM_DIR",
	}
	->
	exec { "create-storm-app-folder":
		command => "mkdir -p $STORM_LOCAL_DIR",
		path => $PATH,
	}
	->
	exec { "chown-storm-app-folder":
		command => "chown -R storm:storm $STORM_LOCAL_DIR",
		path => $PATH,
	}
	->
	exec { "set-perms-app-folder-storm":
		command => "chmod 750 $STORM_LOCAL_DIR",
		path => $PATH,
	}
	->
	file { "$STORM_DIR/storm/conf/storm.yaml":
    	ensure => "present",
		content => template("$STORM_YAML_TEMPLATE"),
	}
	->
	exec { "create-storm-symlink":
		command => "ln -s /usr/hdp/current/storm/storm/bin/storm /usr/local/bin/storm",
		path => $PATH,
	}
}
