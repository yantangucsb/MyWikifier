//1. Read text
//2. call NER

package preprocessing
import scala.io._
import scala.collection.mutable.MutableList
import model.LinkingProblem
import model.StopWords


object Main{
  def main(args: Array[String]) {    
    val source = scala.io.Source.fromFile(args(0))
    val lines = try source.mkString finally source.close()
    //var problem: LinkingProblem = new LinkingProblem(lines, args(0))
    StopWords.loadfile()
    //var nerTagger = new NERTagger()
    //nerTagger.setUp();
    //nerTagger.tagData(lines)
    //var shallowparser = new ShallowParser("configs/NER.config")
    //shallowparser.performChunkerAndPos(lines)
  }                                               //> main: (args: Array[String])Unit
}