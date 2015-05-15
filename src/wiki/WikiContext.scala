package wiki

object WikiPages {
  var id2WikiPage: Map[Int, WikiPage] = null
  var SurfaceForm2Id: Map[String, Int] = null
  
  def generateVecTrainText(){
    var trainText = ""
    for(wikipage <- id2WikiPage.values){
      trainText += wikipage.generateVecTrainFile()
    }
    
  }
}
