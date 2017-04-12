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

import jdk.jshell.JShell;
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
                .err(new PrintStream(new WriterOutputStream(stderrWriter, Charset.defaultCharset()))).build();

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
        ex.printStackTrace(new PrintWriter(stderrWriter));
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

                }
                codeString = completionInfo.remaining();
                if (codeString.isEmpty()) {
                    break;
                }
            }

        } catch (Exception e) {
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
