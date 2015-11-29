#!/bin/sh
set -x
${deployed.container.puppetPath}/puppet apply <#if deployed.modulePath?has_content>--modulepath ${deployed.file.path}/${deployed.modulePath}:/etc/puppet/modules</#if> <#if deployed.hieraConfig?has_content>--hiera_config ${deployed.hieraConfig}</#if> <#if deployed.verbose>--verbose</#if> <#if deployed.debug>--debug</#if> <#if deployed.trace>--trace</#if> ${deployed.file.path}/${deployed.manifestPath}/${deployed.manifestFile}
set +x
rc=$?
if [ $rc -ne 0 ]; then
  echo "Failed to execute puppet apply manifest." >&2
  exit $rc
fi

