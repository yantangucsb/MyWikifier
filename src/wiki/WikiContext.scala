package wiki
import java.io._

import edu.illinois.cs.cogcomp.wikifier.models.WikiCandidate;

object WikiPages {
  var id2WikiPage: Map[Int, WikiPage] = null
  var SurfaceForm2Id: Map[String, Int] = null
  
  //Concatenate words and train vec for every page using word2vec
  //This generates the training file for word2vec
  @throws(classOf[Exception])
  def generateVecTrainText(){
    var trainText = ""
    for(wikipage <- id2WikiPage.values){
      trainText += wikipage.generateVecTrainFile()
    }
    val file = new File("data/WikiVec/Wikvectors.bin")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(trainText)
    bw.close()
  }
  
  def getScore(candidate: WikiCandidate): Double = {
    0
  }
}
