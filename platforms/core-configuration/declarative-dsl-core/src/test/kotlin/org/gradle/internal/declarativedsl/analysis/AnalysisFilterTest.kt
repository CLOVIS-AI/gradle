/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UNUSED_PARAMETER")

package org.gradle.internal.declarativedsl.analysis

import org.gradle.internal.declarativedsl.Configuring
import org.gradle.internal.declarativedsl.Restricted
import org.gradle.internal.declarativedsl.demo.resolve
import org.gradle.internal.declarativedsl.language.Assignment
import org.gradle.internal.declarativedsl.language.FunctionCall
import org.gradle.internal.declarativedsl.language.PropertyAccess
import org.gradle.internal.declarativedsl.schemaBuilder.schemaFromTypes
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


private
class TopLevelForAnalysisFilterTest {
    @Restricted
    val n1: NestedForAnalysisFilterTest
        get() = TODO()

    @Restricted
    val n2: NestedForAnalysisFilterTest
        get() = TODO()

    @Configuring
    fun n1(fn: NestedForAnalysisFilterTest.() -> Unit): Unit = TODO()

    @Configuring
    fun n2(fn: NestedForAnalysisFilterTest.() -> Unit): Unit = TODO()
}


private
class NestedForAnalysisFilterTest {
    @Restricted
    var x = 1
}


class AnalysisFilterTest {
    val schema = schemaFromTypes(TopLevelForAnalysisFilterTest::class, listOf(TopLevelForAnalysisFilterTest::class, NestedForAnalysisFilterTest::class))

    @Test
    fun `filtering assignment is supported`() {
        val result = schema.resolve(
            """
            n1.x = 2
            n2.x = 3
            """.trimIndent(),
            defaultCodeResolver { statement, _ ->
                statement is Assignment && statement.lhs.receiver.run { this is PropertyAccess && name == "n1" }
            }
        )

        assertEquals(1, result.assignments.size)
        assertEquals("2", result.assignments.single().rhs.originElement.sourceData.text())
    }

    @Test
    fun `filtering a function calls also filters out its lambda`() {
        val result = schema.resolve(
            """
            n1 {
                x = 4
            }
            n2 {
                x = 5
            }
            """.trimIndent(),
            defaultCodeResolver { statement, _ ->
                if (statement is FunctionCall) {
                    statement.name != "n2"
                } else true
            }
        )

        assertEquals(1, result.assignments.size)
        assertEquals("4", result.assignments.single().rhs.originElement.sourceData.text())
    }
}
