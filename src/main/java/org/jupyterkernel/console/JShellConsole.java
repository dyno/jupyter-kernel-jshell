/*
 * Copyright 2016 kay schluehr.
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
package org.jupyterkernel.console;

/**
 * @author kay schluehr, thomas kratz
 */

import jdk.jshell.EvalException;
import jdk.jshell.JShell;
import jdk.jshell.JShellException;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis.Completeness;
import jdk.jshell.SourceCodeAnalysis.CompletionInfo;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;
import org.jupyterkernel.json.messages.T_kernel_info_reply;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

public class JShellConsole {

    private JShell jshell;

    private JupyterStreamWriter jupyterStreamWriter;

    private StringWriter stdoutWriter = new StringWriter();
    private StringWriter stderrWriter = new StringWriter();

//    private ProxyWriter proxyWriter = new ProxyWriter(stdoutWriter);


    int cellnum = 0;
    int completionCursorPosition = -1;

    private Exception ex;

    private void getClasses() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            System.out.println(url.getFile());
        }
        String strClassPath = System.getProperty("java.class.path");

        System.out.println("Classpath is " + strClassPath);
    }

    public JShellConsole() {
        jshell = JShell.builder().out(new PrintStream(new WriterOutputStream(stdoutWriter, Charset.defaultCharset(), 100, true)))
                .err(new PrintStream(new WriterOutputStream(stderrWriter, Charset.defaultCharset())))
        .build();




        jshell.addToClasspath("dependency/hibernate-core-5.2.9.Final.jar");
        jshell.addToClasspath("dependency/jboss-logging-3.3.0.Final.jar");
        jshell.addToClasspath("dependency/hibernate-jpa-2.1-api-1.0.0.Final.jar");
        jshell.addToClasspath("dependency/javassist-3.20.0-GA.jar");
        jshell.addToClasspath("dependency/antlr-2.7.7.jar");
        jshell.addToClasspath("dependency/jboss-transaction-api_1.2_spec-1.0.1.Final.jar");
        jshell.addToClasspath("dependency/jandex-2.0.3.Final.jar");
        jshell.addToClasspath("dependency/classmate-1.3.0.jar");
        jshell.addToClasspath("dependency/dom4j-1.6.1.jar");
        jshell.addToClasspath("dependency/hibernate-commons-annotations-5.0.1.Final.jar");
        jshell.addToClasspath("dependency/h2-1.4.194.jar");
    }

    public void setStdinReader(ConsoleInputReader reader) {
        System.setIn(new ReaderInputStream(reader, Charset.defaultCharset()));
    }

    public void setStreamWriter(JupyterStreamWriter streamWriter) {
//        System.out.println("setwriter");
        this.jupyterStreamWriter = streamWriter;
//        proxyWriter.setDelegate(jupyterStreamWriter);
// TODO when do we need this?
    }

    public void stopStreaming() {

        if (jupyterStreamWriter != null) {
            jupyterStreamWriter.stopStreaming();
        }
    }

    public String getMIMEType() {
        return "text/plain";
    }

    public void setCellNumber(int cell) {
        cellnum = cell;
    }

    // used to handle complete_request message
    public int getCompletionCursorPosition() {
        return this.completionCursorPosition;
    }

    protected void setErrorMessage() {
        ex.printStackTrace(new PrintWriter(stdoutWriter));
    }

    public String[] getTraceback() {
        return null;
    }

    /**
     * @param codeString source code which is evaluted by the ScriptEngine
     * @return result of the evaluation
     */
    public Object eval(String codeString) {

        ex = null;
        try {


            while (true) {
                CompletionInfo completionInfo = jshell.sourceCodeAnalysis().analyzeCompletion(codeString);
                if (completionInfo.completeness().equals(Completeness.EMPTY)) {
                    break;
                }
//            System.out.println("eval");
                List<SnippetEvent> snippetEvents = jshell.eval(completionInfo.source());
                for (SnippetEvent evt : snippetEvents) {

                    jshell.diagnostics(evt.snippet()).forEach(snip -> stderrWriter.append(snip.getMessage(Locale.ENGLISH)));
                    JShellException e = evt.exception();
                    if (e != null) {
                        e.printStackTrace(new PrintWriter(stderrWriter));
                        if(e instanceof EvalException){
                            EvalException eve = (EvalException)e;
                            stderrWriter.append(eve.getExceptionClassName());
                            stderrWriter.append(" "+eve.getMessage());
                            for(Throwable t :eve.getSuppressed()){
                                t.printStackTrace();
                            }
                        }
                    }
                }

                codeString = completionInfo.remaining();
                if (codeString.isEmpty()) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
            setErrorMessage();
        }
        return null;
    }

    public String readAndClearStdout() {
        String s = stdoutWriter.toString();
//        System.out.println("readandclear "+s);
        stdoutWriter.flush();
        StringBuffer sb = stdoutWriter.getBuffer();
        sb.delete(0, sb.length());
        return s;
    }

    public String readAndClearStderr() {
        String S = stderrWriter.toString();
        stderrWriter.flush();
        StringBuffer sb = stderrWriter.getBuffer();
        sb.delete(0, sb.length());
        return S;
    }

    // language specific -- not implemented here
    public String[] completion(String source, int cursor_position) {
        return new String[]{};
    }

    public T_kernel_info_reply getKernelInfo() {
        return new T_kernel_info_reply();
    }
}
