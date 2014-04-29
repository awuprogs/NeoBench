import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;


public class Bulkloader {

	protected BatchInserter graph = null;
    protected DynamicRelationshipType relationshipType;

	public Bulkloader(String directory, final Map<String, String> parameters) {
	    
		if (null == parameters)
        	graph = BatchInserters.inserter(directory);
        else
        	graph = BatchInserters.inserter(directory, parameters);
	    
	    relationshipType = DynamicRelationshipType.withName("default");
 	}
	
	public void loadXStreamType1(File file) throws IOException {
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		DataInputStream din = new DataInputStream(in);
		
		byte buffer[] = new byte[4];
		
		long[] nodeMap = new long[80 * 1000 * 1000];
		for (int i = 0; i < nodeMap.length; i++) nodeMap[i] = -1;
		
		long numEdges = 0;
		
		int progressStep = 1000000;
		boolean showProgress = true;
		if (showProgress) System.err.print("Loading:\n  ");
		
		while (true) {
			int r = 0;
			
			r += din.read(buffer);
	        int tail = (buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8
	        		| (buffer[2] & 0xFF) << 16 | (buffer[3] & 0xFF) << 24;
	        r += din.read(buffer);
	        int head = (buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8
	        		| (buffer[2] & 0xFF) << 16 | (buffer[3] & 0xFF) << 24;
	        r += din.read(buffer);	// weight
	        if (r < 12) break;
			
			if (tail >= nodeMap.length || head >= nodeMap.length) {
				int m = Math.max(head, tail) + 1000000;
				int l = nodeMap.length;
				while (l <= m) l *= 2;
				long[] x = new long[l];
				for (int i = 0; i < nodeMap.length; i++) x[i] = nodeMap[i];
				for (int i = nodeMap.length; i < l; i++) x[i] = -1;
				nodeMap = x;
			}
			
			long v_tail = nodeMap[tail];
			if (v_tail < 0) {
				v_tail = nodeMap[tail] = graph.createNode(null);
			}
			
			long v_head = nodeMap[head];
			if (v_head < 0) {
				v_head = nodeMap[head] = graph.createNode(null);
			}
			
			graph.createRelationship(v_tail, v_head, relationshipType, null);
			
			numEdges++;
			if (showProgress && (numEdges % progressStep) == 0) {
				System.err.print(".");
				if ((numEdges % (10 * progressStep)) == 0) {
					System.err.print(" " + (numEdges / 1000000) + " mil. edges\n  ");
				}
			}
		}
		
		if (showProgress) System.err.println("Done.");

		din.close();
	}
	
	public void shutdown() {
		graph.shutdown();		
	}
}
