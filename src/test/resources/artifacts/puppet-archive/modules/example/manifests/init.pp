class example ($value= "/tmp/verfiy_me_i_failed") {
 notify {$value:}
 file {$value:
  ensure => 'directory'
 }
}
