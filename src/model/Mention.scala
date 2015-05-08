package model

import wiki._

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
  var endTokenId = c.getEndSpan
  
  var candidates: List[List[WikiCandidate]] = null
  
  override def toString():String = {
    surfaceForm mkString
  }
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
    isTopLevel
  }
  
  def isAllCaps(): Boolean = {
    
    var tokens:Array[String] = StringUtils.split(surfaceForm, " ");
    
    // First determinant is not allowed when context is available
    if(tokens.length > 0
            && (isFirstTokenInSentence(startTokenId, problem.ta))
            && ("A" == tokens(0) || "The" == (tokens(0)))
           )
    {
      false
    }
    
    for(s <- tokens){
      if(!isCapitalized(s)){
        false;
      }
    }
    true;
  }
  
  def isFirstTokenInSentence(tokenId: Int, ta: TextAnnotation): Boolean = {
        if(tokenId<=0)
            return true;
        var prevTokenId: Int = tokenId-1;
        (ta.getSentenceId(tokenId) != ta.getSentenceId(prevTokenId)) || StopWords.isStopword(ta.getToken(prevTokenId).toLowerCase());
    }
  
  def hasLeftBoundary(): Boolean = {
      if( !isCapitalized(surfaceForm))
          false;
      if(startTokenId == 0 || isFirstTokenInSentence(startTokenId, problem.ta))
          true;
      var previousToken: String = problem.ta.getToken(startTokenId-1);
      var lastChar: Char = previousToken.charAt(previousToken.length()-1);
      !isCapitalized(previousToken) || !Character.isLetterOrDigit(lastChar);
  }
  
  def hasRightBoundary(): Boolean = {
      if( !isCapitalized(surfaceForm))
            false;
        if(endTokenId >= problem.ta.getTokens().length-1)
            return true;
        var nextToken: String = problem.ta.getToken(endTokenId);
        return !isCapitalized(nextToken);
  }

  def isCapitalized(s: String): Boolean = {
    if (s == null || s.length() == 0)
      false
    else
      Character.isUpperCase(s.charAt(0));
  }

  def generateLocalContext(text: String) {

  }
}