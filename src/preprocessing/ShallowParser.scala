package preprocessing

import java.util.Vector;

import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.lbj.chunk.Chunker;
import LBJ2.nlp.Word;
import LBJ2.nlp.seg.Token;
import LBJ2.parse.LinkedVector;
import edu.illinois.cs.cogcomp.LbjNer.ParsingProcessingData.PlainTextReader;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.Parameters;

import scala.collection.mutable.MutableList

class ShallowParser(pathToCofig: String) {
  var chunkerWords: MutableList[String] = MutableList()
  var chunkerTags: MutableList[String] = MutableList()
  var posTags: MutableList[String] = MutableList()
  var chunker: Chunker = new Chunker

  try {
    Parameters.readConfigAndLoadExternalData(pathToCofig);
  } catch {
    case ex: Exception => throw new Exception
  }
  
  def performChunkerAndPos(input: String) = {
    if (!(input.trim().equals(""))) {
      var NEWordSentences: Vector[LinkedVector] = PlainTextReader.parseText(input)
      var i = 0
      for (i <- 0 until NEWordSentences.size()) {
        var chunkerSentence: LinkedVector = new LinkedVector();
        var j = 0
        for (j <- 0 until NEWordSentences.get(i).size()) {
          var t: Token = null;
          var word: Word = NEWordSentences.get(i).get(j).asInstanceOf[Word]
          if (j > 0)
            t = new Token(word, chunkerSentence.get(j - 1).asInstanceOf[Token], "O");
          else
            t = new Token(word, null, "O");
          chunkerSentence.add(t);
        }
        for (j <- 1 until chunkerSentence.size()) {
          chunkerSentence.get(j).previous = chunkerSentence.get(j - 1);
          chunkerSentence.get(j - 1).next = chunkerSentence.get(j);
        }
        for (j <- 0 until chunkerSentence.size()) {
          //println(chunkerSentence.size())
          var w: Token = chunkerSentence.get(j).asInstanceOf[Token];
          chunkerTags += (chunker.discreteValue(w));
//          println(chunker.discreteValue(w))
          posTags += ("B-" + w.partOfSpeech); // this is to make the annotation consistent with NER and the chunker....
          chunkerWords += w.form;
        }
      }
//      println(chunkerTags)
//      println(posTags)
//      println(chunkerWords)
    }
  }
}