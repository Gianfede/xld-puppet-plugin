/*
 *  THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 *  FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.deployit.plugin.puppet.module;


import com.xebialabs.deployit.plugin.api.udm.*;
import com.xebialabs.deployit.test.support.ItestTopology;
import com.xebialabs.overthere.OverthereFile;
import com.xebialabs.overthere.local.LocalFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.xebialabs.platform.test.TestUtils.newInstance;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PuppetProvisionedTarBallModuleItest extends PuppetModuleItestBase {

    public PuppetProvisionedTarBallModuleItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldInstallPuppetTarBallModule() throws IOException {

        Template hostTemplate = getTemplate(container);
        BaseAppliedProvisionerArtifact tarballDeployed = getDeployed(ARTIFACTS_APACHE_1_5_0_TAR, hostTemplate);

        Provisioned<?, ?> provisioned = getProvisioned(hostTemplate, tarballDeployed);
        DeployedApplication deployedTarball = newDeployedApplication(PUPPETLABS_APACHE, "1.0", provisioned);
        assertTarballApplied(provisioned, deployedTarball);
        cleanupModule(PUPPETLABS_APACHE, "1.5.0");
    }

    @Test
    public void shouldFailDeploymentForInstallingInvalidPuppetTarBallModule() throws IOException {

        Template hostTemplate = getTemplate(container);
        BaseAppliedProvisionerArtifact tarballDeployed = getDeployed("test-module.tar.gz", hostTemplate);

        Provisioned<?, ?> provisioned = getProvisioned(hostTemplate, tarballDeployed);
        DeployedApplication deployedTarball = newDeployedApplication(PUPPETLABS_APACHE, "1.0", provisioned);
        assertFailure(deployedTarball);
    }

    private void assertTarballApplied(Deployed<?, ?> puppetDeployed, DeployedApplication deployedTarModule) {
        assertThat(getSteps(puppetDeployed).size(), equalTo(2));
        assertInitial(deployedTarModule);
        getPuppetModuleListCommandOutput();
        assertModuleInstallAndVersionCheck(APACHE_MODULE_VERSION_1_5_0);
    }

    private BaseAppliedProvisionerArtifact getDeployed(String tarBallArchive, Template hostTemplate) throws IOException {

        BaseAppliedProvisionerArtifact manifestSpec = newInstance("puppet.provisioner.InstalledTarBallModule", "tarBallModuleSpec");
        URL artifactURL = Thread.currentThread().getContextClassLoader().getResource("artifacts/" + tarBallArchive);
        File artifactFile = new File(tempFolder.newFolder(), tarBallArchive);
        artifactFile.createNewFile();
        try (InputStream is = artifactURL.openStream()) {
            Files.copy(is, artifactFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        OverthereFile file = LocalFile.valueOf(artifactFile);
        manifestSpec.setFile(file);
        BaseProvisionerArtifact provisioner = newInstance("puppet.provisioner.TarBallModule", "puppetTarBall");
        provisioner.setProperty("hostTemplate", hostTemplate);
        manifestSpec.setDeployable(provisioner);
        return manifestSpec;
    }
}
