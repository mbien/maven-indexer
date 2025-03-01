package org.apache.maven.index.reader.resource;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.net.URI;

import org.apache.maven.index.reader.ResourceHandler;

import static java.util.Objects.requireNonNull;

/**
 * A {@link ResourceHandler} that represents the base of a {@link URI} hierarchy.
 */
public class UriResourceHandler implements ResourceHandler
{
    private final URI uri;

    public UriResourceHandler( URI uri )
    {
        requireNonNull( uri, "uri cannot be null" );
        this.uri = uri;
    }

    @Override
    public Resource locate( String name ) throws IOException
    {
        return new UrlResource( uri.resolve( name ).toURL() );
    }
}
