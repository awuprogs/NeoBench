import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;


public abstract class Benchmark {

	protected GraphDatabaseService graph;

	public Benchmark(String directory) throws IOException {
       	
       	long[] adjustedSizes = FileUtils.getScaledFileSizesMB(new File(directory),
       			Configuration.preferredBufferPoolSizeMB,
                "neostore.nodestore.db", "neostore.relationshipstore.db", "neostore.propertystore.db",
                "neostore.propertystore.db.strings", "neostore.propertystore.db.arrays");
       	
       	graph = new GraphDatabaseFactory()
       	     .newEmbeddedDatabaseBuilder(directory)
       	     .setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size              , adjustedSizes[0] + "M")
       	     .setConfig(GraphDatabaseSettings.relationshipstore_mapped_memory_size      , adjustedSizes[1] + "M")
       	     .setConfig(GraphDatabaseSettings.nodestore_propertystore_mapped_memory_size, adjustedSizes[2] + "M")
       	     .setConfig(GraphDatabaseSettings.strings_mapped_memory_size                , adjustedSizes[3] + "M")
       	     .setConfig(GraphDatabaseSettings.arrays_mapped_memory_size                 , adjustedSizes[4] + "M")
       	     .newGraphDatabase();
	}
	
	public void shutdown() {
		graph.shutdown();
	}
	
	public abstract void run();
}
