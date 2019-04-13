package org.foundaml.exampleapp

data class PostPredictionRequest(val projectId: String, val algorithmId: String, val features: List<List<String>>)