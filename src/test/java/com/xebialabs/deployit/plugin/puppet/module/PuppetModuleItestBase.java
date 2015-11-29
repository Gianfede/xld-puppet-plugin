package com.xebialabs.deployit.plugin.puppet.module;

import com.xebialabs.deployit.plugin.overthere.DefaultExecutionOutputHandler;
import com.xebialabs.deployit.plugin.puppet.PuppetItestBase;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.test.support.ItestTopology;
import com.xebialabs.overthere.CmdLine;
import org.junit.Assert;

import static com.xebialabs.deployit.plugin.overthere.DefaultExecutionOutputHandler.handleStderr;
import static com.xebialabs.deployit.plugin.overthere.DefaultExecutionOutputHandler.handleStdout;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

public class PuppetModuleItestBase extends PuppetItestBase {

    protected static final String APACHE_MODULE_VERSION_1_5_0 = "v1.5.0";
    protected static final String APACHE_MODULE_VERSION_1_7_0 = "v1.7.0";

    public PuppetModuleItestBase(String description, ItestTopology topology, Container container) {
        super(description, topology, container);
    }

    public void assertModuleInstallAndVersionCheck(String version) {
        Assert.assertThat(executionContext.getCapturedOutput(), hasItem(containsString("puppetlabs-apache")));
        Assert.assertThat(executionContext.getCapturedOutput(), hasItem(containsString(version)));
    }

    public void getPuppetModuleListCommandOutput() {
        CmdLine cmd = new CmdLine();
        cmd.addRaw(container.getProperty("puppetPath") + "/puppet module list");
        try (DefaultExecutionOutputHandler stdoutHandler = handleStdout(executionContext);
             DefaultExecutionOutputHandler stderrHandler = handleStderr(executionContext)) {
            int exitCode = getHost().getConnection().execute(stdoutHandler, stderrHandler, cmd);
            Assert.assertThat(exitCode, equalTo(0));
        }
    }
}
