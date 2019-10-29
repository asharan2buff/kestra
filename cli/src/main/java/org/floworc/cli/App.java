package org.floworc.cli;

import io.micronaut.configuration.picocli.PicocliRunner;
import org.floworc.cli.commands.servers.StandAloneCommand;
import org.floworc.cli.commands.TestCommand;
import org.floworc.cli.commands.servers.WebServerCommand;
import org.floworc.cli.commands.servers.WorkerCommand;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "floworc",
    version = "v0.1",

    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    commandListHeading = "%nCommands:%n",

    mixinStandardHelpOptions = true,
    subcommands = {
        StandAloneCommand.class,
        TestCommand.class,
        WebServerCommand.class,
        WorkerCommand.class,
    }
)
public class App implements Callable<Object> {
    public static void main(String[] args) throws Exception {
        PicocliRunner.call(App.class, args);
    }

    @Override
    public Object call() throws Exception {
        return PicocliRunner.call(App.class, "--help");
    }
}
