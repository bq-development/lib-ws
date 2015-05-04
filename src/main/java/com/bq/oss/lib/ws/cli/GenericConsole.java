package com.bq.oss.lib.ws.cli;

import java.lang.Class;
import java.lang.Object;
import java.lang.String;
import java.lang.System;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bq.oss.lib.cli.console.Console;
import com.bq.oss.lib.cli.console.Shell;
import com.bq.oss.lib.ws.log.LogbackUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


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
}