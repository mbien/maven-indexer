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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.apache.maven.index.reader.WritableResourceHandler.WritableResource;

import static java.util.Objects.requireNonNull;

/**
 * A {@link WritableResource} that represents a {@link Path}.
 */
public class PathWritableResource implements WritableResource
{
    private final Path path;

    public PathWritableResource( Path path )
    {
        requireNonNull( path, "path cannot be null" );
        this.path = path;
    }

    @Override
    public OutputStream write() throws IOException
    {
        return Files.newOutputStream( path );
    }

    @Override
    public InputStream read() throws IOException
    {
        try
        {
            return Files.newInputStream( path );
        }
        catch ( NoSuchFileException e )
        {
            return null;
        }
    }

    @Override
    public void close() throws IOException
    {
    }
}
