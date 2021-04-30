package com.roach.asciidoc.extensions.model

@RecipeDslMarker
class RecipeDsl {

    var title = ""

    private var steps = mutableListOf<Step>()
    private var instructions: Instructions = Instructions(mutableListOf())

    fun getSteps() = steps.toList()
    fun getInstructions() = instructions

    fun step(step: StepBuilder.() -> Unit) {
        steps.add(StepBuilder().apply(step).build())
    }

    fun instruction(instructionBuilder: InstructionBuilder.() -> Unit) {
        instructions = InstructionBuilder().apply(instructionBuilder).build()
    }
}

@RecipeDslMarker
data class Recipe(val title: String, val steps: List<Step>, val instructions: Instructions)
@RecipeDslMarker
data class Step(val title: String, val steps: MutableList<String>)
@RecipeDslMarker
data class Instructions(val instructions: MutableList<String>)

@RecipeDslMarker
class StepBuilder {
    var title = ""
    private val steps = mutableListOf<String>()

    fun build(): Step {
        return Step(title, steps)
    }
    operator fun String.unaryPlus() {
        steps.add(this)
    }
}

class InstructionBuilder {
    private val inst = mutableListOf<String>()

    fun build(): Instructions {
        return Instructions(inst)
    }
    operator fun String.unaryPlus() {
        inst.add(this)
    }
}

fun recipe(recipe: RecipeDsl.() -> Unit): Recipe {
    val dsl =  RecipeDsl().apply(recipe)
    return Recipe(dsl.title,dsl.getSteps(), dsl.getInstructions())
}