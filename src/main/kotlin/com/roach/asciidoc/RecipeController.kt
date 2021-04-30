package com.roach.asciidoc

import org.asciidoctor.*
import org.asciidoctor.jruby.AsciiDocDirectoryWalker
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileInputStream
import java.util.HashMap
import org.apache.commons.io.IOUtils
import org.asciidoctor.AttributesBuilder.attributes
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@RestController
@RequestMapping("/api")
class RecipeController {

    private final var asciidoctor: Asciidoctor = Asciidoctor.Factory.create()

    init {
        asciidoctor.requireLibrary("asciidoctor-diagram")
    }

    @GetMapping(path=["/recipe.html"], produces = [MediaType.TEXT_HTML_VALUE])
    fun recipeAsHtml(): ResponseEntity<String> {
        val attributes = HashMap<String, Any>()
        val dir = File("src/main/docs/recipes/spine.adoc")
        val target = File("src/main/docs/recipes/spine.html")
        attributes["backend"] = "html5" // (1)

        val opts = HashMap<String, Any>()
        opts["attributes"] = attributes // (2)
        opts["in_place"] = true

        asciidoctor.convertFile(
            dir,
            OptionsBuilder.options()
                .inPlace(true)
                .docType("book")
                .backend("html5")
                .attributes(
                    AttributesBuilder.attributes()
                    .sectionNumbers(true)
                    .copyCss(true)
                    .icons(Attributes.FONT_ICONS)
                    .sourceHighlighter("rouge")
                    .tableOfContents(true)
                    .tableOfContents(Placement.TOP)
                )
                .safe(SafeMode.UNSAFE)
                .get())
        //asciidoctor.convertDirectory(AsciiDocDirectoryWalker(d).scan(), OptionsBuilder.options().backend("pdf").get())

        val html = IOUtils.toString(FileInputStream(target), "UTF-8")
        return ResponseEntity.ok(html)
    }


    @GetMapping(path=["/recipe.pdf"], produces = [MediaType.APPLICATION_PDF_VALUE])
    fun recipeAsPdf(): ResponseEntity<InputStreamResource> {
        val attributes = HashMap<String, Any>()
        val dir = File("src/main/docs/recipes/spine.adoc")
        val target = File("src/main/docs/recipes/spine.pdf")
        attributes["backend"] = "PDF" // (1)

        val opts = HashMap<String, Any>()
        opts["attributes"] = attributes // (2)
        opts["in_place"] = true
        val f = File("")
        asciidoctor.convertFile(
            dir,
            OptionsBuilder.options()
                .inPlace(true)
                .docType("book")
                .backend("pdf")
                .attributes(
                    AttributesBuilder.attributes()
                        .sectionNumbers(true)
                        .copyCss(true)
                        .icons(Attributes.FONT_ICONS)
                        .sourceHighlighter("rouge")
                        .tableOfContents(true)
                        .tableOfContents(Placement.TOP)
                        .attribute("pdf-stylesdir", "${f.absolutePath}/styles")
                        .attribute("pdf-style", "custom")
                        .attribute("pdf-fontsdir", "${f.absolutePath}/styles/fonts")
                )
                .safe(SafeMode.UNSAFE)
                .get())
        //asciidoctor.convertDirectory(AsciiDocDirectoryWalker(d).scan(), OptionsBuilder.options().backend("pdf").get())

        val headers = HttpHeaders()
        headers.add("Content-Disposition", "inline; filename=spline.pdf")
        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body( InputStreamResource(FileInputStream(target)));
    }

    @GetMapping(path=["/recipe.epub"], produces = ["application/epub+zip"])
    fun getBookEPub(): ResponseEntity<InputStreamResource> {
        val dir = File("src/main/docs/recipes/spine.adoc")
        val target = File("src/main/docs/recipes/spine.epub")

        asciidoctor.convertFile(dir, OptionsBuilder.options().safe(SafeMode.SAFE).backend("epub3").get())

        val headers = HttpHeaders()
        headers.add("Content-Disposition", "inline; filename=spline.epub")
        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.valueOf("application/epub+zip"))
            .body( InputStreamResource(FileInputStream(target)));
    }
}

