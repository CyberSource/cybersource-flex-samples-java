package com.cybersource.example.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// The capture context response has much more to it, but we're mainly interested in the clientLibrary, which is nested
// within ctx.data, so let's make sure we can get that at least.
@JsonIgnoreProperties(ignoreUnknown = true)
public record CaptureContextResponseBody(List<CTX> ctx) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CTX(Data data) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(String clientLibrary) {}
}
