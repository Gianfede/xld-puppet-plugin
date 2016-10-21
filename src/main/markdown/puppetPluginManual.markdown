The XL Deploy Puppet plugin uses the Puppet provisioning tool to install modules and manifests on hosts.

This document provides information that is specific to this version of the Puppet plugin. For general information about the plugin, refer to [Introduction to the XL Deploy Puppet plugin](/xl-deploy/concept/xl-deploy-puppet-plugin.html).

## Requirements

* XL Deploy 5.1.0 or later
* Puppet 3.4.3 (Unix)

## Limitations ##

* The plugin does not support deployments on Microsoft Windows-based hosts
* The plugin only supports deployment on stand-alone Puppet
* The plugin does not support Puppet master and Puppet agent node configuration

## Release notes

### xld-puppet-plugin 5.1.0

Initial release.

### XLD Puppet plugin 5.1.1

#### Bug fixes

* [DEPL-10193] - XLD shows DONE in Execution plan even when there are errors while deploying using puppet plugin
* [DEPL-10529] - Removed ANSI escape code from step logs
