#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#
context.addStep(steps.os_script(
    description="Apply manifest %s on %s " % (deployed.name,deployed.container.name),
    script="puppet/apply-manifest",
    order=deployed.applyOrder,
    freemarker_context = {'puppetPath': deployed.container.puppetPath, 'manifest' : deployed}
))
