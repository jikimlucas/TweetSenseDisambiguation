/**
 * Created by jlucas on 7/26/16.
 */
package org.qcri.jlucas.demo;

import org.qcri.jlucas.demo.model.AverageScore;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ops.impl.accum.distances.CosineSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JordanDisambiguarity {

    private static Logger log = LoggerFactory.getLogger(JordanDisambiguarity.class);
    private static WordVectors model;

    private static String[] country_List = {"france","germany","portugal","canada","spain"};
    private static String[] person_List = {"lebron","kobe","michael","brandon","anthony"};

    private static INDArray centroid_country;
    private static INDArray centroid_person;
    private static int ndim = 200;
    private static String DISAMBIGUATE_WORD = "jordan";
    private static final String WORD2VEC_MODEL_NAME = "glove_final.txt";

    public static void main(String[] args) throws Exception {

       /* String filePath2 = new ClassPathResource(WORD2VEC_MODEL_NAME).getFile().getAbsolutePath();

        DataTypeUtil.setDTypeForContext(DataBuffer.Type.FLOAT);
        INDArray vector = Nd4j.zeros(1, ndim);

        model = WordVectorSerializer.loadTxtVectors(new File(filePath2))  ;

        centroid_country = get_centroid_vector(country_List, ndim);
        centroid_person = get_centroid_vector(person_List, ndim);

        processJason();*/

    }

    public static void processJason() throws Exception{
        try{
            List<String> single_json_list = getEachLineFromFile();
            List<String> single_tweet_text_list  = getTweetTextList(single_json_list);

            Twokenize twokenize = new Twokenize();
            single_tweet_text_list.forEach(item->{
                if(item.contains(DISAMBIGUATE_WORD)){
                    List<String> token_list = twokenize.tokenizeRawTweetText(item);
                    System.out.println("Actual:" + item);
                    System.out.println("token_list: " + token_list);
                    getDecision(token_list);
                }
            });

        }
        catch(Exception e){
            //System.out.println(e);
        }

    }

    public static void getDecision(List<String> token_list){
        for(int i = 0; i < token_list.size(); i ++)
            if (token_list.get(i).toString().contains(DISAMBIGUATE_WORD)) {
                List<String> context_words = get_context_words(i, token_list.toArray());
                double avg_country = getavgsimilarity(context_words.toArray(), country_List);
                double avg_person = getavgsimilarity(context_words.toArray(), person_List);
                double avg_country_centroid = getavgsimilarity_centroid(context_words.toArray(), centroid_country);
                double avg_person_centroid = getavgsimilarity_centroid(context_words.toArray(), centroid_person);

                isPersonOrCountry(avg_country, avg_person, avg_country_centroid, avg_person_centroid);

/*                List<org.qcri.jlucas.demo.model.AverageScore> scoreList = new ArrayList<org.qcri.jlucas.demo.model.AverageScore>();
                scoreList.add(new org.qcri.jlucas.demo.model.AverageScore("avg_country", avg_country));
                scoreList.add(new org.qcri.jlucas.demo.model.AverageScore("avg_person", avg_person));
                scoreList.add(new org.qcri.jlucas.demo.model.AverageScore("avg_country_centroid", avg_country_centroid));
                scoreList.add(new org.qcri.jlucas.demo.model.AverageScore("avg_person_centroid", avg_person_centroid));

                for(org.qcri.jlucas.demo.model.AverageScore item : scoreList){
                    System.out.println(item.getName() + "  :  " + item.getScore());
                }*/

                //System.out.println(token_list.get(i).toString());
                //System.out.println("avg_country: " + avg_country + "  avg_person: " + avg_person);
                //System.out.println("avg_country_centroid: " + avg_country_centroid + "  avg_person_centroid: " + avg_person_centroid);
                System.out.println("=============================================================================================================================================================");
                break;
            }
    }



    public static void isPersonOrCountry(double avg_country, double avg_person, double avg_country_centroid, double avg_person_centroid){
        List<AverageScore> scoreList = new ArrayList<AverageScore>();
        scoreList.add(new AverageScore("avg_country", avg_country));
        scoreList.add(new AverageScore("avg_person", avg_person));
        scoreList.add(new AverageScore("avg_country_centroid", avg_country_centroid));
        scoreList.add(new AverageScore("avg_person_centroid", avg_person_centroid));


        scoreList.sort(Comparator.comparing(a -> a.getScore()));
        AverageScore finalScore = scoreList.get(scoreList.size()-1);
        System.out.println(finalScore.getName() + " : " + finalScore.getScore());


        for(AverageScore item : scoreList){
            System.out.println(item.getName() + "  :  " + item.getScore());
        }
    }

    public static double getavgsimilarity_centroid(Object[] context_words, INDArray centroid){
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

    public static double getavgsimilarity(Object[] context_words, String[] example_list){
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

    private static List<String> getEachLineFromFile() throws Exception{
        String filePath = new ClassPathResource("151107070644_amman_jordan_flood_nov_2015_20151108_vol-1.json").getFile().getAbsolutePath();
        List<String> list = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("UTF-8")))
        {
            list = br.lines().collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<String> getTweetTextList(List<String> list){
        List<String> text_list = new ArrayList<>();

        list.forEach(item->{
            JSONParser parser = new JSONParser();
            Object obj = null;
            try {
                obj = parser.parse(item);
                JSONObject jsonObject = (JSONObject) obj;
                text_list.add(jsonObject.get("text").toString().toLowerCase()) ;

            } catch (ParseException e) {
                //e.printStackTrace();
            }


        });

        return text_list;
    }

    public static INDArray get_centroid_vector(String[] list_of_words, int ndim) {
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

    public static List<String> get_context_words(int pos, Object[] token_list){
        List<String> context_words = new ArrayList<>();

        for(int i =0; i < token_list.length; i++ ){
            if( token_list[i].toString().contains(DISAMBIGUATE_WORD) && i > pos)
            {
                break;
            }
            else{
                context_words.add(token_list[i].toString());
            }
        }

        return context_words;

    }

}
