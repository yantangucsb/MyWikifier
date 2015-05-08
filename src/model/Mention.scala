package model

import org.apache.commons.lang3.StringUtils;

import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;

object SurfaceType extends Enumeration {
  type types = Value
  val NER, LOC, PER, ORG, MISC, ESAEntity, NPChunk, NPSubchunk = Value
  val NERTypes: Set[types] = Set(LOC, PER, ORG, MISC)
  def getNERTypes(): Set[types] = NERTypes
}

class Mention(c: Constituent, problem: LinkingProblem) {
  var types: Set[SurfaceType.types] = Set()
  var isTopLevel: Boolean = false
  var setLevel: Boolean = false
  var surfaceForm: String = c.getSurfaceString.trim()
  var start = c.getStartCharOffset
  var end = c.getEndCharOffset
  var charStart = start
  var startTokenId = c.getStartSpan
  

  def isNamedEntity(): Boolean = types.contains(SurfaceType.NER)
  def setTopLevelEntity() = (isTopLevel = true)
  def isTopLevelMention(): Boolean = {
    if(setLevel)
      isTopLevel
    if(!isAllCaps()){
      isTopLevel = false;
    }else{
      isTopLevel = (hasLeftBoundary() && hasRightBoundary())
    }
    setLevel = true;
    isToplevel
  }
  
  def isAllCaps(): Boolean = {
    
    var tokens:Array[String] = StringUtils.split(surfaceForm, " ");
    
    // First determinant is not allowed when context is available
    if(tokens.length > 0
            && (isFirstTokenInSentence(startTokenId, problem.ta))
            && ("A".equals(tokens[0]) || "The".equals(tokens[0]))
           )
    {
      false
    }
    
    for(String s:tokens){
      if(!WordFeatures.isCapitalized(s)){
        false;
      }
    }
    true;
  }
  
  def isFirstTokenInSentence(tokenId: Int, ta: TextAnnotation): Boolean = {
        if(tokenId<=0)
            return true;
        int prevTokenId = tokenId-1;
        return ta.getSentenceId(tokenId) != ta.getSentenceId(prevTokenId)
                || GlobalParameters.stops.isStopword(ta.getToken(prevTokenId).toLowerCase());
    }
  
  def hasLeftBoundary(){
      if( !WordFeatures.isCapitalized(surfaceForm))
          return false;
      if(startTokenId == 0 || isFirstTokenInSentence(startTokenId, parentProblem.ta))
          return true;
      String previousToken = parentProblem.ta.getToken(startTokenId-1);
      char lastChar = previousToken.charAt(previousToken.length()-1);
      return !WordFeatures.isCapitalized(previousToken) || !Character.isLetterOrDigit(lastChar);
  }
  
  public boolean hasRightBoundary(){
      if( !WordFeatures.isCapitalized(surfaceForm))
            return false;
        if(endTokenId >= parentProblem.ta.getTokens().length-1)
            return true;
        String nextToken = parentProblem.ta.getToken(endTokenId);
        return !WordFeatures.isCapitalized(nextToken);
  }
}