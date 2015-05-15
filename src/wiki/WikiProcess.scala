package wiki

import java.nio.file.Paths;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.Document;
import edu.illinois.cs.cogcomp.wikifier.utils.lucene.Lucene;
import edu.illinois.cs.cogcomp.wikifier.utils.datastructure.StringMap;
import edu.illinois.cs.cogcomp.wikifier.utils.datastructure.StringMap.StringSet;
import edu.illinois.cs.cogcomp.wikifier.wiki.access._;
import edu.illinois.cs.cogcomp.wikifier.models.WikipediaProtobuffers.BasicTitleDataInfoProto;
import edu.illinois.cs.cogcomp.wikifier.models.WikipediaProtobuffers.LexicalTitleDataInfoProto;
import edu.illinois.cs.cogcomp.wikifier.models.WikipediaProtobuffers.SemanticTitleDataInfoProto;
import edu.illinois.cs.cogcomp.wikifier.models.WikipediaProtobuffers.SurfaceFormSummaryProto;
import gnu.trove.map.hash.TIntIntHashMap;


import scala.collection.JavaConverters._

object WikiProcess {
  var wikiaccess: ProtobufferBasedWikipediaAccess = null
  
/*  var unlinkableSurfaces: StringSet = null
  var surfaceFormReader: IndexReader = null
  var completeIndexReader: IndexReader = null
  var surfaceFromToLuceneIdMap: StringMap[Integer] = null
  var wikiTitleToIdMap: StringMap[Integer] = null
  var tid2DocId: TIntIntHashMap = null
  var minSurfaceLen: Double = 0
  var minLinkability: Double = -1
  
  var wikiDataSummary: WikipediaSummaryData = null
  var surfaceFormCache: Map[String, SurfaceFormSummaryProto] = Map()

  final var TOTAL_WIKIPEDIA_TITLE_ESTIMATE: Int = 2478573;*/

  def initialize(indexPath: String) = {
//    loadTitleIndex(Paths.get(protobufferAccessDir, "WikiAccessProtoBuffers").toString());
//    loadSurfaceForms(Paths.get(protobufferAccessDir, "SurfaceFormsInfo").toString());
    wikiaccess = new ProtobufferBasedWikipediaAccess(indexPath)
  }
  
  def isKnownUnlinkable(surface: String): Boolean = {
    wikiaccess.isKnownUnlinkable(surface)
  }
}