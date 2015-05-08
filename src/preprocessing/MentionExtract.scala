package preprocessing

import model._

import edu.illinois.cs.cogcomp.edison.sentences.TokenizerUtilities.SentenceViewGenerators;
import edu.illinois.cs.cogcomp.edison.sentences.ViewNames;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.thrift.base.Labeling;
import edu.illinois.cs.cogcomp.thrift.base.Span;
import scala.collection.JavaConverters._

object MentionExtract {
  def extract(problem: LinkingProblem): List[Mention] = {
    var NerConstituents: List[Constituent] = generateNerConstituents(problem)
    var candidateEntities: Map[String, Mention] = Map()
    candidateEntities = generateMention(NerTypes)(problem.text, NerConstituents, candidateEntities)
    var ChunkConstituents: List[Constituent] = generateChunkConstituents(problem)
    candidateEntities = generateMention(NerTypes)(problem.text, ChunkConstituents, candidateEntities)
    //Merger maps
    for (e <- candidateEntities.values) {

            if ((!e.isTopLevelMention() && stops.isStopword(e.surfaceForm.toLowerCase())) 
                    // Fast unlink
                    || GlobalParameters.wikiAccess.isKnownUnlinkable(e.surfaceForm)) {
                continue;
            }

            // problem is slow
            List<WikiMatchData> matchData = CandidateGenerator.getGlobalCandidates(ta, e);

            // Here we allow zero candidate entities
            if (matchData.size() > 0 
                    || e.isTopLevelMention()
                    || TitleNameIndexer.normalize(e.surfaceForm) != null) {

                e.generateLocalContext(text, wikipedia);

                e.candidates = Lists.newArrayList();
                List<WikiCandidate> firstLayer = Lists.newArrayList();
                for (WikiMatchData wikiMatchData : matchData) {
                    WikiCandidate candidate = new WikiCandidate(e, wikiMatchData);
                    firstLayer.add(candidate);
                }
                e.candidates.add(firstLayer);

                // now mark the corresponding reference instance (if exists) that problem wikifiable
                // entity tries to disambiguate it
                if (refInstanceMap.containsKey(e.getPositionHashKey()))
                    refInstanceMap.get(e.getPositionHashKey()).setAssignedEntity(e);
                res.add(e);
            } else {
                // there was no wiki match data for the entity
                if (manualEntities.contains(e)) {
                    System.out.println("Important warning: the manually defined entity " + e.surfaceForm
                            + " had 0 disambiguation candidates");
                }

            }
        } // running on all potentially the wikifiable entitites

        if (displayProgress) {
            printProgress(res);
        }

        System.out.println("Done constructing the Wikifiable entities");
        expandNER(res);
        return res;
  }
  
  def generateNerConstituents(problem: LinkingProblem): List[Constituent] = {
    var nerTagger: NERTagger = new NERTagger()
    nerTagger.setUp()
    nerTagger.tagData(problem.text)

    var constituents: List[Constituent] = List()
    //get label of ner tags
    var nerLabels: Labeling = TextLabeler.getLabeling(nerTagger.resultWords, nerTagger.resultPredictions, problem.text, "IllinoisNER.2.1")
    var label: Iterator[Span] = nerLabels.getLabelsIterator().asScala
    while(label.hasNext){
      var span: Span = label.next();
      var c: Constituent = makeConstituentFixTokenBoundaries(span.label, ViewNames.NER, problem.ta, span.start, span.ending);
      if(c!=null) constituents = constituents ++ List(c);
    }
    constituents
  }
  
  def generateChunkConstituents(problem: LinkingProblem): List[Constituent] = {
    var shallowparser = new ShallowParser("configs/NER.config")
    shallowparser.performChunkerAndPos(problem.text)

    var constituents: List[Constituent] = List()
    //get label of ner tags
    var chunkerLabels: Labeling = TextLabeler.getLabeling(shallowparser.chunkerWords, shallowparser.chunkerTags, problem.text, "IllinoisChunker")
    var label: Iterator[Span] = chunkerLabels.getLabelsIterator().asScala
    while(label.hasNext){
      var span: Span = label.next();
      var c: Constituent = makeConstituentFixTokenBoundaries(span.label, ViewNames.SHALLOW_PARSE, problem.ta, span.start, span.ending);
      if(c!=null) constituents = constituents ++ List(c);
    }
    constituents
  }
  
  def makeConstituentFixTokenBoundaries(label: String, viewName: String, ta: TextAnnotation, start: Int, end: Int): Constituent = {
    var e: Int = end - 1;
    if (e < start)
      e = start;
    var c: Constituent = null;
    try {
      c = new Constituent(label, viewName, ta, ta.getTokenIdFromCharacterOffset(start), ta.getTokenIdFromCharacterOffset(e) + 1);
      if (ta.getText().substring(start, end).equals(c.getSurfaceString()))
        return c;

      c = new Constituent(label, viewName, ta, ta.getTokenIdFromCharacterOffset(start), ta.getTokenIdFromCharacterOffset(end));
      if (ta.getText().substring(start, end).equals(c.getSurfaceString()))
        return c;
      c = new Constituent(label, viewName, ta, ta.getTokenIdFromCharacterOffset(start), ta.getTokenIdFromCharacterOffset(end) + 1);
      if (ta.getText().substring(start, end).equals(c.getSurfaceString()))
        c
    } catch {
      case ex: Exception => throw new Exception
    }
    null
  }
  
  def NerTypes(c: Constituent): List[SurfaceType.types] = {
    List(SurfaceType.NER, SurfaceType.withName(c.getLabel()))
  }
  
  def ChunkTypes(c: Constituent): List[SurfaceType.types] = {
    List(SurfaceType.NPSubchunk)
  }
  
  def generateMention(types: Constituent => List[SurfaceType.types])(input: String, candidates: List[Constituent], entityMap: Map[String, Mention]): Map[String, Mention] = {
    for (c <- candidates) {
      if (c.getStartSpan() < c.getEndSpan())
        try {
          var key: String = getPositionHashKey(c);
          if (entityMap.contains(key)) {
            var existingEntity: Mention = entityMap.apply(key);
            if (existingEntity.types.size == 0) {
              existingEntity.types = existingEntity.types ++ types(c);
            }
          } else {
            var e: Mention = new Mention(c, problem);
            e.types = e.types ++ types(c);
            if (e.isNamedEntity())
              e.setTopLevelEntity();
            entityMap == entityMap + ((key, e));
          }
        } catch {
          case ex: Exception => throw new Exception("Warning -- a nasty exception caught:");
        }
    }
    entityMap
  }
  def getPositionHashKey(c: Constituent): String = {
    var start: Int = c.getStartCharOffset
    var len: Int = c.getEndCharOffset
    start+ "-" + (start + len)
  }
}