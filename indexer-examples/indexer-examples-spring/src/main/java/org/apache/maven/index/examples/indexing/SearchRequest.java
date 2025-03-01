package org.apache.maven.index.examples.indexing;

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

/**
 * This class holds the details of the search request.
 *
 * @author mtodorov
 */
public class SearchRequest
{

    private String repository;

    private String query;


    public SearchRequest()
    {
        // no op
    }

    public SearchRequest( String repository, String query )
    {
        this.repository = repository;
        this.query = query;
    }

    public String getRepository()
    {
        return repository;
    }

    public void setRepository( String repository )
    {
        this.repository = repository;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

}
