package com.xebialabs.deployit.plugin.puppet;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.xebialabs.deployit.plugin.api.udm.*;
import com.xebialabs.deployit.plugin.overthere.Host;
import com.xebialabs.deployit.test.support.ItestTopology;
import org.junit.Ignore;

import java.io.IOException;
import java.util.*;

import static com.xebialabs.platform.test.TestUtils.*;

@Ignore
public class PuppetProvisionedItestBase extends PuppetItestBase {

    public PuppetProvisionedItestBase(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    protected Template getTemplate(Container container) {

        Template hostTemplate = newInstance("template.overthere.SshHost", "host-template");
        String[] props = new String[]{"os", "connectionType", "address", "port", "username", "password", "sudoUsername"};
        for (String p : props)
            hostTemplate.setProperty(p, container.getProperty(p).toString());

        hostTemplate.setProperty("puppetPath", "/usr/local/bin");
        return hostTemplate;
    }

    protected Provisioned getProvisioned(Template hostTemplate, AppliedProvisioner manifestDeployed) throws IOException {
        Provisioned provisioned = newInstance("dummy-provider.Provisioned", "provisioned");
        Provisionable provisionable = newInstance("dummy-provider.Provisionable", "provisionable");
        Set<Template> templates = new HashSet<>();
        templates.add(hostTemplate);
        provisionable.setProperty("boundTemplates", templates);

        List deployedSet = new ArrayList();
        deployedSet.add(manifestDeployed);
        provisioned.setProperty("provisioners", deployedSet);
        provisioned.setDeployable(provisionable);
        return provisioned;
    }

    private DeployedApplication newDeployedApplication(String name, String version, Provisioned<?, ?>... provisioneds) {
        List<Provisionable> deployables = Lists.transform(Arrays.asList(provisioneds), new Function<Provisioned<?, ?>, Provisionable>() {
            @Override
            public Provisionable apply(final Provisioned<?, ?> d) {
                return d.getDeployable();
            }
        });

        ProvisioningPackage pkg = newProvisioningPackage(name, version, deployables.toArray(new Provisionable[]{}));

        Environment env = createEnvironment();

        DeployedApplication deployedApp = newInstance("udm.DeployedApplication");
        deployedApp.setVersion(pkg);
        deployedApp.setEnvironment(env);
        deployedApp.setId(id(env.getId(), name));

        deployedApp.addDeployeds(provisioneds);


        return deployedApp;
    }

    private static ProvisioningPackage newProvisioningPackage(String name, String version, Provisionable... deployables) {
        Application app = newInstance("udm.Application");
        app.setId(id("Application", name));

        ProvisioningPackage pkg = newInstance("udm.ProvisioningPackage");
        pkg.setId(id(app.getId(), version));
        pkg.setApplication(app);
        Set<Provisionable> set = new HashSet<>();
        for (Provisionable d : deployables) {
            d.setId(id(app.getId(), d.getId()));
            set.add(d);
        }
        pkg.setDeployables(set);
        return pkg;
    }
}
