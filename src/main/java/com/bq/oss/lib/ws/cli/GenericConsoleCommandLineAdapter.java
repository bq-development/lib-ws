package com.bq.oss.lib.ws.cli;

import com.bq.oss.lib.cli.console.GenericConsole;

import java.util.List;

/**
 * @author Alexander De Leon <me@alexdeleon.name>
 */
public class GenericConsoleCommandLineAdapter implements CommandLineI {

    private final GenericConsole console;

    public GenericConsoleCommandLineAdapter(GenericConsole console){
        this.console = console;
    }

    @Override
    public void run(List<String> args) {
        console.run(args);
    }
}
