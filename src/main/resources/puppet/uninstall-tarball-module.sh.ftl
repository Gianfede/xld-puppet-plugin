#!/bin/sh
set -x
${previousDeployed.container.puppetPath}/puppet module uninstall ${previousDeployed.name}
set +x
rc=$?
if [ $rc -ne 0 ]; then
  echo "Failed to execute puppet module uninstall." >&2
  exit $rc
fi
