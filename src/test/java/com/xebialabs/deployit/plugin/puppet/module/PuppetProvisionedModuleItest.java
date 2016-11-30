/*
 *  THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 *  FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.deployit.plugin.puppet.module;


import com.xebialabs.deployit.plugin.api.udm.*;
import com.xebialabs.deployit.test.support.ItestTopology;
import org.junit.Test;

import java.io.IOException;

import static com.xebialabs.platform.test.TestUtils.newInstance;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PuppetProvisionedModuleItest extends PuppetModuleItestBase {

    public PuppetProvisionedModuleItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldInstallPuppetModule() throws IOException {

        Template hostTemplate = getTemplate(container);
        BaseAppliedProvisioner moduleDeployed = getDeployed(PUPPETLABS_APACHE, hostTemplate);

        Provisioned<?, ?> provisioned = getProvisioned(hostTemplate, moduleDeployed);

        DeployedApplication deployedTarball = newDeployedApplication(PUPPETLABS_APACHE, "1.0", provisioned);
        assertModuleInstalled(provisioned, deployedTarball);
        cleanupModule(PUPPETLABS_APACHE, "1.5.0");
    }

    @Test
    public void shouldFailDeploymentForInstallingInvalidModule() throws IOException {

        Template hostTemplate = getTemplate(container);
        BaseAppliedProvisioner moduleDeployed = getDeployed("puppetlabs-test", hostTemplate);

        Provisioned<?, ?> provisioned = getProvisioned(hostTemplate, moduleDeployed);
        DeployedApplication deployedTarball = newDeployedApplication(PUPPETLABS_APACHE, "1.0", provisioned);
        assertFailure(deployedTarball);
    }

    private void assertModuleInstalled(Deployed<?, ?> puppetDeployed, DeployedApplication installedModule) {
        assertThat(getSteps(puppetDeployed).size(), equalTo(2));
        assertInitial(installedModule);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_5_0);
    }

    private BaseAppliedProvisioner getDeployed(String moduleName, Template hostTemplate) throws IOException {
        BaseAppliedProvisioner manifestSpec = newInstance("puppet.provisioner.InstalledModule", "moduleSpec");
        BaseProvisioner provisioner = newInstance("puppet.provisioner.Module", "puppetTarBall");
        provisioner.setProperty("hostTemplate", hostTemplate);
        manifestSpec.setProperty("moduleName", moduleName);
        manifestSpec.setProperty("version", "1.5.0");
        manifestSpec.setDeployable(provisioner);
        return manifestSpec;
    }
}
