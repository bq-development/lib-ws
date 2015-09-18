package io.corbel.lib.ws.cli;

import ch.qos.logback.core.joran.spi.JoranException;
import io.corbel.lib.cli.console.Console;
import io.corbel.lib.cli.console.Shell;
import io.corbel.lib.ws.log.LogbackUtils;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GenericConsole implements CommandLineI {

	private String welcomeMessage;
	private Class<?> ioc;

	public GenericConsole(String namespace, Class<?> ioc) {
		this.welcomeMessage = "Welcome to " + namespace.toUpperCase() + ". Type " + namespace
				+ ".help() to start.";
		this.ioc = ioc;
	}

    @Override
	public void run(List<String> args) {
		Console console = new Console(welcomeMessage, getShells());

		LogbackUtils.setLogLevel("INFO");
        try {
            removeUnneededLoggers();
            if (args.isEmpty()) {
				console.launch();
			} else {
				console.runScripts(args.toArray(new String[args.size()]));
			}
			System.exit(0);
		} catch (java.lang.Throwable e) {
			System.err.println(e.getMessage());
			System.exit(1);
        }
	}

	private Map<String, Object> getShells() {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ioc);
		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Shell.class);
		Map<String, Object> shells = new HashMap<>(beans.size());
		beans.forEach((beanName, bean) -> shells.put(applicationContext.findAnnotationOnBean(beanName, Shell.class)
                .value(), bean));
		return shells;
	}

    private void removeUnneededLoggers() throws JoranException {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ROOT")).detachAppender("FILE");
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ROOT")).detachAppender("SYSLOG");
    }
}