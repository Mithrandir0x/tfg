
class storm_supervisor {
	require storm_base

	file { '/etc/init.d/storm-supervisor':
		ensure => file,
		content => template('storm_supervisor/storm-supervisor.erb'),
		mode => 'a+rx',
	}
 	->
 	service { 'storm-supervisor':
 		ensure => running,
 		enable => true,
 	}
}
