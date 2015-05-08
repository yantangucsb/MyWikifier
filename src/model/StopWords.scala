package model

class StopWords(filename: String) {
  var stopWords: Set[String] = Set()
  def loadfile(): Set[String] = {
     val source = scala.io.Source.fromFile(filename)
    val lines = try source.mkString finally source.close()
    
  }
 
}