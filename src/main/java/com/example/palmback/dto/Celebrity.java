//package com.example.palmback.dto;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//public class Celebrity {
//    private final String name;
//    private final String summary;
//    private float[] embedding; // 타입을 float[]로 변경하여 에러 해결
//
//    @JsonCreator
//    public Celebrity(@JsonProperty("name") String name,
//                     @JsonProperty("summary") String summary) {
//        this.name = name;
//        this.summary = summary;
//    }
//
//    public String getName() { return name; }
//    public String getSummary() { return summary; }
//    public float[] getEmbedding() { return embedding; }
//
//    public void setEmbedding(float[] embedding) {
//        this.embedding = embedding;
//    }
//}