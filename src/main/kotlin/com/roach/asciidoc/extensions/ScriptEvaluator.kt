package com.roach.asciidoc.extensions

import java.lang.RuntimeException
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.StringScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.util.isError
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

class ScriptEvaluator {

    inline fun <reified T> parseResult(source: String) : T {
        val compilerConfiguration = createJvmCompilationConfigurationFromTemplate<Any> {
            jvm {
              dependenciesFromCurrentContext(
                  wholeClasspath = true
              )
            }
        }
        val res = BasicJvmScriptingHost().eval(StringScriptSource(source), compilerConfiguration, null)
        if(!res.isError()) {
            val temp = (res as ResultWithDiagnostics.Success<EvaluationResult>).value
            val q = (temp.returnValue as ResultValue.Value).value
            q?.let {
                return q as T
            }
            throw RuntimeException("Failed parsing $source")
        } else {
            val sb  = StringBuilder()
            res.reports.forEach{scriptDiagnostic -> sb.append(scriptDiagnostic.message) }
            throw RuntimeException(sb.toString())
        }
    }
}