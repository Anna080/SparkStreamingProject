import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.apache.spark.SparkConf
import org.apache.kafka.common.serialization.StringDeserializer
import java.sql.{Connection, DriverManager, PreparedStatement}

object KafkaConsumerApp {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("KafkaConsumerApp").setMaster("local[*]")
    
    // Création du contexte de streaming avec un intervalle de batch de 1 seconde
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    
    // Paramètres de Kafka
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest", //earliest
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("first")
    
    // Définition du sujet Kafka
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )


    // Extraction de la valeur de chaque enregistrement
    val lines = stream.map(record => record.value)
        // Pour chaque RDD dans le flux de données
        lines.foreachRDD { rdd =>
          // Pour chaque partition dans le RDD
          rdd.foreachPartition { partitionOfRecords =>
            val conn = DriverManager.getConnection("jdbc:sqlite:/Users/annadiaw/Desktop/ProjetSparkStreaming/data.db")
            val statement = conn.prepareStatement("INSERT INTO power_data VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
            // Pour chaque enregistrement dans la partition
            partitionOfRecords.foreach { record =>
              val parts = record.split(",")
              for (i <- 0 until parts.length) {
                // Ajout de la partie à la requête SQL
                statement.setString(i + 1, parts(i))
              }
              statement.executeUpdate()
            }
            statement.close()
            conn.close()
          }
        }

        ssc.start()
        ssc.awaitTermination()
      }
    }