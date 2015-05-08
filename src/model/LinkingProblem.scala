package model

import preprocessing._
import model.SurfaceType

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
  //TF_IDF?
  var compponents: List[Mention] = MentionExtract.extract(this)
}