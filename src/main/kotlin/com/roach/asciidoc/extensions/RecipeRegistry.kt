package com.roach.asciidoc.extensions

import org.asciidoctor.Asciidoctor
import org.asciidoctor.extension.JavaExtensionRegistry
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry

class RecipeRegistry : ExtensionRegistry {
    override fun register(asciidoctor: Asciidoctor) {
        val reg = asciidoctor.javaExtensionRegistry()
        reg.block(RecipeBlockProcessor::class.java)
    }
}