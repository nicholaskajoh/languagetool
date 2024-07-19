/* LanguageTool, a natural language style checker
 * Copyright (C) 2007 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.language;
import org.jetbrains.annotations.NotNull;
import org.languagetool.*;
import org.languagetool.languagemodel.LanguageModel;
import org.languagetool.rules.*;
import org.languagetool.rules.pcm.NaijaMultitokenSpeller;
import org.languagetool.rules.spelling.multitoken.MultitokenSpeller;
import org.languagetool.tagging.Tagger;
import org.languagetool.tagging.disambiguation.Disambiguator;
import org.languagetool.tagging.pcm.NaijaHybridDisambiguator;
import org.languagetool.tagging.pcm.NaijaTagger;
import org.languagetool.tokenizers.SRXSentenceTokenizer;
import org.languagetool.tokenizers.SentenceTokenizer;
import org.languagetool.tokenizers.Tokenizer;
import org.languagetool.tokenizers.pcm.NaijaWordTokenizer;
import org.languagetool.tools.Tools;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Naija extends Language implements AutoCloseable {
  private LanguageModel languageModel;

  @NotNull
  @Override
  public Language getDefaultLanguageVariant() {
    return Languages.getLanguageForShortCode("pcm");
  }

  @Override
  public SentenceTokenizer createDefaultSentenceTokenizer() {
    return new SRXSentenceTokenizer(this);
  }

  @Override
  public String getName() {
    return "Naija";
  }

  @Override
  public String getShortCode() {
    return "pcm";
  }

  @Override
  public String[] getCountries() {
    return new String[]{"NG"};
  }

  @NotNull
  @Override
  public Tagger createDefaultTagger() {
    return NaijaTagger.INSTANCE;
  }

  @Override
  public Disambiguator createDefaultDisambiguator() {
    return new NaijaHybridDisambiguator(getDefaultLanguageVariant());
  }

  @Override
  public Tokenizer createDefaultWordTokenizer() {
    return new NaijaWordTokenizer();
  }

  @Override
  public synchronized LanguageModel getLanguageModel(File indexDir) {
    languageModel = initLanguageModel(indexDir, languageModel);
    return languageModel;
  }

  @Override
  public Contributor[] getMaintainers() {
    return new Contributor[] { new Contributor("Nicholas Kajoh") };
  }

  @Override
  public LanguageMaintainedState getMaintainedState() {
    return LanguageMaintainedState.ActivelyMaintained;
  }

  @Override
  public List<Rule> getRelevantRules(ResourceBundle messages, UserConfig userConfig, Language motherTongue, List<Language> altLanguages) {
    return Arrays.asList(
        new CommaWhitespaceRule(messages,
                Example.wrong("We chop coffee<marker> ,</marker> cheese and crackers and grape."),
                Example.fixed("We chop coffee<marker>,</marker> cheese and crackers and grape.")),
        new DoublePunctuationRule(messages, Tools.getUrl("https://languagetool.org/insights/post/punctuation-guide/#what-are-periods")),
        new UppercaseSentenceStartRule(messages, this,
                Example.wrong("Dis house old. <marker>dem</marker> build am for 1950."),
                Example.fixed("Dis house old. <marker>Dem</marker> build am for 1950."),
                Tools.getUrl("https://languagetool.org/insights/post/spelling-capital-letters/")),
        new MultipleWhitespaceRule(messages, this),
        new SentenceWhitespaceRule(messages),
        new WhiteSpaceBeforeParagraphEnd(messages, this),
        new WhiteSpaceAtBeginOfParagraph(messages),
        new EmptyLineRule(messages, this),
        new LongSentenceRule(messages, userConfig, 40),
        new LongParagraphRule(messages, this, userConfig),
        new ParagraphRepeatBeginningRule(messages, this),
        new PunctuationMarkAtParagraphEnd(messages, this),
        new PunctuationMarkAtParagraphEnd2(messages, this)
    );
  }

  /**
   * Closes the language model, if any. 
   * @since 2.7 
   */
  @Override
  public void close() {
    if (languageModel != null) {
      languageModel.close();
    }
  }

  public MultitokenSpeller getMultitokenSpeller() {
    return NaijaMultitokenSpeller.INSTANCE;
  }
}
