package org.elasticsearch.indices.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.AnalyzerScope;
import org.elasticsearch.index.analysis.PreBuiltAnalyzerProviderFactory;
import org.elasticsearch.index.analysis.PreBuiltTokenizerFactoryFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.rsj.analysis.core.Configuration;
import org.rsj.analysis.dic.Dictionaries;
import org.rsj.analysis.lucene.KitAnalyzer;
import org.rsj.analysis.lucene.KitTokenizer;

public class KitIndicesAnalysis {
	@Inject
    public KitIndicesAnalysis(final Settings settings,
                                   IndicesAnalysisService indicesAnalysisService,Environment env) {
//        super(settings);
//        Dictionary.initial(new Configuration(env));
        Dictionaries.initial(new Configuration());

//        this.useSmart = settings.get("use_smart", "false").equals("true");

        indicesAnalysisService.analyzerProviderFactories().put("kit",
                new PreBuiltAnalyzerProviderFactory("kit", AnalyzerScope.GLOBAL,
                        new KitAnalyzer()));

        indicesAnalysisService.tokenizerFactories().put("kit",
                new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
                    @Override
                    public String name() {
                        return "kit";
                    }

                    @Override
                    public Tokenizer create() {
                        return new KitTokenizer();
                    }
                }));

    }
}
