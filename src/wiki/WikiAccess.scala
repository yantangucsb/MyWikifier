package wiki

import java.nio.file.Paths;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.Document;
import edu.illinois.cs.cogcomp.wikifier.utils.lucene.Lucene;
import edu.illinois.cs.cogcomp.wikifier.utils.datastructure.StringMap;
import edu.illinois.cs.cogcomp.wikifier.utils.datastructure.StringMap.StringSet;
import edu.illinois.cs.cogcomp.wikifier.wiki.access.WikiAccess.SurfaceFormFields;
import edu.illinois.cs.cogcomp.wikifier.wiki.access.WikiAccess.WikiDataFields;
import edu.illinois.cs.cogcomp.wikifier.wiki.access.TitleNameNormalizer;
import edu.illinois.cs.cogcomp.wikifier.models.WikipediaProtobuffers.BasicTitleDataInfoProto;
import edu.illinois.cs.cogcomp.wikifier.models.WikipediaProtobuffers.LexicalTitleDataInfoProto;
import edu.illinois.cs.cogcomp.wikifier.models.WikipediaProtobuffers.SemanticTitleDataInfoProto;
import edu.illinois.cs.cogcomp.wikifier.models.WikipediaProtobuffers.SurfaceFormSummaryProto;
import gnu.trove.map.hash.TIntIntHashMap;

object WikiAccess {
  var protobufferAccessDir : String = "data/Wiki_Data/"
   //how to set this set
  var unlinkableSurfaces: StringSet = null
  var surfaceFormReader : IndexReader = null
  var completeIndexReader : IndexReader = null
  var surfaceFromToLuceneIdMap : StringMap[Integer] = null
  var wikiTitleToIdMap : StringMap[Integer] = null
  var tid2DocId : TIntIntHashMap = null
  var minSurfaceLen : Double = 0
  var minLinkability : Double = -1
  
  final var TOTAL_WIKIPEDIA_TITLE_ESTIMATE : Int = 2478573;
  
  def initialize() = {
    loadTitleIndex(Paths.get(protobufferAccessDir,"WikiAccessProtoBuffers").toString());
    loadSurfaceForms(Paths.get(protobufferAccessDir,"SurfaceFormsInfo").toString());
  }
  
  def isKnownUnlinkable(surface: String): Boolean = {
    StringUtils.isEmpty(surface) || unlinkableSurfaces.contains(surface);
  }
  
  def loadTitleIndex(indexPath: String) = {
    println("Opening the index for the complete index interface");
    tid2DocId = new TIntIntHashMap();
    completeIndexReader = Lucene.reader(indexPath);
    println("Prefetching the basic information about the wikipedia articles");

    var totalTitles : Int = completeIndexReader.numDocs();

    wikiTitleToIdMap = new StringMap[Integer](TOTAL_WIKIPEDIA_TITLE_ESTIMATE);
        
    for (docId <- 0 until totalTitles) {
       if (docId % 50000 == 0)
          println(docId + " titles processed out of " + totalTitles);

          var doc : Document = completeIndexReader.document(docId,WikiDataFields.INITIAL_FIELD_SET);

          var currentTitleData : BasicTitleDataInfoProto = BasicTitleDataInfoProto
                    .parseFrom(doc.getBinaryValue(WikiDataFields.BasicInfo.name()).bytes);
            
          var titleId : Int = currentTitleData.getTitleId();

            // Database sanity check
//            if (titleId != currentTitleData.getTitleId())
//                throw new Exception("Data base title id mismatch for " + currentTitleData + " with indexed titleID as " + titleId);
            wikiTitleToIdMap.put(currentTitleData.getTitleSurfaceForm(), titleId);

            // Redirect pages will be masked away by normalization
            tid2DocId.put(titleId, docId);
        }
        // Remapping titles to their redirected ids
        TitleNameNormalizer.normalize(wikiTitleToIdMap);

        println("Actual capacities:\nTitleEssentialData:%d\n", tid2DocId.capacity());
        println("Loaded %d nonNormalizedTitles\n", wikiTitleToIdMap.size());
        println("Done prefetching the basic data about %d Wikipedia articles\n", totalTitles);
  }
  
  def loadSurfaceForms(indexPath: String) = {
    System.out.println("Loading information about surface form to title id mappings");
    surfaceFormReader = Lucene.reader(indexPath);
    // System.out.println(surfaceFormReader.getFieldNames(IndexReader.FieldOption.ALL));

    var addedSurfaceForms : Int = 0;
    var totalSurfaceForms : Int = surfaceFormReader.numDocs();
    surfaceFromToLuceneIdMap = new StringMap[Integer](4500000);
    unlinkableSurfaces = new StringSet(10);
        
        for (docid <- 0 until totalSurfaceForms) {
            var doc : Document = surfaceFormReader.document(docid);
            var surfaceForm : String = doc.get(SurfaceFormFields.SurfaceForm.name());
            var surfaceFormData : SurfaceFormSummaryProto  = surfaceProtoFromDoc(doc);

            if (isUnlinkable(surfaceForm, surfaceFormData)) {
                unlinkableSurfaces.add(surfaceForm);
            } else {
                addedSurfaceForms += 1;
                surfaceFromToLuceneIdMap.put(surfaceForm, docid);
                if (docid % 100000 == 0)
                    println(addedSurfaceForms + " surface forms is linkable out of " + docid + ". There are "
                            + totalSurfaceForms + "  surface forms total; last surface form read: " + surfaceForm);

            }
        }
        println("There are " + unlinkableSurfaces.size() + " unlinkable surface forms");
        println("Actual capacities:\nSurfaceFormData:%d\n", surfaceFromToLuceneIdMap.capacity());
        println("Done loading information about surface form to title id mappings");

  }
  
  def surfaceExists(surfaceForm : String) : Boolean = {
    surfaceFromToLuceneIdMap.containsKey(surfaceForm)
  }
  
  def isUnlinkable(surface : String, proto : SurfaceFormSummaryProto) : Boolean = {
        var linkability : Double = (proto.getLinkedAppearanceCount() / proto.getTotalAppearanceCount())
        surface.length() < minSurfaceLen || linkability < minLinkability;
    }
  
  private def surfaceProtoFromDoc(doc : Document) : SurfaceFormSummaryProto = {
        try {
            SurfaceFormSummaryProto.parseFrom(doc.getBinaryValue(SurfaceFormFields.SurfaceFormSummaryProto.name()).bytes);
        } catch {
          case ex : Exception => {
            ex.printStackTrace()
          }
        }
        return null;
    }
}