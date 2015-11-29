#!/bin/sh
set -x
${deployed.container.puppetPath}/puppet apply ${deployed.file.path}
set +x
rc=$?
if [ $rc -ne 0 ]; then
  echo "Failed to execute puppet apply manifest." >&2
  exit $rc
fi
