package org.qcri.jlucas.demo;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.accum.distances.CosineSimilarity;
import org.nd4j.linalg.factory.Nd4j;
import org.qcri.jlucas.demo.model.FinalOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JordanDisambiguarityService {
    private Logger log = LoggerFactory.getLogger(JordanDisambiguarityService.class);
    private WordVectors model;

    private String[] country_List = {"france","germany","portugal","canada","spain"};
    private String[] person_List = {"lebron","kobe","michael","brandon","anthony"};

    private INDArray centroid_country;
    private INDArray centroid_person;
    private int ndim = 200;
    private String DISAMBIGUATE_WORD = "jordan";
    private final String WORD2VEC_MODEL_NAME = "glove_final.txt";


    public JordanDisambiguarityService() {
        try{
            String filePath2 = new ClassPathResource(WORD2VEC_MODEL_NAME).getFile().getAbsolutePath();

            DataTypeUtil.setDTypeForContext(DataBuffer.Type.FLOAT);
            //INDArray vector = Nd4j.zeros(1, ndim);

            model = WordVectorSerializer.loadTxtVectors(new File(filePath2));

            centroid_country = get_centroid_vector(country_List, ndim);
            centroid_person = get_centroid_vector(person_List, ndim);
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    public FinalOutput processItem(String item) throws Exception{
        FinalOutput output = null;
        String originalText = item;
        item = item.toLowerCase();
        //System.out.println("processItem:" + item);
        try{
            Twokenize twokenize = new Twokenize();

            if(item.contains(DISAMBIGUATE_WORD)){
                List<String> token_list = twokenize.tokenizeRawTweetText(item);
                System.out.println("Actual:" + item);
                System.out.println("token_list: " + token_list);
                output = this.getDecision(originalText, token_list);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        finally {
            return output;
        }

    }

    private FinalOutput getDecision(String item, List<String> token_list){
        List<String> context_words = this.get_context_words(token_list);
        double avg_country_centroid = getavgsimilarity_centroid(context_words.toArray(), centroid_country);
        double avg_person_centroid = getavgsimilarity_centroid(context_words.toArray(), centroid_person);

        return this.isPersonOrCountry(item, avg_country_centroid, avg_person_centroid);
    }

    private FinalOutput isPersonOrCountry(String item, double avg_country_centroid, double avg_person_centroid){
        return new FinalOutput(item, avg_person_centroid, avg_country_centroid);
    }

    private double getavgsimilarity_centroid(Object[] context_words, INDArray centroid){
        double sim = 0.0;
        int total_pass = 0;
        for(Object o : context_words){
            String word = o.toString();
            for(int i = 0; i < word.length(); i++){
                String word_to_search = word.substring(0, word.length() - i);
                try{
                    INDArray vector = model.getWordVectorMatrix(word_to_search);
                    double ret2 =  Nd4j.getExecutioner().execAndReturn(new CosineSimilarity(vector, centroid)).getFinalResult().doubleValue();
                    double simx =  ret2  ;

                    sim = sim + simx;
                    total_pass = total_pass + 1 ;
                }
                catch (Exception e){
                    continue;
                }

            }
        }

        return sim/(total_pass+0.00001);

    }

    private  INDArray get_centroid_vector(String[] list_of_words, int ndim) {
        INDArray vector = Nd4j.zeros(1, ndim);

        for(String word : list_of_words) {
            // System.out.println(word + ':' + org.qcri.jlucas.demo.model.getWordVectorMatrix(word));
            vector = vector.addi(model.getWordVectorMatrix(word));
            //  System.out.println(vector);
        }

        vector =  vector.divi(list_of_words.length);
        //for(int i = 0; i < vector.columns(); i++) {
        //    System.out.println(vector.getDouble(1, i));
        //}

        return vector;
    }

    public List<String> get_context_words(List<String> token_list){
        List<String> context_words = new ArrayList<>();

        token_list.forEach(
                item->{
                    System.out.println(item);
                    if(!item.equalsIgnoreCase(DISAMBIGUATE_WORD)){
                        System.out.println(item);
                        context_words.add(item);
                    }
                }
        );

        return context_words;

    }


    private double getavgsimilarity(Object[] context_words, String[] example_list){
        double sim = 0.0;
        double total_pass = 0;

        for(String example : example_list){
            for(Object o : context_words){
                String word = o.toString();
                for(int i = 0; i < word.length(); i++)
                {
                    int len = word.length() - i;
                    String word_to_search = word.substring(0, len);
                    try{
                        double simx = model.similarity(word_to_search,example) ;
                        sim = sim + simx ;
                        total_pass = total_pass + 1 ;
                    }
                    catch(Exception e){
                        continue;
                    }

                }

            }
        }

        return sim/(total_pass+0.00001) ;
    }


}
