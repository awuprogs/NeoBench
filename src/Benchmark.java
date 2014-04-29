import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;


public abstract class Benchmark {

	protected GraphDatabaseService graph;

	public Benchmark(String directory) {
       	graph = (new GraphDatabaseFactory()).newEmbeddedDatabase(directory);
	}
	
	public void shutdown() {
		graph.shutdown();
	}
	
	public abstract void run();
}
