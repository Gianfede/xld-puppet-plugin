

    exec { 'testmanifest':
        command => '/bin/bash -c echo "folder manifest" > /tmp/folder_manifest.test'
    }
