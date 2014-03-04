package net.objecthunter.timmy;

import static org.apache.commons.cli.OptionBuilder.withLongOpt;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Timmy {
	public static void main(String[] args) {
		final Options ops = initOptions();

		final CommandLineParser cliParser = new BasicParser();
		final String pathOut;
		final String pathIn;
		final String projectName;

		try {
			CommandLine cl = cliParser.parse(ops, args);
			if (cl.hasOption('h')) {
				printHelp(ops);
				System.exit(0);
			}
			if (cl.hasOption('o')) {
				pathOut = cl.getOptionValue('o');
			} else {
				pathOut = "timesheet" + new SimpleDateFormat("d.M.y_H.m.s.S").format(new Date()) + ".pdf";
			}
			if (cl.hasOption('i')) {
				pathIn = cl.getOptionValue('i');
			} else {
				pathIn = "timesheet.csv";
			}
			if (cl.hasOption('p')) {
				projectName = cl.getOptionValue('p');
			} else {
				projectName = "";
			}
			TimeSheetGenerator gen = new TimeSheetGenerator(projectName, pathIn, pathOut);
				gen.generatePdf();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void printHelp(Options ops) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("BenchTool", ops);
	}

	private static Options initOptions() {
		final Options ops = new Options();
		ops.addOption(withLongOpt("help").withDescription("help").create('h'));
		ops.addOption(withLongOpt("out").withDescription("path").hasArg().create('o'));
		ops.addOption(withLongOpt("in").withDescription("path").hasArg().create('i'));
		ops.addOption(withLongOpt("project").withDescription("name").hasArg().create('p'));
		return ops;
	}

}
