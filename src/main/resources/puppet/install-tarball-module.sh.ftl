#!/bin/sh
set -x
${deployed.container.puppetPath}/puppet module install ${deployed.file.path} --ignore-dependencies <#if deployed.debug> --debug  </#if>
set +x
rc=$?
if [ $rc -ne 0 ]; then
  echo "Failed to execute puppet module install." >&2
  exit $rc
fi
