import java.io.StringReader

import org.apache.lucene.analysis.CharArraySet
import org.apache.lucene.analysis.core.StopFilter
import org.apache.lucene.analysis.standard.{StandardAnalyzer, StandardTokenizer}
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.util.Version
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.apache.lucene.analysis.snowball.SnowballFilter

import scala.collection.mutable.ListBuffer
import collection.JavaConverters._


case class ParsedData(topics: List[String], topicsVectors: Seq[Seq[Int]], bodyText: Seq[String])

object ParseInput {
  def getLabelsVector = {
    val topicsDictionaryPath = "C:\\TEMP\\ds challenge\\topics\\topicDictionary.txt"
    val data = scala.io.Source.fromFile(topicsDictionaryPath).getLines().zipWithIndex.toMap
    val length = data.size
    data.map {case(topic, i) => (topic, Range(0, length).map(j => if(i ==j)1 else 0))}
  }
  val labelsVectors: Map[String, Seq[Int]] = getLabelsVector

  val stopwords = scala.io.Source.fromFile("C:\\code\\dsChallengeSbt\\src\\main\\resources\\stopwords").getLines().toList

  val analyser = new StandardAnalyzer(new CharArraySet(stopwords.asJava, false))

  def extractDocumentTokens(bodyText: String): Seq[String] = {
    val reader = new StringReader(bodyText)
    val stream = analyser.tokenStream("", reader)
    stream.reset()

    val buffer = new ListBuffer[String]

    while (stream.incrementToken()) {
      buffer.append(stream.getAttribute(classOf[CharTermAttribute]).toString)
    }

    stream.close()
    buffer.toList
  }

  def extractTopics(topics: List[JString]): List[String] = {
    topics match {
      case Nil => List()
      case JString(head) :: tail => head :: extractTopics(tail)
    }
  }

  val emptyVector: Seq[Int] = Range(0, 160).map(_ => 0)

  def extractTopicsVector(topics: List[String]) = {
    topics.flatten(t => labelsVectors.get(t))
  }

  def parseFile(path: String): Seq[ParsedData] = {
    val data = scala.io.Source.fromFile(path).mkString
    val parsedData = parse(data) \ "TrainingData"

    val result: List[(List[JValue], String)] = for {
      JObject(child) <- parsedData
      JField("bodyText", JString(bodyText)) <- child
      JField("topics", JArray(topics)) <- child
    } yield (topics, bodyText)

    result.map {
      case (Nil, bodyText: String) => ParsedData(List(), Seq(), extractDocumentTokens(bodyText))
      case (topics: List[JString], bodyText: String) =>
        val t = extractTopics(topics)
        ParsedData(t, extractTopicsVector(t), extractDocumentTokens(bodyText))
    }
  }

  val path = "C:\\TEMP\\ds challenge\\"

  def main(args: Array[String]): Unit = {
//    println("list files")
//    val p = new File(path)
//    println(p.isDirectory)
//    for (f <- p.listFiles() if f.isFile) {
//      println(f)
//      parseFile(f.getAbsolutePath)
//    }

    val bodyText = "fucked up world"
    extractDocumentTokens("fucked up world")

    val samplePath = "C:\\code\\dsChallengeSbt\\src\\main\\resources\\sample.json"
//    val result = parseFile(samplePath)
    val result = parseFile("C:\\TEMP\\ds challenge\\\\1999a_TrainingData.json")

    for(r <- result) {
      println(s"${r.topics}, ${r.bodyText}")
    }

    analyser.getStopwordSet.size()
  }
}
