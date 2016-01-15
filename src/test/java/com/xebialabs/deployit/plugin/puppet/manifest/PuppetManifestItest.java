package com.xebialabs.deployit.plugin.puppet.manifest;


import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.base.BaseDeployableArtifact;
import com.xebialabs.deployit.plugin.puppet.PuppetItestBase;
import com.xebialabs.deployit.test.support.ItestTopology;
import org.junit.Test;

import java.io.IOException;

import static com.xebialabs.platform.test.TestUtils.createArtifact;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PuppetManifestItest extends PuppetItestBase {

    private static final String MANIFEST_FILE = "artifacts/manifestTest.pp";
    private static final String MANIFEST_TEST_FILE_PATH = "/tmp/manifest.test";

    public PuppetManifestItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldDeployPuppetManifestFile() throws IOException {
        Deployed<?, ?> puppetDeployed = getDeployed(MANIFEST_FILE);
        DeployedApplication deployedManifest = newDeployedArtifact("puppetManifest", "1.0", puppetDeployed);

        assertManifestApplied(puppetDeployed, deployedManifest);
    }

    @Test
    public void shouldDeployPuppetManifestFileForNOOP() throws IOException {
        Deployed<?, ?> puppetDeployed = getDeployed(MANIFEST_FILE);
        DeployedApplication deployedManifest = newDeployedArtifact("puppetManifest", "1.0", puppetDeployed);
        assertManifestApplied(puppetDeployed, deployedManifest);

        //delete generated file on remote host
        deleteGeneratedFileFromRemoteHost(getHost(), MANIFEST_TEST_FILE_PATH);

        puppetDeployed.setProperty("applyOnNoop", true);
        assertManifestApplied(puppetDeployed, deployedManifest);
    }


    private void assertManifestApplied(Deployed<?, ?> puppetDeployed, DeployedApplication deployedManifest) {
        assertThat(getSteps(puppetDeployed).size(), equalTo(2));
        assertInitial(deployedManifest);
        assertFileExists(getHost(), MANIFEST_TEST_FILE_PATH);
    }

    private Deployed<?, ?> getDeployed(String manifestFile) throws IOException {
        BaseDeployableArtifact manifest = createArtifact("puppetManifest", "1.0", manifestFile, "puppet.ManifestSpec", tempFolder.newFolder());
        return wizard.deployed(manifest, container, "puppet.Manifest");
    }
}
