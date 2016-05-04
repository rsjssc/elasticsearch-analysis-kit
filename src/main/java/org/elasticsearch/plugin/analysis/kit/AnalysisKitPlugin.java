package org.elasticsearch.plugin.analysis.kit;

import java.util.Collection;
import java.util.Collections;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.indices.analysis.KitIndicesAnalysisModule;
import org.elasticsearch.plugins.Plugin;

public class AnalysisKitPlugin extends Plugin{
	@Override public String name() {
        return "KIT-analysis";
    }


    @Override public String description() {
        return "kit analysis";
    }

    @Override
    public Collection<Module> nodeModules() {
        return Collections.<Module>singletonList(new KitIndicesAnalysisModule());
    }
}
