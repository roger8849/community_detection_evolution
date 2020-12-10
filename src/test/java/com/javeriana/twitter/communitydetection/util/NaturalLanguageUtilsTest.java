package com.javeriana.twitter.communitydetection.util;
import static org.junit.Assert.assertThat;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class NaturalLanguageUtilsTest {

  @InjectMocks
  NaturalLanguageUtils naturalLanguageUtils;

  @Mock
  AppProperties properties;

  @Test
  public void testCleanText() {
    String text =
        "RT @RevistaDinero: #Carteles l El superintendente de Industria, Pablo Felipe Robledo #advirtió @que “aquí la víctima es la Agencia Logística… https://t.co/DW0Ux6lInJ";
    text = naturalLanguageUtils.cleanText(text);
    System.out.println(text);
    assertThat(text, CoreMatchers.equalTo("Roger ramirez espejo    asdf asdf asdf"));
  }
}
