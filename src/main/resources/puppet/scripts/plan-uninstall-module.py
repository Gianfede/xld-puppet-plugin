#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#
if previousDeployed.moduleName:
    name = previousDeployed.moduleName
else:
    name = previousDeployed.name

context.addStep(steps.os_script(
    description="Uninstall module %s on %s " % (name, previousDeployed.container.name),
    script="puppet/uninstall-module",
    order=previousDeployed.uninstallOrder
))
