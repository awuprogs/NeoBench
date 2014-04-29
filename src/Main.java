import java.io.File;
import java.util.List;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;


public class Main {
	
	private static void help() {
		System.err.println("Usage:");
		System.err.println("  ./neobench.sh [OPTIONS] COMMAND ARG...");
		System.err.println();
		System.err.println("Options:");
		System.err.println("  -d DIR      Set database directory");
		System.err.println("  -h          Print this help");
		System.err.println();
		System.err.println("Commands:");
		System.err.println("  load FILE   Load the given Type1 file");
		System.err.println("  pagerank    PageRank");
		System.err.println("  tc          Triangle counting");
	}

	public static void main(String[] args) throws ClassNotFoundException {
		
		Class.forName("org.neo4j.kernel.api.index.SchemaIndexProvider");
		
		Map<String, String> parameters = null;
		String directory = "db";
		
        OptionParser parser = new OptionParser( "d:h" );

        OptionSet options = parser.parse(args);
        
        if (options.has("h")) {
        	help();
        	return;
        }
        if (options.has("d")) {
        	directory = options.valueOf("d").toString();
        }
        
        List<?> nonoptArgs = options.nonOptionArguments();
        if (nonoptArgs.isEmpty()) {
        	help();
        	return;
        }
        
        //System.out.println("Heap size: " + Runtime.getRuntime().totalMemory());
        
        String cmd = nonoptArgs.get(0).toString();
        String[] cmdArgs = new String[nonoptArgs.size() - 1];
        for (int i = 0; i < cmdArgs.length; i++) cmdArgs[i] = nonoptArgs.get(i+1).toString();
        
    	System.err.print("Running: " + cmd);
    	for (int i = 0; i < cmdArgs.length; i++) System.err.print(" \"" + cmdArgs[i] + "\"");
    	System.err.println();

        long timeStart = 0;
        long timeEnd = 0;
        
        try {
	        if (cmd.equals("load")) {
	        	if (cmdArgs.length != 1)
	        		throw new Exception("Error: " + cmd + " takes one argument");
	        	
	        	Bulkloader l = new Bulkloader(directory, parameters);
	        	timeStart = System.currentTimeMillis();
	        	l.loadXStreamType1(new File(cmdArgs[0]));
	        	timeEnd = System.currentTimeMillis();
	        	l.shutdown();
	        }
	        else if (cmd.equals("tc")) {
	        	Benchmark b = new TriangleCounting(directory);
	        	timeStart = System.currentTimeMillis();
	        	b.run();
	        	timeEnd = System.currentTimeMillis();
	        	b.shutdown();
	        }
	        else if (cmd.equals("pagerank")) {
	        	Benchmark b = new PageRank(directory);
	        	timeStart = System.currentTimeMillis();
	        	b.run();
	        	timeEnd = System.currentTimeMillis();
	        	b.shutdown();
	        }
	        else {
	        	throw new Exception("Error: Unknown command: " + cmd);
	        }
        }
        catch (Exception e) {
        	System.err.println(e.getMessage());
        	if (e.getMessage() == null || !e.getMessage().startsWith("Error: ")) {
        		System.err.println();
        		e.printStackTrace(System.err);
        	}
        }
        
        if (timeEnd > timeStart) {
        	System.err.println("Time   : " + ((timeEnd - timeStart) / 1000.0) + " seconds");
        }
	}
}
