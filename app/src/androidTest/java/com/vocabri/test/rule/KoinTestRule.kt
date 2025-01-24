package com.vocabri.test.rule

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class KoinTestRule(private val modules: List<org.koin.core.module.Module>) : TestRule {
    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            try {
                stopKoin()
                startKoin { modules(modules) }
                base.evaluate()
            } finally {
                stopKoin()
            }
        }
    }
}
