/*
 *  THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 *  FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.deployit.plugin.puppet;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.xebialabs.deployit.booter.local.LocalBooter;
import com.xebialabs.deployit.deployment.planner.DeltaSpecificationBuilder;
import com.xebialabs.deployit.itest.ItestWizard;
import com.xebialabs.deployit.itest.cloudhost.ItestHostLauncher;
import com.xebialabs.deployit.plugin.api.flow.Step;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.*;
import com.xebialabs.deployit.plugin.overthere.Host;
import com.xebialabs.deployit.test.deployment.DeltaSpecifications;
import com.xebialabs.deployit.test.deployment.DeployitTester;
import com.xebialabs.deployit.test.support.CapturingExecutionContext;
import com.xebialabs.deployit.test.support.ItestTopology;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereFile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.xebialabs.deployit.test.deployment.DeltaSpecifications.createDeployedApplication;
import static com.xebialabs.platform.test.TestUtils.createDeploymentPackage;
import static com.xebialabs.platform.test.TestUtils.createEnvironment;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public abstract class PuppetItestBase {

    private static final Logger LOG = LoggerFactory.getLogger(PuppetItestBase.class);
    protected CapturingExecutionContext executionContext = new CapturingExecutionContext();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    static {
        LocalBooter.bootWithoutGlobalContext();
    }

    protected final DeployitTester tester;
    protected final String description;
    protected final ItestTopology topology;
    protected final Container container;
    protected final PuppetItestWizard wizard;

    public PuppetItestBase(String description, ItestTopology topology, Container container) {
        this.description = description;
        this.topology = topology;
        this.container = container;
        this.tester = new DeployitTester();
        this.wizard = new PuppetItestWizard(tester);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> getTargets() {
        List<Object[]> constructorArgsList = new ArrayList<Object[]>();
        Map<String, ItestTopology> topologies = ItestTopology.load();

        for (ItestTopology topology : topologies.values()) {
            if (ItestTopology.isItestEnabled(topology.getId(), topology.isEnabledByDefault())) {
                for (Container container : topology.getTargets()) {
                    constructorArgsList.add(new Object[]{"Test on " + container, topology, container});
                }
            }
        }

        if (constructorArgsList.isEmpty()) {
            LOG.error("No topologies found");
        }
        return constructorArgsList;
    }


    @Before
    public void takeCareOfVagrantImages() {
        ItestHostLauncher launcher = ItestHostLauncher.getInstance();

        ListenableFuture<CloudHost> future = launcher.launch(topology.getId());
        try {
            CloudHost ch = future.get();
            topology.registerIp(ch.getHostName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void assertUpgrade(final DeployedApplication oldVersion, final DeployedApplication newVersion) {
        wizard.assertUpgrade(oldVersion, newVersion);
    }

    public void assertUndeploy(final DeployedApplication app) {
        wizard.assertUndeploy(app);
    }

    protected void resetContext() {
        executionContext.getCapturedOutput().clear();
        executionContext.getCapturedError().clear();
    }

    public void assertInitial(final DeployedApplication app) {
        wizard.assertInitial(app);
    }

    public void assertFailure(final DeployedApplication app) {
        DeltaSpecificationBuilder builder = (new DeltaSpecificationBuilder()).initial(DeltaSpecifications.createDeployedApplication(app.getVersion(), app.getEnvironment()));
        Iterator var3 = app.getDeployeds().iterator();

        while(var3.hasNext()) {
            Deployed d = (Deployed)var3.next();
            builder.create(d);
        }
        wizard.assertPlanForFailure(builder.build());
    }

    public DeployedApplication newDeployedArtifact(String name, String version, Deployed<?, ?>... deployeds) {
        return ItestWizard.newDeployedApplication(name, version, deployeds);
    }

    protected void assertFileExists(Host host, String... paths) {
        assertFileExists(host, true, paths);
    }

    protected void assertFileExists(Host host, boolean exists, String... paths) {
        OverthereConnection conn = host.getConnection();
        try {
            for (String path : paths) {
                OverthereFile file = conn.getFile(path);
                assertThat(file.exists(), is(exists));
            }
        } finally {
            conn.close();
        }
    }

    protected void deleteGeneratedFileFromRemoteHost(Host host, String path) {
        OverthereConnection conn = host.getConnection();
        try {
            OverthereFile file = conn.getFile(path);
            file.delete();
        } finally {
            conn.close();
        }
    }

    public Deployed<?, ?> deployed(final Deployable deployable, final Container container, final String type) {
        return wizard.deployed(deployable, container, type);
    }

    public DeployedApplication newDeployedApplication(String name, String version, Deployed<?, ?>... deployeds) {
        return ItestWizard.newDeployedApplication(name, version, deployeds);
    }

    public Host getHost() {
        return (Host) topology.findFirstMatchingCi(Type.valueOf("overthere.SshHost"));
    }

    protected DeltaSpecificationBuilder createDeltaSpecBuilder(Deployed<?, ?>... deployeds) {
        Iterable<Deployable> deployables = transform(deployeds);
        DeploymentPackage deploymentPackage = createDeploymentPackage(Iterables
                .toArray(deployables, Deployable.class));
        Environment environment = createEnvironment(container);
        DeltaSpecificationBuilder specificationBuilder = new DeltaSpecificationBuilder();
        specificationBuilder.initial(createDeployedApplication(
                deploymentPackage, environment));
        return specificationBuilder;
    }

    protected Iterable<Deployable> transform(Deployed<?, ?>... deployeds) {
        return Lists.transform(newArrayList(deployeds),
                new Function<Deployed<?, ?>, Deployable>() {
                    @Override
                    public Deployable apply(Deployed<?, ?> input) {
                        return input.getDeployable();
                    }
                });
    }

    protected List<Step> getSteps(Deployed<?, ?> deployed) {
        DeltaSpecificationBuilder specBuilder = createDeltaSpecBuilder(deployed);
        specBuilder.create(deployed);
        return tester.resolvePlan(specBuilder.build());
    }
}
