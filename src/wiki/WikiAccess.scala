package wiki

import org.apache.commons.lang.StringUtils;

object WikiAccess {
   //how to set this set
  var unlinkableSurfaces: Set[String] = null
  def initialize(indexPath: String) = {
    loadTitleIndex(indexPath)
    loadSurfaceForms(indexPath)
  }
  def isKnownUnlinkable(surface: String): Boolean = {
    StringUtils.isEmpty(surface) || unlinkableSurfaces.contains(surface);
  }
  
  def loadTitleIndex(indexPath: String){
    
  }
  
  def loadSurfaceForms(indexPath: String){
    
  }
}