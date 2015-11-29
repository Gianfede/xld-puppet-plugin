## Requirements ##

* **XL Deploy requirements**
    * **XL Deploy**: Version 5.1.0+
    * **Puppet**: Versions 3.4.3+ (Unix)
    * **Other XL Deploy plugins**: None

* **Infrastructural requirements**
    * Puppet software version 3.4.3+ configured on a unix machine.

## Overview ##

The Puppet plugin is an XL-Deploy plugin that manages the Puppet provisioning tool to install modules and manifests on hosts.
See the **Features** section below for all the supported feature lists.

## Features ##

* Apply manifest file for provisioning using `puppet apply` command.

* Install and Uninstall puppet modules from the Puppet Forge.

* Install and Uninstall puppet tar ball modules by uploading module tar file.

* Apply individual manifests files present in a puppet manifest folder.

## Limitations ##

1. The plugin does not currently support deployments on Microsoft Windows-based hosts.
2. The plugin only supports deployment on standalone puppet.
3. The plugin does not support Puppet master and puppet agent nodes configuration.

## Release notes

### xld-puppet-plugin 5.1.0

Initial release.
