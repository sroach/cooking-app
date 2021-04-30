package com.roach.asciidoc.extensions

import com.roach.asciidoc.extensions.model.Instructions
import com.roach.asciidoc.extensions.model.Recipe
import com.roach.asciidoc.extensions.model.Step

class RecipeToSvg {

    private val width = 600
    fun convert(recipe: Recipe) : String {
        val sb = StringBuilder()
        val boxHeight = determineBoxHeight(recipe)
        sb.append("""<svg
                    width="$width" height="${boxHeight + 100}"
                    version="1.1"
                    xmlns="http://www.w3.org/2000/svg"
                    xmlns:xlink="http://www.w3.org/1999/xlink"
            >
                <style>

                    .title {
                        font-size: 36px;
                        font-weight: bold;
                        fill: #ffffff;
                    }

                    .ingredients_title {
                        font-size: 20px;
                        font-weight: bold;
                        fill: #D62229;
                    }

                    .ingredient {
                        font-family: "Helvetica Neue", "Helvetica", Helvetica, Arial, sans-serif;
                        font-size: 10px;
                        font-weight: bold;
                        fill: #222;
                    }
            
                    .ingredient_list {
                        font-family: "Helvetica Neue", "Helvetica", Helvetica, Arial, sans-serif;
                    }


                    .back {
                        fill: none;
                        stroke-width: 12px;
                        stroke: #D62229;
                        stroke-dasharray: 10 10;
                    }

                    .subHead {
                        font: bold 14px "Helvetica Neue", "Helvetica", Helvetica, Arial, sans-serif;
                        fill: #E97639;
                    }

                    .title_back {
                        fill: #7b3900;
                        background-image: linear-gradient(to top, #80001e 0%, white 100%);
                    }
                    .bullet{
                        fill: #D62229;
                    }
                </style>
                <defs>
                    <filter id="filter" x="0" y="0" width="200" height="200" >
                        <feOffset result="offOut" in="SourceAlpha" dx="2" dy="3"/>
                        <feColorMatrix result="matrixOut" in="offOut" type="matrix"
                                       values=" 0.37 0 0 0 0 0 0.36 0 0 0 0 0 0.36 0 0 0 0 0 0.5 0"/>
                        <feGaussianBlur result="blurOut" in="matrixOut" stdDeviation="3"/>
                        <feBlend in="SourceGraphic" in2="blurOut" mode="normal"/>
                    </filter>


                    <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="0">
                        <stop offset="0%" style="stop-color:#33235b;"/>
                        <stop offset="25%" style="stop-color:#D62229;"/>
                        <stop offset="50%" style="stop-color:#E97639;"/>
                        <stop offset="75%" style="stop-color:#792042;"/>
                        <stop offset="100%" style="stop-color:#33235b;"/>
                    </linearGradient>
                    <pattern id="pattern" x="0" y="0" width="300%" height="100%" patternUnits="userSpaceOnUse">
                        <rect x="0" y="0" width="150%" height="100%" fill="url(#gradient)">
                            <animate attributeType="XML"
                                     attributeName="x"
                                     from="0" to="150%"
                                     dur="7s"
                                     repeatCount="indefinite"/>
                        </rect>
                        <rect x="-150%" y="0" width="150%" height="100%" fill="url(#gradient)">
                            <animate attributeType="XML"
                                     attributeName="x"
                                     from="-150%" to="0"
                                     dur="7s"
                                     repeatCount="indefinite"/>
                        </rect>
                    </pattern>
                </defs>

                <rect width="100%" height="100%" class="back" rx="10" ry="10"/>
                <rect x="10" y="10" width="580" height="50" fill="url(#gradient)" rx="5" ry="5"/>
                <text x="290" y="45" text-anchor="middle" class="title" fill="url(#pattern)">${recipe.title}</text>
               
                <rect x="10" y="70" width="580" height="$boxHeight" fill="#ffffff" rx="15" fill-opacity="0.8" filter="url(#filter)"/>
                <text x="290" y="100" class="ingredients_title" text-anchor="middle">Ingredients</text>""")
        var initialY = 130
        val res = steps(recipe.steps, initialY)
        sb.append(res.str)
        initialY = res.y
        val inst = instructions(recipe.instructions, initialY)
        sb.append(inst.str)
        sb.append("</svg>")
        return sb.toString()
    }

    private fun steps(steps: List<Step>,  yStart: Int): StepResult {
        //todo figure out box height before steps plus instructions
        val sb = StringBuilder()
        val xStart = 20
        var start = yStart
        steps.forEach { step ->
            sb.append("""<text x="$xStart" y="$start" class="subHead">${step.title}</text>""")
            start += 12
            sb.append("""<text y="$start" class="ingredient_list">""")
            step.steps.forEach {

                val lines = toLine(it)
                if(lines.size == 1) {
                    start += 14
                    sb.append("""<tspan class="ingredient" x="20" dy="14"><tspan class="bullet">&#x2611;</tspan> $it </tspan>""")
                } else {
                    lines.forEachIndexed { index, line ->
                        start += 14
                        updateLine(index, sb, line)
                    }
                }
            }
            sb.append("""</text>""")
            start += 24
        }
        return StepResult(sb.toString(), start)
    }

    private fun instructions(inst: Instructions, yStart: Int): StepResult {
        val sb = StringBuilder()
        sb.append("""<text x="290" y="$yStart" class="ingredients_title" text-anchor="middle">Instructions</text>""")
        var start = yStart + 12
        sb.append("""<text y="$start">""")
        inst.instructions.forEach {
            val lines = toLine(it)
            if(lines.size == 1) {
                start += 14
                sb.append("""<tspan class="ingredient" x="20" dy="14"><tspan class="bullet">&#x2611;</tspan> $it </tspan>""")
            } else {
                lines.forEachIndexed { index, line ->
                    start += 14
                    updateLine(index, sb, line)
                }
            }
        }
        sb.append("""</text>""")
        return StepResult(sb.toString(), start)
    }

    private fun updateLine(index: Int, sb: StringBuilder, line: String) {
        if (index == 0) {
            sb.append("""<tspan class="ingredient" x="20" dy="14"><tspan class="bullet">&#x2611;</tspan> $line </tspan>""")
        } else {
            sb.append("""<tspan class="ingredient" x="20" dy="14"> $line </tspan>""")
        }
    }

    class StepResult(val str: String, val y:Int)

    private fun determineBoxHeight(recipe: Recipe): Int {
        var initial = 70
        recipe.steps.forEach {
           initial += it.steps.size * 14
        }
        initial += recipe.steps.size * 28
        initial += 28
        initial += recipe.instructions.instructions.size * 14
        initial += 28
        return initial + 10
    }

 }

fun toLine(str: String): MutableList<String> {
    val rez =str.split(" ")
    val LINE_LENGTH = 90
    val arr = mutableListOf<String>()
    var sb = StringBuilder()
    rez.forEach {
        val next = sb.length + it.length + 1
        if(next < LINE_LENGTH ) {
            sb.append("$it ")
        } else {
            arr.add(sb.toString())
            sb = StringBuilder()
        }
    }
    if(sb.isNotEmpty()) {
        arr.add(sb.toString())
    }
    return arr
}