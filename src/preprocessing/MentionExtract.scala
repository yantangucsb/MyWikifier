package preprocessing

import model.Mention
import model.SurfaceType
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;

object MentionExtract {

  //get into the linkingproblem class
  def generateMention(types: Constituent => List[SurfaceType.types])(input: String, candidates: List[Constituent]): Map[String, Mention] = {
    var entityMap: Map[String, Mention] = Map()
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
            var e: Mention = new Mention(c);
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