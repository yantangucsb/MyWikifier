package preprocessing

import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.thrift.base.Labeling;
import edu.illinois.cs.cogcomp.thrift.base.Span;
import edu.illinois.cs.cogcomp.LbjNer.ParsingProcessingData.PlainTextReader;

import scala.collection.mutable.MutableList
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

class TextLabeler {
  def getLabeling(words: List[String], tags: List[String], input: String, labelerName: String): Labeling = {
    var quoteLocs: List[Int] = findQuotationLocations(input);
    var labels: List[Span] = List();

    // track the location we have reached in the input
    var location: Int = 0;
    var open: Boolean = false;

    // the span for this entity
    var span: Span = null;

    for (j <- 0 until words.size) {

      // the current word (NER's interpretation)
      var word: String = words(j);

      var startend: Array[Int] = findStartEndForSpan(input, location, word,
        quoteLocs);
      location = startend(1)

      if (tags.apply(j).startsWith("B-")
        || (j > 0 && tags.apply(j).startsWith("I-") && (!tags.apply(j - 1)
          .endsWith(tags.apply(j).substring(2))))) {
        span = new Span();
        span.setStart(startend(0));
        span.setLabel(tags.apply(j).substring(2));
        open = true;
      }

      if (open) {
        var close: Boolean = false;
        if (j == words.size - 1) {
          close = true;
        } else {
          if (tags.apply(j + 1).startsWith("B-"))
            close = true;
          if (tags.apply(j + 1).equals("O"))
            close = true;
          if (tags.apply(j + 1).indexOf('-') > -1
            && (!tags.apply(j).endsWith(tags.apply(j + 1)
              .substring(2))))
            close = true;
        }
        if (close) {
          span.setEnding(startend(1));
          if (span.start > span.ending) {
            System.out.println("Critical tagging error : negative index.");
            span.setStart(startend(0));
          }

          span.source = input.substring(span.start, span.ending);
          labels = labels ++ List(span);
          // System.out.println("Found span: \""+span.source+"\"\t["+span.start+":"+span.ending+"]\t"+span.label);
          open = false;
        }
      }
    }
    var labeling: Labeling = new Labeling();
    labeling.setSource(labelerName);
    labeling.setLabels(labels.asJava);
    labeling
  }

  def findQuotationLocations(input: String): List[Int] = {
    var quoteLocs: List[Int] = List();
    if (input.contains("``") || input.contains("''")) {
      var from: Int = 0;
      var index: Int = 0
      var counter: Int = 0;
      while ((index = input.indexOf("``", from)) != -1) {
        quoteLocs = quoteLocs ++ List(index - counter);
        counter += 1;
        from = index + 2;
      }
      while ((index = input.indexOf("''", from)) != -1) {
        quoteLocs = quoteLocs ++ List(index - counter);
        counter += 1;
        from = index + 2;
      }
      quoteLocs = quoteLocs.sorted
    }
    quoteLocs
  }

  def findStartEndForSpan(input: String, location: Int, word: String,
                          quoteLocs: List[Int]): Array[Int] = {
    var startend: Array[Int] = null;

    if (word.equals("\"")) {
      // double quote is a special case because it could have been a
      // double tick before
      // inputAsNer is how NER viewed the input (we replicate the
      // important transforms
      // ner makes, this is very fragile!)
      var inputAsNer: String = input.substring(0, location) + input.substring(location).replace("``", "\"")
        .replace("''", "\"");
      // find start end for the word in the input as ner
      startend = findSpan(location, inputAsNer, word);
      if (quoteLocs.contains(startend(0))) {
        // if the double quote was original translated we should move
        // the end pointer one
        startend(1) += 1;
      }
    } else {
      startend = findSpan(location, input, word);
    }
    return startend;
  }

  def findSpan(from: Int, rawText: String, word: String): Array[Int] = {
    var start: Int = rawText.indexOf(word, from);
    if (start > 0) {
      var end: Int = start + word.length();
      Array(start, end)
    } else {
      // find the span brutally (very brute force...)     
      for (start <- from until rawText.length) {
        var sub: String = rawText.substring(start, Math.min(start + 5 + word.length() * 2, rawText.length()));
        if (PlainTextReader.normalizeText(sub).startsWith(word)) {
          for (end <- start + word.length() until start + Math.min(start + 5 + word.length() * 2, rawText.length()))
            if (PlainTextReader.normalizeText(rawText.substring(start, end)).equals(word))
              Array(start, end)
        }
      }
      System.out.println("Critical warning: word " + word + " is not found in the text " + rawText.substring(start, rawText.length()));
      Array(0, 0);
    }
  }
}
  
  