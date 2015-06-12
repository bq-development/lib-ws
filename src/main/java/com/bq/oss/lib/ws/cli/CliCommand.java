package com.bq.oss.lib.ws.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;

public class CliCommand extends Command {

    private Optional<CommandLineI> commandLine;

    public CliCommand(String name, String description) {
        super(name, description);
        this.commandLine = Optional.empty();
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("files").type(String.class).action(Arguments.append()).nargs("*")
            .help("Groovy scripts to run instead a command line shell.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        System.setProperty("mode", "console");
        List<String> commandLineFiles = Optional.ofNullable(namespace.get("files"))
            .map(o -> (List<Object>) o)
            .filter(filesObject -> !filesObject.isEmpty())
            .map(filesObject -> (List<String>) filesObject.get(0))
            .orElse(new ArrayList<>());
        commandLine.orElseThrow(CommandLineNotImplemented::new).run(commandLineFiles);
    }

    public void setCommandLine(CommandLineI commandLine) {
        this.commandLine = Optional.ofNullable(commandLine);
    }
}
