/*
 *  THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 *  FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.deployit.plugin.puppet.manifest;


import com.xebialabs.deployit.plugin.api.udm.*;
import com.xebialabs.deployit.plugin.puppet.PuppetProvisionedItestBase;
import com.xebialabs.deployit.test.support.ItestTopology;
import com.xebialabs.overthere.local.LocalFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.xebialabs.platform.test.TestUtils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PuppetProvisionedManifestItest extends PuppetManifestItest {

    public PuppetProvisionedManifestItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldDeployPuppetManifestFile() throws IOException {
        Template hostTemplate = getTemplate(container);
        BaseAppliedProvisionerArtifact manifestDeployed = getDeployed(MANIFEST_FILE, hostTemplate);
        Provisioned<?, ?> provisioned = getProvisioned(hostTemplate, manifestDeployed);
        DeployedApplication deployedManifest = newDeployedApplication("puppetManifest", "1.0", provisioned);
        assertManifestApplied(provisioned, deployedManifest);
    }

    @Test
    public void shouldFailPuppetManifestFolderDeploymentForAFailingTask() throws IOException, URISyntaxException {
        Template hostTemplate = getTemplate(container);
        BaseAppliedProvisionerArtifact manifestDeployed = getDeployed(MANIFEST_FILE_FAILING, hostTemplate);
        Provisioned<?, ?> provisioned = getProvisioned(hostTemplate, manifestDeployed);
        DeployedApplication deployedManifest = newDeployedApplication("puppetManifest", "1.0", provisioned);
        assertFailure(deployedManifest);
    }

    private void assertManifestApplied(Deployed<?, ?> puppetDeployed, DeployedApplication deployedManifest) {
        assertThat(getSteps(puppetDeployed).size(), equalTo(2));
        assertInitial(deployedManifest);
        assertFileExists(getHost(), MANIFEST_TEST_FILE_PATH);
    }

    private BaseAppliedProvisionerArtifact getDeployed(String manifestFile, Template hostTemplate) throws IOException {

        BaseAppliedProvisionerArtifact manifestSpec = newInstance("puppet.provisioner.AppliedManifest", "puppetManifestSpec");
        URL artifactURL = Thread.currentThread().getContextClassLoader().getResource(manifestFile);
        File artifactFile = new File(tempFolder.newFolder(), "puppetManifest");
        artifactFile.createNewFile();
        try (InputStream is = artifactURL.openStream()) {
            Files.copy(is, artifactFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        manifestSpec.setFile(LocalFile.valueOf(artifactFile));
        BaseProvisionerArtifact provisioner = newInstance("puppet.provisioner.Manifest", "puppetManifest");
        provisioner.setProperty("hostTemplate", hostTemplate);
        manifestSpec.setDeployable(provisioner);
        return manifestSpec;
    }
}
