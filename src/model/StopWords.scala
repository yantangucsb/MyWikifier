package model

import scala.io._

class StopWords(filename: String) {
  var stopWords : Set[String] = Set();
  var path2File : String = filename;
  
  def loadfile() : Unit = {
    for(line <- scala.io.Source.fromFile(path2File).getLines())
      stopWords = stopWords ++ Set(line)
    println(stopWords)
  }

  def isStopword(s : String) : Boolean = {
    stopWords.contains(s);
  }

  def filterStopWords(words : Iterable[String]) : List[String] = {
        if (words == null)
            return null;
        var res : List[String] = List();
        for (word : String <- words)
            if (!isStopword(word.toLowerCase()))
                res = res ++ List(word);
        res;
    }
}