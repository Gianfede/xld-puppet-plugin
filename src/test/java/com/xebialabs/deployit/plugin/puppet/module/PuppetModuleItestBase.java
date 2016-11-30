/*
 *  THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 *  FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.deployit.plugin.puppet.module;

import com.xebialabs.deployit.plugin.overthere.DefaultExecutionOutputHandler;
import com.xebialabs.deployit.plugin.puppet.PuppetItestBase;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.puppet.PuppetProvisionedItestBase;
import com.xebialabs.deployit.test.support.ItestTopology;
import com.xebialabs.overthere.CmdLine;
import org.junit.Assert;

import static com.xebialabs.deployit.plugin.overthere.DefaultExecutionOutputHandler.handleStderr;
import static com.xebialabs.deployit.plugin.overthere.DefaultExecutionOutputHandler.handleStdout;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

public abstract class PuppetModuleItestBase extends PuppetProvisionedItestBase {

    protected static final String APACHE_MODULE_VERSION_1_5_0 = "v1.5.0";
    protected static final String APACHE_MODULE_VERSION_1_7_0 = "v1.7.0";
    protected static final String ARTIFACTS_APACHE_1_5_0_TAR = "puppetlabs-apache-1.5.0.tar.gz";
    protected static final String ARTIFACTS_APACHE_1_7_0_TAR = "puppetlabs-apache-1.7.0.tar.gz";
    protected static final String PUPPETLABS_APACHE = "puppetlabs-apache";

    public PuppetModuleItestBase(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    public void assertModuleInstallAndVersionCheck(String version) {
        Assert.assertThat(executionContext.getCapturedOutput(), hasItem(containsString(PUPPETLABS_APACHE)));
        Assert.assertThat(executionContext.getCapturedOutput(), hasItem(containsString(version)));
    }

    public void getPuppetModuleListCommandOutput() {
        CmdLine cmd = new CmdLine();
        cmd.addRaw(container.getProperty("puppetPath") + "puppet module list");
        try (DefaultExecutionOutputHandler stdoutHandler = handleStdout(executionContext);
             DefaultExecutionOutputHandler stderrHandler = handleStderr(executionContext)) {
            int exitCode = getHost().getConnection().execute(stdoutHandler, stderrHandler, cmd);
            Assert.assertThat(exitCode, equalTo(0));
        }
    }

    public void cleanupModule(String moduleName, String version) {
        CmdLine cmd = new CmdLine();
        cmd.addRaw(container.getProperty("puppetPath") + String.format("puppet module uninstall %s --version %s", moduleName, version));
        try (DefaultExecutionOutputHandler stdoutHandler = handleStdout(executionContext);
             DefaultExecutionOutputHandler stderrHandler = handleStderr(executionContext)) {
            int exitCode = getHost().getConnection().execute(stdoutHandler, stderrHandler, cmd);
            Assert.assertThat(exitCode, equalTo(0));
        }
    }
}
