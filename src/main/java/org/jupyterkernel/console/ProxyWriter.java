package org.jupyterkernel.console;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by thomas on 12.04.17.
 */
public class ProxyWriter  extends Writer {

    public ProxyWriter(StringWriter delegate){
        this.delegate = delegate;
    }

    private StringWriter delegate;

    public StringWriter getDelegate() {
        return delegate;
    }

    public void setDelegate(StringWriter delegate) {
        this.delegate = delegate;
    }


    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        System.out.println("writer"+cbuf);
        delegate.write(cbuf,off,len);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
