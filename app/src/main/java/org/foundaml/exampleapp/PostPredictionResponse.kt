package org.foundaml.exampleapp

data class Label(val label: String, val probability: Float, val correctExampleUrl: String, val incorrectExampleUrl: String)

data class PostPredictionResponse(val type: String, val id: String, val projectId: String, val algorithmId: String, val features: List<List<String>>, val labels: List<Label>)