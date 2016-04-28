/*
 * Copyright 2002-2015 the original author or authors.
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

package net.breakidea.common.support;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import net.breakidea.common.ConfigConstants;
import net.breakidea.common.util.JsonUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * Implementation of {@link org.springframework.http.converter.HttpMessageConverter}
 * that can read and write JSON using the
 * <a href="https://code.google.com/p/google-gson/">Google Gson</a> library's
 * {@link Gson} class.
 *
 * <p>This converter can be used to bind to typed beans or untyped {@code HashMap}s.
 * By default, it supports {@code application/json} and {@code application/*+json} with
 * {@code UTF-8} character set.
 *
 * <p>Tested against Gson 2.3; compatible with Gson 2.0 and higher.
 *
 * @author Roy Clarkson
 * @since 4.1
 * @see #setGson
 * @see #setSupportedMediaTypes
 */
public class JsonMessageConverter extends AbstractGenericHttpMessageConverter<Object> implements ConfigConstants, GenericHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName(CHARSET);

    private Gson gson = JsonUtils.getInstance();

    /**
     * Construct a new {@code GsonHttpMessageConverter}.
     */
    public JsonMessageConverter() {
        super(MediaType.APPLICATION_JSON_UTF8, new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    @Override
    public boolean canRead( Class<?> clazz, MediaType mediaType ) {
        return canRead(mediaType);
    }

    @Override
    public boolean canWrite( Class<?> clazz, MediaType mediaType ) {
        return canWrite(mediaType);
    }

    /**
     * @param headers
     * @return
     */
    private Charset getCharset( HttpHeaders headers ) {
        if (headers == null || headers.getContentType() == null || headers.getContentType().getCharSet() == null) {
            return DEFAULT_CHARSET;
        }
        return headers.getContentType().getCharSet();
    }

    /**
     * Return the Gson {@link TypeToken} for the specified type.
     * <p>The default implementation returns {@code TypeToken.get(type)}, but
     * this can be overridden in subclasses to allow for custom generic
     * collection handling. For instance:
     * <pre class="code">
     * protected TypeToken<?> getTypeToken(Type type) {
     *   if (type instanceof Class && List.class.isAssignableFrom((Class<?>) type)) {
     *     return new TypeToken<ArrayList<MyBean>>() {};
     *   }
     *   else {
     *     return super.getTypeToken(type);
     *   }
     * }
     * </pre>
     * @param type the type for which to return the TypeToken
     * @return the type token
     */
    protected TypeToken<?> getTypeToken( Type type ) {
        return TypeToken.get(type);
    }

    @Override
    public Object read( Type type, Class<?> contextClass, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException {

        TypeToken<?> token = getTypeToken(type);
        return readTypeToken(token, inputMessage);
    }

    @Override
    protected Object readInternal( Class<?> clazz, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException {

        TypeToken<?> token = getTypeToken(clazz);
        return readTypeToken(token, inputMessage);
    }

    /**
     * @param token
     * @param inputMessage
     * @return
     * @throws IOException
     */
    private Object readTypeToken( TypeToken<?> token, HttpInputMessage inputMessage ) throws IOException {
        Reader json = new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));
        try {
            return gson.fromJson(json, token.getType());
        } catch (JsonParseException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected boolean supports( Class<?> clazz ) {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal( Object o, Type type, HttpOutputMessage outputMessage ) throws IOException, HttpMessageNotWritableException {

        Charset charset = getCharset(outputMessage.getHeaders());
        OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), charset);
        try {
            if (type != null) {
                gson.toJson(o, type, writer);
            } else {
                gson.toJson(o, writer);
            }
            writer.close();
        } catch (JsonIOException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

}
