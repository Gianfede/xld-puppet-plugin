#!/bin/sh
set -x
${deployed.container.puppetPath}/puppet module install <#if deployed.moduleName?has_content > ${deployed.moduleName} <#else> ${deployed.name} </#if> <#if deployed.version?has_content > --version ${deployed.version} </#if> <#if deployed.force> --force  </#if> <#if deployed.ignoreDependencies> --ignore-dependencies  </#if> <#if deployed.debug> --debug  </#if>
set +x
rc=$?
if [ $rc -ne 0 ]; then
  echo "Failed to execute puppet module install." >&2
  exit $rc
fi
