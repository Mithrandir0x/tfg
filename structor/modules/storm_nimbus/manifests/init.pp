
class storm_nimbus {
	require storm_base

	file { '/etc/init.d/storm-nimbus':
		ensure => file,
		content => template('storm_nimbus/storm-nimbus.erb'),
		mode => 'a+rx',
	}
 	->
 	service { 'storm-nimbus':
 		ensure => running,
 		enable => true,
 	}
}
