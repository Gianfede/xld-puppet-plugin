#!/bin/sh
#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#
${deployed.container.puppetPath}/puppet module install <#if deployed.moduleName?has_content > ${deployed.moduleName} <#else> ${deployed.name} </#if> <#if deployed.version?has_content > --version ${deployed.version} </#if> <#if deployed.force> --force  </#if> <#if deployed.ignoreDependencies> --ignore-dependencies  </#if> <#if deployed.debug> --debug  </#if> --color false
rc=$?
if [ $rc -ne 0 ]; then
  echo "Failed to execute puppet module install." >&2
  exit $rc
fi
