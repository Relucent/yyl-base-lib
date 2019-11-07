package com.github.relucent.base.plug.jackson.databind;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter(MixinSource.FILTER_ID)
public interface MixinSource {
    String FILTER_ID = "myJsonFilter";
}
