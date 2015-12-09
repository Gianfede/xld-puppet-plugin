#
if deployed.moduleName:
    name = deployed.moduleName
else:
    name = deployed.name

context.addStep(steps.os_script(
    description="Install tarball module %s on %s " % (name, deployed.container.name),
    script="puppet/install-tarball-module",
    order=deployed.installOrder
))
