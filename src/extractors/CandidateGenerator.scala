package extractors

import wiki._
import model._

import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;

object CandidateGenerator {
  def getGlobalCandidates(ta: TextAnnotation, e: Mention): List[WikiMatchData] = {
    null
  }
/*    var surfaceForm: String = e.surfaceForm;
    var maxCandidates: Int = GlobalParameters.params.maxCandidatesToGenerateInitially;

    // Initial candidate generation
    List<WikiMatchData> candidates = getCandidates(surfaceForm);
    
    
    if(!GlobalParameters.params.GENERATE_CONTEXTUAL_CANDIDATES)
        return candidates;

    // Handles possessive cases
    if(e.endTokenId < ta.getTokens().length){
        String nextToken = ta.getToken(e.endTokenId);
        String original = surfaceForm;
        if("'s".equals(nextToken))
            surfaceForm = surfaceForm+"'s";
        if(surfaceForm.endsWith("'") && nextToken.equals("s"))
            surfaceForm = surfaceForm+"s";
        if(original !=surfaceForm){
                List<WikiMatchData> possessiveCandidates = getCandidates(surfaceForm);
                if(possessiveCandidates.size()>0)
                    return possessiveCandidates;
        }
    }

    
    // This is the case where a concept such as Ministry of Defense that
    // each country has one
    // Potential candidate leaking, needs to collect additional evidence to
    // narrow down
    if (candidates.size() == maxCandidates) {
      candidates = filterLongList(ta, e, candidates);
    }
    
    // US navy => US Navy
    if(WordFeatures.isCapitalized(surfaceForm)){
        String[] tokens = StringUtils.split(surfaceForm,' ');
        if(tokens.length==2 
                && Character.isLowerCase(tokens[1].charAt(0))
                && !WordFeatures.isCapitalized(tokens[1])
                && !isStopWord(tokens[0])){
            String newSurface = tokens[0] + " " + StringUtils.capitalize(tokens[1]);
            candidates.addAll(getCandidates(newSurface));
        }
        // Another form of acronym
        if(WordFeatures.isLikelyAcronym(surfaceForm) && surfaceForm.contains(".")){
            candidates.addAll(getCandidates(surfaceForm.replace(".", "")));
        }
//            String[] tokens = StringUtils.split(surfaceForm,' ');
//              if(tokens.length==2 && tokens[1].length()>0 
//                      && Character.isLowerCase(tokens[1].charAt(0))
//                      && !GlobalParameters.stops.isStopword(tokens[0].toLowerCase())){
//                  String newSurface = tokens[0] + " " + StringUtils.capitalize(tokens[1]);
//                  List<WikiMatchData> newCands = getCandidates(newSurface);
//                  candidates.addAll(newCands);
//              }
        }

    if (candidates.size() == 0 && surfaceForm.endsWith(".")) {
        candidates = getCandidates(StringUtils.stripEnd(surfaceForm, "."));
    }
    
    // Spell check and query expansion
    if (candidates.size() == 0) {
        String original = surfaceForm;
        if(GlobalParameters.params.USE_SPELL_CHECK)
            surfaceForm = SurfaceFormSpellChecker.getCorrection(surfaceForm);
      if(surfaceForm.endsWith("Inc") || surfaceForm.endsWith("Corp") || surfaceForm.endsWith("Co"))
          surfaceForm = surfaceForm + '.';
        surfaceForm = StringUtils.strip(surfaceForm,"'");
      if(!original.equals(surfaceForm))
          candidates = getCandidates(surfaceForm);
    }
    
    // Remove trailing qualifiers
    if (candidates.size() == 0 &&
            ( surfaceForm.endsWith("Inc.") 
            || surfaceForm.endsWith("Corp.") 
            || surfaceForm.endsWith("Co."))){
            
        surfaceForm = StringUtils.substringBeforeLast(surfaceForm, " ");
        candidates = getCandidates(surfaceForm);
    }

    // Then fix idiosyncratic expressions
    if (candidates.size() == 0) {
      surfaceForm = normalizePhrase(surfaceForm);
      candidates = getCandidates(surfaceForm);
    }

    return candidates;
  }

  public static List<WikiMatchData> filterLongList(TextAnnotation ta, Mention e, List<WikiMatchData> currentList)
      throws Exception {

    String extendedSurface = e.surfaceForm;
    boolean extended = false;
    // Try filter using NP pre-modifier
    if (e.startTokenId > 0) {
      String prevToken = ta.getToken(e.startTokenId - 1);
      if (WordFeatures.isCapitalized(prevToken)) {
        extendedSurface = prevToken + " " + extendedSurface;
        extended = true;
      }
    }
    if (extended) {
      List<WikiMatchData> filteredList = getCandidates(extendedSurface);
      if (filteredList.size() > 0)
        return filteredList;
    }

    return currentList;
  }*/
}