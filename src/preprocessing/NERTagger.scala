package preprocessing

import java.lang._;
import java.lang.Exception._;
import java.util.Vector;
import edu.illinois.cs.cogcomp.LbjNer.LbjFeatures.NETaggerLevel1;
import edu.illinois.cs.cogcomp.LbjNer.LbjFeatures.NETaggerLevel2;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.Data;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.NERDocument;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.NETagPlain;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.Parameters;
import edu.illinois.cs.cogcomp.LbjNer.LbjTagger.ParametersForLbjCode;
import edu.illinois.cs.cogcomp.LbjNer.ParsingProcessingData.PlainTextReader;
import LBJ2.parse.LinkedVector;

class NERTagger {
  var CONFIG : String = "configs/NER.config";
  var t1 : NETaggerLevel1 = null;
  var t2 : NETaggerLevel2 = null;
  
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
  def tagData(text : String) : Map[String, String] = {
    var NERCollection : Map[String, String] = Map()
    
    var sentences : Vector[LinkedVector] = PlainTextReader.parseText(text)
    var data : Data = new Data(new NERDocument(sentences, "input"))
    var output : String = "";
    try {
      var output = NETagPlain.tagData(data, t1, t2);
      println(output);
    } catch {
      case ex : Exception => {
        println("Exception in NERing!");
      }
    }
    return NERCollection
  }

}