
class storm_ui {
	require storm_base

	file { '/etc/init.d/storm-ui':
		ensure => file,
		content => template('storm_ui/storm-ui.erb'),
		mode => 'a+rx',
	}
 	->
 	service { 'storm-ui':
 		ensure => running,
 		enable => true,
 	}
}
