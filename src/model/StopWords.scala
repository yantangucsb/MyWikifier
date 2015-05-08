package model

import scala.io._

object StopWords {
  var stopWords : Set[String] = Set();
  var loaded : Boolean = false;
  var path2File : String = "data/Other_Data/stopwords_big"
  
  def loadfile() : Unit = {
      for(line <- scala.io.Source.fromFile(path2File).getLines())
        stopWords = stopWords ++ Set(line)
    //println(stopWords)
      loaded = true
  }

  def isStopword(s : String) : Boolean = {
    if (!loaded)
      loadfile()
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