package org.elasticsearch.indices.analysis;

import org.elasticsearch.common.inject.AbstractModule;

public class KitIndicesAnalysisModule extends AbstractModule {
	@Override
    protected void configure() {
        bind(KitIndicesAnalysis.class).asEagerSingleton();
    }
}
