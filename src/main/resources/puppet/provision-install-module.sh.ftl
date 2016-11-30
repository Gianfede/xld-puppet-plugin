#!/bin/sh
#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#
<#if puppetPath?has_content>${puppetPath}/</#if>puppet module install <#if module.moduleName?has_content> ${module.moduleName} </#if> <#if module.version?has_content> --version ${module.version} </#if> <#if module.force?has_content> --force  </#if> <#if module.ignoreDependencies?has_content> --ignore-dependencies </#if> <#if module.debug?has_content> --debug  </#if> --color false
rc=$?
if [ $rc -ne 0 ]; then
  echo "Failed to execute puppet module install." >&2
  exit $rc
fi
