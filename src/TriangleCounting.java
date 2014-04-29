import java.io.IOException;
import java.util.HashSet;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;


public class TriangleCounting extends Benchmark {

	public TriangleCounting(String directory) throws IOException {
		super(directory);
	}

	@Override
	public void run() {

		long tc = 0;
		
		Transaction tx = graph.beginTx();
		
		Iterable<Node> nodes = GlobalGraphOperations.at(graph).getAllNodes();
		HashSet<Node> set = new HashSet<Node>();

		for (Node u : nodes) {
			
			set.clear();
			for (Relationship r : u.getRelationships(Direction.BOTH)) {
				Node v = r.getOtherNode(u);
				set.add(v);
			}
			
			for (Relationship r : u.getRelationships(Direction.BOTH)) {
				Node v = r.getOtherNode(u);
				
				for (Relationship r2 : v.getRelationships(Direction.BOTH)) {
					Node w = r2.getOtherNode(v);
					if (u.getId() < v.getId() && v.getId() < w.getId()) {
						if (set.contains(w)) tc++;
					}
				}
			}
		}
		
		tx.close();
		
		System.out.println("Result : " + tc); 
	}
}
