package model

import wiki._

class WikiCandidate(mention: Mention, matchData: WikiMatchData) {
  var titleName: String = matchData.getTitleName()
  var menttionToDisambiguate: Mention = mention
  var wikiData = matchData
}