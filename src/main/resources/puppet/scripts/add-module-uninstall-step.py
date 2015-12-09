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
