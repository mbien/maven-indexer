package org.apache.maven.index.context;

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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.maven.index.IndexerField;
import org.apache.maven.index.creator.MinimalArtifactInfoIndexCreator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NexusAnalyzerTest
{
    protected NexusAnalyzer nexusAnalyzer = new NexusAnalyzer();

    @Test
    public void testGroupIdTokenization()
        throws IOException
    {
        runAndCompare( MinimalArtifactInfoIndexCreator.FLD_GROUP_ID, "org.slf4j", new String[] { "org", "slf4j" } );

        runAndCompare( MinimalArtifactInfoIndexCreator.FLD_GROUP_ID_KW, "org.slf4j", new String[] { "org.slf4j" } );
    }

    protected void runAndCompare( IndexerField indexerField, String text, String[] expected )
        throws IOException
    {
        TokenStream ts = nexusAnalyzer.tokenStream(indexerField.getKey(), new StringReader( text ) );
        ts.reset();

        ArrayList<String> tokenList = new ArrayList<>();

        if ( !indexerField.isKeyword() )
        {
            while ( ts.incrementToken() )
            {
                CharTermAttribute term = ts.addAttribute( CharTermAttribute.class );
                tokenList.add( term.toString());
            }
        }
        else
        {
            tokenList.add( text );
        }

        assertEquals( "The result does not meet the expectations.", Arrays.asList( expected ), tokenList );
    }

}
