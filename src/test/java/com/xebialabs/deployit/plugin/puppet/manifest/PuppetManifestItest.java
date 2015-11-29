package com.xebialabs.deployit.plugin.puppet.manifest;


import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;
import com.xebialabs.deployit.plugin.api.udm.base.BaseDeployableArtifact;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.puppet.PuppetItestBase;
import com.xebialabs.deployit.test.support.ItestTopology;
import org.junit.Test;

import java.io.IOException;

import static com.xebialabs.platform.test.TestUtils.createArtifact;

public class PuppetManifestItest extends PuppetItestBase {

    private static final String MANIFEST_FILE = "artifacts/manifestTest.pp";

    public PuppetManifestItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldDeployPuppetManifestFile() throws IOException {
        DeployedApplication deployedManifest = getDeployedArtifact(MANIFEST_FILE);
        assertInitial(deployedManifest);
        assertFileExists(getHost(), "/tmp/manifest.test");
    }

    private DeployedApplication getDeployedArtifact(String manifestFile) throws IOException {
        BaseDeployableArtifact manifest = createArtifact("puppetManifest", "1.0", manifestFile, "puppet.ManifestSpec", tempFolder.newFolder());
        Deployed<?, ?> puppetDeployed = wizard.deployed(manifest, container, "puppet.Manifest");
        return newDeployedArtifact("puppetManifest", "1.0", puppetDeployed);
    }
}