#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#
if deployed.moduleName:
    name = deployed.moduleName
else:
    name = deployed.name

context.addStep(steps.os_script(
    description="Install tarball module %s on %s " % (name, deployed.container.name),
    script="puppet/install-tarball-module",
    order=deployed.installOrder,
    freemarker_context = {'puppetPath': deployed.container.puppetPath, 'tarball' : deployed}
))
