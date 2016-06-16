#!/bin/sh
#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#
set -x
${deployed.container.puppetPath}/puppet apply ${deployed.file.path}
set +x
rc=$?
if [ $rc -ne 0 ]; then
  echo "Failed to execute puppet apply manifest." >&2
  exit $rc
fi
