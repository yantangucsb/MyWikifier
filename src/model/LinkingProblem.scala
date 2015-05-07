package model

import preprocessing._
import model.SurfaceType

import edu.illinois.cs.cogcomp.edison.sentences.TokenizerUtilities.SentenceViewGenerators;
import edu.illinois.cs.cogcomp.edison.sentences.ViewNames;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.thrift.base.Labeling;
import edu.illinois.cs.cogcomp.thrift.base.Span;
import scala.collection.JavaConverters._

class LinkingProblem(input: String, filename: String) {
  var sourceFilename: String = filename
  var text: String = input
  //TF_IDF?
  var compponents: List[Mention] = List()
  var entityMap: Map[String, Mention] = MentionExtract.generateMention(types)(text, generateConstituents())

  def types(c: Constituent): List[SurfaceType.types] = {
    List(SurfaceType.NPSubchunk);
  }
  
  def generateConstituents(): List[Constituent] = {
    var nerTagger: NERTagger = new NERTagger()
    nerTagger.setUp()
    nerTagger.tagData(text)
    var shallowparser = new ShallowParser("configs/NER.config")
    shallowparser.performChunkerAndPos(text)

    var constituents: List[Constituent] = List()
    //get label of ner tags
    var chunkerLabels: Labeling = TextLabeler.getLabeling(shallowparser.chunkerWords, shallowparser.chunkerTags, text, "IllinoisChunker")
    var label: Iterator[Span] = chunkerLabels.getLabelsIterator().asScala
    var ta: TextAnnotation = createAnnotation()
    while(label.hasNext){
      var span: Span = label.next();
      var c: Constituent = makeConstituentFixTokenBoundaries(span.label, ViewNames.NER, ta, span.start, span.ending);
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
  
  def createAnnotation(): TextAnnotation = 
    new TextAnnotation("fakeCorpus","fakeId", text,SentenceViewGenerators.LBJSentenceViewGenerator)
}