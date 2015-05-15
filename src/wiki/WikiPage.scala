package wiki

class WikiPage(index: Int, surfaceForm: String) {
  //Most of these members should loaded from file
  //Not all the members should be loaded
  //When training: no vec
  //When testing: no left or right context
  val id: Int = index
  val name: String = surfaceForm
  var intLink: List[WikiPage] = null;
  var outLink: List[WikiPage] = null;
  var contextWords: List[String] = null;
  var vec: List[Double] = null;
  var leftContexts: List[String] = null
  var rightContexts: List[String] = null
  
  def generateVecTrainFile():String = {
    var trainText = ""
    var nameForTrain = "Train" + id.toString()
    for(i <- 0 until leftContexts.size){
      trainText += leftContexts.apply(i) + nameForTrain + rightContexts.apply(i)
    }
    trainText
  }
}