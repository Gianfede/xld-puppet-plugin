package com.xebialabs.deployit.plugin.puppet.manifest;


import com.xebialabs.deployit.plugin.api.udm.DeployableArtifact;
import com.xebialabs.deployit.plugin.api.udm.Deployed;
import com.xebialabs.deployit.plugin.api.udm.DeployedApplication;

import com.xebialabs.deployit.plugin.api.udm.base.BaseDeployableFolderArtifact;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.puppet.PuppetItestBase;
import com.xebialabs.deployit.test.support.ItestTopology;

import com.xebialabs.overthere.local.LocalFile;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static com.xebialabs.platform.test.TestUtils.id;
import static com.xebialabs.platform.test.TestUtils.newInstance;

public class PuppetManifestFolderItest extends PuppetItestBase {

    private static final String MANIFEST_ARCHIVE = "artifacts/puppet-archive";

    public PuppetManifestFolderItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldDeployPuppetManifestFolder() throws IOException, URISyntaxException {
        DeployedApplication deployedManifest = getDeployedArtifact(MANIFEST_ARCHIVE);
        assertInitial(deployedManifest);
        assertFileExists(getHost(), "/tmp/folder_manifest.test");
    }

    private DeployedApplication getDeployedArtifact(String manifestFile) throws IOException, URISyntaxException {
        BaseDeployableFolderArtifact manifest = null;
        manifest = createFolder("puppetManifest", "1.0", manifestFile, "puppet.ManifestFolderSpec");

        Deployed<?, ?> deployed = wizard.deployed(manifest, container, "puppet.ManifestFolder");
        deployed.setProperty("manifestFile", "start.pp");
        deployed.setProperty("manifestPath", "manifests");
        deployed.setProperty("modulePath", "modules");
        return newDeployedArtifact("puppetManifest", "1.0", deployed);
    }

    public <T extends DeployableArtifact> T createFolder(String name,
                                                         String version, String classpathResource, String type) throws URISyntaxException {
        T artifact = (T) newInstance(type, id("Applications", "Test", version, name));
        URL artifactURL = Thread.currentThread().getContextClassLoader()
                .getResource(classpathResource);
        artifact.setFile(LocalFile.valueOf(new java.io.File(artifactURL.toURI())));
        return artifact;
    }
}
