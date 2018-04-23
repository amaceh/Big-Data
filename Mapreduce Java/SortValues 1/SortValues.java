import java.nio.ByteBuffer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class SortValues {

	public static void main(String[] args) throws Exception {

	Path inputPath = new Path(args[0]);
	Path outputDir = new Path(args[1]);

	// Create configuration
	Configuration conf = new Configuration();

	// Create job
	// Job job = new Job(conf, "Sort the Numbers");
	// job.setJarByClass(SortValues .class);

	// Create a new Job
	Job job = Job.getInstance(conf, "Sort the Numbers");
	job.setJarByClass(SortValues.class);

	// Setup MapReduce
	job.setMapperClass(MapTask.class);
	job.setReducerClass(ReduceTask.class);
	job.setNumReduceTasks(1);

	// Specify key / value
	job.setMapOutputKeyClass(IntWritable.class);
	job.setMapOutputValueClass(IntWritable.class);
	job.setOutputKeyClass(IntWritable.class);
	job.setOutputValueClass(IntWritable.class);

	//job.setSortComparatorClass(IntComparator.class);
	
	// Input
	FileInputFormat.addInputPath(job, inputPath);
	job.setInputFormatClass(TextInputFormat.class);

	// Output
	FileOutputFormat.setOutputPath(job, outputDir);
	job.setOutputFormatClass(TextOutputFormat.class);

	// Execute job
	int code = job.waitForCompletion(true) ? 0 : 1;
	System.exit(code);
}

public static class MapTask extends
	Mapper<LongWritable, Text, IntWritable, IntWritable> {
	public void map(LongWritable key, Text value, Context context) 
		throws java.io.IOException, InterruptedException {
		
		String line = value.toString();
		String[] tokens = line.split("\t"); // This is the delimiter between

		int keypart = Integer.parseInt(tokens[0]);
		int valuePart = Integer.parseInt(tokens[1]);

		context.write(new IntWritable(valuePart), new IntWritable(keypart));
	}
}

public static class ReduceTask extends
	Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

	public void reduce(IntWritable key, Iterable<IntWritable> list, Context context)
		throws java.io.IOException, InterruptedException {
			for (IntWritable value : list) {
				context.write(value, key);
			}
		}
	}
}