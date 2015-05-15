//1. Read text
//2. call NER

package preprocessing
import scala.io._
import scala.collection.mutable.MutableList
import model.EntityLinkProblem
import model.StopWords
import ranker.CandidateRanking

import edu.illinois.cs.cogcomp.wikifier.common.GlobalParameters;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;



object Main{
  def main(args: Array[String]) {   
    if (args.length <= 1) ()
    GlobalParameters.loadConfig(args(args.length-1));
    
    var inference: CandidateRanking = new CandidateRanking(false);
    val source = scala.io.Source.fromFile(args(0))
    val text = try source.mkString finally source.close()
    //StopWords.loadfile()
    var ta: TextAnnotation = GlobalParameters.curator.getTextAnnotation(text); 

    var problem = new EntityLinkProblem(ta, args(0))
    inference.annotateWithVec(problem, false, 0)
    
    //var nerTagger = new NERTagger()
    //nerTagger.setUp();
    //nerTagger.tagData(lines)
    //var shallowparser = new ShallowParser("configs/NER.config")
    //shallowparser.performChunkerAndPos(lines)
  }                                               //> main: (args: Array[String])Unit
}