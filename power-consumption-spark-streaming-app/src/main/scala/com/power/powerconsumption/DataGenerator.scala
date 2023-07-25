import java.util.Properties
import org.apache.kafka.clients.producer._
import scala.io.Source
import java.util.logging.{Level, Logger}

object KafkaProducerApp {
  val logger: Logger = Logger.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {
    logger.setLevel(Level.INFO) // Configure logging level
    
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    
    logger.info("Initializing Kafka producer.") // Log the initialization of the Kafka producer
    
    // Create a new Kafka producer with the above properties
    val producer = new KafkaProducer[String, String](props)
    val topic = "first"

    val sourceFile = "resources/power_consumption_data.csv"
    val lines = Source.fromFile(sourceFile).getLines().toList

    var mlk = 0
    for (line <- lines) {
      mlk = mlk + 1

      val record = new ProducerRecord[String, String](topic, "key", line)
      producer.send(record)

      logger.info(s"Sent record to topic: $topic.") // Log the record send operation
      if (mlk % 10000 == 0) {
        Thread.sleep(1000) // sleep for 1 second
      }
    }

    producer.close()
    logger.info("Producer closed.") // Log the closing of the producer
  }
}