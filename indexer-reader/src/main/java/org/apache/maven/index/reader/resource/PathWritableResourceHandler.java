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

import java.nio.file.Path;

import org.apache.maven.index.reader.WritableResourceHandler;

import static java.util.Objects.requireNonNull;

/**
 * A {@link WritableResourceHandler} that represents the base of a {@link Path} hierarchy.
 */
public class PathWritableResourceHandler implements WritableResourceHandler
{
    private final Path path;

    public PathWritableResourceHandler( Path path )
    {
        requireNonNull( path, "path cannot be null" );
        this.path = path;
    }

    @Override
    public WritableResource locate( String name )
    {
        return new PathWritableResource( path.resolve( name ) );
    }
}
