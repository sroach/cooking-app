package com.roach.asciidoc.extensions.model

import com.roach.asciidoc.extensions.toLine
import org.junit.Assert
import org.junit.Test

class RecipeDslTest {

    @Test
    fun dsl() {
        val dsl = recipe {
            title = "Plait Bread &#x1F956;"

            step {
                title = "Yeast Prep"
                +"1/4 cup warm water about 100 ℉."
                +"1 tsp sugar"
                +"In a bowl place water and sugar and yeast together. Stir and let it sit for 10 minutes."
            }
            step {
                title = "Ingredients"
                +"3 1/4 cups flour"
                +"1/2 tsp salt"
                +"1/3 cup sugar"
                +"1/4 melted butter"
                +"3/4 cup warm milk"
                +"1/4 cup powdered"
            }
            instruction {
                +"In a stand mixer add flour, sugar and salt."
                +"Start the mixer on low, slowly add the butter, milk and yeast."
                +"Let the mixer go for about 8 minutes."
                +"Turn off mixer, cover dough with damp cloth for an hour."
                +"After an Hour punch air out from dough and let rise for another hour"
                +"Hand Knead dough for a minute"
                +"Cut into 3 equal parts and roll them to about 9 inches long"
                +"Plait the 3 parts together and let sit to rise for 30 minutes"
                +"Preheat oven for 30 minutes at 350℉"
                +"Bake bread in the oven for about 25-30 minutes"
                +"Remove bread if brown enough and let cool for 10 minutes, enjoy!"
            }
        }
        Assert.assertEquals(2, dsl.steps.size)
    }
    @Test
    fun splitIntoSubLine() {
        val str = "HOW MANY CHARS needed before we need a new line? Is it 47 is it 90 what really is the length? I would think it's somewhere close to 80 but who know? let's try this Deprecated Gradle features were used in this build, making it incompatible with Gradle 7.0. " +
                "Use '--warning-mode all' to show the individual deprecation warnings."
        val lines = toLine(str)
        lines.forEach { println(it) }
    }

}