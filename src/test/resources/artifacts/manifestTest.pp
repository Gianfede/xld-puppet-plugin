exec { 'testfile':
    command => '/bin/bash -c echo "folder manifest" > /tmp/manifest.test'
}
