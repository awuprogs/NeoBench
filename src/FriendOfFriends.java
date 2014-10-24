import java.io.IOException;
import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

public class FriendOfFriends extends Benchmark {
	
	private int iterations = 10;
        public static final int ITERATIONS = 100;

	public FriendOfFriends(String directory) throws IOException {
		super(directory);
	}
	

	@Override
	public void run() {

		Transaction tx = graph.beginTx();
		// Initialize

                ArrayList<Node> friendOfFriends = new ArrayList<Node>();
		Iterable<Node> allVertices = GlobalGraphOperations.at(graph).getAllNodes();
		int N = 0;
		int m = 0;
		for (Node v : allVertices) {
			N++;
                        if (N < ITERATIONS) {
                                break;
                        }
                        Iterable<Relationship> ri = v.getRelationships(Direction.OUTGOING);
                        for (Relationship r : ri) {
                                Node n = r.getOtherNode(v);
                                Iterable<Relationship> rn = n.getRelationships(Direction.OUTGOING);
                                for (Relationship r2 : rn) {
                                        friendOfFriends.add(r2.getOtherNode(n));
                                }
                        }
		}
		

		tx.close();

		System.out.println("Result : " + friendOfFriends.size());
	}
}
