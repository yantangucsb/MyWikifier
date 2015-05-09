package model

import preprocessing._
import model._
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;


import edu.illinois.cs.cogcomp.edison.sentences.TokenizerUtilities.SentenceViewGenerators;
import edu.illinois.cs.cogcomp.edison.sentences.ViewNames;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.thrift.base.Labeling;
import edu.illinois.cs.cogcomp.thrift.base.Span;
import scala.collection.JavaConverters._

class LinkingProblem(input: String, filename: String) {
  var sourceFilename: String = filename
  var text: String = input
  var ta: TextAnnotation = createAnnotation(text)
  //TF_IDF?
  var components: List[Mention] = MentionExtract.extract(this)
  println(components)
  def createAnnotation(text: String): TextAnnotation = {
      new TextAnnotation("fakeCorpus","fakeId", text,SentenceViewGenerators.LBJSentenceViewGenerator);
  }
}

