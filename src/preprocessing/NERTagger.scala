package preprocessing

import java.lang._;
import java.lang.Exception._;
import java.util.Vector;
import edu.illinois.cs.cogcomp.LbjNer.LbjFeatures.NETaggerLevel1;
import edu.illinois.cs.cogcomp.LbjNer.LbjFeatures.NETaggerLevel2;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.Data;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.NEWord;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.NERDocument;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.Parameters;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.ParametersForLbjCode;
import edu.illinois.cs.cogcomp.LbjNer.ParsingProcessingData.PlainTextReader;
import edu.illinois.cs.cogcomp.LbjNer.ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import edu.illinois.cs.cogcomp.LbjNer.InferenceMethods.Decoder;
import LBJ2.parse.LinkedVector;

class NERTagger {
  var CONFIG : String = "configs/NER.config";
  var t1 : NETaggerLevel1 = null;
  var t2 : NETaggerLevel2 = null;
  var resultWords : List[String] = List();
  var resultPredictions : List[String] = List();
  
  //Before NER
  def setUp() : Unit = {
    try {
      Parameters.readConfigAndLoadExternalData(CONFIG);
      ParametersForLbjCode.currentParameters.forceNewSentenceOnLineBreaks = false;
      System.out.println("Reading model file : " + ParametersForLbjCode.currentParameters.pathToModelFile+".level1");
      t1=new NETaggerLevel1(ParametersForLbjCode.currentParameters.pathToModelFile+".level1",
          ParametersForLbjCode.currentParameters.pathToModelFile+".level1.lex");
      System.out.println("Reading model file : " + ParametersForLbjCode.currentParameters.pathToModelFile+".level2");
      t2=new NETaggerLevel2(ParametersForLbjCode.currentParameters.pathToModelFile+".level2", 
          ParametersForLbjCode.currentParameters.pathToModelFile+".level2.lex");
    }
    catch {
      case ex : Exception => {
        println("Exception in setting up NER tagger!");
      }
    }
  }
  
  //NER
  def tagData(text : String) : Unit = { 
    var sentences : Vector[LinkedVector] = PlainTextReader.parseText(text)
    var data : Data = new Data(new NERDocument(sentences, "input"))
    var output : String = "";
    try {
      ExpressiveFeaturesAnnotator.annotate(data);
      Decoder.annotateDataBIO(data, t1, t2);
    } catch {
      case ex: Exception => {
      ex.printStackTrace();
      }
    }
    for (i <- 0 until sentences.size()) {
        for (j <- 0 until sentences.get(i).size()) {
            var w : NEWord = sentences.get(i).get(j).asInstanceOf[NEWord]
//            println(w.form)
//            println(w.neTypeLevel2)
            resultWords = resultWords ++ List(w.form);
            resultPredictions = resultPredictions ++ List(w.neTypeLevel2);
        }
    }
  }
}