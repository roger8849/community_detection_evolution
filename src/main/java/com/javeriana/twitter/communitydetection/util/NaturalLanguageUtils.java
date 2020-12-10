package com.javeriana.twitter.communitydetection.util;

import com.javeriana.twitter.communitydetection.service.impl.DefaultTwitterService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.jws.soap.SOAPBinding.Use;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

@Component
public class NaturalLanguageUtils {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultTwitterService.class);


  @Autowired
  private StanfordCoreNLP stanfordCoreNLP;

  @Autowired
  private AppProperties properties;


  public Set<String> getEntitiesFromText(String text, Boolean shouldIgnoreNumbers) {
    shouldIgnoreNumbers =
        shouldIgnoreNumbers == null ? this.properties.isShouldIgnoreNumbers() : shouldIgnoreNumbers;
    Set<String> entities = new HashSet<>();
    String cleanedText = cleanTextWithTokenizers(text);
    try {
      CoreDocument document = new CoreDocument(cleanedText);
      this.stanfordCoreNLP.annotate(document);
      List<CoreSentence> sentences = document.sentences();
      for (CoreSentence sentence : sentences) {
        List<CoreEntityMention> entityMentions = sentence.entityMentions();
        entities.addAll(entityMentions.parallelStream().map(c -> c.toString().toLowerCase())
            .collect(Collectors.toSet()));
      }
      this.cleanDetectedEntities(entities, shouldIgnoreNumbers);
    } catch (RuntimeException exception){
      LOG.warn(new StringBuilder("Fail to detect entities in text: ").append(cleanedText).toString());
      entities = new HashSet<>();
    }

    return entities;
  }

  public void cleanDetectedEntities(Set<String> entities, Boolean shouldIgnoreNumbers) {
    Set<String> entitiesToClean = new HashSet<>();
    for (String entity : entities) {
      if (entity.contains("#") || entity.contains("@")
          || (Boolean.TRUE.equals(shouldIgnoreNumbers) && StringUtils.isNumeric(entity))) {
        entitiesToClean.add(entity);
      }
      for (String stopWord : this.properties.getStopWords()) {
        if (entity.contains(stopWord)) {
          entitiesToClean.add(entity);
        }
      }
    }
    entities.removeAll(entitiesToClean);
  }

  /**
   * this method improves the detection of unwanted text by reducing the complexity to O(n) where n
   * is the number of words in the text parameter
   * 
   * @param text
   * @return text without hashtags mentions and stop words configured in the application.properties
   *         file
   */
  public String cleanTextWithTokenizers(String text) {
    StringTokenizer tokenizer = new StringTokenizer(text);
    StringBuilder phrase = new StringBuilder();
    while (tokenizer.hasMoreTokens()) {
      String word = tokenizer.nextToken();
      if (!word.startsWith("http")) {
        phrase.append(word).append(" ");
      }
    }
    return phrase.toString().trim();
  }

  /**
   * This method is deprecated because the multiple use of regular expressions might impact in the
   * performance of the application please {@link Use} <b>cleanTextWithTokenizers </b>
   * 
   * @param text
   * @return text without hashtags mentions and stop words configured in the application.properties
   *         file
   */
  @Deprecated
  public String cleanText(String text) {
    text = removeHashtagsAndMentions(text);
    text = removeStopWords(text);
    text = removeUrl(text);
    return text;
  }

  @Deprecated
  private String removeStopWords(String text) {
    Set<String> stopWords = new HashSet<>(properties.getStopWords());

    StringBuilder cleanText = new StringBuilder();
    int index = 0;

    while (index < text.length()) {
      int nextIndex = text.indexOf(' ', index);
      if (nextIndex == -1) {
        nextIndex = text.length() - 1;
      }
      String word = text.substring(index, nextIndex);
      if (!stopWords.contains(word.toLowerCase())) {
        cleanText.append(word);
        if (nextIndex < text.length()) {
          // this adds the word delimiter, e.g. the following space
          cleanText.append(text.substring(nextIndex, nextIndex + 1));
        }
      }
      index = nextIndex + 1;
    }

    return cleanText.toString();
  }

  @Deprecated
  private String removeHashtagsAndMentions(String text) {
    return text.replaceAll("#\\p{L}+", "").replaceAll("@\\p{L}+", "").replaceAll("#", "")
        .replaceAll("@", "");
  }

  @Deprecated
  private String removeUrl(String text) {
    // rid of ? and & in urls since replaceAll can't deal with them
    String noParamsUrl = text.replaceAll("\\?", "").replaceAll("\\&", "");

    String urlPattern =
        "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
    Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(text);
    int i = 0;
    while (m.find()) {
      text =
          noParamsUrl.replaceAll(m.group(i).replaceAll("\\?", "").replaceAll("\\&", ""), "").trim();
      i++;
    }
    return text;
  }

}
