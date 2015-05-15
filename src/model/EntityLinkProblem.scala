package model

import preprocessing._

import edu.illinois.cs.cogcomp.edison.sentences.TokenizerUtilities.SentenceViewGenerators;
import edu.illinois.cs.cogcomp.edison.sentences.ViewNames;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.thrift.base.Labeling;
import edu.illinois.cs.cogcomp.thrift.base.Span;
import scala.collection.JavaConverters._
import edu.illinois.cs.cogcomp.wikifier.models.LinkingProblem
import edu.illinois.cs.cogcomp.wikifier.models.ReferenceInstance

import java.util.ArrayList

class EntityLinkProblem(ta: TextAnnotation, filename: String)  extends LinkingProblem(filename, ta, new ArrayList[ReferenceInstance]()){
  //TF_IDF?
}

