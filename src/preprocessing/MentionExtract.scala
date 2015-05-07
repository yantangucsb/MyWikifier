package preprocessing

import model.Mention
import model.SurfaceType
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;

class MentionExtract {
  var entityMap: Map[String, Mention] = Map()
  //get into the linkingproblem class
  def generate(types: Constituent => Set[SurfaceType.types])(input: String, candidates: Set[Constituent]) {
        for (c <- candidates) {
            if (c.getStartSpan()<c.getEndSpan())
            try {
                var key: String = getPositionHashKey(c);
                if (entityMap.contains(key)) {
                    var existingEntity: Mention = entityMap.apply(key);
                    if (existingEntity.types.size == 0) {
                        existingEntity.types = existingEntity.types ++ types(c);
                    }
                } else {
                    var e: Mention = new Mention(c);
                    e.types =  e.types ++ types(c);
                    if(e.isNamedEntity())
                        e.setTopLevelEntity();
                    entityMap == entityMap + ((key, e));
                }
            } catch {
              case ex: Exception=> throw new Exception("Warning -- a nasty exception caught:");
            }
        }
  }
  def getPositionHashKey(c: Constituent): String = {
    var start: Int = c.getStartCharOffset
    var len: Int = c.getEndCharOffset
    start+ "-" + (start + len)
  }
}