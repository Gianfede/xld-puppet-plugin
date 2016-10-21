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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PuppetManifestFolderItest extends PuppetItestBase {

    private static final String MANIFEST_ARCHIVE = "artifacts/puppet-archive";
    private static final String MANIFEST_FOLDER_TMP_FILE_PATH = "/tmp/folder_manifest.test";

    public PuppetManifestFolderItest(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    @Test
    public void shouldDeployPuppetManifestFolder() throws IOException, URISyntaxException {
        Deployed<?, ?> deployed = getDeployed(MANIFEST_ARCHIVE);
        DeployedApplication deployedManifest = newDeployedArtifact("puppetManifest", "1.0", deployed);
        assertManifestFolderApplied(deployed, deployedManifest);
    }

    private void assertManifestFolderApplied(Deployed<?, ?> deployed, DeployedApplication deployedManifest) {
        assertThat(getSteps(deployed).size(), equalTo(1));
        assertInitial(deployedManifest);
        assertFileExists(getHost(), MANIFEST_FOLDER_TMP_FILE_PATH);
    }

    @Test
    public void shouldDeployPuppetManifestFolderForNOOP() throws IOException, URISyntaxException {
        Deployed<?, ?> deployed = getDeployed(MANIFEST_ARCHIVE);
        DeployedApplication deployedManifest = newDeployedArtifact("puppetManifest", "1.0", deployed);
        assertManifestFolderApplied(deployed, deployedManifest);

        //delete generated file on remote host
        deleteGeneratedFileFromRemoteHost(getHost(), MANIFEST_FOLDER_TMP_FILE_PATH);

        assertManifestFolderApplied(deployed, deployedManifest);
    }

    @Test
    public void shouldFailPuppetManifestFolderDeploymentWhenInvalidHieraConfigPath() throws IOException, URISyntaxException {
        Deployed<?, ?> deployed = getDeployed(MANIFEST_ARCHIVE);
        deployed.setProperty("hieraConfig", "hiera.xyz");
        DeployedApplication deployedManifest = newDeployedArtifact("puppetManifest", "1.0", deployed);
        assertFailure(deployedManifest);
    }

    @Test
    public void shouldDeployPuppetManifestFolderForValidHieraConfigPath() throws IOException, URISyntaxException {
        Deployed<?, ?> deployed = getDeployed(MANIFEST_ARCHIVE);
        deployed.setProperty("manifestFile", "site.pp");
        deployed.setProperty("hieraConfig", "hiera.yaml");
        DeployedApplication deployedManifest = newDeployedArtifact("puppetManifest", "1.0", deployed);
        assertThat(getSteps(deployed).size(), equalTo(2));
        assertInitial(deployedManifest);
    }

    @Test
    public void shouldFailPuppetManifestFolderDeploymentForAFailingTask() throws IOException, URISyntaxException {
        Deployed<?, ?> deployed = getDeployed(MANIFEST_ARCHIVE);
        deployed.setProperty("manifestFile", "failing-task.pp");
        DeployedApplication deployedManifest = newDeployedArtifact("puppetManifest", "1.0", deployed);
        assertFailure(deployedManifest);
    }

    private Deployed<?, ?> getDeployed(String manifestFile) throws IOException, URISyntaxException {
        BaseDeployableFolderArtifact manifest = null;
        manifest = createFolder("puppetManifest", "1.0", manifestFile, "puppet.ManifestFolderSpec");
        Deployed<?, ?> deployed = wizard.deployed(manifest, container, "puppet.ManifestFolder");
        deployed.setProperty("manifestFile", "start.pp");
        deployed.setProperty("manifestPath", "manifests");
        deployed.setProperty("modulePath", "modules");
        return deployed;
    }

    public <T extends DeployableArtifact> T createFolder(String name, String version, String classpathResource, String type) throws URISyntaxException {
        T artifact = (T) newInstance(type, id("Applications", "Test", version, name));
        URL artifactURL = Thread.currentThread().getContextClassLoader()
            .getResource(classpathResource);
        artifact.setFile(LocalFile.valueOf(new java.io.File(artifactURL.toURI())));
        return artifact;
    }
}
