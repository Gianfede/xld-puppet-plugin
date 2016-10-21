#!/bin/sh
#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#
#
<#if deployed.hieraConfig?has_content>
$([ -f ${deployed.file.path}/${deployed.hieraConfig} ])
rc=$?
if [ $rc -ne 0 ]; then
  echo "Invalid hiera config path: " ${deployed.file.path}/${deployed.hieraConfig}
  exit $rc
fi
</#if>

${deployed.container.puppetPath}/puppet apply <#if deployed.modulePath?has_content>--modulepath ${deployed.file.path}/${deployed.modulePath}:/etc/puppet/modules</#if> <#if deployed.hieraConfig?has_content>--hiera_config ${deployed.file.path}/${deployed.hieraConfig}</#if> <#if deployed.verbose>--verbose</#if> <#if deployed.debug>--debug</#if> <#if deployed.trace>--trace</#if> ${deployed.file.path}/${deployed.manifestPath}/${deployed.manifestFile} --detailed-exitcodes --color false
rc=$?
if [ $rc -ne 0 ] && [ $rc -ne 2 ]; then
  echo "Failed to execute puppet apply manifest." >&2
  exit $rc
fi

