//1. Read text
//2. call NER

package preprocessing
import scala.io._
import scala.collection.mutable.MutableList
import model.LinkingProblem


object Main{
  def main(args: Array[String]) {    
    val source = scala.io.Source.fromFile(args(0))
    val lines = try source.mkString finally source.close()
    var nerTagger : NERTagger = new NERTagger()
    print(lines)
    nerTagger.setUp()
    nerTagger.tagData(lines)
    var problem: LinkingProblem = new LinkingProblem(lines, args(0))
    //var shallowparser = new ShallowParser("configs/NER.config")
    //shallowparser.performChunkerAndPos(lines)
  }                                               //> main: (args: Array[String])Unit
}