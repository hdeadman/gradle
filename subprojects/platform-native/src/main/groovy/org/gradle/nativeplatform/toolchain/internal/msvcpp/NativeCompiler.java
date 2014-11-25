/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.nativeplatform.toolchain.internal.msvcpp;

import org.gradle.api.Transformer;
import org.gradle.api.internal.tasks.SimpleWorkResult;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.api.tasks.WorkResult;
import org.gradle.nativeplatform.toolchain.internal.*;
import org.gradle.nativeplatform.toolchain.internal.ShortCircuitArgsTransformer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

abstract public class NativeCompiler<T extends NativeCompileSpec> implements Compiler<T> {

    private final CommandLineTool commandLineTool;
    private final ArgsTransformer<T> argsTransFormer;
    private final Transformer<T, T> specTransformer;
    private final CommandLineToolInvocation baseInvocation;
    private final String objectFileSuffix;
    private final boolean useCommandFile;
    private final Transformer<List<String>, File> outputFileArgTransformer;

    NativeCompiler(CommandLineTool commandLineTool, CommandLineToolInvocation invocation, ArgsTransformer<T> argsTransFormer, Transformer<T, T> specTransformer, Transformer<List<String>, File> outputFileArgTransformer, String objectFileSuffix, boolean useCommandFile) {
        this.argsTransFormer = argsTransFormer;
        this.commandLineTool = commandLineTool;
        this.baseInvocation = invocation;
        this.specTransformer = specTransformer;
        this.objectFileSuffix = objectFileSuffix;
        this.useCommandFile = useCommandFile;
        this.outputFileArgTransformer = outputFileArgTransformer;
    }

    public WorkResult execute(T spec) {
        boolean windowsPathLimitation = OperatingSystem.current().isWindows();

        MutableCommandLineToolInvocation invocation = baseInvocation.copy();
        invocation.setWorkDirectory(spec.getObjectFileDir());
        if (useCommandFile) {
            invocation.addPostArgsAction(getPostArgsAction(spec));
        }

        for (File sourceFile : spec.getSourceFiles()) {
            SingleSourceCompileArgTransformer<T> argTransformer = new SingleSourceCompileArgTransformer<T>(sourceFile,
                    objectFileSuffix,
                    new ShortCircuitArgsTransformer<T>(argsTransFormer),
                    windowsPathLimitation,
                    outputFileArgTransformer);
            invocation.setArgs(argTransformer.transform(specTransformer.transform(spec)));
            commandLineTool.execute(invocation);
        }
        return new SimpleWorkResult(!spec.getSourceFiles().isEmpty());
    }

    protected abstract OptionsFileArgsTransformer getPostArgsAction(T spec);
}
