package ranker

import model._
import wiki.WikiPages
import edu.illinois.cs.cogcomp.wikifier.inference.features.FeatureExtractorInterface
import edu.illinois.cs.cogcomp.wikifier.models.Mention;
import edu.illinois.cs.cogcomp.wikifier.models.LinkingProblem

import scala.collection.JavaConverters._

class FeatureExtractorWordVec(_extractorName: String, inTraining: Boolean, pathToSaves: String) extends FeatureExtractorInterface(_extractorName, inTraining, pathToSaves) {
  @throws(classOf[Exception])
  def extractFeatures(problem: LinkingProblem) {
    var lastTime: Long = System.currentTimeMillis();
    for(i <- 0 until problem.components.size()){
      extractFeatures(problem, i)
    }
    featureExtractionTime += System.currentTimeMillis()-lastTime;
    System.out.println( System.currentTimeMillis()-lastTime+" milliseconds elapsed extracting features for the level: "+extractorName);
  }
  
  @throws(classOf[Exception])
  def extractFeatures(problem: LinkingProblem, componentId: Int){
    var component: Mention = problem.components.get(componentId)
    var lastLevel = component.getLastPredictionLevel().asScala
//    var vecScore: Array[Double] = new Array(lastLevel.size)
    for(i <- 0 until lastLevel.size){
      var candidate = lastLevel.apply(i)
      val score = WikiPages.getScore(candidate)
      candidate.otherFeatures.addFeature("ContextSimlarity", score)
    }
  }
}