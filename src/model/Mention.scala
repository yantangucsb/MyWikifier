package model

import edu.illinois.cs.cogcomp.edison.sentences.Constituent;

object SurfaceType extends Enumeration {
  type types = Value
  val NER, LOC, PER, ORG, MISC, ESAEntity, NPChunk, NPSubchunk = Value
  val NERTypes: Set[types] = Set(LOC, PER, ORG, MISC)
  def getNERTypes(): Set[types] = NERTypes
}

class Mention(c: Constituent) {
  var types: Set[SurfaceType.types] = Set()
  var isTopLevel: Boolean
  def isNamedEntity(): Boolean = types.contains(SurfaceType.NER)
  def setTopLevelEntity() = (isTopLevel = true)
}