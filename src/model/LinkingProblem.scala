package model

class LinkingProblem(input: String, filename: String) {
  var sourceFilename: String = filename
  var text: String = input
  //TF_IDF?
  var compponents: List[Mention]
  
}