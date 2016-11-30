/*
 *  THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 *  FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.deployit.plugin.puppet.module;

import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.test.support.ItestTopology;
import org.junit.Test;

import java.io.IOException;

import static com.xebialabs.platform.test.TestUtils.newInstance;

import com.xebialabs.deployit.plugin.api.udm.Container;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PuppetModuleItest extends PuppetModuleItestBase {

    public PuppetModuleItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldInstallUpgradeAndUnInstallPuppetModule() throws IOException {
        Deployed<?, ?> deployed = getDeployed("1.5.0", PUPPETLABS_APACHE);
        assertThat(getSteps(deployed).size(), equalTo(2));
        DeployedApplication puppetModule = newDeployedApplication(PUPPETLABS_APACHE, "1.5.0", deployed);
        assertInitial(puppetModule);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_5_0);

        Deployed<?, ?> upgradedDeployed = getDeployed("1.7.0", PUPPETLABS_APACHE);
        assertThat(getSteps(upgradedDeployed).size(), equalTo(2));
        DeployedApplication puppetUpgradedModule = newDeployedApplication(PUPPETLABS_APACHE, "1.7.0", upgradedDeployed);

        resetContext();
        assertUpgrade(puppetModule, puppetUpgradedModule);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_7_0);

        resetContext();
        assertUndeploy(puppetUpgradedModule);
        getPuppetModuleListCommandOutput();
        assertThat(executionContext.getCapturedOutput(), hasItem(not(containsString(PUPPETLABS_APACHE))));
    }

    @Test
    public void shouldInstallAndUnInstallPuppetModule() {

        Deployed<?, ?> deployed = getDeployed("1.5.0", PUPPETLABS_APACHE);
        assertThat(getSteps(deployed).size(), equalTo(2));
        DeployedApplication puppetModuleApp = newDeployedApplication(PUPPETLABS_APACHE, "1.5.0", deployed);
        assertInitial(puppetModuleApp);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_5_0);

        assertUndeploy(puppetModuleApp);
        getPuppetModuleListCommandOutput();
        assertThat(executionContext.getCapturedOutput(), hasItem(not(containsString(PUPPETLABS_APACHE))));
    }

    @Test
    public void shouldFailDeploymentForInstallingInvalidPuppetModule() {

        Deployed<?, ?> deployed = getDeployed("1.5.0", "puppetlabs-apache-invalid");
        assertThat(getSteps(deployed).size(), equalTo(2));
        DeployedApplication puppetModuleApp = newDeployedApplication("puppetlabs-apache-invalid", "1.5.0", deployed);
        assertFailure(puppetModuleApp);
    }

    private Deployed<?, ?> getDeployed(String version, String name) {
        Deployed<?, ?> puppetModule = deployed((Deployable) newInstance("puppet.Module", name), container, "puppet.InstalledModule");
        puppetModule.setProperty("version", version);
        puppetModule.setProperty("force", true);
        puppetModule.setProperty("ignoreDependencies", false);
        return puppetModule;
    }
}
