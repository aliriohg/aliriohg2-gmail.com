package com.alirio.simulation

import com.github.mnogu.gatling.kafka.Predef._
import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig

import scala.concurrent.duration._


class KafkaSimulation extends Simulation {
  val kafkaConf = kafka
    // Kafka topic name
    .topic("test-ali")
    // Kafka producer configs
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        // list of Kafka broker hostname and port pairs
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "192.168.0.18:9092",
        // Required since Apache Kafka 0.8.2.0
        // in most cases, StringSerializer or ByteArraySerializer
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer"))

  val scn = scenario("Kafka Test")
    .exec(
      kafka("request")
        // message to send
        .send[String]("foo"))
  // You can also use feeder
  //
  //val scn = scenario("Kafka Test")
  //  .feed(csv("test.csv").circular)
  //  .exec(kafka("request").send("${value}"))

  setUp(
    scn
      .inject(constantUsersPerSec(2) during(10 seconds)))
    .protocols(kafkaConf)


}
