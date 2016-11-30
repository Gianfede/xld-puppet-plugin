import com.xebialabs.deployit.provision.host.LazyHost as LazyHost

puppet_provisioners = filter(lambda provisioner: provisioner.type == "puppet.provisioner.InstalledModule", deployed.provisioners)

for p in puppet_provisioners:
    host = wrap(LazyHost())
    host.setHostTemplate(p.deployable.hostTemplate)
    host.setProvisionedBlueprint(deployedApplication)
    host.setSourceProvisioned(deployed)
    step = steps.puppet_install_module(targetHost=host,
                                       module=p,
                                       description="Install Puppet module {0} on {1}".format(p.moduleName, deployed.name, p.version))
    context.addStep(step)
