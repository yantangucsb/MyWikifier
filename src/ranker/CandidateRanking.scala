package ranker

import model._
import edu.illinois.cs.cogcomp.wikifier.inference.InferenceEngine

class CandidateRanking(_inTraining: Boolean) extends InferenceEngine(_inTraining){
  featureExtractors.add(new FeatureExtractorWordVec("FeatureExtractorWordVec", _inTraining, ""))
  def annotateWithVec(problem: EntityLinkProblem, tracePerformance: Boolean,linkerPredictionThreshold: Double) {
    annotate(problem, null, tracePerformance, false, 0)
  }
}