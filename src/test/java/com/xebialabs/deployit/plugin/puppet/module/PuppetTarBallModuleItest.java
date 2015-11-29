package com.xebialabs.deployit.plugin.puppet.module;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.artifact.SourceArtifact;
import com.xebialabs.deployit.plugin.api.udm.base.BaseDeployableFileArtifact;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.test.support.ItestTopology;
import com.xebialabs.overthere.local.LocalFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.xebialabs.platform.test.TestUtils.id;
import static com.xebialabs.platform.test.TestUtils.newInstance;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PuppetTarBallModuleItest extends PuppetModuleItestBase {


    private static final String ARTIFACTS_APACHE_1_5_0_TAR = "puppetlabs-apache-1.5.0.tar.gz";
    private static final String ARTIFACTS_APACHE_1_7_0_TAR = "puppetlabs-apache-1.7.0.tar.gz";

    public PuppetTarBallModuleItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldInstallUpgradeAndUnInstallPuppetTarBallModule() throws IOException {
        DeployedApplication deployedTarModule = getDeployedTarBallModule(ARTIFACTS_APACHE_1_5_0_TAR);
        assertModuleInstallSuccessfully(deployedTarModule);

        DeployedApplication deployedTarUpgradedModule = getDeployedTarBallModule(ARTIFACTS_APACHE_1_7_0_TAR);
        resetContext();
        assertUpgrade(deployedTarModule, deployedTarUpgradedModule);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_7_0);

        resetContext();
        assertUndeploy(deployedTarUpgradedModule);
        getPuppetModuleListCommandOutput();
        assertThat(executionContext.getCapturedOutput(), hasItem(not(containsString("puppetlabs-apache"))));
    }

    @Test
    public void shouldInstallAndUnInstallPuppetTarBallModule() throws IOException {
        DeployedApplication deployedTarModule = getDeployedTarBallModule(ARTIFACTS_APACHE_1_5_0_TAR);
        assertModuleInstallSuccessfully(deployedTarModule);

        resetContext();
        assertUndeploy(deployedTarModule);
        getPuppetModuleListCommandOutput();
        assertThat(executionContext.getCapturedOutput(), hasItem(not(containsString("puppetlabs-apache"))));
    }

    private DeployedApplication getDeployedTarBallModule(String artifactsApacheTarFile) throws IOException {
        BaseDeployableFileArtifact module = createFileArtifact("puppetlabs-apache", "1.0", artifactsApacheTarFile, "puppet.TarBallModuleSpec", tempFolder.newFolder());
        Deployed<?, ?> puppetDeployed = wizard.deployed(module, container, "puppet.TarBallModule");

        return newDeployedApplication("puppetlabs-apache", "1.0", puppetDeployed);
    }

    private void assertModuleInstallSuccessfully(DeployedApplication deployedTarModule) {
        assertInitial(deployedTarModule);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_5_0);
    }

    private static <T extends SourceArtifact> T createFileArtifact(String name, String version, String artifactFileName, String type, File workingFolder)
            throws IOException {
        T artifact = (T) newInstance(type);
        artifact.setId(id("Applications", "Test", version, name));
        String artifactFileClasspathResource = "artifacts/"+artifactFileName;
        URL artifactURL = Thread.currentThread().getContextClassLoader().getResource(artifactFileClasspathResource);
        File artifactFile = new File(workingFolder, artifactFileName);
        artifactFile.createNewFile();
        Resources.asByteSource(artifactURL).copyTo(Files.asByteSink(artifactFile));
        artifact.setFile(LocalFile.valueOf(artifactFile));
        return artifact;
    }
}
