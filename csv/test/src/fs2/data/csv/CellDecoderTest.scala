package fs2.data.csv

import weaver._

import scala.concurrent.duration._

object CellDecoderTest extends SimpleIOSuite {

  // CellDecoder should have implicit instances available for standard types
  CellDecoder[String]
  CellDecoder[Array[Char]]
  CellDecoder[Boolean]
  CellDecoder[Unit]
  CellDecoder[Int]
  CellDecoder[BigDecimal]
  CellDecoder[FiniteDuration]
  CellDecoder[Duration]

  CellDecoder[java.net.URL]
  CellDecoder[java.util.UUID]
  CellDecoder[java.time.Instant]
  CellDecoder[java.time.LocalTime]
  CellDecoder[java.time.ZonedDateTime]

  CellDecoder[DecoderResult[Char]]
  CellDecoder[Either[String, Char]]

  test("CellDecoder should decode standard types correctly") {
    expect(CellDecoder[Unit].apply("") == Right(())) and
      expect(CellDecoder[Int].apply("78") == Right(78)) and
      expect(CellDecoder[Boolean].apply("true") == Right(true)) and
      expect(CellDecoder[Char].apply("C") == Right('C')) and
      expect(CellDecoder[Double].apply("1.2") == Right(1.2)) and
      expect(CellDecoder[BigDecimal].apply("1.2e456") == Right(BigDecimal("12e455"))) and
      expect(CellDecoder[String].apply("foobar") == Right("foobar")) and
      expect(CellDecoder[FiniteDuration].apply("2 seconds") == Right(2.seconds)) and
      expect(CellDecoder[java.net.URL].apply("http://localhost:8080/path?a=b").isRight == true) and
      expect(
        CellDecoder[java.util.UUID].apply("6f55090e-a807-49c2-a142-2a0db1f079df").map(_.toString) == Right(
          "6f55090e-a807-49c2-a142-2a0db1f079df")) and
      expect(CellDecoder[java.time.LocalTime].apply("13:04:29") == Right(java.time.LocalTime.of(13, 4, 29)))
  }

  test("CellDecoder should handle container types properly") {
    expect(CellDecoder[DecoderResult[Char]].apply("G") == Right(Right('G'))) and
      expect(CellDecoder[DecoderResult[Char]].apply("").map(_.isLeft) == Right(true)) and
      expect(CellDecoder[Either[String, Char]].apply("F") == Right(Right('F'))) and
      expect(CellDecoder[Either[String, Char]].apply("hello") == Right(Left("hello")))
  }

  test("CellDecoder should fail on invalid inputs") {
    expect(CellDecoder[Unit].apply("some random non empty string").isLeft == true) and
      expect(CellDecoder[Int].apply("asdf").isLeft == true) and
      expect(CellDecoder[Boolean].apply("maybe").isLeft == true) and
      expect(CellDecoder[Char].apply("Chars").isLeft == true) and
      expect(CellDecoder[Double].apply("-").isLeft == true) and
      expect(CellDecoder[FiniteDuration].apply("2 meters").isLeft == true)
  }

}
