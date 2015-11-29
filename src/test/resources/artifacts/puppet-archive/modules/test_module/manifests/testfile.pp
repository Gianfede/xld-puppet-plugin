class testfile::test_module {

    exec { 'testmodule':
        command => '/bin/bash -c echo "test module" > /tmp/module_file.test'
    }
}
