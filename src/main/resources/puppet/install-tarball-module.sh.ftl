#!/bin/sh
#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#
<#if puppetPath?has_content >${puppetPath}/</#if>puppet module install ${tarball.file.path} --ignore-dependencies <#if tarball.debug> --debug  </#if> --color false
rc=$?
if [ $rc -ne 0 ]; then
  echo "Failed to execute puppet module install." >&2
  exit $rc
fi
