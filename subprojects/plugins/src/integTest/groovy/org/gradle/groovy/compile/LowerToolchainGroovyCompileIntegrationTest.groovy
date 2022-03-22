/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.groovy.compile

import org.gradle.integtests.fixtures.AvailableJavaHomes
import org.gradle.internal.jvm.Jvm

class LowerToolchainGroovyCompileIntegrationTest extends AbstractToolchainGroovyCompileIntegrationTest {

    @Override
    def getGroovyJarVariants() {
        // Do not test with groovy-all with Groovy 4
        versionNumber.major >= 4
            ? ["groovy"]
            : ["groovy-all", "groovy"]
    }

    @Override
    Jvm computeJdkForTest() {
        AvailableJavaHomes.getAvailableJdk {
            it.languageVersion < Jvm.current().javaVersion && it.languageVersion.isJava8Compatible()
        }
    }
}
