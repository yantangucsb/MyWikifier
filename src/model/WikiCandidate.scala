package model

import wiki._

import edu.illinois.cs.cogcomp.wikifier.wiki.access.WikiAccess.WikiMatchData;

class WikiCandidate(mention: TextMention, matchData: WikiMatchData) {
  var menttionToDisambiguate: TextMention = mention
  var wikiData = matchData
}