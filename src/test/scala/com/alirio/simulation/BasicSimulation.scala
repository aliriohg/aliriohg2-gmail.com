package com.alirio.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef.http

import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps


class BasicSimulation extends Simulation {

  val baseUrl = sys.env.getOrElse("GATLING_BASEURL", "https://czjnw9wwrl.execute-api.us-east-1.amazonaws.com").toString
  val users = sys.env.getOrElse("GATLING_NR_USERS", "10").toInt
  val maxDuration: FiniteDuration = sys.env.getOrElse("GATLING_MAX_DURATION", "2").toInt minutes
  val rampUpTime: FiniteDuration = sys.env.getOrElse("GATLING_RAMPUP_TIME", "10").toInt seconds

  val httpProtocol = http
    .baseUrl(baseUrl)
    .doNotTrackHeader("1")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val basicScenario = scenario("BasicSimulation")
    .exec(http("request_1")
      .get("/gatling/test-load"))

  setUp(
    basicScenario.inject(
      nothingFor(4 seconds), // Tiempo inicial de espera, puede ser 4 segundos o 10 lo que quieras
      atOnceUsers(1), // Puedes inyectarlos de una, acá probamos cargas súbitas y el comportamiento del sistema
      rampUsersPerSec(1) to 300 during (8 minutes), // Se pueden inyectar gradualmente hasta ese valor de quiebre
      constantUsersPerSec(300) during ( 7 minutes),
    )
  ).protocols(httpProtocol)

}
