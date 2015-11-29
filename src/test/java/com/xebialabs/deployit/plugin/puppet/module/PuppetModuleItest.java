package com.xebialabs.deployit.plugin.puppet.module;

import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.test.support.ItestTopology;
import org.junit.Test;

import java.io.IOException;

import static com.xebialabs.platform.test.TestUtils.newInstance;
import com.xebialabs.deployit.plugin.api.udm.Container;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PuppetModuleItest extends PuppetModuleItestBase {

    public PuppetModuleItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldInstallUpgradeAndUnInstallPuppetModule() throws IOException {
        DeployedApplication puppetModule = getDeployedPuppetModuleApplication("1.5.0");
        assertInitial(puppetModule);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_5_0);

        DeployedApplication puppetUpgradedModule = getDeployedPuppetModuleApplication("1.7.0");
        resetContext();
        assertUpgrade(puppetModule, puppetUpgradedModule);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_7_0);

        resetContext();
        assertUndeploy(puppetUpgradedModule);
        getPuppetModuleListCommandOutput();
        assertThat(executionContext.getCapturedOutput(), hasItem(not(containsString("puppetlabs-apache"))));
    }

    @Test
    public void shouldInstallAndUnInstallPuppetModule() {
        DeployedApplication puppetModuleApp = getDeployedPuppetModuleApplication("1.5.0");
        assertInitial(puppetModuleApp);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_5_0);

        assertUndeploy(puppetModuleApp);
        getPuppetModuleListCommandOutput();
        assertThat(executionContext.getCapturedOutput(), hasItem(not(containsString("puppetlabs-apache"))));
    }

    private DeployedApplication getDeployedPuppetModuleApplication(String version) {
        Deployed<?, ?> puppetModule = deployed((Deployable) newInstance("puppet.ModuleSpec", "puppetlabs-apache"), container, "puppet.Module");
        puppetModule.setProperty("version", version);
        puppetModule.setProperty("force", true);
        puppetModule.setProperty("ignoreDependencies", false);

        return newDeployedApplication("puppetlabs-apache", version, puppetModule);
    }
}
