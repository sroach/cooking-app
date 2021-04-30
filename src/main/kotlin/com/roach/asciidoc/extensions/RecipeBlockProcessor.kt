package com.roach.asciidoc.extensions

import com.roach.asciidoc.extensions.model.Recipe
import org.asciidoctor.ast.ContentModel
import org.asciidoctor.ast.StructuralNode
import org.asciidoctor.extension.BlockProcessor
import org.asciidoctor.extension.Contexts
import org.asciidoctor.extension.Name
import org.asciidoctor.extension.Reader
import java.io.File


@Name("recipe")
@Contexts(Contexts.LISTING)
@ContentModel(ContentModel.COMPOUND)
class RecipeBlockProcessor : BlockProcessor (){
    override fun process(parent: StructuralNode, reader: Reader, attributes: MutableMap<String, Any>): Any? {
        val content = reader.read() 

        val recipe = ScriptEvaluator().parseResult<Recipe>("import com.roach.asciidoc.extensions.model.*\n$content")

        val filename = attributes.getOrDefault("2", "${System.currentTimeMillis()}_unk")
        val svg = RecipeToSvg().convert(recipe)

        val f = File("${reader.dir}/images/$filename.svg")
        f.writeBytes(svg.toByteArray())

        val contentLines = mutableListOf<String>()
        contentLines.add(".$filename");
        contentLines.add("image::images/$filename.svg[Interactive,opts=interactive]");

        parseContent(parent, contentLines)
        return null

    }
}