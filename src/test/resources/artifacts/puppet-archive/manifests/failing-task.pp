# execute 'apt-get update'
exec { 'apt-update':                    # exec resource named 'apt-update'
  command => '/usr/bin/apt-get update'  # command this resource will run
}

# install fail2ban package
package { 'apache3':
  require => Exec['apt-update'],        # require 'apt-update' before installing
  ensure => installed,
}
